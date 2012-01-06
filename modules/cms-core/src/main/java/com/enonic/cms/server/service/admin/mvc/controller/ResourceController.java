/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;
import com.enonic.cms.framework.util.MimeTypeResolver;

/**
 * This class implements a file controller that returns the actual referenced file in
 * the servlet context.
 */
public class ResourceController
    extends AbstractController
    implements InitializingBean
{

    private UrlPathHelper urlPathHelper;

    private ServletContextResourceLoader resourceLoader;

    public ResourceController()
    {
        urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode( true );
    }

    public void afterPropertiesSet()
    {
        this.resourceLoader = new ServletContextResourceLoader( getServletContext() );
    }

    /**
     * Handle the request.
     */
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        String path = urlPathHelper.getPathWithinApplication( request );
        Resource resource = this.resourceLoader.getResource( path );

        if ( resource.exists() )
        {
            DateTime now = new DateTime();
            DateTime expirationDate = now.plusHours( 1 );

            HttpCacheControlSettings cacheControlSettings = new HttpCacheControlSettings();
            cacheControlSettings.maxAgeSecondsToLive = new Interval( now, expirationDate ).toDurationMillis() / 1000;
            cacheControlSettings.publicAccess = true;

            HttpServletUtil.setDateHeader( response, now.toDate() );
            HttpServletUtil.setExpiresHeader( response, expirationDate.toDate() );
            HttpServletUtil.setCacheControl( response, cacheControlSettings );

            serveResourceToResponse( request, response, resource );
        }

        return null;
    }

    /**
     * Serve resource to response.
     */
    protected void serveResourceToResponse( HttpServletRequest request, HttpServletResponse response, Resource resource )
        throws Exception
    {
        BufferedInputStream in = new BufferedInputStream( resource.getInputStream() );
        BufferedOutputStream out = new BufferedOutputStream( response.getOutputStream() );
        response.setContentType( MimeTypeResolver.getInstance().getMimeType( resource.getFilename() ) );
        HttpServletUtil.copyNoCloseOut( in, out );
    }
}
