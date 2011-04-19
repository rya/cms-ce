package com.enonic.cms.api.plugin.ext.http;

import javax.servlet.http.HttpServletRequest;

/**
 * This class implements the http response filter plugin.
 */
public abstract class HttpResponseFilter
    extends HttpProcessor
{
    /**
     * Filters the textural response.
     */
    public abstract String filterResponse( HttpServletRequest request, String response, String contentType )
        throws Exception;
}
