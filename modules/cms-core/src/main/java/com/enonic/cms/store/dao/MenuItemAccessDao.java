/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessKey;

/**
 * Created by IntelliJ IDEA. User: jvs Date: Nov 8, 2008 Time: 4:26:47 PM To change this template use File | Settings | File Templates.
 */
public interface MenuItemAccessDao
    extends EntityDao<MenuItemAccessEntity>
{
    /**
     * Find all menu items access objects.
     */
    Collection<MenuItemAccessEntity> findAll();

    /**
     * Find a single menu item access entity.
     */
    MenuItemAccessEntity find( MenuItemAccessKey key );

    void deleteByGroupKey( GroupKey key );

    List<MenuItemAccessEntity> findByMenuItemKey( int key );
}
