/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.userstore.connector.config;

import com.enonic.cms.core.security.userstore.connector.config.InvalidUserStoreConnectorConfigException;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserPolicyConfigTest
{

    /**
     * Possible to setup a blank user policy, which means a "read only" userstore connector.
     */
    @Test
    public void empty_user_policy_gives_readonly()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, "" );

        assertFalse( config.canUpdate() );
        assertFalse( config.canUpdatePassword() );
        assertFalse( config.canCreate() );
        assertFalse( config.canDelete() );
    }

    /**
     * Possible to setup a null user policy, which means a "read only" userstore connector.
     */
    @Test
    public void null_user_policy_gives_readonly()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, null );

        assertFalse( config.canUpdate() );
        assertFalse( config.canUpdatePassword() );
        assertFalse( config.canCreate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testCreateOnly()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, "create" );

        assertFalse( config.canUpdate() );
        assertFalse( config.canUpdatePassword() );
        assertTrue( config.canCreate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testUpdateOnly()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, "update" );

        assertTrue( config.canUpdate() );
        assertFalse( config.canUpdatePassword() );
        assertFalse( config.canCreate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testUpdatePasswordOnly()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, "updatepassword" );

        assertFalse( config.canUpdate() );
        assertTrue( config.canUpdatePassword() );
        assertFalse( config.canCreate() );
        assertFalse( config.canDelete() );
    }

    @Test
    public void testDeleteOnly()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, "delete" );

        assertFalse( config.canUpdate() );
        assertFalse( config.canUpdatePassword() );
        assertFalse( config.canCreate() );
        assertTrue( config.canDelete() );
    }

    @Test
    public void testAll()
    {
        final UserPolicyConfig config = new UserPolicyConfig( null, "all" );

        assertTrue( config.canUpdate() );
        assertTrue( config.canUpdatePassword() );
        assertTrue( config.canCreate() );
        assertTrue( config.canDelete() );
    }

    @Test
    public void testIllegalConfig()
    {
        try
        {
            final UserPolicyConfig config = new UserPolicyConfig( null, "all,update" );
            fail( "An exception should have been thrown." );
        }
        catch ( Exception ex )
        {
            assertEquals( InvalidUserStoreConnectorConfigException.class.getName(), ex.getClass().getName() );
        }
    }
}

