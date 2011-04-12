/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentHandlerKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentHandlerKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ContentHandlerKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        final ContentHandlerKey instance1 = new ContentHandlerKey( 2 );
        final ContentHandlerKey instance2 = new ContentHandlerKey( 3 );

        return new Object[]{instance1, instance2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ContentHandlerKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ContentHandlerKey( 1 );
    }
}