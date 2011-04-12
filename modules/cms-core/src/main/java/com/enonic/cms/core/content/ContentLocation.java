/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.Assert;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;


public class ContentLocation
{
    private ContentLocationType type;

    private ContentEntity content;

    private SiteEntity site;

    private String siteName;

    private MenuItemKey menuItemKey;

    private String menuItemName;

    private MenuItemEntity menuItem;

    private String menuItemPathAsString;

    private boolean isApproved;

    private boolean isUserDefinedSectionHome;


    public static ContentLocation createMenuItemLocation( ContentEntity content, MenuItemEntity menuItem )
    {
        ContentLocation location = new ContentLocation( ContentLocationType.MENUITEM, content, menuItem );
        return location;
    }

    public static ContentLocation createSectionHomeLocation( ContentEntity content, MenuItemEntity menuItem )
    {
        ContentLocation location = new ContentLocation( ContentLocationType.SECTION_HOME, content, menuItem );
        location.isUserDefinedSectionHome = true;
        return location;
    }

    public static ContentLocation createSectionLocation( ContentEntity content, MenuItemEntity menuItem, boolean isApprovedInSection )
    {
        ContentLocation location = new ContentLocation( ContentLocationType.SECTION, content, menuItem, isApprovedInSection );
        return location;
    }

    public static ContentLocation createSectionAndSectionHomeLocation( ContentEntity content, MenuItemEntity menuItem,
                                                                       boolean isApprovedInSection )
    {
        ContentLocation location =
            new ContentLocation( ContentLocationType.SECTION_AND_SECTION_HOME, content, menuItem, isApprovedInSection );
        location.isUserDefinedSectionHome = true;
        return location;
    }

    private ContentLocation( ContentLocationType type, ContentEntity content, MenuItemEntity menuItem )
    {
        this( type, content, menuItem, false );
    }

    private ContentLocation( ContentLocationType type, ContentEntity content, MenuItemEntity menuItem, boolean isApprovedInSection )
    {
        Assert.notNull( type );
        Assert.notNull( content );
        Assert.notNull( menuItem );

        this.type = type;
        this.menuItemKey = menuItem.getMenuItemKey();
        this.menuItemName = menuItem.getName();
        this.content = content;
        this.menuItem = menuItem;
        this.site = menuItem.getSite();
        this.siteName = this.site.getName();

        this.menuItemPathAsString = this.menuItem.getPath().toString();

        this.isApproved = isApprovedInSection;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public MenuItemKey getMenuItemKey()
    {
        return menuItemKey;
    }

    public String getMenuItemName()
    {
        return menuItemName;
    }

    public ContentLocationType getType()
    {
        return type;
    }

    public boolean isInSection()
    {
        return type == ContentLocationType.SECTION || type == ContentLocationType.SECTION_AND_SECTION_HOME;
    }

    public boolean isHomeButNotInSection()
    {
        return type == ContentLocationType.SECTION_HOME;
    }

    public boolean isOnMenuItem()
    {
        return type == ContentLocationType.MENUITEM;
    }

    public boolean isInSectionOrSectionHome()
    {
        return type == ContentLocationType.SECTION || type == ContentLocationType.SECTION_AND_SECTION_HOME ||
            type == ContentLocationType.SECTION_HOME;
    }

    public String getMenuItemPathAsString()
    {
        return menuItemPathAsString;
    }

    public SiteKey getSiteKey()
    {
        return site.getKey();
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public boolean isApproved()
    {
        return isApproved;
    }

    public boolean isUserDefinedSectionHome()
    {
        return isUserDefinedSectionHome;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ContentLocation that = (ContentLocation) o;

        if ( !menuItem.equals( that.menuItem ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return menuItem.hashCode();
    }

    public String toString()
    {
        ToStringBuilder s = new ToStringBuilder( this );
        s.append( "menuItemKey", menuItemKey );
        s.append( "type", type );
        return s.toString();
    }


    public boolean isLocationFor( MenuItemEntity menuItem )
    {
        return this.menuItem.equals( menuItem );
    }
}
