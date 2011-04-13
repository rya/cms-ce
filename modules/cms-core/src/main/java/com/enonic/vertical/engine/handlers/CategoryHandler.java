/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.primitives.Ints;

import com.enonic.cms.store.dao.CategoryDao;

public class CategoryHandler
    extends BaseHandler
{
    @Autowired
    private CategoryDao categoryDao;

    public int[] getCategoryKeysBySuperCategories( int[] superCategoryKeys, boolean recursive )
    {

        if ( superCategoryKeys == null || superCategoryKeys.length == 0 )
        {
            return new int[0];
        }

        List<Integer> categoryKeys = new ArrayList<Integer>();
        for ( int superCategoryKey : superCategoryKeys )
        {
            categoryKeys.addAll( getSubCategoriesByParent( new CategoryKey( superCategoryKey ), recursive ) );
        }

        return Ints.toArray( categoryKeys );
    }

    public List<Integer> getSubCategoriesByParent( CategoryKey parentKey, boolean recursive )
    {
        List<Integer> categoryKeys = new ArrayList<Integer>();
        CategoryEntity parent = categoryDao.findByKey( parentKey );
        if ( parent == null )
        {
            return categoryKeys;
        }
        for ( CategoryKey categoryKey : parent.getChildrenKeys() )
        {
            categoryKeys.add( categoryKey.toInt() );
            if ( recursive )
            {
                categoryKeys.addAll( getSubCategoriesByParent( categoryKey, true ) );
            }
        }
        return categoryKeys;
    }

    public int getContentTypeKey( CategoryKey categoryKey )
    {
        int contentTypeKey = -1;
        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category != null && category.getContentType() != null )
        {
            contentTypeKey = category.getContentType().getKey();
        }
        return contentTypeKey;
    }

    public StringBuffer getPathString( CategoryKey categoryKey )
    {
        CommonHandler commonHandler = getCommonHandler();
        return commonHandler.getPathString( db.tCategory, db.tCategory.cat_lKey, db.tCategory.cat_cat_lSuper, db.tCategory.cat_sName,
                                            categoryKey.toInt(), null, true );
    }
}
