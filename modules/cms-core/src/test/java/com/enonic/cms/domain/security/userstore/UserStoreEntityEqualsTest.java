/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.userstore;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class UserStoreEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        UserStoreEntity i1 = new UserStoreEntity();
        i1.setKey( new UserStoreKey( 1 ) );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        UserStoreEntity i1 = new UserStoreEntity();
        i1.setKey( new UserStoreKey( 2 ) );
        return new Object[]{i1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        UserStoreEntity i1 = new UserStoreEntity();
        i1.setKey( new UserStoreKey( 1 ) );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        UserStoreEntity i1 = new UserStoreEntity();
        i1.setKey( new UserStoreKey( 1 ) );
        return i1;
    }
}
