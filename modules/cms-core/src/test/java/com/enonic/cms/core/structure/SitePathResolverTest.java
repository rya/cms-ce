/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.UrlPathHelper;

import junit.framework.TestCase;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SitePathResolver;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.UrlPathHelperManager;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class SitePathResolverTest
    extends TestCase
{

    private UrlPathHelper urlPathHelper;

    private SitePathResolver sitePathResolver;

    private MockHttpServletRequest httpServletRequest;

    private UrlPathHelperManager urlPathHelperManager;

    private MockSitePropertiesService sitePropertiesService;

    private SiteKey siteKey_1 = new SiteKey( 1 );

    private SiteKey siteKey_123 = new SiteKey( 123 );


    protected void setUp()
        throws Exception
    {
        super.setUp();

        urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode( true );
        urlPathHelper.setDefaultEncoding( "UTF-8" );

        sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey_1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );

        urlPathHelperManager = new UrlPathHelperManager();
        urlPathHelperManager.setSitePropertiesService( sitePropertiesService );

        sitePathResolver = new SitePathResolver();
        sitePathResolver.setSitePathPrefix( "/site" );
        sitePathResolver.setUrlPathHelperManager( urlPathHelperManager );

        httpServletRequest = new MockHttpServletRequest();
        //httpServletRequest.setCharacterEncoding("ISO-8859-1");
    }

    public void testResolveSimpleSitePath1()
    {

        httpServletRequest.setRequestURI( "/site/1/Frontpage" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/Frontpage", sitePath.getLocalPath().toString() );
    }

    public void testResolveSimpleSitePathWithNoLocalPath()
    {

        httpServletRequest.setCharacterEncoding( "ISO-8859-1" );
        httpServletRequest.setRequestURI( "/site/123/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_123, sitePath.getSiteKey() );
        assertEquals( "/", sitePath.getLocalPath().toString() );
    }

    public void testResolveSimpleSitePathWithNoLocalPathAndNoEndSlash()
    {

        httpServletRequest.setCharacterEncoding( "ISO-8859-1" );
        httpServletRequest.setRequestURI( "/site/123" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_123, sitePath.getSiteKey() );
        assertEquals( "", sitePath.getLocalPath().toString() );
    }

    public void testResolveSimpleSitePath123()
    {

        httpServletRequest.setCharacterEncoding( "ISO-8859-1" );
        httpServletRequest.setRequestURI( "/site/123/Frontpage/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_123, sitePath.getSiteKey() );
        assertEquals( "/Frontpage/", sitePath.getLocalPath().toString() );
    }

    public void testResolveLongSitePath1()
    {

        httpServletRequest.setRequestURI( "/site/1/About/Jobs/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/About/Jobs/", sitePath.getLocalPath().toString() );
    }

    public void testResolveSitePathWithNoPrefix()
    {

        sitePathResolver.setSitePathPrefix( "" );
        httpServletRequest.setRequestURI( "/1/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/", sitePath.getLocalPath().toString() );
    }

    public void testResolveSitePathExceptionSitePathPrefixNotFoundInPath()
    {

        sitePathResolver.setSitePathPrefix( "/y" );
        httpServletRequest.setRequestURI( "/x/1/" );

        try
        {
            sitePathResolver.resolveSitePath( httpServletRequest );
            fail( "Exception expected" );
        }
        catch ( IllegalStateException e )
        {
            assertTrue( e.getMessage().startsWith( "sitePathPrefix '/y' not found in path: /x/1/" ) );
        }
    }

    public void xtestNorwegianSitePath_With_ISO_8859_1()
    {

        sitePropertiesService.setProperty( siteKey_1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "ISO-8859-1" );
        httpServletRequest.setRequestURI( "/site/1/B%E5t/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/Båt/", sitePath.getLocalPath().toString() );
    }

    public void xtestSweedishSitePath_With_ISO_8859_1()
    {

        sitePropertiesService.setProperty( siteKey_1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "ISO-8859-1" );
        httpServletRequest.setRequestURI( "/site/1/B%F6t/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/Böt/", sitePath.getLocalPath().toString() );
    }

    public void xtestRussianSitePath_WithDefaultCharacterEncoding_UTF_8()
    {

        sitePropertiesService.setProperty( siteKey_1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        httpServletRequest.setRequestURI( "/site/1/Services%D0%BB/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/Servicesл/", sitePath.getLocalPath().toString() );
    }

    public void testResolveIncludePath()
    {

        httpServletRequest.setAttribute( "javax.servlet.include.request_uri", "/site/1/About/Jobs/" );
        httpServletRequest.setRequestURI( "/About/Jobs/" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/About/Jobs/", sitePath.getLocalPath().toString() );
    }

    public void testGetParam()
    {

        httpServletRequest.setRequestURI( "/site/1/Frontpage" );
        httpServletRequest.setParameter( "param1", "value1" );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/Frontpage", sitePath.getLocalPath().toString() );
        assertEquals( "value1", sitePath.getParam( "param1" ) );
    }

    public void testGetParamWithMultipleValues()
    {

        httpServletRequest.setRequestURI( "/site/1/Frontpage" );
        httpServletRequest.setParameter( "param1", new String[]{"value0", "value1"} );

        SitePath sitePath = sitePathResolver.resolveSitePath( httpServletRequest );

        assertEquals( siteKey_1, sitePath.getSiteKey() );
        assertEquals( "/Frontpage", sitePath.getLocalPath().toString() );
        assertEquals( "value0", sitePath.getParam( "param1" ) );
    }

}
