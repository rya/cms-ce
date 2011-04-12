/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.category;

import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryAccessKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.group.GroupKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class CategoryAccessEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        CategoryAccessEntity i1 = new CategoryAccessEntity();
        i1.setKey( new CategoryAccessKey( new CategoryKey( 1 ), new GroupKey( "ABC" ) ) );
        return i1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        CategoryAccessEntity i1 = new CategoryAccessEntity();
        i1.setKey( new CategoryAccessKey( new CategoryKey( 1 ), new GroupKey( "123" ) ) );

        CategoryAccessEntity i2 = new CategoryAccessEntity();
        i2.setKey( new CategoryAccessKey( new CategoryKey( 2 ), new GroupKey( "ABC" ) ) );

        return new Object[]{i1, i2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        CategoryAccessEntity i1 = new CategoryAccessEntity();
        i1.setKey( new CategoryAccessKey( new CategoryKey( 1 ), new GroupKey( "ABC" ) ) );
        return i1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        CategoryAccessEntity i1 = new CategoryAccessEntity();
        i1.setKey( new CategoryAccessKey( new CategoryKey( 1 ), new GroupKey( "ABC" ) ) );
        return i1;
    }
}
