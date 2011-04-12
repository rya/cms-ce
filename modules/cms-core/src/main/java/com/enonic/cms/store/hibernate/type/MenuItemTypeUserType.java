/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.structure.menuitem.MenuItemType;

public class MenuItemTypeUserType
    extends AbstractIntegerBasedUserType<MenuItemType>
{
    public MenuItemTypeUserType()
    {
        super( MenuItemType.class );
    }

    public MenuItemType get( int value )
    {
        return MenuItemType.get(value);
    }

    public Integer getIntegerValue( MenuItemType value )
    {
        return value.getKey();
    }

    public boolean isMutable()
    {
        return false;
    }
}
