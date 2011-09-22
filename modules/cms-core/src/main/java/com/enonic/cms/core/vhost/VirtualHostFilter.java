/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

public final class VirtualHostFilter
        extends GenericFilterBean
{

    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostFilter.class );

    private VirtualHostResolver virtualHostResolver;

    protected void initFilterBean()
            throws ServletException
    {
        initVirtualHosts();
    }

    private void initVirtualHosts()
            throws ServletException
    {
        ServletContext servletContext = getServletContext();
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext );
        String[] beanNames = appContext.getBeanNamesForType( VirtualHostResolver.class );

        if ( ( beanNames != null ) && ( beanNames.length > 0 ) )
        {
            this.virtualHostResolver = (VirtualHostResolver) appContext.getBean( beanNames[0] );
        }
    }

    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
            throws IOException, ServletException
    {

        try
        {
            doFilter( (HttpServletRequest) req, (HttpServletResponse) res, chain );
        }
        catch ( IOException e )
        {
            throw e;
        }
        catch ( ServletException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new ServletException( e );
        }
    }

    private void doFilter( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
            throws Exception
    {

        String fullTargetPath = null;

        VirtualHost virtualHost = this.virtualHostResolver != null ? this.virtualHostResolver.resolve( req ) : null;
        if ( virtualHost != null )
        {
            fullTargetPath = virtualHost.getFullTargetPath( req );
            String fullSourcePath = virtualHost.getFullSourcePath( req );
            VirtualHostHelper.setBasePath( req, fullSourcePath );
        }

        if ( fullTargetPath != null )
        {
            RequestDispatcher dispatcher = req.getRequestDispatcher( fullTargetPath );
            dispatcher.forward( req, res );
        }
        else
        {
            chain.doFilter( req, res );
        }
    }
}
