/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.dwr;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.servlet.DwrServlet;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.business.vhost.VirtualHostHelper;

/**
 * This class implements a servlet that wraps around dwr. It fixes a path problem that is seen when certain virtual hosts are used.
 */
public final class DwrServletWrapper
    extends HttpServlet
{
    /**
     * Real dwr servlet.
     */
    private final DwrServlet servlet;

    /**
     * Construct the wrapper.
     */
    public DwrServletWrapper()
    {
        this.servlet = new DwrServlet();
    }

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( VirtualHostHelper.hasBasePath( req ) )
        {
            String basePath = VirtualHostHelper.getBasePath( req );
            final String servletPath = req.getServletPath().replace( "/admin", basePath );

            HttpServletRequest newReq = new HttpServletRequestWrapper( req )
            {
                public String getServletPath()
                {
                    return servletPath;
                }
            };

            ServletRequestAccessor.setRequest( newReq );
            this.servlet.service( newReq, res );
        }
        else
        {
            ServletRequestAccessor.setRequest( req );
            this.servlet.service( req, res );
        }
    }

    @Override
    public void init( ServletConfig config )
        throws ServletException
    {
        this.servlet.init( config );
        super.init( config );
    }
}
