/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.core.plugin.ExtensionManager;
import com.enonic.cms.core.plugin.ExtensionManagerAccessor;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.server.service.upgrade.UpgradeCheckerHelper;

import com.enonic.cms.domain.Attribute;

/**
 * This class implements a modification of the dispatcher servlet.
 */
public final class CmsDispatcherServlet
    extends DispatcherServlet
{
    private static final Logger LOG = LoggerFactory.getLogger( CmsDispatcherServlet.class );

    private final static List<HttpMethod> ALLOWED_HTTP_METHODS =
        Arrays.asList( HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS );

    /**
     * Upgrade check parameter.
     */
    private final static String UPGRADE_CHECK_PARAM = "upgradeCheck";

    private OriginalPathResolver originalPathResolver = new OriginalPathResolver();

    @Override
    public void init( ServletConfig config )
        throws ServletException
    {
        super.init( config );
        startContextIfNeeded();
    }

    @Override
    protected void doOptions( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        response.setHeader( "Allow", StringUtils.join( ALLOWED_HTTP_METHODS, "," ) );
        response.setStatus( HttpServletResponse.SC_OK );
    }

    protected void doService( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {

        HttpMethod requestMethod = HttpMethod.valueOf( req.getMethod() );

        if ( !ALLOWED_HTTP_METHODS.contains( requestMethod ) )
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
            return;
        }

        startContextIfNeeded();
        if ( upgradeIsNeeded( res ) )
        {
            return;
        }

        ServletRequestAccessor.setRequest( req );
        // resolve and set original url if not set
        if ( req.getAttribute( Attribute.ORIGINAL_URL ) == null )
        {
            final String originalUrl = OriginalUrlResolver.get().resolveOriginalUrl( req );
            req.setAttribute( Attribute.ORIGINAL_URL, originalUrl );
        }

        if ( LOG.isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Dispatching request to URL: " + req.getRequestURL() );
            LOG.debug( msg.toString() );
        }

        Stack<HttpInterceptor> pluginsReadyForPostHandle = new Stack<HttpInterceptor>();
        if ( executePreHandle( req, res, pluginsReadyForPostHandle ) )
        {
            super.doService( req, res );
        }

        executePostHandle( req, res, pluginsReadyForPostHandle );
    }

    private Collection<HttpInterceptor> getInterceptorPlugins( HttpServletRequest req )
    {
        ExtensionManager pluginManager = ExtensionManagerAccessor.getExtensionManager();
        String path = originalPathResolver.getRequestPathFromHttpRequest( req );
        return pluginManager.findMatchingHttpInterceptors( path );

    }

    /**
     * Find the applicable interceptor plugins for the given request, and execute their pre processing routine if they
     * have not allready been executed.
     *
     * @param req                       The servlet request.
     * @param res                       The servlet response.
     * @param pluginsReadyForPostHandle A list of all previously executed plugins. These will not be executed again,
     *                                  while all the new plugins that are executed this time around, are added to the list.
     * @return <code>true</code> if it should proceed, <code>false</code> if execution should be interrupted.
     * @throws Exception Any exception that a plugin may throw.
     */
    private boolean executePreHandle( HttpServletRequest req, HttpServletResponse res, Stack<HttpInterceptor> pluginsReadyForPostHandle )
        throws Exception
    {
        for ( HttpInterceptor plugin : getInterceptorPlugins( req ) )
        {
            boolean proceed = plugin.preHandle( req, res );
            pluginsReadyForPostHandle.add( plugin );
            if ( !proceed )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Execute the post processing routine of the interceptor plugins that was prehandled successfully.
     *
     * @param req                       The servlet request.
     * @param res                       The servlet response.
     * @param pluginsReadyForPostHandle The plugins to execute.
     * @throws Exception Any exception that a plugin may throw.
     */
    private void executePostHandle( HttpServletRequest req, HttpServletResponse res, Stack<HttpInterceptor> pluginsReadyForPostHandle )
        throws Exception
    {
        for ( HttpInterceptor plugin : pluginsReadyForPostHandle )
        {
            plugin.postHandle( req, res );
        }
    }

    /**
     * Check if upgrade is needed.
     */
    private boolean upgradeIsNeeded( HttpServletResponse res )
        throws Exception
    {
        return "true".equals( getInitParameter( UPGRADE_CHECK_PARAM ) ) && UpgradeCheckerHelper.checkUpgrade( getServletContext(), res );
    }

    private ApplicationContext startContextIfNeeded()
    {
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext( getServletContext() );
        if ( appContext instanceof ConfigurableWebApplicationContext )
        {
            startContextIfNeeded( (ConfigurableWebApplicationContext) appContext );
        }
        return appContext;
    }

    private static void startContextIfNeeded( ConfigurableWebApplicationContext context )
    {
        if ( !context.isRunning() )
        {
            context.start();
        }
    }

}
