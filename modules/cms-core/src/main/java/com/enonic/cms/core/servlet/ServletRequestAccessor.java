/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class implements the accessor for servlet request.
 */
public final class ServletRequestAccessor
{
    /**
     * Thread local.
     */
    private final static ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<HttpServletRequest>();

    /**
     * Return the current servlet request.
     */
    public static HttpServletRequest getRequest()
    {
        return REQUEST.get();
    }

    /**
     * Set the current servlet request.
     */
    public static void setRequest( HttpServletRequest request )
    {
        REQUEST.set( request );
    }

    /**
     * Return the current session.
     */
    public static HttpSession getSession()
    {
        HttpServletRequest request = getRequest();
        return request != null ? request.getSession( true ) : null;
    }
}
