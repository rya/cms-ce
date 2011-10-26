/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.framework.util.HttpServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public final class StaticFileController
    extends AbstractController
{
    private ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res)
        throws Exception
    {
        String resourcePath = resolveResourcePath( req );
        Resource resource = resourceLoader.getResource( resourcePath );
        HttpServletUtil.copyNoCloseOut( resource.getInputStream(), res.getOutputStream() );
        return null;
    }

    private String resolveResourcePath( HttpServletRequest req )
    {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path;
        if ( requestURI.startsWith( contextPath ) )
        {
            path = requestURI.substring( contextPath.length() );
        }
        else
        {
            path = requestURI;
        }

        return path;
    }
}
