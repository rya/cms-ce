/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.business;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.DeploymentPathResolver;
import com.enonic.cms.core.vhost.VirtualHostHelper;

import static org.junit.Assert.*;

/**
 * Aug 9, 2010
 */
public class DeploymentPathResolverTest
{
    @Test
    public void getAdminDeploymentPath_when_context_path_not_set_and_vhost_not_set()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath( null );
        assertEquals( "/admin", DeploymentPathResolver.getAdminDeploymentPath( request ) );
    }

    @Test
    public void getAdminDeploymentPath_when_vhost_set()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath( null );

        VirtualHostHelper.setBasePath( request, "" );
        assertEquals( "", DeploymentPathResolver.getAdminDeploymentPath( request ) );

        VirtualHostHelper.setBasePath( request, "/" );
        assertEquals( "/", DeploymentPathResolver.getAdminDeploymentPath( request ) );

        VirtualHostHelper.setBasePath( request, "/admin" );
        assertEquals( "/admin", DeploymentPathResolver.getAdminDeploymentPath( request ) );
    }

    @Test
    public void testGetAdminDeploymentPath_when_context_path_set()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath( "/cms" );
        assertEquals( "/cms/admin", DeploymentPathResolver.getAdminDeploymentPath( request ) );
    }

    @Test
    public void testGetAdminDeploymentPath_when_no_context_path_or_vhost_set()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI( "/site/0/" );
        request.setContextPath( null );
        assertEquals( "/site/0", DeploymentPathResolver.getSiteDeploymentPath( request ) );
    }

    @Test
    public void testGetAdminDeploymentPath_when_vhost_set()
        throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI( "/site/0/" );
        request.setContextPath( null );

        VirtualHostHelper.setBasePath( request, "" );
        assertEquals( "", DeploymentPathResolver.getSiteDeploymentPath( request ) );

        VirtualHostHelper.setBasePath( request, "/" );
        assertEquals( "/", DeploymentPathResolver.getSiteDeploymentPath( request ) );

    }

}
