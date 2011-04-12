/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessKey;
import com.enonic.cms.core.security.group.GroupKey;

public class MenuItemAccessEntityDao
    extends AbstractBaseEntityDao<MenuItemAccessEntity>
    implements MenuItemAccessDao
{
    public Collection<MenuItemAccessEntity> findAll()
    {
        return findByNamedQuery( MenuItemAccessEntity.class, "MenuItemAccessEntity.findAll" );
    }

    public MenuItemAccessEntity find( MenuItemAccessKey key )
    {
        return get( MenuItemAccessEntity.class, key );
    }

    public void deleteByGroupKey( GroupKey groupKey )
    {
        deleteByNamedQuery( "MenuItemAccessEntity.deleteByGroupKey", "groupKey", groupKey );
    }

    public List<MenuItemAccessEntity> findByMenuItemKey( int key )
    {
        return findByNamedQuery( MenuItemAccessEntity.class, "MenuItemAccessEntity.findByMenuItemKey", "menuItemKey", key );
    }
}
