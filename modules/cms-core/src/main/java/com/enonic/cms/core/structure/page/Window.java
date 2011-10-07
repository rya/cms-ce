/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * May 15, 2009
 */
public class Window
{
    private WindowKey key;

    private PortletEntity portlet;

    private Region region;

    public WindowKey getKey()
    {
        return key;
    }

    public void setKey( WindowKey key )
    {
        this.key = key;
    }

    public PortletEntity getPortlet()
    {
        return portlet;
    }

    public void setPortlet( PortletEntity portlet )
    {
        this.portlet = portlet;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion( Region region )
    {
        this.region = region;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "key = " ).append( key );
        str.append( ", portlet.key = " ).append( portlet.getPortletKey() );
        str.append( ", portlet.name = " ).append( portlet.getName() );
        return str.toString();
    }
}
