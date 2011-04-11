/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class SitePathResolver
{
    private UrlPathHelperManager urlPathHelperManager;

    private String sitePathPrefix = "";

    private SiteKeyResolver siteKeyResolver = new SiteKeyResolver();

    public void setUrlPathHelperManager( UrlPathHelperManager value )
    {
        this.urlPathHelperManager = value;
    }

    public void setSitePathPrefix( String value )
    {
        this.sitePathPrefix = value;
    }

    public SiteKey resolveSiteKey( HttpServletRequest request )
    {
        return siteKeyResolver.resolveSiteKey( request, sitePathPrefix );
    }

    @SuppressWarnings({"unchecked"})
    public SitePath resolveSitePath( HttpServletRequest request )
    {
        SiteKey siteKey = resolveSiteKey( request );
        String path = getSitePathString( request, siteKey );

        int indexToSitePathPrefix = path.indexOf( sitePathPrefix, 0 );
        int indexToSlashBeforeSiteKey = path.indexOf( '/', indexToSitePathPrefix + sitePathPrefix.length() ) + 1;
        int indexToSlashAfterSiteKey = path.indexOf( '/', indexToSlashBeforeSiteKey + 1 );

        String localPath;
        if ( indexToSlashAfterSiteKey == -1 )
        {
            // assume: /sites/123
            localPath = "";
        }
        else
        {
            localPath = path.substring( indexToSlashAfterSiteKey );
        }

        Map params = request.getParameterMap();

        return new SitePath( siteKey, new Path( localPath ), params );
    }

    private String getSitePathString( HttpServletRequest request, SiteKey siteKey )
    {

        UrlPathHelper urlPathHelper = urlPathHelperManager.getUrlPathHelper( siteKey );

        String path = urlPathHelper.getRequestUri( request );
        if ( !hasSitePathPrefix( path ) )
        {
            path = urlPathHelper.getOriginatingRequestUri( request );
        }
        return path;
    }

    private boolean hasSitePathPrefix( String path )
    {
        return path.indexOf( sitePathPrefix ) >= 0;
    }
}
