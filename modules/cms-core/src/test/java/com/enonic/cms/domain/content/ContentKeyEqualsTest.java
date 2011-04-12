/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentKeyEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ContentKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new ContentKey( 2 )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ContentKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ContentKey( 1 );
    }
}
