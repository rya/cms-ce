/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentTypeKeyEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ContentTypeKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new ContentTypeKey( 2 )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ContentTypeKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ContentTypeKey( 1 );
    }
}

