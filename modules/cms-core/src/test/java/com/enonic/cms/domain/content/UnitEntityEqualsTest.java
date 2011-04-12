/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.UnitEntity;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class UnitEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        UnitEntity instance1 = new UnitEntity();
        instance1.setKey( 1 );
        return instance1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        UnitEntity instance1 = new UnitEntity();
        instance1.setKey( 2 );

        return new Object[]{instance1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        UnitEntity instance1 = new UnitEntity();
        instance1.setKey( 1 );
        return instance1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        UnitEntity instance1 = new UnitEntity();
        instance1.setKey( 1 );
        return instance1;
    }
}
