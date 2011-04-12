/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import junit.framework.TestCase;

import static org.junit.Assert.*;

public class CategoryEntityTest
    extends TestCase
{

    public void testGetAllChildrenWithNullChildren()
    {

        CategoryEntity root = createCategory( 0, "root", null );

        List<CategoryEntity> actualChildren = root.getDescendants();
        assertNotNull( actualChildren );
        assertEquals( 0, actualChildren.size() );
    }

    public void testAllChildrenWithOneChild()
    {

        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );

        Set<CategoryEntity> expectedChildren = new HashSet<CategoryEntity>();
        expectedChildren.add( cat_1 );

        List<CategoryEntity> actualChildren = root.getDescendants();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new CategoryEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new CategoryEntity[actualChildren.size()] ) );
    }

    public void testGetAllChildren()
    {

        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );
        CategoryEntity cat_1_1 = createCategory( 2, "0.1.1", cat_1 );
        CategoryEntity cat_1_2 = createCategory( 3, "0.1.2", cat_1 );
        CategoryEntity cat_1_2_1 = createCategory( 4, "0.1.2.1", cat_1_2 );
        CategoryEntity cat_2 = createCategory( 5, "0.2", root );
        CategoryEntity cat_2_1 = createCategory( 6, "0.2", cat_2 );

        List<CategoryEntity> expectedChildren = new ArrayList<CategoryEntity>();
        expectedChildren.add( cat_1 );
        expectedChildren.add( cat_1_1 );
        expectedChildren.add( cat_1_2 );
        expectedChildren.add( cat_1_2_1 );
        expectedChildren.add( cat_2 );
        expectedChildren.add( cat_2_1 );

        List<CategoryEntity> actualChildren = root.getDescendants();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new CategoryEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new CategoryEntity[actualChildren.size()] ) );
    }

    public void testGetAllChildren2()
    {

        CategoryEntity root = createCategory( 0, "root", null );
        CategoryEntity cat_1 = createCategory( 1, "0.1", root );
        CategoryEntity cat_1_1 = createCategory( 2, "0.1.1", cat_1 );
        CategoryEntity cat_1_2 = createCategory( 3, "0.1.2", cat_1 );
        CategoryEntity cat_1_2_1 = createCategory( 4, "0.1.2.1", cat_1_2 );
        CategoryEntity cat_2 = createCategory( 5, "0.2", root );
        CategoryEntity cat_2_1 = createCategory( 6, "0.2", cat_2 );

        List<CategoryEntity> expectedChildren = new ArrayList<CategoryEntity>();
        expectedChildren.add( cat_1_1 );
        expectedChildren.add( cat_1_2 );
        expectedChildren.add( cat_1_2_1 );

        List<CategoryEntity> actualChildren = cat_1.getDescendants();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new CategoryEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new CategoryEntity[actualChildren.size()] ) );
    }

    private CategoryEntity createCategory( int key, String name, CategoryEntity parent )
    {
        CategoryEntity cat = new CategoryEntity();
        cat.setKey( new CategoryKey( key ) );
        cat.setName( name );
        if ( parent != null )
        {
            cat.setParent( parent );
            parent.addChild( cat );
        }
        return cat;
    }
}
