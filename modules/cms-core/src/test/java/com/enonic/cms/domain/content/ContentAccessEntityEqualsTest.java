/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentAccessEntity;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentAccessEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        ContentAccessEntity instance = new ContentAccessEntity();
        instance.setKey( "ABC" );
        return instance;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        ContentAccessEntity instance1 = new ContentAccessEntity();
        instance1.setKey( "ABC1" );

        ContentAccessEntity instance2 = new ContentAccessEntity();
        instance2.setKey( "ABC2" );

        return new Object[]{instance1, instance2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        ContentAccessEntity instance = new ContentAccessEntity();
        instance.setKey( "ABC" );
        return instance;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        ContentAccessEntity instance = new ContentAccessEntity();
        instance.setKey( "ABC" );
        return instance;
    }
}
