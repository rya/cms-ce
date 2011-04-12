/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentHandlerEntityEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        ContentHandlerEntity instance = new ContentHandlerEntity();
        instance.setKey( new ContentHandlerKey( 1 ) );
        return instance;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        ContentHandlerEntity instance = new ContentHandlerEntity();
        instance.setKey( new ContentHandlerKey( 2 ) );
        return new Object[]{instance};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        ContentHandlerEntity instance = new ContentHandlerEntity();
        instance.setKey( new ContentHandlerKey( 1 ) );
        return instance;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        ContentHandlerEntity instance = new ContentHandlerEntity();
        instance.setKey( new ContentHandlerKey( 1 ) );
        return instance;
    }
}
