/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.portal.security;

import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.core.plugin.ExtensionManager;
import com.enonic.cms.server.service.servlet.OriginalPathResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Stack;

public class HttpInterceptorInterceptor extends HandlerInterceptorAdapter
{
    private static final String POST_HANDLE_PLUGINS_PARAM = "httpInterceptorInterceptor.postHandlePlugins";

    private ExtensionManager pluginManager;

    public void setPluginManager( ExtensionManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }

    private OriginalPathResolver originalPathResolver = new OriginalPathResolver();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        super.preHandle(request, response, handler);

        Stack<HttpInterceptor> pluginsReadyForPostHandle = new Stack<HttpInterceptor>();
        boolean continueExecutionAsNormal = executePreHandle( request, response, pluginsReadyForPostHandle );
        request.setAttribute(POST_HANDLE_PLUGINS_PARAM, pluginsReadyForPostHandle);

        return continueExecutionAsNormal;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        super.postHandle(request, response, handler, modelAndView);
        Stack<HttpInterceptor> pluginsReadyForPostHandle = (Stack<HttpInterceptor>) request.getAttribute(POST_HANDLE_PLUGINS_PARAM);
        if (pluginsReadyForPostHandle != null)
        {
            executePostHandle( request, response, pluginsReadyForPostHandle );
        }
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

    private Collection<HttpInterceptor> getInterceptorPlugins( HttpServletRequest req )
    {
        String path = originalPathResolver.getRequestPathFromHttpRequest( req );
        return pluginManager.findMatchingHttpInterceptors( path );

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
}
