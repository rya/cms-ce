/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.portal.mvc.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.image.ImageRequest;

import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.core.content.access.ContentAccessResolver;

import com.enonic.cms.core.image.ImageRequestParser;
import com.enonic.cms.core.image.ImageResponse;

import com.enonic.cms.business.portal.image.ImageProcessorException;
import com.enonic.cms.business.portal.image.ImageRequestAccessResolver;
import com.enonic.cms.business.portal.image.ImageService;
import com.enonic.cms.business.portal.livetrace.ImageRequestTrace;
import com.enonic.cms.business.portal.livetrace.ImageRequestTracer;
import com.enonic.cms.business.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.business.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.business.portal.livetrace.PortalRequestTracer;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.portal.ReservedLocalPaths;
import com.enonic.cms.domain.portal.ResourceNotFoundException;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

public final class ImageController
    extends AbstractSiteController
{
    private ImageService imageService;

    private boolean disableParamEncoding;

    private final ImageRequestParser requestParser = new ImageRequestParser();

    private LivePortalTraceService livePortalTraceService;

    public ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws Exception
    {
        PortalRequestTrace portalRequestTrace =
            PortalRequestTracer.startTracing( (String) request.getAttribute( Attribute.ORIGINAL_URL ), livePortalTraceService );

        try
        {
            PortalRequestTracer.traceMode( portalRequestTrace, previewService );
            PortalRequestTracer.traceHttpRequest( portalRequestTrace, request );
            PortalRequestTracer.traceRequestedSitePath( portalRequestTrace, sitePath );
            PortalRequestTracer.traceRequestedSite( portalRequestTrace, siteDao.findByKey( sitePath.getSiteKey() ) );

            UserEntity loggedInUser = securityService.getLoggedInPortalUserAsEntity();
            if ( loggedInUser.isAnonymous() )
            {
                if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED,
                                                                 sitePath.getSiteKey() ) )
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

            ImageRequest imageRequest = createImageRequest( request );

            final ImageRequestTrace imageRequestTrace = ImageRequestTracer.startTracing( livePortalTraceService );
            try
            {
                ImageRequestTracer.traceImageRequest( imageRequestTrace, imageRequest );
                process( imageRequest, response, sitePath, imageRequestTrace );
            }
            finally
            {
                ImageRequestTracer.stopTracing( imageRequestTrace, livePortalTraceService );
            }
            return null;
        }
        catch ( Exception e )
        {
            throw new ImageRequestException( sitePath, request.getHeader( "referer" ), e );
        }
        finally
        {
            PortalRequestTracer.stopTracing( portalRequestTrace, livePortalTraceService );
        }
    }

    private ImageRequest createImageRequest( final HttpServletRequest request )
    {
        final HashMap<String, String> params = new HashMap<String, String>();
        final Enumeration e = request.getParameterNames();

        while ( e.hasMoreElements() )
        {
            final String key = (String) e.nextElement();
            params.put( key, request.getParameter( key ) );
        }

        final boolean encodeParams = !( disableParamEncoding || RenderTrace.isTraceOn() );
        final ImageRequest imageRequest = requestParser.parse( request.getPathInfo(), params, encodeParams );

        imageRequest.setRequester( securityService.getLoggedInPortalUser() );
        imageRequest.setRequestDateTime( new DateTime() );
        return imageRequest;
    }

    private void process( final ImageRequest imageRequest, final HttpServletResponse response, final SitePath sitePath,
                          final ImageRequestTrace imageRequestTrace )
        throws IOException
    {
        verifyValidMenuItemInPath( sitePath );
        checkRequestAccess( imageRequest, sitePath );

        try
        {
            final ImageResponse imageResponse = imageService.process( imageRequest );

            if ( imageResponse.isImageNotFound() )
            {
                throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
            }

            final boolean anonymousAccess = securityService.getLoggedInPortalUser().isAnonymous();
            setHttpHeaders( response, sitePath, anonymousAccess );

            response.setContentType( imageResponse.getMimeType() );
            response.setContentLength( imageResponse.getSize() );
            ImageRequestTracer.traceSize( imageRequestTrace, (long) imageResponse.getSize() );

            HttpServletUtil.setContentDisposition( response, false, imageResponse.getName() );
            HttpServletUtil.copyNoCloseOut( imageResponse.getDataAsStream(), response.getOutputStream() );
        }
        catch ( ImageProcessorException e )
        {
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    private void verifyValidMenuItemInPath( SitePath sitePath )
    {
        SiteEntity site = siteDao.findByKey( sitePath.getSiteKey() );

        Path menuItemPath = getImageMenuItemPath( sitePath );

        MenuItemEntity menuItem = site.resolveMenuItemByPath( menuItemPath );

        if ( menuItem == null )
        {
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }
    }

    private Path getImageMenuItemPath( SitePath sitePath )
    {
        String pathAsString = sitePath.getLocalPath().toString();

        if ( !pathAsString.contains( ReservedLocalPaths.PATH_IMAGE.toString() ) )
        {
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }

        int i = pathAsString.lastIndexOf( ReservedLocalPaths.PATH_IMAGE.toString() );

        String menuItemPathAsString = pathAsString.substring( 0, i );

        return new Path( menuItemPathAsString );
    }

    private void setHttpHeaders( final HttpServletResponse response, final SitePath sitePath, final boolean anonymousAccess )
    {
        final DateTime now = new DateTime();
        HttpServletUtil.setDateHeader( response, now.toDate() );

        final boolean cacheHeadersEnabled =
            sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.IMAGE_CACHE_HEADERS_ENABLED, sitePath.getSiteKey() );
        if ( cacheHeadersEnabled )
        {
            final boolean forceNoCache =
                sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.IMAGE_CACHE_HEADERS_FORCENOCACHE, sitePath.getSiteKey() );
            if ( forceNoCache )
            {
                HttpServletUtil.setCacheControlNoCache( response );
            }
            else
            {
                Integer siteCacheSettingsMaxAge =
                    sitePropertiesService.getPropertyAsInteger( SitePropertyNames.IMAGE_CACHE_HEADERS_MAXAGE, sitePath.getSiteKey() );
                enableHttpCacheHeaders( response, sitePath, now, siteCacheSettingsMaxAge, anonymousAccess );
            }
        }
    }

    private void checkRequestAccess( final ImageRequest imageRequest, final SitePath sitePath )
    {
        final UserEntity loggedInPortalUser = securityService.getLoggedInPortalUserAsEntity();

        ImageRequestAccessResolver accessResolver = new ImageRequestAccessResolver( contentDao, new ContentAccessResolver( groupDao ) );
        accessResolver.imageRequester( loggedInPortalUser );
        accessResolver.requireMainVersion();
        accessResolver.requireOnlineNow( timeService.getNowAsDateTime(), previewService );
        final ImageRequestAccessResolver.Access access = accessResolver.isAccessible( imageRequest );

        if ( access != ImageRequestAccessResolver.Access.OK )
        {
            throw new ResourceNotFoundException( sitePath.getSiteKey(), sitePath.getLocalPath() );
        }
    }

    public void setDisableParamEncoding( boolean disableParamEncoding )
    {
        this.disableParamEncoding = disableParamEncoding;
    }

    public void setImageService( ImageService imageService )
    {
        this.imageService = imageService;
    }

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }
}
