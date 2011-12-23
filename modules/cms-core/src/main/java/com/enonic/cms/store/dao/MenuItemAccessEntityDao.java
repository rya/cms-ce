/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;
import org.springframework.stereotype.Repository;

@Repository("menuItemAccessDao")
public final class MenuItemAccessEntityDao
    extends AbstractBaseEntityDao<MenuItemAccessEntity>
    implements MenuItemAccessDao
{
    public void deleteByGroupKey( GroupKey groupKey )
    {
        deleteByNamedQuery( "MenuItemAccessEntity.deleteByGroupKey", "groupKey", groupKey );
    }
}
