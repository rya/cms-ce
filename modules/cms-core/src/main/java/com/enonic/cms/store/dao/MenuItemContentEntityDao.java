/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.structure.menuitem.MenuItemContentEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemContentKey;
import org.springframework.stereotype.Repository;

@Repository("menuItemContentDao")
public final class MenuItemContentEntityDao
    extends AbstractBaseEntityDao<MenuItemContentEntity>
    implements MenuItemContentDao
{
    public MenuItemContentEntity findByKey( MenuItemContentKey key )
    {
        return get( MenuItemContentEntity.class, key );
    }
}
