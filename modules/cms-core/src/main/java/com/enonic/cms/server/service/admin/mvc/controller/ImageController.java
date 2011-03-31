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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.business.image.ImageRequest;
import com.enonic.cms.business.image.ImageRequestParser;
import com.enonic.cms.business.image.ImageResponse;
import com.enonic.cms.business.portal.image.ImageProcessorException;
import com.enonic.cms.business.portal.image.ImageService;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.security.user.User;

public final class ImageController
    extends AbstractController
{
    private ImageService imageService;

    private boolean disableParamEncoding;

    private final ImageRequestParser requestParser = new ImageRequestParser( true );

    private SecurityService securityService;


    protected final ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
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
        imageRequest.setServeOfflineContent( true );
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
            res.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
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
}
