package com.enonic.cms.server.service.portal.mvc.controller;

import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.plugin.ExtensionManager;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.Path;

import com.enonic.cms.domain.portal.PortalRequest;
import com.enonic.cms.domain.portal.PortalResponse;
import com.enonic.cms.core.structure.SiteEntity;

import com.enonic.cms.server.DeploymentAndRequestSetup;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * unit tests for HEAD functionality
 */
public class PortalRenderResponseServerHeadTest
{
    public static final String CONTENT_VALUE = "content text";

    public static final String ETAG_VALUE = "content_F98393E248D02CFD7C597B8E640EED1D8F684824";

    public static final String ETAG_VALUE_INCORRECT = "content_F98393E248D02CFD7C597B8E640EED1D8F684824_";

    public static final String ETAG_HEADER_NAME = "Etag";

    private PortalRenderResponseServer portalRenderResponseServer = new PortalRenderResponseServer();

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    private MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

    private UserDao userDao = Mockito.mock( UserDao.class );

    private SiteDao siteDao = Mockito.mock( SiteDao.class );

    private SitePath sitePath = new SitePath( new SiteKey( 0 ), new Path( "/news/local politics" ) );

    private PortalRequest portalRequest = new PortalRequest();

    private PortalResponse portalResponse = new PortalResponse();


    @Before
    public void before()
    {
        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( new SiteKey( 0 ), SitePropertyNames.PAGE_CACHE_HEADERS_ENABLED, "true" );

        httpServletRequest.setServerPort( 80 );

        ServletRequestAccessor.setRequest( httpServletRequest );
        portalRenderResponseServer.setSitePropertiesService( sitePropertiesService );

        portalRenderResponseServer.setUserDao( userDao );
        portalRenderResponseServer.setSiteDao( siteDao );
        portalRenderResponseServer.setExtensionManager( Mockito.mock(ExtensionManager.class));

        Mockito.when( siteDao.findByKey( sitePath.getSiteKey() ) ).thenReturn( new SiteEntity() );

        new DeploymentAndRequestSetup().
                appDeployedAtRoot().
                originalRequest( "localhost", "/admin/site/0/political news shortcut" ).
                requestedSite( 0, "political news shortcut" ).
                requestedAdminDebugAt().
                setupAtDefaultPath().
                back().
                setup( httpServletRequest );

        httpServletRequest.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );

        portalRequest.setOriginalUrl( "http://localhost/admin/site/0/political news shortcut?" );
        portalRequest.setRequestTime( new DateTime() );
        portalRequest.setSitePath( sitePath );
        portalRequest.setRequester( new UserKey( "1" ) );

        portalResponse.setContent( CONTENT_VALUE );
    }


    @Test
    public void testServeResponse_check_modified_GET()
            throws Exception
    {
        httpServletRequest.setMethod( "GET" );

        // exercise
        portalRenderResponseServer.serveResponse( portalRequest, portalResponse, httpServletResponse,
                                                  httpServletRequest );

        // verify that length is equal to content and content exists
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_modified_HEAD()
            throws Exception
    {
        httpServletRequest.setMethod( "HEAD" );

        // exercise
        portalRenderResponseServer.serveResponse( portalRequest, portalResponse, httpServletResponse,
                                                  httpServletRequest );

        // verify that length is equal to content but no content exists
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_HEAD()
            throws Exception
    {
        httpServletRequest.setMethod( "HEAD" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE );

        // exercise
        portalRenderResponseServer.serveResponse( portalRequest, portalResponse, httpServletResponse,
                                                  httpServletRequest );

        // verify that length is equal to content but content does not exist
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );  // most important test
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_GET()
            throws Exception
    {
        httpServletRequest.setMethod( "GET" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE );

        // exercise
        portalRenderResponseServer.serveResponse( portalRequest, portalResponse, httpServletResponse,
                                                  httpServletRequest );

        // verify that length is zero and no content exists
        assertEquals( 0, httpServletResponse.getContentLength() );
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_HEAD_non_matching_etag()
            throws Exception
    {
        httpServletRequest.setMethod( "HEAD" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE_INCORRECT );

        // exercise
        portalRenderResponseServer.serveResponse( portalRequest, portalResponse, httpServletResponse,
                                                  httpServletRequest );

        // verify that length is equal to content but content does not exist
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );  // most important test
        assertEquals( 0, httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void testServeResponse_check_not_modified_GET_non_matching_etag()
            throws Exception
    {
        httpServletRequest.setMethod( "GET" );
        httpServletRequest.addHeader( "If-None-Match", ETAG_VALUE_INCORRECT );

        // exercise
        portalRenderResponseServer.serveResponse( portalRequest, portalResponse, httpServletResponse,
                                                  httpServletRequest );

        // verify that length is zero and no content exists
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentLength() );
        assertEquals( CONTENT_VALUE.length(), httpServletResponse.getContentAsByteArray().length );

        assertEquals( ETAG_VALUE, httpServletResponse.getHeader( ETAG_HEADER_NAME ) );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );

    }

}
