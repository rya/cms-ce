/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.page.WindowKey;
import com.enonic.cms.core.structure.portlet.PortletKey;

import static com.enonic.cms.core.preferences.PreferenceScopeType.PAGE;
import static com.enonic.cms.core.preferences.PreferenceScopeType.SITE;
import static com.enonic.cms.core.preferences.PreferenceScopeType.WINDOW;

public class PortalInstanceKey
{
    private SiteKey siteKey;

    private MenuItemKey menuItemKey;

    private PortletKey portletKey;

    public static PortalInstanceKey createSite( SiteKey siteKey )
    {
        PortalInstanceKey key = new PortalInstanceKey();
        key.setSite( siteKey );
        return key;
    }

    public static PortalInstanceKey createPage( MenuItemKey menuItemKey )
    {
        PortalInstanceKey key = new PortalInstanceKey();
        key.setMenuItem( menuItemKey );
        return key;
    }

    public static PortalInstanceKey createWindow( WindowKey windowKey )
    {
        return createWindow( windowKey.getMenuItemKey(), windowKey.getPortletKey() );
    }

    public static PortalInstanceKey createWindow( MenuItemKey menuItemKey, PortletKey portletKey )
    {
        PortalInstanceKey key = new PortalInstanceKey();
        key.setWindow( menuItemKey, portletKey );
        return key;
    }

    private PortalInstanceKey()
    {
        // private
    }

    public void setSite( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public void setMenuItem( MenuItemKey menuItemKey )
    {
        this.menuItemKey = menuItemKey;
    }

    public void setWindow( MenuItemKey menuItemKey, PortletKey portletKey )
    {
        this.menuItemKey = menuItemKey;
        this.portletKey = portletKey;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public PortletKey getPortletKey()
    {
        return portletKey;
    }

    public boolean isMenuItem()
    {
        return !isWindow() && menuItemKey != null;
    }

    public boolean isWindow()
    {
        return portletKey != null && menuItemKey != null;
    }

    public WindowKey getWindowKey()
    {
        if ( !isWindow() )
        {
            return null;
        }

        return new WindowKey( menuItemKey, portletKey );
    }

    public String toString()
    {
        return resolveAsString();
    }

    private String resolveAsString()
    {
        if ( isWindow() )
        {
            return WINDOW + ":" + menuItemKey.toString() + ":" + portletKey.toString();
        }
        if ( isMenuItem() )
        {
            return PAGE + ":" + menuItemKey.toString();
        }
        if ( siteKey != null )
        {
            return SITE + ":" + siteKey.toString();
        }

        return null;

    }

}
