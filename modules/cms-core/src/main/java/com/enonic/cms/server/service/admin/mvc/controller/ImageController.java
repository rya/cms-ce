/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.core.image.ImageRequestParser;
import com.enonic.cms.core.image.ImageResponse;
import com.enonic.cms.business.portal.image.ImageProcessorException;
import com.enonic.cms.business.portal.image.ImageRequestAccessResolver;
import com.enonic.cms.business.portal.image.ImageService;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;

public final class ImageController
    extends AbstractController
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageController.class );

    private ImageService imageService;

    private boolean disableParamEncoding;

    private final ImageRequestParser requestParser = new ImageRequestParser( true );

    private SecurityService securityService;

    private ContentDao contentDao;

    private GroupDao groupDao;


    public final ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        ImageRequest imageRequest = createImageRequest( request );
        process( imageRequest, response );
        return null;
    }

    private ImageRequest createImageRequest( HttpServletRequest request )
    {
        HashMap<String, String> params = new HashMap<String, String>();
        Enumeration e = request.getParameterNames();

        while ( e.hasMoreElements() )
        {
            String key = (String) e.nextElement();
            params.put( key, request.getParameter( key ) );
        }

        boolean encodeParams = !( this.disableParamEncoding || RenderTrace.isTraceOn() );
        ImageRequest imageRequest = this.requestParser.parse( request.getPathInfo(), params, encodeParams );
        imageRequest.setRequester( resolveRequester() );
        imageRequest.setRequestDateTime( new DateTime() );
        return imageRequest;
    }

    private User resolveRequester()
    {
        return securityService.getLoggedInAdminConsoleUser();
    }

    private void process( ImageRequest req, HttpServletResponse res )
        throws IOException
    {
        if ( !hasRequestAccess( req ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        try
        {
            ImageResponse imageResponse = this.imageService.process( req );
            if ( imageResponse.isImageNotFound() )
            {
                res.sendError( HttpServletResponse.SC_NOT_FOUND );
            }
            else
            {
                res.setContentType( imageResponse.getMimeType() );
                res.setContentLength( imageResponse.getSize() );
                HttpServletUtil.copyNoCloseOut( imageResponse.getDataAsStream(), res.getOutputStream() );
            }
        }
        catch ( ImageProcessorException e )
        {
            LOG.warn( e.getMessage(), e );
            res.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    private boolean hasRequestAccess( final ImageRequest imageRequest )
    {
        final UserEntity loggedInPortalUser = securityService.getLoggedInAdminConsoleUserAsEntity();

        ImageRequestAccessResolver.Access access =
            new ImageRequestAccessResolver( contentDao, new ContentAccessResolver( groupDao ) ).imageRequester(
                loggedInPortalUser ).isAccessible( imageRequest );

        return access == ImageRequestAccessResolver.Access.OK;
    }

    public void setImageService( ImageService imageService )
    {
        this.imageService = imageService;
    }

    public void setDisableParamEncoding( boolean disableParamEncoding )
    {
        this.disableParamEncoding = disableParamEncoding;
    }

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }
}
