/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import org.springframework.mock.web.MockHttpServletRequest;

import junit.framework.TestCase;

import com.enonic.cms.core.vhost.VirtualHost;

public class VirtualHostTest
    extends TestCase
{
    private MockHttpServletRequest request;


    protected void setUp()
        throws Exception
    {
        super.setUp();

        request = new MockHttpServletRequest();
    }

    public void testGetFullSourcePath()
    {

        request.setContextPath( "/cms-server" );

        VirtualHost vh = new VirtualHost( "jvs", "/site/0" );

        String fullSourcePath = vh.getFullSourcePath( request );
        assertEquals( "/cms-server", fullSourcePath );
    }

    public void testGetFullTargetPath()
    {

        request.setPathInfo( "/Svensk%C3%B6/" );
        request.setRequestURI( "/Svensk%C3%B6/" );
        request.setServletPath( "" );

        VirtualHost vh = new VirtualHost( "jvs", "/site/0" );

        String fullTargetPath = vh.getFullTargetPath( request );
        assertEquals( "/site/0/Svensk%C3%B6/", fullTargetPath );
    }

    public void testMatchesServerName()
    {
        request.setServerName( "www.enonic.com" );

        VirtualHost vh = new VirtualHost( "www.enonic.no", "/site/0" );
        assertFalse( vh.matches( request ) );

        vh = new VirtualHost( "www.enonic.com", "/site/0" );
        assertTrue( vh.matches( request ) );

        vh = new VirtualHost( "www.enonic.com.", "/site/0" );
        assertTrue( vh.matches( request ) );
    }
}
