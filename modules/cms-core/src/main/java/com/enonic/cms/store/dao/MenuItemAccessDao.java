/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;

public interface MenuItemAccessDao
    extends EntityDao<MenuItemAccessEntity>
{
    void deleteByGroupKey( GroupKey key );
}
