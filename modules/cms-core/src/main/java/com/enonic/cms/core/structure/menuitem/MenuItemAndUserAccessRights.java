/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

/**
 * Dec 4, 2009
 */
public class MenuItemAndUserAccessRights
{
    private MenuItemEntity menuItem;

    private MenuItemAccumulatedAccessRights accessrightsForAnonymous;

    private MenuItemAccumulatedAccessRights accessRightsForUser;

    public MenuItemAndUserAccessRights( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public MenuItemAndUserAccessRights( MenuItemEntity menuItem, MenuItemAccumulatedAccessRights accessRightsForUser )
    {
        this.menuItem = menuItem;
        this.accessRightsForUser = accessRightsForUser;
    }

    public MenuItemAndUserAccessRights( MenuItemEntity menuItem, MenuItemAccumulatedAccessRights accessRightsForUser,
                                        MenuItemAccumulatedAccessRights accessrightsForAnonymous )
    {
        this.menuItem = menuItem;
        this.accessRightsForUser = accessRightsForUser;
        this.accessrightsForAnonymous = accessrightsForAnonymous;
    }

    public void setAccessrightsForAnonymous( MenuItemAccumulatedAccessRights accessrightsForAnonymous )
    {
        this.accessrightsForAnonymous = accessrightsForAnonymous;
    }

    public void setAccessRightsForUser( MenuItemAccumulatedAccessRights accessRightsForUser )
    {
        this.accessRightsForUser = accessRightsForUser;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public MenuItemAccumulatedAccessRights getAccessrightsForAnonymous()
    {
        return accessrightsForAnonymous;
    }

    public MenuItemAccumulatedAccessRights getAccessRightsForUser()
    {
        return accessRightsForUser;
    }
}