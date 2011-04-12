/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.group;

import com.enonic.cms.core.security.group.GroupEntity;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class GroupEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        GroupEntity i1 = new GroupEntity();
        i1.setKey( "ABC" );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        GroupEntity i1 = new GroupEntity();
        i1.setKey( "CBA" );
        return new Object[]{i1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        GroupEntity i1 = new GroupEntity();
        i1.setKey( "ABC" );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        GroupEntity i1 = new GroupEntity();
        i1.setKey( "ABC" );
        return i1;
    }
}
