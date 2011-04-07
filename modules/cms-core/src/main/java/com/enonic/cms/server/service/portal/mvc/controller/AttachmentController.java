/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.portal.mvc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.common.io.ByteStreams;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.content.binary.BinaryService;
import com.enonic.cms.core.security.AutoLoginService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.server.domain.content.binary.AttachmentRequestResolver;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.business.SitePathResolver;
import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.business.portal.livetrace.AttachmentRequestTrace;
import com.enonic.cms.business.portal.livetrace.AttachmentRequestTracer;
import com.enonic.cms.business.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.business.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.business.portal.livetrace.PortalRequestTracer;
import com.enonic.cms.business.preview.PreviewService;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.binary.AttachmentNotFoundException;
import com.enonic.cms.domain.content.binary.AttachmentRequest;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.portal.PathRequiresAuthenticationException;
import com.enonic.cms.domain.portal.ReservedLocalPaths;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

public class AttachmentController
    extends AbstractController
    implements InitializingBean
{

    private BinaryService binaryService;

    private ContentDao contentDao;

    private SiteService siteService;

    private AutoLoginService autoLoginService;

    private SitePathResolver sitePathResolver;

    private SecurityService securityService;

    private AttachmentRequestResolver attachmentRequestResolver;

    protected ResourceService resourceService;

    protected SitePropertiesService sitePropertiesService;

    private SiteDao siteDao;

    private UserDao userDao;

    private LivePortalTraceService livePortalTraceService;

    private PreviewService previewService;

    public void afterPropertiesSet()
        throws Exception
    {

        attachmentRequestResolver = new AttachmentRequestResolver()
        {

            @Override
            protected BinaryDataKey getBinaryData( ContentEntity content, String label )
            {
                BinaryDataEntity binaryData = content.getMainVersion().getSingleBinaryData( label );
                if ( binaryData != null )
                {
                    return new BinaryDataKey( binaryData.getKey() );
                }
                return null;
            }

            @Override
            protected ContentEntity getContent( ContentKey contentKey )
            {
                return contentDao.findByKey( contentKey );
            }
        };

    }

    protected final ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        PortalRequestTrace portalRequestTrace =
            PortalRequestTracer.startTracing( (String) request.getAttribute( Attribute.ORIGINAL_URL ), livePortalTraceService );
        try
        {
            PortalRequestTracer.traceMode( portalRequestTrace, previewService );
            PortalRequestTracer.traceHttpRequest( portalRequestTrace, request );

            // Get check and eventually set original sitePath
            SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
            if ( originalSitePath == null )
            {
                originalSitePath = sitePathResolver.resolveSitePath( request );
                siteService.checkSiteExist( originalSitePath.getSiteKey() );
                request.setAttribute( Attribute.ORIGINAL_SITEPATH, originalSitePath );
            }

            // Get and set the current sitePath
            SitePath currentSitePath = sitePathResolver.resolveSitePath( request );
            request.setAttribute( Attribute.CURRENT_SITEPATH, currentSitePath );

            PortalRequestTracer.traceRequestedSitePath( portalRequestTrace, currentSitePath );
            PortalRequestTracer.traceRequestedSite( portalRequestTrace, siteDao.findByKey( currentSitePath.getSiteKey() ) );

            try
            {
                return handleRequestInternal( request, response, currentSitePath, portalRequestTrace );
            }
            catch ( Exception e )
            {
                throw new AttachmentRequestException( originalSitePath, request.getHeader( "referer" ), e );
            }
        }
        finally
        {
            PortalRequestTracer.stopTracing( portalRequestTrace, livePortalTraceService );
        }
    }

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath,
                                                  PortalRequestTrace portalRequestTrace )
        throws Exception
    {

        UserEntity loggedInUser = userDao.findByKey( securityService.getLoggedInPortalUser().getKey() );
        if ( loggedInUser.isAnonymous() )
        {
            if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED, sitePath.getSiteKey() ) )
            {
                loggedInUser = autoLoginService.autologinWithRemoteUser( request );
            }
        }
        if ( loggedInUser.isAnonymous() )
        {
            if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_REMEMBER_ME_COOKIE_ENABLED,
                                                             sitePath.getSiteKey() ) )
            {
                loggedInUser = autoLoginService.autologinWithCookie( sitePath.getSiteKey(), request, response );
            }
        }

        PortalRequestTracer.traceRequester( portalRequestTrace, loggedInUser );

        final AttachmentRequestTrace attachmentRequestTrace = AttachmentRequestTracer.startTracing( livePortalTraceService );

        try
        {
            final AttachmentRequest attachmentRequest = attachmentRequestResolver.resolveBinaryDataKey( sitePath.getPathAndParams() );

            AttachmentRequestTracer.traceAttachmentRequest( attachmentRequestTrace, attachmentRequest );

            verifyValidMenuItemInPath( sitePath );

            boolean downloadRequested = "true".equals( request.getParameter( "download" ) );
            downloadRequested |= "true".equals( request.getParameter( "_download" ) );

            final BinaryDataEntity binaryData;

            try
            {
                binaryData = binaryService.getBinaryDataForPortal( loggedInUser, attachmentRequest );
            }
            catch ( AttachmentNotFoundException e )
            {
                if ( loggedInUser.isAnonymous() && downloadRequested && e.reasonIsNoAccess() )
                {
                    throw new PathRequiresAuthenticationException( sitePath );
                }
                else
                {
                    throw e;
                }
            }

            setHttpHeaders( request, response, sitePath, loggedInUser );

            putBinaryOnResponse( downloadRequested, response, binaryData );

            return null;
        }
        finally
        {
            AttachmentRequestTracer.stopTracing( attachmentRequestTrace, livePortalTraceService );
        }
    }

    private void verifyValidMenuItemInPath( SitePath sitePath )
    {
        SiteEntity site = siteDao.findByKey( sitePath.getSiteKey() );

        Path menuItemPath = getAttachmentMenuItemPath( sitePath );

        MenuItemEntity menuItem = site.resolveMenuItemByPath( menuItemPath );

        if ( menuItem == null )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }
    }


    private Path getAttachmentMenuItemPath( SitePath sitePath )
    {
        String pathAsString = sitePath.getLocalPath().toString();

        if ( !pathAsString.contains( ReservedLocalPaths.PATH_ATTACHMENT.toString() ) )
        {
            throw AttachmentNotFoundException.notFound( sitePath.getLocalPath().toString() );
        }

        int i = pathAsString.lastIndexOf( ReservedLocalPaths.PATH_ATTACHMENT.toString() );

        String menuItemPathAsString = pathAsString.substring( 0, i );

        return new Path( menuItemPathAsString );
    }

    private void setHttpHeaders( final HttpServletRequest request, final HttpServletResponse response, final SitePath sitePath,
                                 final UserEntity requester )
    {
        final DateTime now = new DateTime();
        HttpServletUtil.setDateHeader( response, now.toDate() );

        final boolean cacheHeadersEnabled =
            sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_ENABLED, sitePath.getSiteKey() );

        if ( cacheHeadersEnabled )
        {
            final boolean forceNoCache =
                sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_FORCENOCACHE,
                                                            sitePath.getSiteKey() );

            if ( forceNoCache || isInPreviewMode( request ) )
            {
                HttpServletUtil.setCacheControlNoCache( response );
            }
            else
            {
                Integer siteCacheSettingsMaxAge =
                    sitePropertiesService.getPropertyAsInteger( SitePropertyNames.ATTACHMENT_CACHE_HEADERS_MAXAGE, sitePath.getSiteKey() );
                boolean publicAccess = requester.isAnonymous();
                enableHttpHeadersCache( response, sitePath, now, siteCacheSettingsMaxAge, publicAccess );
            }
        }
    }

    private void enableHttpHeadersCache( HttpServletResponse response, SitePath sitePath, DateTime now, Integer siteCacheSettingsMaxAge,
                                         boolean publicAccess )
    {
        int maxAge;

        boolean cacheForever = hasTimestampParameter( sitePath );

        if ( cacheForever )
        {
            maxAge = HttpCacheControlSettings.CACHE_FOREVER_SECONDS;
        }
        else
        {
            maxAge = siteCacheSettingsMaxAge;
        }

        final DateTime expirationTime = now.plusSeconds( maxAge );

        final HttpCacheControlSettings cacheControlSettings = new HttpCacheControlSettings();
        cacheControlSettings.maxAgeSecondsToLive = (long) maxAge;
        cacheControlSettings.publicAccess = publicAccess;
        HttpServletUtil.setExpiresHeader( response, expirationTime.toDate() );
        HttpServletUtil.setCacheControl( response, cacheControlSettings );
    }

    protected boolean hasTimestampParameter( SitePath sitePath )
    {
        String timestamp = sitePath.getParam( "_ts" );
        return StringUtils.isNotBlank( timestamp );
    }

    private void putBinaryOnResponse( boolean download, HttpServletResponse response, BinaryDataEntity binaryData )
        throws IOException
    {
        final BlobRecord blob = this.binaryService.fetchBinary( binaryData.getBinaryDataKey() );
        HttpServletUtil.setContentDisposition( response, download, binaryData.getName() );

        response.setContentType( HttpServletUtil.resolveMimeType( getServletContext(), binaryData.getName() ) );
        response.setContentLength( (int) blob.getLength() );

        ByteStreams.copy( blob.getStream(), response.getOutputStream() );
    }

    private boolean isInPreviewMode( HttpServletRequest httpRequest )
    {
        String previewEnabled = (String) httpRequest.getAttribute( Attribute.PREVIEW_ENABLED );
        return "true".equals( previewEnabled );
    }

    public void setBinaryService( BinaryService value )
    {
        this.binaryService = value;
    }

    public void setContentDao( ContentDao dao )
    {
        contentDao = dao;
    }

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setAutoLoginService( AutoLoginService value )
    {
        this.autoLoginService = value;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    public void setResourceService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }

    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }
}
