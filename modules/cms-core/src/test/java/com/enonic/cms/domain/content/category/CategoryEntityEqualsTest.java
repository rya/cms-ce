/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.category;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class CategoryEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        CategoryEntity i1 = new CategoryEntity();
        i1.setKey( new CategoryKey( 1 ) );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        CategoryEntity i1 = new CategoryEntity();
        i1.setKey( new CategoryKey( 2 ) );
        return new Object[]{i1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        CategoryEntity i1 = new CategoryEntity();
        i1.setKey( new CategoryKey( 1 ) );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        CategoryEntity i1 = new CategoryEntity();
        i1.setKey( new CategoryKey( 1 ) );
        return i1;
    }
}
