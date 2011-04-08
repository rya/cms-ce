/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.io.IOException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import junit.framework.TestCase;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SitePathResolver;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.SiteURLResolver;
import com.enonic.cms.business.UrlPathHelperManager;

import com.enonic.cms.domain.SiteKey;

public class SiteRedirectHelperTest
    extends TestCase
{

    private SiteRedirectHelper siteRedirectHelper;

    private SitePathResolver sitePathResolver;

    private SiteURLResolver siteURLResolver;

    private MockSitePropertiesService sitePropertiesService;

    private UrlPathHelperManager urlPathHelperManager;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private final SiteKey siteKey0 = new SiteKey( 0 );

    protected void setUp()
        throws Exception
    {
        super.setUp();

        sitePropertiesService = new MockSitePropertiesService();

        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );

        urlPathHelperManager = new UrlPathHelperManager();
        urlPathHelperManager.setSitePropertiesService( sitePropertiesService );

        sitePathResolver = new SitePathResolver();
        sitePathResolver.setUrlPathHelperManager( urlPathHelperManager );
        sitePathResolver.setSitePathPrefix( SiteURLResolver.DEFAULT_SITEPATH_PREFIX );

        siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        siteRedirectHelper = new SiteRedirectHelper();
        siteRedirectHelper.setSitePathResolver( sitePathResolver );
        siteRedirectHelper.setSiteURLResolver( siteURLResolver );

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    public void testSendRedirectWithHttpUrl()
        throws IOException
    {

        request.setScheme( "http" );
        siteRedirectHelper.sendRedirect( request, response, "http://someurl.com" );
        assertEquals( "http://someurl.com", response.getHeader( "Location" ) );
    }

    public void testSendRedirectWithHttpsUrl()
        throws IOException
    {

        request.setScheme( "http" );
        siteRedirectHelper.sendRedirect( request, response, "https://someurl.com" );
        assertEquals( "https://someurl.com", response.getHeader( "Location" ) );
    }

    public void testSendRedirectWithGopherUrl()
        throws IOException
    {

        request.setScheme( "http" );
        siteRedirectHelper.sendRedirect( request, response, "gopher://someurl.com" );
        assertEquals( "gopher://someurl.com", response.getHeader( "Location" ) );
    }

//    public void testSendRedirectWithHttpsUr2() throws IOException {
//
//        request.setRequestURI("/site/0/home");
//
//        siteRedirectHelper.sendRedirect(request, response, "/site/0/");
//        assertEquals("https://someurl.com", response.getRedirectedUrl());
//    }
}
