/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.servlet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.business.SiteURLResolver;

public class OriginalPathResolver
{

    public String getRequestPathFromHttpRequest( HttpServletRequest req )
    {

        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode( true );
        urlPathHelper.setDefaultEncoding( "UTF-8" );

        String path = urlPathHelper.getPathWithinApplication( req );

        if ( !hasSitePathPrefix( path ) )
        {
            path = urlPathHelper.getOriginatingRequestUri( req );
            String contextPath = urlPathHelper.getOriginatingContextPath( req );
            if ( contextPath != null && path.startsWith( contextPath ) )
            {
                path = path.substring( contextPath.length() );
            }
        }
        return path;
    }

    private boolean hasSitePathPrefix( String path )
    {
        return path.indexOf( SiteURLResolver.DEFAULT_SITEPATH_PREFIX ) >= 0;
    }

}
