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

public class WindowCacheKey
    implements Serializable
{

    private String userKey;

    private MenuItemKey menuItemKey;

    private int portletKey;

    private String queryString;

    private String paramsString;

    private String deviceClass;

    private Locale locale;

    public WindowCacheKey()
    {
    }

    public WindowCacheKey( String userKey, MenuItemKey menuItemKey, int portletKey, String queryString, String paramsString,
                           String deviceClass, Locale locale )
    {
        this.userKey = userKey;
        this.menuItemKey = menuItemKey;
        this.portletKey = portletKey;
        this.queryString = queryString;
        this.paramsString = paramsString;
        this.deviceClass = deviceClass;
        this.locale = locale;
    }

    public void setUserKey( String value )
    {
        this.userKey = value;
    }

    public void setMenuItemKey( MenuItemKey value )
    {
        this.menuItemKey = value;
    }

    public void setPortletKey( int value )
    {
        this.portletKey = value;
    }

    public void setQueryString( String value )
    {
        this.queryString = value;
    }

    public void setParamsString( String value )
    {
        this.paramsString = value;
    }

    public void setDeviceClass( String value )
    {
        this.deviceClass = value;
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

    public int getPortletKey()
    {
        return portletKey;
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
        if ( !( o instanceof WindowCacheKey ) )
        {
            return false;
        }

        WindowCacheKey that = (WindowCacheKey) o;

        EqualsBuilder equalsBuilder = new EqualsBuilder();
        //equalsBuilder.appendSuper( super.equals( that ) );
        equalsBuilder.append( portletKey, that.portletKey );
        equalsBuilder.append( menuItemKey, that.menuItemKey );
        equalsBuilder.append( paramsString, that.paramsString );
        equalsBuilder.append( queryString, that.queryString );
        equalsBuilder.append( userKey, that.userKey );
        equalsBuilder.append( deviceClass, that.deviceClass );
        equalsBuilder.append( locale, that.locale );
        return equalsBuilder.isEquals();
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 543, 371 ).append( userKey ).append( queryString ).append( menuItemKey ).append( portletKey ).append(
            paramsString ).append( deviceClass ).toHashCode();
    }

    @Override
    public String toString()
    {
        return "WindowCacheKey{" + "userKey='" + userKey + '\'' + ", menuItemKey=" + menuItemKey + ", portletKey=" + portletKey +
            ", queryString='" + queryString + '\'' + ", paramsString='" + paramsString + '\'' + ", deviceClass='" + deviceClass + '\'' +
            ", resolvedLocale=" + locale + '\'' + '}';
    }
}
