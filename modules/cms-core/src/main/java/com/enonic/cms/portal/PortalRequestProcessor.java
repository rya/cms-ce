/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.page.WindowKey;
import com.enonic.cms.portal.processor.*;
import com.enonic.cms.portal.rendering.*;
import com.google.common.base.Preconditions;

import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.portal.livetrace.PortalRequestTracer;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.livetrace.PortalRequestTrace;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.RequestParametersMerger;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.portal.processor.DirectiveRequestProcessorContext;
import com.enonic.cms.portal.processor.DirectiveRequestProcessorResult;
import com.enonic.cms.portal.processor.PageRequestProcessorContext;
import com.enonic.cms.portal.processor.PageRequestProcessorResult;
import com.enonic.cms.portal.rendering.RenderedPageResult;
import com.enonic.cms.portal.rendering.RenderedWindowResult;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * Class for processing portal requests. Create new instance for each use.
 */
public class PortalRequestProcessor
{
    private final PortalRequest request;

    private UserEntity requester;

    private SiteEntity requestedSite;

    private PortalRequestTrace portalRequestTrace;

    private SiteDao siteDao;

    private PortletDao portletDao;

    private UserDao userDao;

    private ContentDao contentDao;

    private LanguageDao languageDao;

    private PageRendererFactory pageRendererFactory;

    private WindowRendererFactory windowRendererFactory;

    private DataSourceService dataSourceService;

    private PortalAccessService portalAccessService;

    private PageRequestProcessorFactory pageRequestProcessorFactory;

    private LivePortalTraceService liveTraceService;

    public PortalRequestProcessor( PortalRequest request )
    {
        this.request = request;
    }

    public PortalResponse processRequest()
    {
        portalRequestTrace = liveTraceService.getCurrentPortalRequestTrace();

        PortalResponse portalResponse = null;
        try
        {
            final SitePath sitePath = request.getSitePath();

            requester = resolveRequester( request );
            requestedSite = resolveSite( sitePath );

            PortalRequestTracer.traceRequestedSite( portalRequestTrace, requestedSite );
            PortalRequestTracer.traceRequester( portalRequestTrace, requester );

            PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
            final PageRequestContext pageRequestContext = pageRequestContextResolver.resolvePageRequestContext( requestedSite, sitePath );

            final MenuItemEntity menuItem = pageRequestContext.getRequestedMenuItem();
            if ( menuItem == null )
            {
                throw new ResourceNotFoundException( sitePath.getSiteKey(), request.getOriginalUrl(), request.getHttpReferer() );
            }

            final MenuItemRequestType type = resolveMenuItemRequestType( menuItem, pageRequestContext );
            if ( type == MenuItemRequestType.PAGE )
            {
                RequestParameters mergedRequestParameters =
                    RequestParametersMerger.mergeWithMenuItemRequestParameters( sitePath.getRequestParameters(),
                                                                                menuItem.getRequestParameters() );

                SitePath newSitePath = new SitePath( sitePath.getSiteKey(), sitePath.getLocalPath(), mergedRequestParameters );
                newSitePath.setContentPath( pageRequestContext.getResolvedContentPath() );
                request.setSitePath( newSitePath );
                pageRequestContext.setSitePath( newSitePath );

                portalResponse = processPageRequest( pageRequestContext );
            }
            else if ( type == MenuItemRequestType.DIRECTIVE )
            {
                portalResponse = processDirectiveRequest( request, menuItem );
            }
            else
            {
                throw new IllegalArgumentException( "Menuitem request type not supported: " + type );
            }
        }
        finally
        {
            PortalRequestTracer.tracePortalResponse( portalRequestTrace, portalResponse );
        }

        return portalResponse;
    }

    private PortalResponse processPageRequest( final PageRequestContext pageRequestContext )
    {
        Preconditions.checkNotNull( pageRequestContext.getRequestedMenuItem(), "Expected requested menuItem not to be null at this stage" );
        Preconditions.checkNotNull( pageRequestContext.getPageRequestType(), "Expected pageRequestType not to be null at this stage" );

        final AbstractPageRequestProcessor pageRequestProcessor = createPageRequestProcessor( request, pageRequestContext, requester );
        final PageRequestProcessorResult processorResult = pageRequestProcessor.process();

        if ( processorResult.getRedirectToSitePath() != null )
        {
            RedirectInstruction redirectInstruction = new RedirectInstruction( processorResult.getRedirectToSitePath() );
            redirectInstruction.setPermanentRedirect( true );

            return PortalResponse.createRedirect( redirectInstruction );
        }

        HttpServletRequest httpRequest = ServletRequestAccessor.getRequest();
        if ( processorResult.getHttpRequest() != null )
        {
            httpRequest = processorResult.getHttpRequest();
        }
        ServletRequestAccessor.setRequest( httpRequest );

        final SitePath sitePath = processorResult.getSitePath();
        final PageRequestType pageRequestType = pageRequestContext.getPageRequestType();
        final MenuItemEntity menuItem = pageRequestContext.getRequestedMenuItem();
        final SiteEntity site = menuItem.getSite();

        if ( sitePath.hasReferenceToWindow() )
        {
            final WindowReference windowReference = sitePath.getPortletReference();
            final PortletEntity requestedPortlet =
                portletDao.findBySiteKeyAndNameIgnoreCase( site.getKey(), windowReference.getPortletName() );

            if ( requestedPortlet == null )
            {
                throw new ResourceNotFoundException( site.getKey(), sitePath.getLocalPath() );
            }

            final WindowRendererContext windowRendererContext = new WindowRendererContext();
            windowRendererContext.setContentFromRequest( processorResult.getContentFromRequest() );
            windowRendererContext.setDeviceClass( processorResult.getDeviceClass() );
            windowRendererContext.setForceNoCacheUsage( false );
            windowRendererContext.setEncodeURIs( request.isEncodeURIs() );
            windowRendererContext.setHttpRequest( httpRequest );
            windowRendererContext.setInvocationCache( new InvocationCache( dataSourceService ) );
            windowRendererContext.setLanguage( processorResult.getLanguage() );
            windowRendererContext.setLocale( processorResult.getLocale() );
            windowRendererContext.setMenuItem( menuItem );
            windowRendererContext.setOriginalSitePath( request.getOriginalSitePath() );
            windowRendererContext.setOriginalUrl( request.getOriginalUrl() );
            windowRendererContext.setPageRequestType( pageRequestType );
            windowRendererContext.setPreviewContext( request.getPreviewContext() );
            windowRendererContext.setProcessors( null );
            windowRendererContext.setRegionsInPage( processorResult.getRegionsInPage() );
            windowRendererContext.setRenderedInline( false );
            windowRendererContext.setRenderer( requester );
            windowRendererContext.setTicketId( request.getTicketId() );
            windowRendererContext.setShoppingCart( request.getShoppingCart() );
            windowRendererContext.setSite( site );
            windowRendererContext.setSitePath( sitePath );
            windowRendererContext.setVerticalSession( request.getVerticalSession() );

            final WindowRenderer windowRenderer = windowRendererFactory.createPortletRenderer( windowRendererContext );

            WindowKey requestedWindow = new WindowKey( menuItem.getMenuItemKey(), requestedPortlet.getPortletKey() );
            final RenderedWindowResult renderedWindowResult = windowRenderer.renderWindowDirect( requestedWindow );

            return PortalResponse.createContent( renderedWindowResult );
        }
        else
        {
            final PageRendererContext pageRendererContext = new PageRendererContext();
            pageRendererContext.setContentFromRequest( processorResult.getContentFromRequest() );
            pageRendererContext.setDeviceClass( processorResult.getDeviceClass() );
            pageRendererContext.setForceNoCacheUsage( false );
            pageRendererContext.setHttpRequest( httpRequest );
            pageRendererContext.setMenuItem( menuItem );
            pageRendererContext.setLanguage( processorResult.getLanguage() );
            pageRendererContext.setLocale( processorResult.getLocale() );
            pageRendererContext.setOriginalUrl( request.getOriginalUrl() );
            pageRendererContext.setOriginalSitePath( request.getOriginalSitePath() );
            pageRendererContext.setRegionsInPage( processorResult.getRegionsInPage() );
            pageRendererContext.setRenderer( requester );
            pageRendererContext.setRequestTime( request.getRequestTime() );
            pageRendererContext.setRunAsUser( processorResult.getRunAsUser() );
            pageRendererContext.setPageRequestType( pageRequestType );
            pageRendererContext.setPreviewContext( request.getPreviewContext() );
            pageRendererContext.setProfile( request.getProfile() );
            pageRendererContext.setProcessors( null );
            pageRendererContext.setTicketId( request.getTicketId() );
            pageRendererContext.setSite( site );
            pageRendererContext.setSitePath( sitePath );
            pageRendererContext.setShoppingCart( request.getShoppingCart() );
            pageRendererContext.setVerticalSession( request.getVerticalSession() );
            PageRenderer pageRenderer = pageRendererFactory.createPageRenderer( pageRendererContext );

            final RenderedPageResult result = pageRenderer.renderPage( processorResult.getPageTemplate() );

            return PortalResponse.createContent( result );
        }
    }

    private PortalResponse processDirectiveRequest( PortalRequest request, final MenuItemEntity menuItem )
    {
        final UserEntity requester = userDao.findByKey( request.getRequester() );
        if ( requester == null )
        {
            throw new PortalRenderingException( "Requester not found, user key: " + request.getRequester() );
        }

        DirectiveRequestProcessorContext context = new DirectiveRequestProcessorContext();
        context.setMenuItem( menuItem );
        context.setOriginalSitePath( request.getOriginalSitePath() );
        context.setSitePath( request.getSitePath() );
        context.setRequester( requester );
        context.setRequestParams( request.getRequestParams() );

        DirectiveRequestProcessor directiveProcessor = new DirectiveRequestProcessor( context );
        directiveProcessor.setPortalAccessService( portalAccessService );
        DirectiveRequestProcessorResult result = directiveProcessor.process();

        if ( result.getRedirectToAbsoluteURL() != null )
        {
            RedirectInstruction redirectInstruction = new RedirectInstruction( result.getRedirectToAbsoluteURL() );
            redirectInstruction.setPermanentRedirect( false );

            return PortalResponse.createRedirect( redirectInstruction );
        }
        else if ( result.getRedirectToSitePath() != null )
        {
            RedirectInstruction redirectInstruction = new RedirectInstruction( result.getRedirectToSitePath() );
            redirectInstruction.setPermanentRedirect( true );

            return PortalResponse.createRedirect( redirectInstruction );
        }
        else if ( result.getForwardToSitePath() != null )
        {
            return PortalResponse.createForward( result.getForwardToSitePath() );
        }
        else
        {
            throw new PortalRenderingException( "Unexpected directive request processor result: nothing set" );
        }
    }

    private MenuItemRequestType resolveMenuItemRequestType( MenuItemEntity menuItem, PageRequestContext pageRequestContext )
    {
        MenuItemType menuItemType = menuItem.getType();

        if ( menuItemType == MenuItemType.PAGE )
        {
            return MenuItemRequestType.PAGE;
        }
        else if ( menuItemType == MenuItemType.CONTENT )
        {
            return MenuItemRequestType.PAGE;
        }
        else if ( menuItemType == MenuItemType.SHORTCUT )
        {
            return MenuItemRequestType.DIRECTIVE;
        }
        else if ( menuItemType == MenuItemType.URL )
        {
            return MenuItemRequestType.DIRECTIVE;
        }
        else if ( menuItemType == MenuItemType.SECTION )
        {
            if ( pageRequestContext.hasPathToContent() )
            {
                // request to a content is a PAGE request, even if under a SECTION (that normally do not support requests)
                return MenuItemRequestType.PAGE;
            }
            else
            {
                throw new ResourceNotFoundException( pageRequestContext.getSitePath().getSiteKey(),
                                                     pageRequestContext.getSitePath().getLocalPath() );
            }
        }
        else if ( menuItemType == MenuItemType.LABEL )
        {
            if ( pageRequestContext.hasPathToContent() )
            {
                // request to a content is a PAGE request, even if under a LABEL (that normally do not support requests)
                return MenuItemRequestType.PAGE;
            }

            throw new PortalConfigurationException( "Menuitem of type label does not support requests" );
        }
        else
        {
            throw new IllegalArgumentException( "Unhandled menuitem type for request: " + menuItemType );
        }
    }

    private AbstractPageRequestProcessor createPageRequestProcessor( final PortalRequest request,
                                                                     final PageRequestContext pageRequestContext,
                                                                     final UserEntity requester )
    {
        final PageRequestProcessorContext pageRequestProcessorContext = new PageRequestProcessorContext();
        pageRequestProcessorContext.setHttpRequest( ServletRequestAccessor.getRequest() );
        pageRequestProcessorContext.setRequestTime( request.getRequestTime() );
        pageRequestProcessorContext.setPageRequestType( pageRequestContext.getPageRequestType() );
        pageRequestProcessorContext.setRequester( requester );
        pageRequestProcessorContext.setSitePath( request.getSitePath() );
        pageRequestProcessorContext.setSite( pageRequestContext.getRequestedMenuItem().getSite() );
        pageRequestProcessorContext.setMenuItem( pageRequestContext.getRequestedMenuItem() );
        pageRequestProcessorContext.setContentPath( pageRequestContext.getContentPath() );
        pageRequestProcessorContext.setPreviewContext( request.getPreviewContext() );
        if ( request.getOverridingLanguage() > -1 )
        {
            LanguageEntity overridingLanguage = languageDao.findByKey( new LanguageKey( request.getOverridingLanguage() ) );
            pageRequestProcessorContext.setOverridingLanguage( overridingLanguage );
        }

        return pageRequestProcessorFactory.create( pageRequestProcessorContext );
    }

    private SiteEntity resolveSite( final SitePath sitePath )
    {
        final SiteEntity site = siteDao.findByKey( sitePath.getSiteKey() );
        if ( site == null )
        {
            throw new SiteNotFoundException( sitePath.getSiteKey() );
        }
        return site;
    }

    private UserEntity resolveRequester( final PortalRequest request )
    {
        final UserEntity requester = userDao.findByKey( request.getRequester() );
        if ( requester == null )
        {
            throw new PortalRenderingException( "Requester not found, user key: " + request.getRequester() );
        }

        return requester;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setPortletDao( PortletDao portletDao )
    {
        this.portletDao = portletDao;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setLanguageDao( LanguageDao languageDao )
    {
        this.languageDao = languageDao;
    }

    public void setPageRendererFactory( PageRendererFactory pageRendererFactory )
    {
        this.pageRendererFactory = pageRendererFactory;
    }

    public void setWindowRendererFactory( WindowRendererFactory windowRendererFactory )
    {
        this.windowRendererFactory = windowRendererFactory;
    }

    public void setDataSourceService( DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }

    public void setPortalAccessService( PortalAccessService portalAccessService )
    {
        this.portalAccessService = portalAccessService;
    }

    public void setPageRequestProcessorFactory( PageRequestProcessorFactory pageRequestProcessorFactory )
    {
        this.pageRequestProcessorFactory = pageRequestProcessorFactory;
    }

    public void setLiveTraceService( LivePortalTraceService liveTraceService )
    {
        this.liveTraceService = liveTraceService;
    }
}
