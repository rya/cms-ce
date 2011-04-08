/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.octo.captcha.service.CaptchaServiceException;

import com.enonic.cms.core.captcha.CaptchaRepository;

/**
 * The captcha image generator.
 */
public class CaptchaController
        extends AbstractController
{
    CaptchaRepository captchaRepository;

    protected ModelAndView handleRequestInternal( HttpServletRequest req, HttpServletResponse resp )
            throws Exception
    {
        generateImageOnResponse( req, resp );
        return null;
    }

    public void generateImageOnResponse( HttpServletRequest request, HttpServletResponse response )
            throws IOException
    {

        try
        {

            byte[] captchaChallengeAsJpeg;
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();

            String captchaId = request.getSession().getId();
            BufferedImage challenge = captchaRepository.getImageChallengeForID( captchaId, request.getLocale() );

            ImageIO.write( challenge, "png", imageOutputStream );
            captchaChallengeAsJpeg = imageOutputStream.toByteArray();

            // flush it in the response
            response.setHeader( "Cache-Control", "no-store" );
            response.setHeader( "Pragma", "no-cache" );
            response.setDateHeader( "Expires", 0 );
            response.setContentType( "image/png" );
            ServletOutputStream responseOutputStream = response.getOutputStream();
            responseOutputStream.write( captchaChallengeAsJpeg );
            responseOutputStream.flush();
            responseOutputStream.close();
//            ( "CaptchaController: Image returned." );

        }
        catch ( IIOException e )
        {
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            return;
        }
        catch ( CaptchaServiceException e )
        {
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            return;
        }
        catch ( IllegalArgumentException e )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }


    }

    public void setCaptchaRepository( CaptchaRepository captchaRepository )
    {
        this.captchaRepository = captchaRepository;
    }
}
