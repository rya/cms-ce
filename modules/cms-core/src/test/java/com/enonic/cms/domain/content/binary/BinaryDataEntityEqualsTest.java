/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.binary;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class BinaryDataEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        BinaryDataEntity instance1 = new BinaryDataEntity();
        instance1.setKey( 1 );
        return instance1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        BinaryDataEntity instance1 = new BinaryDataEntity();
        instance1.setKey( 2 );

        return new Object[]{instance1};  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        BinaryDataEntity instance1 = new BinaryDataEntity();
        instance1.setKey( 1 );
        return instance1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        BinaryDataEntity instance1 = new BinaryDataEntity();
        instance1.setKey( 1 );
        return instance1;
    }
}
