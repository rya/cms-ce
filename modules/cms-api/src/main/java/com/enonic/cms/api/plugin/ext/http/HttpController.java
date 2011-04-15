package com.enonic.cms.api.plugin.ext.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class implements the web controller plugin.
 */
public abstract class HttpController
    extends HttpInterceptor
{
    public HttpController()
    {
        setPriority( Integer.MAX_VALUE );
    }

    /**
     * Executes before the actual resource being called.
     */
    public final boolean preHandle( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        handleRequest( request, response );
        return false;
    }

    /**
     * Executes after the actual resource being called.
     */
    public final void postHandle( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        // Do nothing
    }

    /**
     * Service the request.
     *
     * @param request  The HttpRequest from the container.  Use this as with a normal servlet.
     * @param response The HttpResponse that instructs the container how to respond to the request.
     * @throws Exception When implementing this method, any exceptions may be thrown.  An Exception will halt execution, send an error on
     *                   the response, and also log the error in the error log.
     */
    public abstract void handleRequest( HttpServletRequest request, HttpServletResponse response )
        throws Exception;
}
