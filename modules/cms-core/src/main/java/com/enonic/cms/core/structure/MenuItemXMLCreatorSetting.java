/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

public class MenuItemXMLCreatorSetting
{

    /**
     * To include hidden menu items or not.
     */
    public boolean includeHiddenMenuItems = false;

    /**
     * Create only menu items readable by this user.
     */
    public UserEntity user;

    /**
     * Marks the selected menu item as active and marks its path to root.
     */
    public MenuItemEntity activeMenuItem;

    /**
     * How many levels to descend. Zero gives all levels.
     */
    public int menuItemLevels = 0;

    /**
     * At what level to start including menu items from a branch. Level 0 is the top.
     */
    public int branchStartLevel = 0;

    /**
     * Decides if the breadcrumbspath is to be included in the XML.
     */
    public boolean includeParents = false;

    /**
     * Includes a larger set of attributes for the document and data nodes.
     */
    public boolean includeTypeSpecificXML = false;

    public boolean includeDocumentElement = false;

    /**
     * If the the menuitem's menuitems element shall be included.
     */
    public boolean includeChildren = true;

    public static MenuItemXMLCreatorSetting createFrom( SiteXmlCreator siteXmlCreator )
    {

        MenuItemXMLCreatorSetting newSetting = new MenuItemXMLCreatorSetting();
        newSetting.includeHiddenMenuItems = siteXmlCreator.includeHiddenMenuItems();
        newSetting.user = siteXmlCreator.getUser();
        newSetting.activeMenuItem = siteXmlCreator.getActiveMenuItem();
        newSetting.menuItemLevels = siteXmlCreator.getMenuItemLevels();
        newSetting.branchStartLevel = siteXmlCreator.getBranchStartLevel();
        return newSetting;
    }

}
