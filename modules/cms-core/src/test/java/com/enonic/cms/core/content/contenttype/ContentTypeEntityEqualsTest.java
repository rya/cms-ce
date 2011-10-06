/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ContentTypeEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        ContentTypeEntity i1 = new ContentTypeEntity();
        i1.setKey( 1 );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        ContentTypeEntity i1 = new ContentTypeEntity();
        i1.setKey( 2 );

        return new Object[]{i1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        ContentTypeEntity i1 = new ContentTypeEntity();
        i1.setKey( 1 );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        ContentTypeEntity i1 = new ContentTypeEntity();
        i1.setKey( 1 );
        return i1;
    }
}
