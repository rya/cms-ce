/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import java.io.Serializable;
import java.util.Locale;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class PageCacheKey
    implements Serializable
{
    private String userKey;

    private MenuItemKey menuItemKey;

    private String queryString;

    private String deviceClass;

    private Locale locale;

    public PageCacheKey()
    {
    }

    public void setUserKey( String userKey )
    {
        this.userKey = userKey;
    }

    public void setMenuItemKey( MenuItemKey menuItemKey )
    {
        this.menuItemKey = menuItemKey;
    }

    public void setQueryString( String queryString )
    {
        this.queryString = queryString;
    }

    public void setDeviceClass( String deviceClass )
    {
        this.deviceClass = deviceClass;
    }

    public void setLocale( Locale value )
    {
        this.locale = value;
    }

    public String getUserKey()
    {
        return userKey;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public String getDeviceClass()
    {
        return deviceClass;
    }

    public Locale getLocale()
    {
        return locale;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageCacheKey ) )
        {
            return false;
        }

        PageCacheKey that = (PageCacheKey) o;

        EqualsBuilder eb = new EqualsBuilder();
        eb.append( menuItemKey, that.menuItemKey );
        eb.append( queryString, that.queryString );
        eb.append( userKey, that.userKey );
        eb.append( deviceClass, that.deviceClass );
        eb.append( locale, that.locale );

        return eb.isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 659, 347 ).append( menuItemKey ).append( queryString ).append( userKey ).append( deviceClass ).append(
            locale ).toHashCode();
    }

    public String toString()
    {
        return new StringBuffer().append( "menuItemKey = " ).append( menuItemKey ).append( ", queryString = " ).append(
            queryString ).append( ", userKey = " ).append( userKey ).append( ", deviceClass = " ).append( deviceClass ).append(
            ", locale = " ).append( locale ).toString();
    }
}