/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import com.enonic.cms.core.security.group.GroupKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MenuItemAccessKey
    implements Serializable
{
    private int menuItemKey;

    private GroupKey groupKey;

    public MenuItemAccessKey()
    {
    }

    public MenuItemAccessKey( int menuItemKey, GroupKey groupKey )
    {
        this.menuItemKey = menuItemKey;
        this.groupKey = groupKey;
    }

    public int getMenuItemKey()
    {
        return menuItemKey;
    }

    public GroupKey getGroupKey()
    {
        return groupKey;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MenuItemAccessKey ) )
        {
            return false;
        }

        MenuItemAccessKey that = (MenuItemAccessKey) o;

        if ( menuItemKey != that.getMenuItemKey() )
        {
            return false;
        }
        if ( !groupKey.equals( that.getGroupKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 453, 335 ).append( menuItemKey ).append( groupKey ).toHashCode();
    }
}
