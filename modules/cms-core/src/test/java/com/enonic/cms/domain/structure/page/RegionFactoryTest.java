/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.page;

import java.util.Date;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.*;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletKey;
import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import org.junit.Test;

import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;
import com.enonic.cms.core.structure.portlet.PortletKey;

import static org.junit.Assert.*;

/**
 * Aug 21, 2009
 */
public class RegionFactoryTest
{
    @Test
    public void createRegionsOriginatingPageTemplate()
    {
        // setup
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.addPageTemplateRegion( createPageTemplateRegion( 1, "north", pageTemplate ) );
        pageTemplate.addPageTemplateRegion( createPageTemplateRegion( 2, "center", pageTemplate ) );
        pageTemplate.addPageTemplateRegion( createPageTemplateRegion( 3, "west", pageTemplate ) );
        pageTemplate.addPageTemplateRegion( createPageTemplateRegion( 4, "east", pageTemplate ) );
        pageTemplate.addPageTemplateRegion( createPageTemplateRegion( 5, "south", pageTemplate ) );

        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "north", createPortlet( 1, "Portlet-1" ), pageTemplate ) );
        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "west", createPortlet( 2, "Portlet-2" ), pageTemplate ) );
        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "east", createPortlet( 3, "Portlet-3" ), pageTemplate ) );
        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "center", createPortlet( 4, "Portlet-4" ), pageTemplate ) );
        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "south", createPortlet( 5, "Portlet-5" ), pageTemplate ) );

        MenuItemEntity menuItem = createMenuItem( 1, "forsiden", null );

        // excercise
        Regions regions = RegionFactory.createRegionsOriginatingPageTemplate( pageTemplate, menuItem );

        // verify
        assertEquals( 5, regions.numberOfRegions() );
    }


    @Test
    public void pagePortletsOverridesAllTemplatePortlets()
    {
        // setup
        PageTemplateEntity pageTemplate = new PageTemplateEntity();

        PageTemplateRegionEntity region_leftColumn = createPageTemplateRegion( 1, "leftColumn", pageTemplate );
        PageTemplateRegionEntity region_mainColumn = createPageTemplateRegion( 2, "mainColumn", pageTemplate );

        pageTemplate.addPageTemplateRegion( region_leftColumn );
        pageTemplate.addPageTemplateRegion( region_mainColumn );

        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "leftColumn", createPortlet( 11, "Portlet-1" ), pageTemplate ) );
        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "mainColumn", createPortlet( 22, "Portlet-2" ), pageTemplate ) );

        PageEntity page = createPage( 1, pageTemplate );
        page.addPortletPlacement( createPagePortletWindow( page, region_leftColumn, createPortlet( 101, "Overriding-Portlet-1" ) ) );
        page.addPortletPlacement( createPagePortletWindow( page, region_mainColumn, createPortlet( 102, "Overriding-Portlet-2" ) ) );
        MenuItemEntity menuItem = createMenuItem( 123, "forsiden", page );

        // excercise
        Regions regions = RegionFactory.createRegionsForPage( pageTemplate, menuItem );

        // verify correct portlet windows in leftColumn region
        assertEquals( 2, regions.numberOfRegions() );

        Region actualLeftColumnRegion = regions.getRegion( "leftColumn" );
        assertNotNull( actualLeftColumnRegion );
        assertNotNull( actualLeftColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 101 ) ) ) );
        assertEquals( 1, actualLeftColumnRegion.numberOfWindows() );

        // verify correct portlet windows in mainColumn region
        Region actualMainColumnRegion = regions.getRegion( "mainColumn" );
        assertNotNull( actualMainColumnRegion );
        assertNotNull( "expected window missing",
                       actualMainColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 102 ) ) ) );
        assertEquals( 1, actualMainColumnRegion.numberOfWindows() );
    }

    @Test
    public void onePagePortletOverridesATemplatePortlet()
    {
        // setup
        PageTemplateEntity pageTemplate = new PageTemplateEntity();

        PageTemplateRegionEntity region_leftColumn = createPageTemplateRegion( 1, "leftColumn", pageTemplate );
        PageTemplateRegionEntity region_mainColumn = createPageTemplateRegion( 2, "mainColumn", pageTemplate );

        pageTemplate.addPageTemplateRegion( region_leftColumn );
        pageTemplate.addPageTemplateRegion( region_mainColumn );

        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "leftColumn", createPortlet( 11, "Portlet-1" ), pageTemplate ) );
        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "mainColumn", createPortlet( 22, "Portlet-2" ), pageTemplate ) );

        PageEntity page = createPage( 1, pageTemplate );
        page.addPortletPlacement( createPagePortletWindow( page, region_leftColumn, createPortlet( 101, "Overriding-Portlet-1" ) ) );
        MenuItemEntity menuItem = createMenuItem( 123, "forsiden", page );

        // excercise
        Regions regions = RegionFactory.createRegionsForPage( pageTemplate, menuItem );

        // verify correct portlet windows in leftColumn region
        assertEquals( 2, regions.numberOfRegions() );
        Region actualLeftColumnRegion = regions.getRegion( "leftColumn" );
        assertNotNull( actualLeftColumnRegion );
        assertNotNull( actualLeftColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 101 ) ) ) );
        assertNull( actualLeftColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 11 ) ) ) );

        // verify correct portlet windows in mainColumn region
        Region actualMainColumnRegion = regions.getRegion( "mainColumn" );
        assertNotNull( actualMainColumnRegion );
        assertNotNull( actualMainColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 22 ) ) ) );
    }

    @Test
    public void twoPagePortletOverridesATemplatePortlet()
    {
        // setup
        PageTemplateEntity pageTemplate = new PageTemplateEntity();

        PageTemplateRegionEntity region_leftColumn = createPageTemplateRegion( 1, "leftColumn", pageTemplate );

        pageTemplate.addPageTemplateRegion( region_leftColumn );

        pageTemplate.addPagetTemplatePortlet( createPageTemplatePortlet( "leftColumn", createPortlet( 11, "Portlet-1" ), pageTemplate ) );

        PageEntity page = createPage( 1, pageTemplate );
        page.addPortletPlacement( createPagePortletWindow( page, region_leftColumn, createPortlet( 101, "Overriding-Portlet-1" ) ) );
        page.addPortletPlacement( createPagePortletWindow( page, region_leftColumn, createPortlet( 102, "Overriding-Portlet-2" ) ) );
        MenuItemEntity menuItem = createMenuItem( 123, "forsiden", page );

        // excercise
        Regions regions = RegionFactory.createRegionsForPage(pageTemplate, menuItem);

        // verify correct portlet windows in leftColumn region
        assertEquals( 1, regions.numberOfRegions() );
        Region actualLeftColumnRegion = regions.getRegion( "leftColumn" );
        assertNotNull( actualLeftColumnRegion );
        assertEquals( 2, actualLeftColumnRegion.numberOfWindows() );
        assertNotNull( actualLeftColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 101 ) ) ) );
        assertNotNull( actualLeftColumnRegion.getWindow( new WindowKey( new MenuItemKey( 123 ), new PortletKey( 102 ) ) ) );

    }

    private PageWindowEntity createPagePortletWindow( PageEntity page, PageTemplateRegionEntity region, PortletEntity portlet )
    {
        PageWindowEntity pagePortletWindow = new PageWindowEntity();
        pagePortletWindow.setKey( new PageWindowKey( page.getKey(), portlet.getKey() ) );
        pagePortletWindow.setOrder( 0 );
        pagePortletWindow.setPage( page );
        pagePortletWindow.setPortlet( portlet );
        pagePortletWindow.setPageTemplateRegion( region );
        return pagePortletWindow;
    }

    private PageEntity createPage( int key, PageTemplateEntity pageTemplate )
    {
        PageEntity page = new PageEntity();
        page.setKey( key );
        page.setTemplate( pageTemplate );
        return page;
    }

    private MenuItemEntity createMenuItem( int key, String name, PageEntity page )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( key );
        menuItem.setName( name );
        menuItem.setPage( page );
        return menuItem;
    }

    private PortletEntity createPortlet( int key, String name )
    {
        PortletEntity portlet = new PortletEntity();
        portlet.setKey( key );
        portlet.setName( name );
        return portlet;
    }

    private PageTemplatePortletEntity createPageTemplatePortlet( String regionName, PortletEntity portlet, PageTemplateEntity pageTemplate )
    {
        PageTemplatePortletEntity i = new PageTemplatePortletEntity();
        i.setKey( new PageTemplatePortletKey( pageTemplate.getKey(), portlet.getKey() ) );
        i.setPortlet( portlet );
        i.setPageTemplate( pageTemplate );
        i.setPageTemplateRegion( findPageTemplateRegion( regionName, pageTemplate ) );
        i.setTimestamp( new Date() );
        return i;
    }

    private PageTemplateRegionEntity findPageTemplateRegion( String name, PageTemplateEntity pageTemplate )
    {
        for ( PageTemplateRegionEntity region : pageTemplate.getPageTemplateRegions() )
        {
            if ( name.equals( region.getName() ) )
            {
                return region;
            }
        }
        return null;
    }

    private PageTemplateRegionEntity createPageTemplateRegion( int key, String name, PageTemplateEntity pageTemplate )
    {
        PageTemplateRegionEntity i = new PageTemplateRegionEntity();
        i.setKey( key );
        i.setName( name );
        i.setMultiple( true );
        i.setOverride( true );
        i.setSeparator( "dummySeparator" );
        i.setPageTemplate( pageTemplate );
        return i;
    }
}
