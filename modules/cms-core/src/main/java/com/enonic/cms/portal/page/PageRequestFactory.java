/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.page;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

/**
 * This class implements the page request factory.
 */
public abstract class PageRequestFactory
{
    /**
     * Request attribute.
     */
    private final static String ATTRIBUTE_KEY = "cms:pageRequest";

    /**
     * Return the page request.
     */
    public static PageRequest getPageRequest()
    {
        return getPageRequest( ServletRequestAccessor.getRequest() );
    }

    /**
     * Return the page request.
     */
    public static PageRequest getPageRequest( HttpServletRequest req )
    {
        PageRequest result = (PageRequest) req.getAttribute( ATTRIBUTE_KEY );
        if ( result == null )
        {
            result = createPageRequest( req );
            req.setAttribute( ATTRIBUTE_KEY, result );
        }

        return result;
    }

    /**
     * Create the page request.
     */
    private static PageRequest createPageRequest( HttpServletRequest req )
    {
        return new ServletPageRequest( req );
    }
}
