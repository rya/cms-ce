/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import junit.framework.TestCase;

import com.enonic.cms.core.MockSitePropertiesService;
import com.enonic.cms.core.UrlPathHelperManager;

import com.enonic.cms.domain.portal.httpservices.IllegalRedirectException;

public class UserServicesRedirectUrlResolverTest
    extends TestCase
{

    private MockSitePropertiesService sitePropertiesService;

    private UserServicesRedirectUrlResolver userServicesRedirectUrlResolver;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        sitePropertiesService = new MockSitePropertiesService();

        UrlPathHelperManager urlPathHelperManager = new UrlPathHelperManager();
        urlPathHelperManager.setSitePropertiesService( sitePropertiesService );

        userServicesRedirectUrlResolver = new UserServicesRedirectUrlResolver();

        request = new MockHttpServletRequest();
        request.setContextPath( "cms-server" );
        response = new MockHttpServletResponse();

        request.setRequestURI( "/cms-server-idea/site/0/servlet/com.enonic.vertical.userservices.UserHandlerServlet" );
    }

    public void testResolveRedirectToPageIllegalRedirect()
    {
        String redirect = "illegalRedirect";
        try
        {
            userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
            fail( "Expected IllegalRedirectException" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalRedirectException );
            assertEquals( redirect, e.getMessage() );
        }
    }

    public void testResolveRedirectToPageWithFullUrl()
    {
        String redirect = "http://www.mycompany.com/page?id=0";
        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
        assertEquals( "http://www.mycompany.com/page?id=0", url );
    }

    public void testResolveRedirectToPageWithNoRedirectAndNoReferer()
    {
        String redirect = null;
        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
        assertEquals( "/", url );
    }

    public void testResolveRedirectToPageWithNoRedirectAndEmptyReferer()
    {
        request.addHeader( "referer", "" );

        String redirect = null;
        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
        assertEquals( "/", url );
    }

    public void testResolveRedirectToPageWithReferer1()
    {
        request.addHeader( "referer", "http://www.mycompany.com/page?id=0" );

        String redirect = null;

        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
        assertEquals( "http://www.mycompany.com/page?id=0", url );
    }

    public void testResolveRedirectToPageWithReferer2()
    {
        request.addHeader( "referer", "ftp://www.mycompany.com/download?file=text.txt" );

        String redirect = null;

        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
        assertEquals( "ftp://www.mycompany.com/download?file=text.txt", url );
    }

    public void testResolveRedirectToPageWithRefererAndRedirect()
    {
        request.addHeader( "referer", "ftp://www.mycompany.com/download?file=text.txt" );

        String redirect = "https://www.mycompany.com/";

        String url = userServicesRedirectUrlResolver.resolveRedirectUrlToPage( request, redirect, null );
        assertEquals( "https://www.mycompany.com/", url );
    }
}
