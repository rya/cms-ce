/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.page;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class implements the page request.
 */
public final class ServletPageRequest
    extends PageRequestBase
{
    /**
     * Request.
     */
    private final HttpServletRequest req;

    /**
     * Header map.
     */
    private final Map<String, String> headerMap;

    /**
     * Parameter map.
     */
    private final Map<String, String[]> parameterMap;

    /**
     * Redirect path
     */
    private String redirectPath;

    /**
     * Construct the request.
     */
    public ServletPageRequest( HttpServletRequest req )
    {
        this.req = req;
        this.headerMap = createHeaderMap( this.req );
        this.parameterMap = createParameterMap( this.req );
    }

    /**
     * Return the method.
     */
    public String getMethod()
    {
        return this.req.getMethod();
    }

    /**
     * Return the session id.
     */
    public String getSessionId()
    {
        return this.req.getSession( true ).getId();
    }

    /**
     * Return the locale.
     */
    public String getLocale()
    {
        return this.req.getLocale().toString();
    }

    /**
     * Return the request URI.
     */
    public String getRequestUri()
    {
        return this.req.getRequestURI();
    }

    /**
     * Return the remote host.
     */
    public String getRemoteHost()
    {
        return this.req.getRemoteHost();
    }

    /**
     * Return the remote address.
     */
    public String getRemoteAddr()
    {
        return this.req.getRemoteAddr();
    }

    /**
     * Return the profile.
     */
    public String getProfile()
    {
        return "browser";
    }

    /**
     * Return the header map.
     */
    public Map<String, String> getHeaderMap()
    {
        return Collections.unmodifiableMap( this.headerMap );
    }

    /**
     * Return the parameter map.
     */
    public Map<String, String[]> getParameterMap()
    {
        return Collections.unmodifiableMap( this.parameterMap );
    }

    /**
     * Create header map.
     */
    private static Map<String, String> createHeaderMap( HttpServletRequest req )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        for ( Enumeration e = req.getHeaderNames(); e.hasMoreElements(); )
        {
            String key = (String) e.nextElement();
            map.put( key, req.getHeader( key ) );
        }

        return map;
    }

    /**
     * Create parameter map.
     */
    private static Map<String, String[]> createParameterMap( HttpServletRequest req )
    {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        for ( Enumeration e = req.getParameterNames(); e.hasMoreElements(); )
        {
            String key = (String) e.nextElement();
            String[] values = req.getParameterValues( key );

            if ( values != null )
            {
                map.put( key, values );
            }
            else
            {
                map.put( key, new String[]{req.getParameter( key )} );
            }
        }

        return map;
    }

}
