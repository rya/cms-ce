/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.vhost.VirtualHostHelper;

import com.enonic.cms.domain.Attribute;

import static org.junit.Assert.*;

/**
 * Aug 24, 2010
 */
public class DeploymentAndRequestSetupTest
{

    /**
     * Deployed @ /
     * Request is via admin debug @ http://localhost/admin/site/0/political news shortcut
     */
    @Test
    public void when_deployed_at_root_debug_requested_at_localhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost",
                                                                             "/admin/site/0/political news shortcut" ).requestedSite( 0,
                                                                                                                                      "political news shortcut" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "localhost", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );
        assertEquals( "requestURL", "http://localhost:80/site/0/political+news+shortcut", request.getRequestURL().toString() );
        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );

        assertNull( VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /
     * Vhost: admin.enonic-cms.com = /admin
     * Request is via admin debug @ http://admin.enonic-cms.com/site/0/political news shortcut
     */
    @Test
    public void when_deployed_at_root_debug_requested_at_vhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "admin.enonic-cms.com", "/admin" ).originalRequest(
            "admin.enonic-cms.com", "/site/0/political news shortcut" ).requestedSite( 0,
                                                                                       "political news shortcut" ).requestedAdminDebugAt().setupAtRoot().back().setup(
            request );

        assertEquals( "requestURL", "http://admin.enonic-cms.com:80/site/0/political+news+shortcut", request.getRequestURL().toString() );
        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "admin.enonic-cms.com", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );
        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );

        assertEquals( "", VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Vhost: www.mysite.com/admin = /admin
     * Deployed @ /
     * Request is via debug @ http://www.mysite.com/admin/site/0/political news shortcut
     */
    @Test
    public void when_deployed_at_root_debug_requested_at_vhost_under_admin()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com/admin", "admin" ).originalRequest(
            "www.mysite.com", "/admin/site/0/political news shortcut" ).requestedSite( 0,
                                                                                       "political news shortcut" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup(
            request );

        assertEquals( "requestURL", "http://www.mysite.com:80/site/0/political+news+shortcut", request.getRequestURL().toString() );
        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "www.mysite.com", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );
        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );

        assertEquals( "/admin", VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /cms
     * Request is via debug @ http://www.mysite.com/cms/admin/site/0/political news shortcut
     */
    @Test
    public void when_deployed_at_cms_debug_requested_at_under_subpath_admin()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAt( "/cms" ).originalRequest( "www.mysite.com",
                                                                                 "/cms/admin/site/0/political news shortcut" ).requestedSite(
            0, "political news shortcut" ).requestedAdminDebugAt().setupAtDefaultPath().back().setup( request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "www.mysite.com", request.getServerName() );
        assertEquals( "contextPath", "/cms", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/cms/site/0/political+news+shortcut", request.getRequestURI() );

        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertEquals( null, VirtualHostHelper.getBasePath( request ) );

        assertEquals( "requestURL", "http://www.mysite.com:80/cms/site/0/political+news+shortcut", request.getRequestURL().toString() );
    }

    /**
     * Deployed @ /
     * Request is via admin preview @ http://localhost/admin/preview/0/political news shortcut
     */
    @Test
    public void when_deployed_at_root_preview_requested_at_localhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost",
                                                                             "/admin/preview/0/political news shortcut" ).requestedSite( 0,
                                                                                                                                         "political news shortcut" ).requestedAdminPreviewAt().setupAtDefaultPath().back().setup(
            request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "localhost", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );
        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertEquals( Attribute.PREVIEW_ENABLED, "true", request.getAttribute( Attribute.PREVIEW_ENABLED ) );

        assertNull( VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /cms
     * Request is via admin preview @ http://localhost/cms/admin/preview/0/political news shortcut
     */
    @Test
    public void when_deployed_at_cms_preview_requested_at_localhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAt( "/cms" ).originalRequest( "localhost",
                                                                                 "/admin/preview/0/political news shortcut" ).requestedSite(
            0, "political news shortcut" ).requestedAdminPreviewAt().setupAtDefaultPath().back().setup( request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "localhost", request.getServerName() );
        assertEquals( "contextPath", "/cms", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/cms/site/0/political+news+shortcut", request.getRequestURI() );
        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertEquals( Attribute.PREVIEW_ENABLED, "true", request.getAttribute( Attribute.PREVIEW_ENABLED ) );

        assertNull( VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /
     * Request is via portal @ http://localhost/site/0/political news shortcut
     */
    @Test
    public void when_deployed_at_root_portal_requested_at_localhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAtRoot().originalRequest( "localhost", "/site/0/political news shortcut" ).requestedSite(
            0, "political news shortcut" ).requestedPortalAt().siteSetupAtDefaultPath().back().setup( request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "localhost", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );

        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertNull( VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /cms
     * Request is via portal @ http://localhost/cms/site/0/political news shortcut
     */
    @Test
    public void when_deployed_at_cms_portal_requested_at_localhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAt( "/cms" ).originalRequest( "localhost",
                                                                                 "/cms/site/0/political news shortcut" ).requestedSite( 0,
                                                                                                                                        "political news shortcut" ).requestedPortalAt().siteSetupAtDefaultPath().back().setup(
            request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "localhost", request.getServerName() );
        assertEquals( "contextPath", "/cms", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/cms/site/0/political+news+shortcut", request.getRequestURI() );

        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertNull( VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /
     * Request is via portal @ http://www.mysite.com/en/political news shortcut
     */
    @Test
    public void when_deployed_at_root_portal_requested_at_vhost_under_subpath()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com/en", "/site/0" ).originalRequest(
            "www.mysite.com", "/en/political news shortcut" ).requestedSite( 0,
                                                                             "political news shortcut" ).requestedPortalAt().siteSetupAtPath(
            "/en" ).back().setup( request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "www.mysite.com", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );

        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertEquals( "basePath", "/en", VirtualHostHelper.getBasePath( request ) );
    }

    /**
     * Deployed @ /
     * Request is via portal @ http://www.mysite.com/political news shortcut
     */
    @Test
    public void when_deployed_at_root_portal_requested_at_vhost()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        new DeploymentAndRequestSetup().appDeployedAtRoot().addVirtualHost( "www.mysite.com", "/site/0" ).originalRequest( "www.mysite.com",
                                                                                                                           "/site/0/political news shortcut" ).requestedSite(
            0, "political news shortcut" ).requestedPortalAt().siteSetupAtRoot().back().setup( request );

        assertEquals( "scheme", "http", request.getScheme() );
        assertEquals( "serverName", "www.mysite.com", request.getServerName() );
        assertEquals( "contextPath", "", request.getContextPath() );
        assertEquals( "servletPath", "/site", request.getServletPath() );
        assertEquals( "requestURI", "/site/0/political+news+shortcut", request.getRequestURI() );
        assertEquals( "requestURL", "http://www.mysite.com:80/site/0/political+news+shortcut", request.getRequestURL().toString() );

        assertEquals( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, null,
                      request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME ) );
        assertEquals( "basePath", "", VirtualHostHelper.getBasePath( request ) );
    }
}
