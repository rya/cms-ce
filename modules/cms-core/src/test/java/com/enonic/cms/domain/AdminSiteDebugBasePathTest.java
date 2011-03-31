/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Aug 6, 2010
 */
public class AdminSiteDebugBasePathTest
{
    @Test
    public void getAsPath()
    {
        assertEquals( "/admin/site/0", new AdminSiteDebugBasePath( new Path( "admin" ), new SiteKey( 0 ) ).getAsPath().toString() );
        assertEquals( "/site/0", new AdminSiteDebugBasePath( new Path( "" ), new SiteKey( 0 ) ).getAsPath().toString() );
        assertEquals( "/site/0", new AdminSiteDebugBasePath( new Path( "/" ), new SiteKey( 0 ) ).getAsPath().toString() );
    }
}
