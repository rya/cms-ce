/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.core.structure.portlet.PortletKey;

/**
 * May 13, 2009
 */
public class WindowKey
{
    private MenuItemKey menuItemKey;

    private PortletKey portletKey;

    private String asString;

    public WindowKey( MenuItemKey menuItemKey, PortletKey portletKey )
    {
        if ( menuItemKey == null )
        {
            throw new IllegalArgumentException( "menuItemKey cannot be null" );
        }
        if ( portletKey == null )
        {
            throw new IllegalArgumentException( "portletKey cannot be null" );
        }
        this.menuItemKey = menuItemKey;
        this.portletKey = portletKey;
        this.asString = menuItemKey + ":" + portletKey;
    }

    public WindowKey( String key )
    {
        if ( key == null )
        {
            throw new InvalidKeyException( "null", WindowKey.class );
        }

        if ( key.length() < 3 )
        {
            throw new InvalidKeyException( key, WindowKey.class );
        }

        int colonPos = key.indexOf( ":" );
        if ( colonPos < 0 )
        {
            throw new InvalidKeyException( key, WindowKey.class );
        }

        menuItemKey = new MenuItemKey( key.substring( 0, colonPos ) );
        portletKey = new PortletKey( key.substring( colonPos + 1, key.length() ) );
        asString = key;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public PortletKey getPortletKey()
    {
        return portletKey;
    }

    public String asString()
    {
        return asString;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        WindowKey windowKey = (WindowKey) o;

        if ( !asString.equals( windowKey.asString ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return asString.hashCode();
    }

    public String toString()
    {
        return asString;
    }
}
