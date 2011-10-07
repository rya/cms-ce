/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * May 28, 2009
 */
public class Regions
{
    private Map<String, Region> regionMapByName = new LinkedHashMap<String, Region>();

    public int numberOfRegions()
    {
        return regionMapByName.size();
    }

    public void addRegion( Region region )
    {
        regionMapByName.put( region.getName(), region );
    }

    public Collection<Region> getRegions()
    {
        return regionMapByName.values();
    }

    public List<PortletEntity> getPortlets()
    {
        List<PortletEntity> portlets = new ArrayList<PortletEntity>();

        for ( Region region : regionMapByName.values() )
        {
            for ( Window window : region.getWindows() )
            {
                portlets.add( window.getPortlet() );
            }
        }

        return portlets;
    }

    public Region getRegion( String regionName )
    {
        return regionMapByName.get( regionName );
    }

    public Window getWindowByKey( WindowKey windowKey )
    {
        for ( Region region : regionMapByName.values() )
        {
            Window window = region.getWindow( windowKey );
            if ( window != null )
            {
                return window;
            }
        }
        return null;
    }
}
