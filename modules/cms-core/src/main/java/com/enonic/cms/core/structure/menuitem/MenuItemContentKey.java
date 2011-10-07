/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.ContentKey;

public class MenuItemContentKey
    implements Serializable
{
    private int menuItemKey;

    private ContentKey contentKey;

    public MenuItemContentKey()
    {
    }

    public MenuItemContentKey( int menuItemKey, ContentKey contentKey )
    {
        this.menuItemKey = menuItemKey;
        this.contentKey = contentKey;
    }

    public int getMenuItemKey()
    {
        return menuItemKey;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setMenuItemKey( int menuItemKey )
    {
        this.menuItemKey = menuItemKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MenuItemContentKey ) )
        {
            return false;
        }

        MenuItemContentKey that = (MenuItemContentKey) o;

        if ( !( contentKey.equals( that.getContentKey() ) ) )
        {
            return false;
        }
        if ( menuItemKey != that.getMenuItemKey() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 551, 729 ).append( contentKey ).append( menuItemKey ).toHashCode();
    }
}