/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;

import com.enonic.cms.business.DeploymentPathResolver;

import com.enonic.cms.core.SiteKey;

public class AdminHelper
{

    public static String getDebugPath( HttpServletRequest request, SiteKey siteKey )
    {
        return getAdminPath( request, false ) + "/site/" + siteKey.toInt() + "/";
    }

    public static String getAdminPath( HttpServletRequest request, boolean relative )
    {
        StringBuffer path = new StringBuffer();
        if ( !relative )
        {
            path.append( request.getScheme() );
            path.append( "://" );
            path.append( request.getServerName() );
            if ( request.getServerPort() != 80 )
            {
                path.append( ":" );
                path.append( request.getServerPort() );
            }
        }
        path.append( DeploymentPathResolver.getAdminDeploymentPath( request ) );

        return path.toString();
    }

    public static void redirectClientToAdminPath( HttpServletRequest request, HttpServletResponse response, String adminPath,
                                                  MultiValueMap queryParams )
    {

        StringBuffer urlString = new StringBuffer();
        urlString.append( getAdminPath( request, true ) );
        urlString.append( "/" ).append( adminPath );

        URL url = new URL( urlString );
        if ( queryParams != null )
        {
            url.addParameters( queryParams );
        }
        redirectClient( url.toString(), response );
    }

    public static void redirectClientToReferer( HttpServletRequest request, HttpServletResponse response )
    {
        redirectClient( request.getHeader( "referer" ), response );
    }

    public static void redirectClientToAbsoluteUrl( String url, HttpServletResponse response )
    {
        Assert.notNull( url, "Given url cannot be null" );
        if ( !url.matches( "^[a-z]{3,6}://.+" ) )
        {
            throw new RuntimeException( "Given url must be absolute" );
        }

        redirectClient( url, response );
    }

    public static void redirectToURL( URL url, HttpServletResponse response )
    {

        String urlString = url.toString();
        redirectClient( urlString, response );
    }

    private static void redirectClient( String url, HttpServletResponse response )
    {
        try
        {
            response.sendRedirect( response.encodeRedirectURL( url ) );
        }
        catch ( IOException ioe )
        {
            throw new RuntimeException( "Failed to redirect client", ioe );
        }
    }
}
