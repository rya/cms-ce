package com.enonic.cms.api.plugin.ext.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class implements the web interceptor plugin.
 */
public abstract class HttpInterceptor
    extends HttpProcessor
{
    /**
     * Executes before the actual resource being called. Returns true if the execution chain should proceed with the next interceptor.
     */
    public abstract boolean preHandle( HttpServletRequest request, HttpServletResponse response )
        throws Exception;

    /**
     * Executes after the actual resource being called.
     */
    public abstract void postHandle( HttpServletRequest request, HttpServletResponse response )
        throws Exception;
}
