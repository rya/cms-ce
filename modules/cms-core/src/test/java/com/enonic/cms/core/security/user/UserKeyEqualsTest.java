/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class UserKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new UserKey( "ABC" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new UserKey( "CBA" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new UserKey( "ABC" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new UserKey( "ABC" );
    }
}
