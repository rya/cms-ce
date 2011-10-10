/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class UserEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        UserEntity i1 = new UserEntity();
        i1.setKey( new UserKey( "ABC" ) );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        UserEntity i1 = new UserEntity();
        i1.setKey( new UserKey( "CBA" ) );

        return new Object[]{i1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        UserEntity i1 = new UserEntity();
        i1.setKey( new UserKey( "ABC" ) );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        UserEntity i1 = new UserEntity();
        i1.setKey( new UserKey( "ABC" ) );
        return i1;
    }
}
