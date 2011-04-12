/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.content.category.CategoryKey;

public class CategoryEntityDao
    extends AbstractBaseEntityDao<CategoryEntity>
    implements CategoryDao
{
    public CategoryEntity findByKey( CategoryKey key )
    {
        CategoryEntity category = get( CategoryEntity.class, key );

        if ( category == null )
        {
            return null;
        }

        if ( category.isDeleted() )
        {
            return null;
        }
        return category;
    }

    public List<CategoryEntity> findRootCategories()
    {
        return findByNamedQuery( CategoryEntity.class, "CategoryEntity.findAllRootCategories" );
    }

    public List<CategoryEntity> findRootCategories( List<GroupKey> groupKeys )
    {
        List<String> groupKeysStr = GroupKey.convertToStringList( groupKeys );
        return findByNamedQuery( CategoryEntity.class, "CategoryEntity.findRootCategories", "groupKeys", groupKeysStr );
    }

    public EntityPageList<CategoryEntity> findAll( int index, int count )
    {
        return findPageList( CategoryEntity.class, "x.deleted = 0", index, count );
    }
}
