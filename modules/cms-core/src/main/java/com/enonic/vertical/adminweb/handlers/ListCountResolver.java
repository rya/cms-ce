/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.servlet.http.CookieUtil;

public class ListCountResolver
{
    public static int resolveCount( HttpServletRequest request, ExtendedMap formItems, String cookieName )
    {
        int count = 20;
        Cookie itemsPerPageCookie = CookieUtil.getCookie( request, cookieName );

        if ( formItems.containsKey( "count" ) )
        {
            count = formItems.getInt( "count", 20 );
        }
        else if ( itemsPerPageCookie != null )
        {
            count = Integer.parseInt( itemsPerPageCookie.getValue() );
        }

        return count;
    }
}
