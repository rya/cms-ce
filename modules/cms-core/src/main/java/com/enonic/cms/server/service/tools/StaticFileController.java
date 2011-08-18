/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import com.enonic.esl.containers.ExtendedMap;

public final class StaticFileController
    extends AbstractToolController
{


    private ResourceLoader resourceLoader;

    @Override
    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Autowired
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

  //  @Override
  //  protected ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
  //      throws Exception
  //  {
  //      String resourcePath = resolveResourcePath( req );
  //      Resource resource = resourceLoader.getResource( resourcePath );
  //      HttpServletUtil.copyNoCloseOut( resource.getInputStream(), res.getOutputStream() );
  //      return null;
  //  }

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
