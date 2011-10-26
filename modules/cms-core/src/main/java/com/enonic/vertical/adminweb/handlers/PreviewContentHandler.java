/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.servlet.http.HttpServletRequestWrapper;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.LanguageEntity;
import com.enonic.cms.core.Path;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.content.ContentAndVersion;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;

import com.enonic.cms.core.portal.rendering.PageRenderer;
import com.enonic.cms.core.portal.rendering.PageRendererContext;
import com.enonic.cms.core.portal.rendering.PageRendererFactory;
import com.enonic.cms.core.portal.rendering.RegionsResolver;
import com.enonic.cms.business.preview.ContentPreviewContext;
import com.enonic.cms.business.preview.PreviewContext;
import com.enonic.cms.business.preview.PreviewService;
import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;

import com.enonic.cms.core.LanguageResolver;

import com.enonic.cms.core.content.ContentVersionEntity;

import com.enonic.cms.core.portal.PageRequestType;
import com.enonic.cms.core.portal.ReservedLocalPaths;
import com.enonic.cms.core.portal.rendering.RenderedPageResult;
import com.enonic.cms.core.resolver.ResolverContext;

import com.enonic.cms.core.structure.page.template.PageTemplateEntity;

/**
 * Feb 22, 2010
 */
public class PreviewContentHandler
{
    private HttpServletRequest request;

    private HttpSession session;

    private TimeService timeService;

    private PreviewService previewService;

    private PageRendererFactory pageRendererFactory;

    private ContentDao contentDao;

    private ContentVersionDao contentVersionDao;

    private LocaleResolverService localeResolverService;

    private DeviceClassResolverService deviceClassResolverService;

    private ExtendedMap formItems;

    private UserEntity previewer;

    private String sessionId;

    private SiteEntity site;

    private PageTemplateEntity pageTemplate;

    private final ContentKey contentKey;

    private final boolean contentIsNew;

    public PreviewContentHandler( ContentKey contentKey )
    {
        this.contentKey = contentKey;
        this.contentIsNew = contentKey == null;
    }

    public RenderedPageResult renderPreview()
    {
        final ContentAndVersion contentAndVersion = resolveContentAndVersion();

        final ContentEntity content = contentAndVersion.getContent();
        if ( content.getKey() == null )
        {
            throw new IllegalArgumentException( "Preview of unsaved content is not supported" );
        }

        //final XMLDocument contentXml = createXml( contentAndVersion );
        final MenuItemEntity menuItem = resolveContentHome( content );

        // Override base path
        final SitePath sitePath = new SitePath( site.getKey(), Path.ROOT.appendPath( ReservedLocalPaths.PATH_PAGE ) );

        request.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );
        request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        // wrap and modify request
        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request );
        wrappedRequest.setServletPath( "/site" );
        wrappedRequest.setParameter( "id", menuItem.getMenuItemKey().toString() );
        if ( this.contentKey != null )
        {
            wrappedRequest.setParameter( "key", contentKey.toString() );
        }
        ServletRequestAccessor.setRequest( wrappedRequest );

        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersion );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );
        previewService.setPreviewContext( previewContext );

        // prepare data source result processors
        //final DataSourceProcessor[] dsrProcessors = createDataSourceProcessor( contentXml );

        final UserEntity runAsUser = resolveRunAsUser( menuItem );
        final Regions regionsInPage = RegionsResolver.resolveRegionsForPageRequest( menuItem, pageTemplate, PageRequestType.CONTENT );
        final LanguageEntity language = LanguageResolver.resolve( content, site, menuItem );
        final ResolverContext resolverContext = createResolverContext( menuItem, wrappedRequest, language );
        final Locale locale = localeResolverService.getLocale( resolverContext );
        final String deviceClass = deviceClassResolverService.getDeviceClass( resolverContext );

        sitePath.addParam( "id", menuItem.getMenuItemKey().toString() );
        if ( this.contentKey != null )
        {
            sitePath.addParam( "key", contentKey.toString() );
        }

        PageRendererContext pageRendererContext = new PageRendererContext();
        pageRendererContext.setDeviceClass( deviceClass );
        pageRendererContext.setForceNoCacheUsage( true );
        pageRendererContext.setHttpRequest( wrappedRequest );
        pageRendererContext.setLanguage( language );
        pageRendererContext.setLocale( locale );
        pageRendererContext.setMenuItem( menuItem );
        pageRendererContext.setContentFromRequest( content );
        pageRendererContext.setOriginalSitePath( sitePath );
        pageRendererContext.setPageRequestType( PageRequestType.CONTENT );
        pageRendererContext.setPreviewContext( previewContext );
        pageRendererContext.setProcessors( null );
        pageRendererContext.setRegionsInPage( regionsInPage );
        pageRendererContext.setRenderer( previewer );
        pageRendererContext.setRequestTime( new DateTime() );
        pageRendererContext.setRunAsUser( runAsUser );
        pageRendererContext.setTicketId( sessionId );
        pageRendererContext.setSite( site );
        pageRendererContext.setSitePath( sitePath );

        // render page
        PageRenderer renderer = pageRendererFactory.createPageRenderer( pageRendererContext );

        return renderer.renderPage( pageTemplate );
    }

    private ContentAndVersion resolveContentAndVersion()
    {
        ContentAndVersion contentAndVersion;
        final boolean previewNewContentOrVersionBeeingEdited = contentIsNew || formItems.getBoolean( "sessiondata", false );

        final boolean previewOfSpecifiedApprovedOrArchivedVersion = formItems.containsKey( "versionkey" );

        if ( previewNewContentOrVersionBeeingEdited )
        {
            // preview of content being edited, either new or existing requested
            contentAndVersion = resolveContentAndVersionForNewContentOrEditedVersion();
        }
        else if ( previewOfSpecifiedApprovedOrArchivedVersion )
        {
            // preview of archived or approved version requested...
            contentAndVersion = resolveContentAndVersionForArchivedOrApprovedVersion();
        }
        else
        {
            // preview of main version...
            contentAndVersion = resolveContentAndVersionForMainVersion();
        }

        final ContentEntity content = contentAndVersion.getContent();

        // Ensure available from is set when not, cause many rendering templates are depending on that, since that will be the normal situation when rendering
        if ( content.getAvailableFrom() == null )
        {
            content.setAvailableFrom( timeService.getNowAsDateTime().toDate() );
        }

        return contentAndVersion;
    }

    private ContentAndVersion resolveContentAndVersionForNewContentOrEditedVersion()
    {
        ContentAndVersion contentAndVersion;
        ContentAndVersion contentAndVersionFromSession = (ContentAndVersion) session.getAttribute( "_preview-content-and-version" );
        ContentEntity content = new ContentEntity( contentAndVersionFromSession.getContent() );
        ContentVersionEntity version = contentAndVersionFromSession.getVersion();

        if ( contentIsNew )
        {
            // apply not-yet set (but needed) meta data on the new content
            final Date timeNow = timeService.getNowAsDateTime().toDate();
            content.setCreatedAt( timeNow );
            content.setTimestamp( timeNow );
            content.setOwner( previewer );
            version.setModifiedBy( previewer );
            version.setModifiedAt( timeNow );
        }
        else
        {
            // ensure edited content are having the same menu locations as the existing one
            ContentEntity existingContent = contentDao.findByKey( contentKey );
            content.setDirectMenuItemPlacements( existingContent.getDirectMenuItemPlacements() );
            content.setSectionContents( existingContent.getSectionContents() );
            content.setContentHomes( existingContent.getContentHomesAsMap() );
        }

        contentAndVersion = new ContentAndVersion( content, version );
        return contentAndVersion;
    }

    private ContentAndVersion resolveContentAndVersionForArchivedOrApprovedVersion()
    {
        ContentAndVersion contentAndVersion;
        ContentEntity existingContent = contentDao.findByKey( contentKey );
        ContentEntity content = new ContentEntity( existingContent );
        ContentVersionKey versionKey = new ContentVersionKey( formItems.getInt( "versionkey" ) );

        ContentVersionEntity version = contentVersionDao.findByKey( versionKey );
        contentAndVersion = new ContentAndVersion( content, version );
        content.setMainVersion( version );
        version.setContent( content );
        return contentAndVersion;
    }

    private ContentAndVersion resolveContentAndVersionForMainVersion()
    {
        ContentAndVersion contentAndVersion;
        ContentEntity existingContent = contentDao.findByKey( contentKey );
        ContentEntity content = new ContentEntity( existingContent );
        ContentVersionEntity version = content.getMainVersion();
        contentAndVersion = new ContentAndVersion( content, version );
        content.setMainVersion( version );
        version.setContent( content );
        return contentAndVersion;
    }

    private MenuItemEntity resolveContentHome( ContentEntity content )
    {
        if ( contentIsNew )
        {
            // fallback when previewing new content
            return getSiteFrontPageOrFirstMenuItem();
        }

        ContentLocationSpecification contentLocationSpec = new ContentLocationSpecification();
        contentLocationSpec.setSiteKey( site.getKey() );
        contentLocationSpec.setIncludeInactiveLocationsInSection( false );
        ContentLocations contentLocations = content.getLocations( contentLocationSpec );
        ContentLocation contentLocation = contentLocations.getHomeLocation( site.getKey() );

        if ( contentLocation != null )
        {
            return contentLocation.getMenuItem();
        }

        // fallback
        return getSiteFrontPageOrFirstMenuItem();
    }

    private MenuItemEntity getSiteFrontPageOrFirstMenuItem()
    {
        if ( site.getFrontPage() != null )
        {
            return site.getFrontPage();
        }
        return site.getFirstMenuItem();
    }

    private UserEntity resolveRunAsUser( MenuItemEntity menuItem )
    {
        UserEntity runAsUser = menuItem.resolveRunAsUser( previewer, true );
        if ( runAsUser == null )
        {
            runAsUser = previewer;
        }
        return runAsUser;
    }

    private ResolverContext createResolverContext( MenuItemEntity menuItem, HttpServletRequestWrapper wrappedRequest,
                                                   LanguageEntity language )
    {
        final ResolverContext resolverContext = new ResolverContext( wrappedRequest, site, menuItem, language );
        resolverContext.setUser( previewer );
        return resolverContext;
    }

    public void setPageRendererFactory( PageRendererFactory pageRendererFactory )
    {
        this.pageRendererFactory = pageRendererFactory;
    }

    public void setRequest( HttpServletRequest request )
    {
        this.request = request;
    }

    public void setSession( HttpSession session )
    {
        this.session = session;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setContentVersionDao( ContentVersionDao contentVersionDao )
    {
        this.contentVersionDao = contentVersionDao;
    }

    public void setLocaleResolverService( LocaleResolverService localeResolverService )
    {
        this.localeResolverService = localeResolverService;
    }

    public void setDeviceClassResolverService( DeviceClassResolverService deviceClassResolverService )
    {
        this.deviceClassResolverService = deviceClassResolverService;
    }

    public void setFormItems( ExtendedMap formItems )
    {
        this.formItems = formItems;
    }

    public void setPreviewer( UserEntity previewer )
    {
        this.previewer = previewer;
    }

    public void setSessionId( String sessionId )
    {
        this.sessionId = sessionId;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setPageTemplate( PageTemplateEntity pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }
}
