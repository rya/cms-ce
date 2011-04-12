/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.userstore.connector.config;

import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.InvalidUserStoreConnectorConfigException;
import org.junit.Test;

import junit.framework.TestCase;

public class GroupPolicyConfigTest
    extends TestCase
{
    @Test
    public void testReadOnly()
    {
        final GroupPolicyConfig config = new GroupPolicyConfig( null, "read" );

        assertFalse( config.useLocal() );
        assertTrue( config.canRead() );
        assertFalse( config.canCreate() );
        assertFalse( config.canUpdate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testCreateOnly()
    {
        final GroupPolicyConfig config = new GroupPolicyConfig( null, "create" );

        assertFalse( config.useLocal() );
        assertFalse( config.canRead() );
        assertTrue( config.canCreate() );
        assertFalse( config.canUpdate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testUpdateOnly()
    {
        final GroupPolicyConfig config = new GroupPolicyConfig( null, "update" );

        assertFalse( config.useLocal() );
        assertFalse( config.canRead() );
        assertFalse( config.canCreate() );
        assertTrue( config.canUpdate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testDeleteOnly()
    {
        final GroupPolicyConfig config = new GroupPolicyConfig( null, "delete" );

        assertFalse( config.useLocal() );
        assertFalse( config.canRead() );
        assertFalse( config.canUpdate() );
        assertFalse( config.canCreate() );
        assertTrue( config.canDelete() );
    }

    @Test
    public void testAll()
    {
        final GroupPolicyConfig config = new GroupPolicyConfig( null, "all" );

        assertFalse( config.useLocal() );
        assertTrue( config.canRead() );
        assertTrue( config.canUpdate() );
        assertTrue( config.canCreate() );
        assertTrue( config.canDelete() );
    }

    @Test
    public void testNone()
    {
        final GroupPolicyConfig config = new GroupPolicyConfig( null, "none" );

        assertFalse( config.useLocal() );
        assertFalse( config.canRead() );
        assertFalse( config.canUpdate() );
        assertFalse( config.canCreate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testLocal()
    {
        final GroupPolicyConfig config1 = new GroupPolicyConfig( null, "local" );

        assertTrue( config1.useLocal() );
        assertFalse( config1.canRead() );
        assertTrue( config1.canUpdate() );
        assertTrue( config1.canCreate() );
        assertTrue( config1.canDelete() );

        try
        {
            final GroupPolicyConfig config = new GroupPolicyConfig( null, "local,read" );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }

        try
        {
            final GroupPolicyConfig config = new GroupPolicyConfig( null, "update,local" );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testIllegalConfig()
    {
        try
        {
            final GroupPolicyConfig config = new GroupPolicyConfig( null, "all,none" );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testUnknownConfig()
    {
        try
        {
            final GroupPolicyConfig config = new GroupPolicyConfig( null, "fisk" );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testMissingConfig()
    {
        try
        {
            final GroupPolicyConfig config = new GroupPolicyConfig( null, null );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }
    }

    @Test
    public void testEmptyConfig()
    {
        try
        {
            final GroupPolicyConfig config = new GroupPolicyConfig( null, "" );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }
    }
}