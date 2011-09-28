/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.domain.content.category.CategoryAccessEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import org.springframework.stereotype.Repository;

@Repository("categoryAccessDao")
final class CategoryAccessEntityDao
    extends AbstractBaseEntityDao<CategoryAccessEntity>
    implements CategoryAccessDao
{
    public void deleteByGroupKey( GroupKey groupKey )
    {
        deleteByNamedQuery( "CategoryAccessEntity.deleteByGroupKey", "groupKey", groupKey );
    }
}
