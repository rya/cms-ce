/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import org.springframework.mock.web.MockHttpServletRequest;

import junit.framework.TestCase;

public class AdminHelperTest
    extends TestCase
{

    MockHttpServletRequest request;
    //MockHttpServletRe request;


    protected void setUp()
        throws Exception
    {

        request = new MockHttpServletRequest();

    }

    public void testGetAdminPathWithBlankBasePathSet()
    {

        request.setContextPath( "" );
        request.setScheme( "http" );
        request.setServerName( "admin.balle.com" );
        request.setServerPort( 80 );
        request.setAttribute( "com.enonic.cms.business.vhost.BASE_PATH", "" );

        String adminPath;

        adminPath = AdminHelper.getAdminPath( request, true );
        assertEquals( "", adminPath );

        adminPath = AdminHelper.getAdminPath( request, false );
        assertEquals( "http://admin.balle.com", adminPath );
    }

    public void testGetAdminPathWithNullBasePathSet()
    {

        request.setContextPath( "" );
        request.setScheme( "http" );
        request.setServerName( "admin.balle.com" );
        request.setServerPort( 80 );
        request.setAttribute( "com.enonic.cms.business.vhost.BASE_PATH", null );

        String adminPath;

        adminPath = AdminHelper.getAdminPath( request, true );
        assertEquals( "/admin", adminPath );

        adminPath = AdminHelper.getAdminPath( request, false );
        assertEquals( "http://admin.balle.com/admin", adminPath );
    }

    public void testGetAdminPathWithNullBasePathSetAndSomeContextPathSet()
    {

        request.setContextPath( "/cms-server" );
        request.setScheme( "http" );
        request.setServerName( "admin.balle.com" );
        request.setServerPort( 80 );
        request.setAttribute( "com.enonic.cms.business.vhost.BASE_PATH", null );

        String adminPath;

        adminPath = AdminHelper.getAdminPath( request, true );
        assertEquals( "/cms-server/admin", adminPath );

        adminPath = AdminHelper.getAdminPath( request, false );
        assertEquals( "http://admin.balle.com/cms-server/admin", adminPath );
    }

    public void testGetAdminPathWithSomeBasePathSet()
    {

        request.setContextPath( "" );
        request.setScheme( "http" );
        request.setServerName( "admin.balle.com" );
        request.setServerPort( 80 );
        request.setAttribute( "com.enonic.cms.business.vhost.BASE_PATH", "/bjarne" );

        String adminPath;

        adminPath = AdminHelper.getAdminPath( request, true );
        assertEquals( "/bjarne", adminPath );

        adminPath = AdminHelper.getAdminPath( request, false );
        assertEquals( "http://admin.balle.com/bjarne", adminPath );
    }


}
