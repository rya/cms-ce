/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.structure.menuitem.MenuItemContentKey;
import com.enonic.cms.core.structure.menuitem.MenuItemContentEntity;


public interface MenuItemContentDao
    extends EntityDao<MenuItemContentEntity>
{
    MenuItemContentEntity findByKey( MenuItemContentKey key );
}
