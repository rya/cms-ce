/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.remote;

import org.junit.Assert;
import org.junit.Test;

public class RemoteGroupTest
{
    @Test
    public void testBasic()
    {
        RemoteGroup group = new RemoteGroup( "mygroup" );
        Assert.assertEquals( "mygroup", group.getId() );

        group.setSync( "mysync" );
        Assert.assertEquals( "mysync", group.getSync() );
    }

    @Test
    public void testEquals()
    {
        RemoteGroup group = new RemoteGroup( "mygroup" );
        Assert.assertTrue( group.equals( group ) );
        Assert.assertTrue( group.equals( new RemoteGroup( "mygroup" ) ) );
        Assert.assertFalse( group.equals( new RemoteGroup( "unknown" ) ) );
    }
}
