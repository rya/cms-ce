/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.remote;

import org.junit.Assert;
import org.junit.Test;

public class RemoteUserTest
{
    @Test
    public void testBasic()
    {
        RemoteUser user = new RemoteUser( "myuser" );
        Assert.assertEquals( "myuser", user.getId() );

        user.setSync( "mysync" );
        Assert.assertEquals( "mysync", user.getSync() );

        user.setEmail( "user@domain.com" );
        Assert.assertEquals( "user@domain.com", user.getEmail() );
    }

    @Test
    public void testEquals()
    {
        RemoteUser user = new RemoteUser( "myuser" );
        Assert.assertTrue( user.equals( user ) );
        Assert.assertTrue( user.equals( new RemoteUser( "myuser" ) ) );
        Assert.assertFalse( user.equals( new RemoteUser( "unknown" ) ) );
    }
}
