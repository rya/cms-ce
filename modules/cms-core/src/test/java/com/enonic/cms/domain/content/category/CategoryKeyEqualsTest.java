/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.category;

import com.enonic.cms.core.content.category.CategoryKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class CategoryKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new CategoryKey( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new CategoryKey( 2 )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new CategoryKey( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new CategoryKey( 1 );
    }
}
