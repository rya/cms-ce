/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.servlet.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil
{

    /**
     * Get a request's cookie. If no cookie with this name, the method returns null.
     *
     * @param request    HttpRequest the cookie to search for cookie
     * @param cookieName String a cookie name
     * @return Cookie the cookie found, null if not found
     */
    public static Cookie getCookie( HttpServletRequest request, String cookieName )
    {
        if ( request != null && request.getCookies() != null && cookieName != null && cookieName.length() > 0 )
        {
            Cookie[] cookies = request.getCookies();
            for ( Cookie c : cookies )
            {
                if ( cookieName.equals( c.getName() ) )
                {
                    return c;
                }
            }
        }
        return null;
    }

    public static void setCookie( HttpServletResponse response, String cookieName, String value, int maxAge, String path )
    {
        Cookie cookie = new Cookie( cookieName, value );
        cookie.setMaxAge( maxAge );
        cookie.setPath( getCookiePath( path ) );
        response.addCookie( cookie );
    }

    private static String getCookiePath( String path )
    {
        final String pathSeparator = "/";
        String cookiePath = pathSeparator;
        if ( path != null && path.length() > 0 )
        {
            cookiePath = path;

            if ( !cookiePath.startsWith( pathSeparator ) )
            {
                cookiePath = pathSeparator + cookiePath;
            }
            if ( !cookiePath.endsWith( pathSeparator ) )
            {
                cookiePath = cookiePath + pathSeparator;
            }
        }
        return cookiePath;
    }
}
