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
import java.util.Set;

import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * Aug 21, 2009
 */
public class RegionFactory
{
    public static Regions createRegionsForPage( PageTemplateEntity pageTemplate, MenuItemEntity menuItem )
    {
        Regions regions = new Regions();

        Map<String, Region> regionsInPageTemplateMapByName = doCreateRegionsAsMapByName( pageTemplate );
        for ( Region region : regionsInPageTemplateMapByName.values() )
        {
            region.makeWindowsOfTemplates( menuItem );
        }

        if ( menuItem.getPage() != null )
        {
            Set<PageWindowEntity> pageWindows = menuItem.getPage().getPageWindows();
            for ( PageWindowEntity pageWindow : pageWindows )
            {
                String regionName = pageWindow.getPageTemplateRegion().getName();
                Region region = regionsInPageTemplateMapByName.get( regionName );
                if ( region == null )
                {
                    // if region does not exist in page template we do not allow addition
                    continue;
                }
                else if ( region.originatesFrom( RegionOrigin.PAGETEMPLATE ) )
                {
                    region = region.cloneWithoutPortletWindows( RegionOrigin.PAGE );
                    regionsInPageTemplateMapByName.put( region.getName(), region );
                }
                PortletEntity portlet = pageWindow.getPortlet();

                Window window = new Window();
                window.setKey( new WindowKey( menuItem.getMenuItemKey(), portlet.getPortletKey() ) );
                window.setPortlet( portlet );
                window.setRegion( region );
                region.addWindow( window );
            }
        }

        for ( Region region : regionsInPageTemplateMapByName.values() )
        {
            regions.addRegion( region );
        }

        return regions;
    }

    public static Regions createRegionsOriginatingPageTemplate( PageTemplateEntity pageTemplate, MenuItemEntity menuItem )
    {
        Collection<Region> regionsOnPageTemplate = doCreateRegionsCollection( pageTemplate );
        Regions regions = new Regions();
        for ( Region region : regionsOnPageTemplate )
        {
            regions.addRegion( region );
            region.makeWindowsOfTemplates( menuItem );
        }
        return regions;
    }


    private static Map<String, Region> doCreateRegionsAsMapByName( final PageTemplateEntity pageTemplate )
    {
        if ( pageTemplate == null )
        {
            throw new NullPointerException( "pageTemplate cannot be null" );
        }

        Collection<Region> regions = doCreateRegionsCollection( pageTemplate );
        Map<String, Region> regionMapByName = new LinkedHashMap<String, Region>( regions.size() );
        for ( Region region : regions )
        {
            regionMapByName.put( region.getName(), region );
        }
        return regionMapByName;
    }

    private static Collection<Region> doCreateRegionsCollection( final PageTemplateEntity pageTemplate )
    {
        if ( pageTemplate == null )
        {
            throw new NullPointerException( "pageTemplate cannot be null" );
        }

        final Set<PageTemplateRegionEntity> pageTemplateRegions = pageTemplate.getPageTemplateRegions();

        List<Region> regions = new ArrayList<Region>( pageTemplateRegions.size() );

        for ( PageTemplateRegionEntity pagetTemplateRegion : pageTemplateRegions )
        {
            Region region = new Region( pagetTemplateRegion.getName(), pagetTemplateRegion.getSeparator(), pageTemplate.getPortlets() );
            regions.add( region );
        }

        return regions;
    }


}