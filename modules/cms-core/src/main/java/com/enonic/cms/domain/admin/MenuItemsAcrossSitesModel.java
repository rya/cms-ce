/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Oct 1, 2009
 */
public class MenuItemsAcrossSitesModel
{
    private Map<SiteEntity, List<MenuItemEntity>> sites = new LinkedHashMap<SiteEntity, List<MenuItemEntity>>();

    public void addMenuItems( Collection<MenuItemEntity> menuItems )
    {
        for ( MenuItemEntity menuItem : menuItems )
        {
            addMenuItem( menuItem );
        }
    }

    public void addMenuItem( MenuItemEntity menuItem )
    {
        SiteEntity site = menuItem.getSite();
        List<MenuItemEntity> menuItems = sites.get( site );
        if ( menuItems == null )
        {
            menuItems = new ArrayList<MenuItemEntity>();
            sites.put( site, menuItems );
        }
        menuItems.add( menuItem );
    }

    public Map<SiteEntity, List<MenuItemEntity>> getMap()
    {
        return sites;
    }
}
