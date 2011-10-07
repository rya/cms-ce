/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote.plugin;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RemoteUserStoreFactoryTest
{
    private RemoteUserStoreFactory factory;

    @Before
    public void setUp()
    {
        this.factory = new RemoteUserStoreFactory();
    }

    @Test
    public void testCustom()
    {
        RemoteUserStorePlugin dir = this.factory.create( NopRemoteUserStorePlugin.class.getName() );
        assertEquals( NopRemoteUserStorePlugin.class, dir.getClass() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegal()
    {
        this.factory.create( "dummy" );
    }

    @Test
    public void testConfigure()
    {
        Properties props = new Properties();
        props.setProperty( "prop1", "hello" );
        props.setProperty( "prop2", "11" );
        props.setProperty( "prop3", "true" );

        NopRemoteUserStorePlugin dir = (NopRemoteUserStorePlugin) this.factory.create( NopRemoteUserStorePlugin.class.getName(), props );

        assertEquals( "hello", dir.getProp1() );
        assertEquals( 11, dir.getProp2() );
        assertEquals( true, dir.getProp3() );
    }
}