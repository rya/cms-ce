/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.userstore;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class UserStoreKeyEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new UserStoreKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new UserStoreKey( 2 )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new UserStoreKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new UserStoreKey( 1 );
    }
}
