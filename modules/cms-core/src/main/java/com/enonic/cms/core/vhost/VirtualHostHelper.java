/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import javax.servlet.http.HttpServletRequest;

/**
 * This class implements some helpers to ease use of virtual hosts.
 */
public final class VirtualHostHelper
{
    /**
     * Source base.
     */
    private final static String BASE_PATH = "com.enonic.cms.business.vhost.BASE_PATH";

    /**
     * Return true if it has base path.
     */
    public static boolean hasBasePath( HttpServletRequest request )
    {
        return getBasePath( request ) != null;
    }

    /**
     * Return the source base for rewriting.
     */
    public static String getBasePath( HttpServletRequest request )
    {
        return (String) request.getAttribute( BASE_PATH );
    }

    /**
     * Set the source base for rewriting.
     */
    public static void setBasePath( HttpServletRequest request, String basePath )
    {
        request.setAttribute( BASE_PATH, basePath );
    }
}
