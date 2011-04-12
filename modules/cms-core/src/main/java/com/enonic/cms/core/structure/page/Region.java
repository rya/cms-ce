/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * May 15, 2009
 */
public class Region
{
    private String name;

    private String separator;

    private RegionOrigin origin;

    private LinkedHashMap<String, PageTemplatePortletEntity> pageTemplatePortlets = new LinkedHashMap<String, PageTemplatePortletEntity>();

    private LinkedHashMap<WindowKey, Window> windows = new LinkedHashMap<WindowKey, Window>();

    public Region( String name, String separator, Collection<PageTemplatePortletEntity> pageTemplatePortletWindows )
    {
        this.name = name;
        this.separator = separator;
        this.origin = RegionOrigin.PAGETEMPLATE;

        for ( PageTemplatePortletEntity pageTemplatePortletWindow : pageTemplatePortletWindows )
        {
            if ( name.equals( pageTemplatePortletWindow.getPageTemplateRegion().getName() ) )
            {
                final PortletEntity portlet = pageTemplatePortletWindow.getPortlet();
                pageTemplatePortlets.put( portlet.getName(), pageTemplatePortletWindow );
            }
        }
    }

    public Region( String name, String separator, RegionOrigin origin )
    {
        this.name = name;
        this.separator = separator;
        this.origin = origin;
    }

    @Override
    public String toString()
    {
        return "Region{ name='" + name + "\' separator='" + separator + "\'}";
    }

    public String getName()
    {
        return name;
    }

    public String getSeparator()
    {
        return separator;
    }

    public RegionOrigin getOrigin()
    {
        return origin;
    }

    public void addWindow( Window value )
    {
        windows.put( value.getKey(), value );
    }

    public Collection<Window> getWindows()
    {
        return Collections.unmodifiableCollection( windows.values() );
    }

    protected void makeWindowsOfTemplates( MenuItemEntity menuItem )
    {
        for ( PageTemplatePortletEntity pageTemplatePortlet : pageTemplatePortlets.values() )
        {
            WindowKey key = new WindowKey( menuItem.getMenuItemKey(), pageTemplatePortlet.getPortlet().getPortletKey() );
            Window window = new Window();
            window.setKey( key );
            window.setPortlet( pageTemplatePortlet.getPortlet() );
            window.setRegion( this );
            windows.put( key, window );
        }
    }

    public boolean originatesFrom( RegionOrigin givenRegion )
    {
        return this.origin.equals( givenRegion );
    }

    public Region cloneWithoutPortletWindows( RegionOrigin origin )
    {
        Region region = new Region( getName(), getSeparator(), origin );
        for ( PageTemplatePortletEntity e : region.pageTemplatePortlets.values() )
        {
            region.addPageTemplatePortlet( e );
        }

        return region;
    }

    private void addPageTemplatePortlet( PageTemplatePortletEntity value )
    {
        PortletEntity portlet = value.getPortlet();
        pageTemplatePortlets.put( portlet.getName(), value );
    }

    public int numberOfWindows()
    {
        return windows.size();
    }

    public Window getWindow( WindowKey windowKey )
    {
        return windows.get( windowKey );
    }

    public Collection<PageTemplatePortletEntity> getPageTemplatePortlets()
    {
        return pageTemplatePortlets.values();
    }
}
