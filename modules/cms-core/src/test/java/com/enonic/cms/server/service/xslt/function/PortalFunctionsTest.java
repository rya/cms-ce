/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.xslt.function;


import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.structure.SiteEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.MockSitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.xslt.lib.PortalFunctions;
import com.enonic.cms.portal.rendering.portalfunctions.PortalFunctionsContext;
import com.enonic.cms.portal.rendering.portalfunctions.PortalFunctionsFactory;
import com.enonic.cms.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.server.DeploymentAndRequestSetup;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.SiteKey;

import static org.junit.Assert.*;

/**
 * Aug 27, 2010
 */
public class PortalFunctionsTest
{
    private SiteKey siteKey0 = new SiteKey( 0 );

    private ContentKey contentKey1 = new ContentKey( 1 );

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockSitePropertiesService sitePropertiesService;

    private PortalFunctionsContext context = new PortalFunctionsContext();

    private SiteURLResolver siteUrlResolver;

    private ContentDao contentDao;

    @Before
    public void setup()
    {
        ServletRequestAccessor.setRequest( request );

        sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );

        context.setSite( createSite( siteKey0 ) );

        siteUrlResolver = new SiteURLResolver();
        siteUrlResolver.setSitePropertiesService( sitePropertiesService );

        new PortalFunctionsFactory();
        PortalFunctionsFactory.get().setContext( context );
        PortalFunctionsFactory.get().setSiteURLResolver( siteUrlResolver );

        contentDao = Mockito.mock( ContentDao.class );
        PortalFunctionsFactory.get().setContentDao( contentDao );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Deployed @ /
     * Request is via admin debug @ http://localhost/admin/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_root_debug_requested_at_localhost_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/admin/site/0/" ).requestedSite( 0,
                                                                                                                            "" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "http://localhost/admin/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Deployed @ /
     * Request is via admin debug @ http://localhost/admin/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_root_debug_requested_at_localhost_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/admin/site/0/" ).requestedSite( 0,
                                                                                                                            "" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "/admin/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Vhost: admin.enonic-cms.com = /admin
     * Deployed @ /
     * Request is via debug @ http://admin.enonic-cms.com/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_root_debug_requested_at_vhost_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "admin.enonic-cms.com", "/admin" ).originalRequest(
            "admin.enonic-cms.com", "/site/0/" ).requestedSite( 0, "" ).requestedAdminDebugAt().setupAtRoot().back().setup( request );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "http://admin.enonic-cms.com/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Vhost: admin.enonic-cms.com = /admin
     * Deployed @ /
     * Request is via debug @ http://admin.enonic-cms.com/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_root_debug_requested_at_vhost_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "admin.enonic-cms.com", "/admin" ).originalRequest(
            "admin.enonic-cms.com", "/site/0/" ).requestedSite( 0, "" ).requestedAdminDebugAt().setupAtRoot().back().setup( request );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Vhost: www.mysite.com/admin = /admin
     * Deployed @ /
     * Request is via debug @ http://www.mysite.com/admin/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_root_debug_requested_at_vhost_under_admin_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com/admin", "/admin" ).originalRequest(
            "www.mysite.com", "/admin/site/0/mypage" ).requestedSite( 0, "" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        Mockito.when( contentDao.findByKey( contentKey1 ) ).thenReturn( createContent( contentKey1, "Content One" ) );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "http://www.mysite.com/admin/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Vhost: www.mysite.com/admin = /admin
     * Deployed @ /
     * Request is via debug @ http://www.mysite.com/admin/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_root_debug_requested_at_vhost_under_admin_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com/admin", "/admin" ).originalRequest(
            "www.mysite.com", "/admin/site/0/mypage" ).requestedSite( 0, "" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "/admin/site/0/mypage", result );
    }

    /**
     * Deployed @ /cms
     * Request is via debug @ http://www.mysite.com/cms/admin/site/0/
     */
    @Test
    public void createUrl_when_deployed_at_cms_debug_requested_under_subpath_admin_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAt( "/cms" ).originalRequest( "www.mysite.com", "/cms/admin/site/0/" ).requestedSite( 0,
                                                                                                                                         "" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        RenderTrace.enter();

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        RenderTrace.exit();

        // verify
        assertEquals( "http://localhost/admin/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Deployed @ /
     * Request is via admin debug @ http://localhost/admin/preview/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_root_preview_requested_at_localhost_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/admin/preview/0/mypage" ).requestedSite( 0,
                                                                                                                                     "mypage" ).requestedAdminPreviewAt().setupAtDefaultPath().back().setup(
            request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "http://localhost/admin/preview/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Deployed @ /
     * Request is via admin debug @ http://localhost/admin/preview/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_root_preview_requested_at_localhost_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/admin/preview/0/mypage" ).requestedSite( 0,
                                                                                                                                     "mypage" ).requestedAdminPreviewAt().setupAtDefaultPath().back().setup(
            request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "/admin/preview/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Deployed @ /
     * Request is via admin debug @ http://localhost/site/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_root_portal_requested_at_localhost_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/site/0/mypage" ).requestedSite( 0,
                                                                                                                            "mypage" ).requestedPortalAt().siteSetupAtDefaultPath().back().setup(
            request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "http://localhost/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Deployed @ /
     * Request is via admin debug @ http://localhost/site/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_root_portal_requested_at_localhost_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/site/0/mypage" ).requestedSite( 0,
                                                                                                                            "mypage" ).requestedPortalAt().siteSetupAtDefaultPath().back().setup(
            request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Deployed @ /
     * Request is via admin debug @ http://localhost/site/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_root_portal_requested_at_vhost_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com", "/site/0" ).originalRequest( "www.mysite.com",
                                                                                                                           "/site/0/mypage" ).requestedSite(
            0, "mypage" ).requestedPortalAt().siteSetupAtRoot().back().setup( request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "http://www.mysite.com/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Deployed @ /
     * Request is via admin debug @ http://localhost/site/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_root_portal_requested_at_vhost_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com", "/site/0" ).originalRequest( "www.mysite.com",
                                                                                                                           "/site/0/mypage" ).requestedSite(
            0, "mypage" ).requestedPortalAt().siteSetupAtRoot().back().setup( request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = false
     * Deployed @ /
     * Request is via admin debug @ http://localhost/site/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_cms_portal_requested_at_localhost_with_absolute_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        new DeploymentAndRequestSetup().appDeployedAt( "/cms" ).originalRequest( "localhost", "/cms/site/0/mypage" ).requestedSite( 0,
                                                                                                                                    "mypage" ).requestedPortalAt().siteSetupAtDefaultPath().back().setup(
            request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "http://localhost/cms/site/0/mypage", result );
    }

    /**
     * site-0.properties cms.site.createUrlAsPath = true
     * Deployed @ /
     * Request is via admin debug @ http://localhost/site/0/mypage
     */
    @Test
    public void createUrl_when_deployed_at_cms_portal_requested_at_localhost_with_relative_urls()
        throws Exception
    {
        // setup
        sitePropertiesService.setProperty( siteKey0, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "true" );

        new DeploymentAndRequestSetup().appDeployedAt( "/cms" ).originalRequest( "localhost", "/cms/site/0/mypage" ).requestedSite( 0,
                                                                                                                                    "mypage" ).requestedPortalAt().siteSetupAtDefaultPath().back().setup(
            request );

        // exercise
        String result = PortalFunctions.createUrl( "/mypage" );

        // verify
        assertEquals( "/cms/site/0/mypage", result );
    }


    private ContentEntity createContent( ContentKey key, String title )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( key );
        content.setDeleted( false );
        ContentVersionEntity version = new ContentVersionEntity();
        content.setMainVersion( version );
        content.addVersion( version );
        version.setTitle( title );
        return content;
    }

    private SiteEntity createSite( SiteKey key )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( key.toInt() );
        return site;
    }
}
