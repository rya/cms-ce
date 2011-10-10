/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Aug 16, 2010
 */
public class SiteBasePathAndSitePathToStringBuilderTest
{
    @Test
    public void toString_with_AdminDebugSiteBasePath()
    {
        String adminPath = "/admin";
        String siteLocalPathAsString = "/news/politics";

        SiteBasePath portalSiteBasePath = new AdminSiteDebugBasePath( new Path( adminPath ), new SiteKey( 0 ) );
        SiteBasePathAndSitePath siteBasePathAndSitePath =
            new SiteBasePathAndSitePath( portalSiteBasePath, new SitePath( new SiteKey( 0 ), siteLocalPathAsString ) );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );

        String actual = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        assertEquals( "/admin/site/0/news/politics", actual );
    }

    @Test
    public void toString_with_PortalSiteBasePath_when_siteBasePath_hasTrailingSlash_and_siteLocalPath_startsWithSlash()
    {
        String siteBasePathAsString = "/site/0/";
        String siteLocalPathAsString = "/news/politics";

        SiteBasePath portalSiteBasePath = new PortalSiteBasePath( new Path( siteBasePathAsString ), new SiteKey( 0 ) );
        SiteBasePathAndSitePath siteBasePathAndSitePath =
            new SiteBasePathAndSitePath( portalSiteBasePath, new SitePath( new SiteKey( 0 ), siteLocalPathAsString ) );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );

        String actual = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        assertEquals( "/site/0/news/politics", actual );
    }

    @Test
    public void toString_with_PortalSiteBasePath_when_siteBasePath_hasTrailingSlash_and_siteLocalPath_doNotStartWithSlash()
    {
        String siteBasePathAsString = "/site/0/";
        String siteLocalPathAsString = "news/politics";

        SiteBasePath portalSiteBasePath = new PortalSiteBasePath( new Path( siteBasePathAsString ), new SiteKey( 0 ) );
        SiteBasePathAndSitePath siteBasePathAndSitePath =
            new SiteBasePathAndSitePath( portalSiteBasePath, new SitePath( new SiteKey( 0 ), siteLocalPathAsString ) );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );

        String actual = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        assertEquals( "/site/0/news/politics", actual );
    }

    @Test
    public void toString_with_PortalSiteBasePath_when_siteBasePath_hasNotTrailingSlash_and_siteLocalPath_startsWithSlash()
    {
        String siteBasePathAsString = "/site/0";
        String siteLocalPathAsString = "/news/politics";

        SiteBasePath portalSiteBasePath = new PortalSiteBasePath( new Path( siteBasePathAsString ), new SiteKey( 0 ) );
        SiteBasePathAndSitePath siteBasePathAndSitePath =
            new SiteBasePathAndSitePath( portalSiteBasePath, new SitePath( new SiteKey( 0 ), siteLocalPathAsString ) );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );

        String actual = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        assertEquals( "/site/0/news/politics", actual );
    }

    @Test
    public void toString_with_PortalSiteBasePath_when_siteBasePath_hasNotTrailingSlash_and_siteLocalPath_doNotStartWithSlash()
    {
        String siteBasePathAsString = "/site/0/";
        String siteLocalPathAsString = "/news/politics";

        SiteBasePath portalSiteBasePath = new PortalSiteBasePath( new Path( siteBasePathAsString ), new SiteKey( 0 ) );
        SiteBasePathAndSitePath siteBasePathAndSitePath =
            new SiteBasePathAndSitePath( portalSiteBasePath, new SitePath( new SiteKey( 0 ), siteLocalPathAsString ) );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );

        String actual = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        assertEquals( "/site/0/news/politics", actual );
    }
}
