/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.structure.MenuItemXMLCreatorSetting;
import com.enonic.cms.core.structure.MenuItemXmlCreator;
import com.enonic.cms.core.structure.SiteXmlCreator;

import com.enonic.cms.domain.structure.DefaultSiteAccumulatedAccessRights;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.SiteProperties;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemAndUserAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * Nov 24, 2009
 */
public class MenuBrowseMenuItemsModel
{
    private SiteEntity site;

    private SiteProperties siteProperties;

    private DefaultSiteAccumulatedAccessRights userRightsForSite;

    private MenuItemEntity selectedMenuItem;

    private MenuItemAccumulatedAccessRights userRightsForSelectedMenuItem;

    private Collection<MenuItemEntity> selectedMenuItemPath;

    private MenuItemEntity parentToSelectedMenuItem;

    private MenuItemAccumulatedAccessRights userRightsForParentToSelectedMenuItem;

    private Collection<MenuItemAndUserAccessRights> menuItemsToList;

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setSiteProperties( SiteProperties siteProperties )
    {
        this.siteProperties = siteProperties;
    }

    public void setUserRightsForSite( DefaultSiteAccumulatedAccessRights value )
    {
        this.userRightsForSite = value;
    }

    public void setSelectedMenuItem( MenuItemEntity value )
    {
        this.selectedMenuItem = value;
    }

    public void setSelectedMenuItemPath( Collection<MenuItemEntity> value )
    {
        this.selectedMenuItemPath = value;
    }

    public void setUserRightsForSelectedMenuItem( MenuItemAccumulatedAccessRights value )
    {
        this.userRightsForSelectedMenuItem = value;
    }

    public void setParentToSelectedMenuItem( MenuItemEntity value )
    {
        this.parentToSelectedMenuItem = value;
    }

    public void setUserRightsForParentToSelectedMenuItem( MenuItemAccumulatedAccessRights value )
    {
        this.userRightsForParentToSelectedMenuItem = value;
    }

    public void setMenuItemsToList( Collection<MenuItemAndUserAccessRights> collection )
    {
        this.menuItemsToList = collection;
    }

    public Collection<MenuItemAndUserAccessRights> getMenuItemsToList()
    {
        return menuItemsToList;
    }

    XMLDocument toXML()
    {
        Element modelEl = new org.jdom.Element( "model" );

        modelEl.addContent( createSelectedMenuElement() );
        modelEl.addContent( createSelectedMenuItemElement() );
        modelEl.addContent( createParentToSelectedMenuItemElement() );
        modelEl.addContent( createSelectedMenuItemPathElement() );
        modelEl.addContent( createMenuItemsToListElement() );

        return XMLDocumentFactory.create( new Document( modelEl ) );
    }


    private Element createSelectedMenuElement()
    {
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( null );
        org.jdom.Element selectedMenuEl = siteXmlCreator.createMenuElement( site, siteProperties, userRightsForSite );
        return new org.jdom.Element( "selected-menu" ).addContent( selectedMenuEl );
    }

    private Element createSelectedMenuItemElement()
    {
        Element selectedMenuItemEl = new org.jdom.Element( "selected-menuitem" );

        if ( selectedMenuItem != null )
        {
            MenuItemXMLCreatorSetting miXmlCreatorSetting = new MenuItemXMLCreatorSetting();
            miXmlCreatorSetting.includeTypeSpecificXML = true;
            miXmlCreatorSetting.includeParents = false;
            miXmlCreatorSetting.includeChildren = false;

            MenuItemXmlCreator menuItemXmlCreator = new MenuItemXmlCreator( miXmlCreatorSetting, null );
            menuItemXmlCreator.setIncludeUserAccessRightsInfo( true );
            menuItemXmlCreator.setIncludePathInfo( true );
            Element menuItemEl = menuItemXmlCreator.createMenuItemElement( selectedMenuItem, userRightsForSelectedMenuItem );
            selectedMenuItemEl.addContent( menuItemEl );
        }

        return selectedMenuItemEl;
    }

    private Element createSelectedMenuItemPathElement()
    {
        Element selectedMenuItemPathEl = new Element( "selected-menuitem-path" );
        if ( selectedMenuItemPath != null )
        {
            for ( MenuItemEntity currMenuItem : selectedMenuItemPath )
            {
                MenuItemXMLCreatorSetting miPathXmlCreatorSetting = new MenuItemXMLCreatorSetting();
                miPathXmlCreatorSetting.includeTypeSpecificXML = false;
                miPathXmlCreatorSetting.includeParents = false;
                miPathXmlCreatorSetting.includeChildren = false;
                MenuItemXmlCreator menuItemPathXmlCreator = new MenuItemXmlCreator( miPathXmlCreatorSetting, null );
                Element currMenuItemEl = menuItemPathXmlCreator.createMenuItemElement( currMenuItem );
                selectedMenuItemPathEl.addContent( currMenuItemEl );
            }
        }
        return selectedMenuItemPathEl;
    }

    private Element createParentToSelectedMenuItemElement()
    {
        Element parentEl = new org.jdom.Element( "parent-to-selected-menuitem" );

        if ( parentToSelectedMenuItem != null )
        {
            MenuItemXMLCreatorSetting miXmlCreatorSetting = new MenuItemXMLCreatorSetting();
            miXmlCreatorSetting.includeTypeSpecificXML = true;
            miXmlCreatorSetting.includeParents = false;
            miXmlCreatorSetting.includeChildren = false;
            MenuItemXmlCreator menuItemXmlCreator = new MenuItemXmlCreator( miXmlCreatorSetting, null );
            menuItemXmlCreator.setIncludeUserAccessRightsInfo( true );
            Element menuItemEl =
                menuItemXmlCreator.createMenuItemElement( parentToSelectedMenuItem, userRightsForParentToSelectedMenuItem );
            parentEl.addContent( menuItemEl );
        }

        return parentEl;
    }

    private Element createMenuItemsToListElement()
    {
        Element menuItemsToListEl = new org.jdom.Element( "menuitems-to-list" );

        MenuItemXMLCreatorSetting miXmlCreatorSetting = new MenuItemXMLCreatorSetting();
        miXmlCreatorSetting.includeTypeSpecificXML = true;
        miXmlCreatorSetting.includeParents = false;
        miXmlCreatorSetting.includeChildren = false;
        miXmlCreatorSetting.user = null;

        MenuItemXmlCreator menuItemXmlCreator = new MenuItemXmlCreator( miXmlCreatorSetting, null );
        menuItemXmlCreator.setIncludePageTemplateName( true );
        menuItemXmlCreator.setIncludeAnonynousReadInfo( true );
        menuItemXmlCreator.setIncludeUserAccessRightsInfo( true );
        menuItemXmlCreator.setIncludePathInfo( true );
        for ( MenuItemAndUserAccessRights menuItemAndUserAccessRights : menuItemsToList )
        {
            final MenuItemEntity menuItem = menuItemAndUserAccessRights.getMenuItem();
            final MenuItemAccumulatedAccessRights accessRightsForAnonymousUser = menuItemAndUserAccessRights.getAccessrightsForAnonymous();
            final MenuItemAccumulatedAccessRights accessRightsForRunningUser = menuItemAndUserAccessRights.getAccessRightsForUser();
            menuItemsToListEl.addContent(
                menuItemXmlCreator.createMenuItemElement( menuItem, accessRightsForRunningUser, accessRightsForAnonymousUser ) );
        }
        return menuItemsToListEl;
    }
}
