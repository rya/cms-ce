/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.security.group.GroupKey;

public interface CategoryDao
    extends EntityDao<CategoryEntity>
{
    CategoryEntity findByKey( CategoryKey key );

    List<CategoryEntity> findRootCategories();

    List<CategoryEntity> findRootCategories( List<GroupKey> groupKeys );

    EntityPageList<CategoryEntity> findAll( int index, int count );
}
