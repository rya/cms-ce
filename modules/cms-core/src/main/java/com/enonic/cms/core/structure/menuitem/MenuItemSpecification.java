/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.core.structure.page.PageSpecification;
import com.enonic.cms.domain.SiteKey;

/**
 * Sep 30, 2009
 */
public class MenuItemSpecification
{
    private SiteKey siteKey;

    private MenuItemType type;

    private MenuItemEntity menuItemShortcut;

    private PageSpecification pageSpecification;

    private String menuItemName;

    private MenuItemKey parentKey;

    private Boolean rootLevelOnly;

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void setSiteKey( final SiteKey value )
    {
        siteKey = value;
    }

    public MenuItemType getType()
    {
        return type;
    }

    public void setType( MenuItemType type )
    {
        this.type = type;
    }

    public MenuItemEntity getMenuItemShortcut()
    {
        return menuItemShortcut;
    }

    public void setMenuItemShortcut( MenuItemEntity menuItemShortcut )
    {
        this.menuItemShortcut = menuItemShortcut;
    }

    public PageSpecification getPageSpecification()
    {
        return pageSpecification;
    }

    public void setPageSpecification( PageSpecification pageSpecification )
    {
        this.pageSpecification = pageSpecification;
    }

    public String getMenuItemName()
    {
        return menuItemName;
    }

    public void setMenuItemName( String menuItemName )
    {
        this.menuItemName = menuItemName;
    }

    public MenuItemKey getParentKey()
    {
        return parentKey;
    }

    public void setParentKey( MenuItemKey parentKey )
    {
        this.parentKey = parentKey;
    }

    public Boolean getRootLevelOnly()
    {
        return rootLevelOnly;
    }

    public void setRootLevelOnly( Boolean rootLevelOnly )
    {
        this.rootLevelOnly = rootLevelOnly;
    }
}
