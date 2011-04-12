/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentIndexEntity;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentIndexEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        ContentIndexEntity instance = new ContentIndexEntity();
        instance.setKey( "ABC" );
        return instance;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        ContentIndexEntity instance = new ContentIndexEntity();
        instance.setKey( "123" );
        return new Object[]{instance};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        ContentIndexEntity instance = new ContentIndexEntity();
        instance.setKey( "ABC" );
        return instance;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        ContentIndexEntity instance = new ContentIndexEntity();
        instance.setKey( "ABC" );
        return instance;
    }
}
