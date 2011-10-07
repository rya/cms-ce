/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class MenuItemContentEntity
    implements Serializable
{
    private MenuItemContentKey key;

    public MenuItemContentKey getKey()
    {
        return key;
    }

    public void setKey( MenuItemContentKey key )
    {
        this.key = key;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MenuItemContentEntity ) )
        {
            return false;
        }

        MenuItemContentEntity that = (MenuItemContentEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 763, 515 ).append( key ).toHashCode();
    }
}