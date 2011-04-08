/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This class implements the abstract tool controller.
 */
public abstract class AbstractToolController
    implements Controller
{
    /**
     * Access resolver.
     */
    private ToolsAccessResolver toolsAccessResolver;

    /**
     * Set access resolver.
     */
    @Autowired
    public void setToolsAccessResolver( ToolsAccessResolver toolsAccessResolver )
    {
        this.toolsAccessResolver = toolsAccessResolver;
    }

    /**
     * Handle the request.
     */
    public final ModelAndView handleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        if ( hasAccess( req ) )
        {
            return doHandleRequest( req, res );
        }
        else
        {
            return handleErrorPage( req, res );
        }
    }

    /**
     * Do handle request.
     */
    protected abstract ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception;

    /**
     * Check if access.
     */
    private boolean hasAccess( HttpServletRequest req )
    {
        return ( this.toolsAccessResolver == null ) || this.toolsAccessResolver.hasAccess( req );
    }

    /**
     * Show the error page.
     */
    private ModelAndView handleErrorPage( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        res.sendError( HttpServletResponse.SC_FORBIDDEN, this.toolsAccessResolver.getErrorMessage( req ) );
        return null;
    }

    /**
     * Return the base path.
     */
    protected String createBaseUrl( HttpServletRequest req )
    {
        StringBuffer str = new StringBuffer();
        str.append( req.getScheme() ).append( "://" ).append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( req.getContextPath() );
        return str.toString();
    }

    /**
     * Redirect to self.
     */
    protected void redirectToSelf( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        String url = req.getRequestURL().toString();
        int index = url.indexOf( "?" );

        if ( index > -1 )
        {
            url = url.substring( 0, index );
        }

        res.sendRedirect( url );
    }
}
