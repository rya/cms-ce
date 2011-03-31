/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax.dto;

public class PreferenceDto
{

    String scope;

    String key;

    String path;

    String value;

    String siteName;

    String menuItemPath;

    String portletName;


    public String getScope()
    {
        return scope;
    }

    public void setScope( String scope )
    {
        this.scope = scope;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName( String siteName )
    {
        this.siteName = siteName;
    }

    public String getMenuItemPath()
    {
        return menuItemPath;
    }

    public void setMenuItemPath( String menuItemPath )
    {
        this.menuItemPath = menuItemPath;
    }

    public String getPortletName()
    {
        return portletName;
    }

    public void setPortletName( String portletName )
    {
        this.portletName = portletName;
    }

}
