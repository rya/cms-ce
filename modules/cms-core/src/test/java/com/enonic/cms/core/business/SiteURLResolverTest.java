/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.business;

import org.springframework.mock.web.MockHttpServletRequest;

import junit.framework.TestCase;

import com.enonic.cms.core.MockSitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class SiteURLResolverTest
    extends TestCase
{

    private SiteURLResolver siteURLResolver;

    private SiteKey siteKey1 = new SiteKey( 1 );

    private MockHttpServletRequest request;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );

        siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        request = new MockHttpServletRequest();

        ServletRequestAccessor.setRequest( request );
    }

    public void testCreateFullPathForRedirectWithoutContextPath()
    {

        String fullPath = siteURLResolver.createFullPathForRedirect( request, siteKey1, "frontpage/news" );
        assertEquals( "/site/1/frontpage/news", fullPath );
    }

    public void testCreateFullPathForRedirectWithtContextPath()
    {

        request.setContextPath( "cms-server" );
        String fullPath = siteURLResolver.createFullPathForRedirect( request, siteKey1, "frontpage/news" );
        assertEquals( "cms-server/site/1/frontpage/news", fullPath );
    }

    public void testCreateUrlWithPropertyCreateUrlAsPathTrue()
    {

        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        sitePropertiesService.setProperty( siteKey1, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        SiteURLResolver siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        request.setProtocol( "http" );
        request.setServerName( "localhost" );
        request.setRequestURI( "/site/1/" );

        String url;

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ), true );
        assertEquals( "/site/1/home", url );

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "/home/" ) ), true );
        assertEquals( "/site/1/home/", url );

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ).addParam( "balle", "rusk" ), true );
        assertEquals( "/site/1/home?balle=rusk", url );

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ).addParam( "balle", "rusk" ), false );
        assertEquals( "/site/1/home", url );
    }

    public void testCreateUrlWithPropertyCreateUrlAsPathFalse()
    {

        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        sitePropertiesService.setProperty( siteKey1, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        SiteURLResolver siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        request.setProtocol( "http" );
        request.setServerName( "localhost" );
        request.setRequestURI( "/site/1/" );

        String url;

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ), true );
        assertEquals( "http://localhost/site/1/home", url );

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "/home/" ) ), true );
        assertEquals( "http://localhost/site/1/home/", url );

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ).addParam( "balle", "rusk" ), true );
        assertEquals( "http://localhost/site/1/home?balle=rusk", url );

        url = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ).addParam( "balle", "rusk" ), false );
        assertEquals( "http://localhost/site/1/home", url );
    }

    public void testGetPathUrlWithVHOSTSet()
    {

        request.setAttribute( "com.enonic.cms.core.business.vhost.BASE_PATH", "" );

        String path;

        path = siteURLResolver.createUrl( request, new SitePath( siteKey1, new Path( "home" ) ), false );
        assertEquals( "http://localhost/home", path );
    }
}
