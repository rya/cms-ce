/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.domain.SiteKey;

public class SiteKeyResolver
{
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public SiteKey resolveSiteKey( HttpServletRequest request, String sitePathPrefix )
    {
        String path = urlPathHelper.getRequestUri( request );
        if ( !hasSitePathPrefix( path, sitePathPrefix ) )
        {
            throw new IllegalStateException( "sitePathPrefix '" + sitePathPrefix + "' not found in path: " + path );
        }

        int indexToSitePathPrefix = path.indexOf( sitePathPrefix, 0 );
        int indexToSlashBeforeSiteKey = path.indexOf( '/', indexToSitePathPrefix + sitePathPrefix.length() ) + 1;
        int indexToSlashAfterSiteKey = path.indexOf( '/', indexToSlashBeforeSiteKey + 1 );

        String siteKeyString;
        SiteKey siteKey;
        if ( indexToSlashAfterSiteKey == -1 )
        {
            // assume: /site/123
            siteKeyString = path.substring( indexToSlashBeforeSiteKey, path.length() );
            siteKey = new SiteKey( siteKeyString );
        }
        else
        {
            siteKeyString = path.substring( indexToSlashBeforeSiteKey, indexToSlashAfterSiteKey );
            siteKey = new SiteKey( siteKeyString );
        }

        return siteKey;
    }

    private boolean hasSitePathPrefix( String path, String sitePathPrefix )
    {
        return path.indexOf( sitePathPrefix ) >= 0;
    }
}
