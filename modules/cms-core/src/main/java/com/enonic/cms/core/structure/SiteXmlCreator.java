/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.structure.access.MenuItemAccessResolver;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserXmlCreator;
import com.enonic.cms.domain.structure.DefaultSiteAccumulatedAccessRights;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.SiteProperties;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

public class SiteXmlCreator
{
    private DefaultSiteAccessRightsAccumulatedXmlCreator accessRightsAccumulatedXmlCreator =
        new DefaultSiteAccessRightsAccumulatedXmlCreator();

    private MenuItemXmlCreator menuItemXmlCreator;

    private MenuItemAccessResolver menuItemAccessResolver;

    private boolean includeHiddenMenuItems = false;

    private UserEntity user;

    private MenuItemEntity activeMenuItem;

    private boolean includeMenuItems = true;

    private MenuItemEntity menuItemInBranch;

    private int menuItemLevels = 0;

    private int branchStartLevel = 0;

    private boolean includeTopLevel = false;

    private boolean includeSiteURLInfo = false;

    private boolean includeDeviceClassResolverInfo = false;

    private boolean includeLocalizationInfo = false;

    private boolean includePathToPublicHome = false;

    private boolean includePathToHome = false;

    private boolean includeRunAs = false;

    private boolean userXmlAsAdminConsoleStyle = true;

    public SiteXmlCreator( MenuItemAccessResolver menuItemAccessResolver )
    {
        this.menuItemAccessResolver = menuItemAccessResolver;
    }

    public Element createSitesElement( Iterable<SiteEntity> sites, Map<SiteKey, SiteProperties> sitesPropertiesMap, String rootElementName )
    {
        return doCreateSitesElement( sites, sitesPropertiesMap, rootElementName );
    }

    private Element doCreateSitesElement( Iterable<SiteEntity> sites, Map<SiteKey, SiteProperties> sitesPropertiesMap,
                                          String rootElementName )
    {
        Element rootEl = new Element( rootElementName );
        for ( SiteEntity site : sites )
        {
            rootEl.addContent( doCreateMenuElement( site, sitesPropertiesMap.get( site.getKey() ), "site" ) );
        }
        return rootEl;
    }

    public Element createMenuElement( SiteEntity site, SiteProperties siteProperties,
                                      DefaultSiteAccumulatedAccessRights accessRightsAccumulated )
    {
        includeMenuItems = false;
        XMLBuilder xmlDoc = new XMLBuilder( "menus" );
        createMenuElement( site, siteProperties, xmlDoc, accessRightsAccumulated );
        return (Element) xmlDoc.getRootElement().getChild( "menu" ).detach();
    }

    public XMLDocument createLegacyGetMenuData( SiteEntity site, SiteProperties siteProperties )
    {
        includeMenuItems = false;
        menuItemXmlCreator = new MenuItemXmlCreator( MenuItemXMLCreatorSetting.createFrom( this ), menuItemAccessResolver );
        XMLBuilder xmlDoc = new XMLBuilder( "menus" );
        createMenuElement( site, siteProperties, xmlDoc, null );
        return xmlDoc.getDocument();
    }

    public XMLDocument createLegacyGetMenu( SiteEntity site, SiteProperties siteProperties )
    {

        menuItemXmlCreator = new MenuItemXmlCreator( MenuItemXMLCreatorSetting.createFrom( this ), menuItemAccessResolver );
        XMLBuilder xmlDoc = new XMLBuilder( "menus" );

        if ( site != null )
        {
            createMenuElement( site, siteProperties, xmlDoc, null );
        }

        return xmlDoc.getDocument();
    }

    public XMLDocument createLegacyGetMenus( Collection<SiteEntity> sites, Map<SiteKey, SiteProperties> sitesPropertiesMap )
    {

        XMLBuilder xmlDoc = new XMLBuilder( "menus" );

        for ( SiteEntity site : sites )
        {
            createMenuElement( site, sitesPropertiesMap.get( site.getKey() ), xmlDoc, null );
        }

        return xmlDoc.getDocument();
    }

    public static XMLDocument createEmptyMenuBranch()
    {
        return XMLDocumentFactory.create( new Document( new Element( "menuitems" ) ) );
    }

    public static XMLDocument createEmptyMenus()
    {
        return XMLDocumentFactory.create( new Document( new Element( "menus" ) ) );
    }

    public XMLDocument createLegacyGetMenuBranch( SiteEntity siteEntity )
    {
        if ( siteEntity == null )
        {
            return createEmptyMenuBranch();
        }

        if ( getMenuItemInBranch() == null )
        {
            return createMenuItemsWithErrorMessage( "Menu item not specified" );
        }
        menuItemXmlCreator = new MenuItemXmlCreator( MenuItemXMLCreatorSetting.createFrom( this ), menuItemAccessResolver );
        MenuItemEntity menuItemInBranch = getMenuItemInBranch();
        if ( menuItemInBranch == null )
        {
            return createMenuItemsWithErrorMessage( "Menu item " + getMenuItemInBranch() + " not found in site " + siteEntity.getKey() );
        }

        int menuItemInBranchLevel = menuItemInBranch.getLevel();
        MenuItemEntity topLevelMenuItem;
        XMLBuilder xmlDoc = new XMLBuilder();
        if ( getBranchStartLevel() <= 0 )
        {

            topLevelMenuItem = menuItemInBranch.getTopLevelMenuItem();
            if ( includeTopLevel() )
            {
                createMenuItemsElementWithTopLevel( xmlDoc, siteEntity, topLevelMenuItem );
            }
            else
            {
                createMenuItemsElement( xmlDoc, topLevelMenuItem, true );
            }
        }
        else if ( getBranchStartLevel() > menuItemInBranchLevel + 1 )
        {

            return createMenuItemsWithErrorMessage(
                "Start level (" + getBranchStartLevel() + ") cannot be more than one level below the level of the given menuItem (" +
                    menuItemInBranchLevel + ")" );
        }
        else if ( getBranchStartLevel() == menuItemInBranchLevel + 1 )
        {

            topLevelMenuItem = menuItemInBranch;
            createMenuItemsElement( xmlDoc, topLevelMenuItem.getChildren(), false );
        }
        else if ( getBranchStartLevel() == menuItemInBranchLevel )
        {

            topLevelMenuItem = menuItemInBranch.getParent();
            createMenuItemsElement( xmlDoc, topLevelMenuItem.getChildren(), false );
        }
        else if ( getBranchStartLevel() < menuItemInBranchLevel )
        {

            topLevelMenuItem = menuItemInBranch.getParentAtLevel( getBranchStartLevel() );
            topLevelMenuItem = topLevelMenuItem.getParent();
            createMenuItemsElement( xmlDoc, topLevelMenuItem.getChildren(), false );
        }
        else
        {
            return createMenuItemsWithErrorMessage( "I am not sure what to respond to this" );
        }

        return xmlDoc.getDocument();
    }

    public XMLDocument createLegacyGetSubMenu( SiteEntity siteEntity )
    {

        if ( siteEntity == null )
        {
            return createEmptyMenuBranch();
        }

        if ( getMenuItemInBranch() == null )
        {
            return createMenuItemsWithErrorMessage( "Menu item not specified" );
        }
        if ( getMenuItemLevels() > 0 )
        {
            // on GetSubMenu-calls shall x menuItemLevels be interpreted as x+1
            setMenuItemLevels( getMenuItemLevels() + 1 );
        }
        menuItemXmlCreator = new MenuItemXmlCreator( MenuItemXMLCreatorSetting.createFrom( this ), menuItemAccessResolver );
        MenuItemEntity menuItemInBranch = getMenuItemInBranch();
        if ( menuItemInBranch == null )
        {
            return createMenuItemsWithErrorMessage( "Menu item " + getMenuItemInBranch() + " not found in site " + siteEntity.getKey() );
        }

        boolean isTop = menuItemInBranch.isAtTopLevel();
        XMLBuilder xmlDoc = new XMLBuilder();
        createMenuItemsElement( xmlDoc, menuItemInBranch, isTop );
        return xmlDoc.getDocument();
    }

    private void createMenuItemsElementWithTopLevel( XMLBuilder xmlDoc, SiteEntity site, MenuItemEntity rootMenuItem )
    {

        xmlDoc.startElement( "menuitems" );
        xmlDoc.setAttribute( "istop", "yes" );

        if ( rootMenuItem != null )
        {

            for ( MenuItemEntity menuItem : site.getTopMenuItems() )
            {
                if ( menuItemXmlCreator.addable( menuItem ) )
                {
                    boolean includeChildren = rootMenuItem == menuItem;
                    menuItemXmlCreator.addMenuItemElement( xmlDoc, menuItem, includeChildren );
                }
            }
        }
        xmlDoc.endElement();
    }

    private void createMenuItemsElement( XMLBuilder xmlDoc, MenuItemEntity rootMenuItem, boolean isTop )
    {

        xmlDoc.startElement( "menuitems" );
        xmlDoc.setAttribute( "istop", isTop ? "yes" : "no" );

        if ( rootMenuItem != null && menuItemXmlCreator.addable( rootMenuItem ) )
        {

            menuItemXmlCreator.addMenuItemElement( xmlDoc, rootMenuItem );
        }
        xmlDoc.endElement();
    }

    private void createMenuItemsElement( XMLBuilder xmlDoc, Collection<MenuItemEntity> menuItems, boolean isTop )
    {

        xmlDoc.startElement( "menuitems" );
        xmlDoc.setAttribute( "istop", isTop ? "yes" : "no" );

        for ( MenuItemEntity menuItem : menuItems )
        {
            if ( menuItemXmlCreator.addable( menuItem ) )
            {
                menuItemXmlCreator.addMenuItemElement( xmlDoc, menuItem );
            }
        }
        xmlDoc.endElement();
    }

    private XMLDocument createMenuItemsWithErrorMessage( String errorMessage )
    {
        Element menuItems = new Element( "menuitems" );
        menuItems.setAttribute( "error", errorMessage );
        return XMLDocumentFactory.create( new Document( menuItems ) );
    }

    private Element doCreateMenuElement( SiteEntity site, SiteProperties siteProperties, String rootElementName )
    {
        includeMenuItems = false;
        XMLBuilder xmlDoc = new XMLBuilder( "dummy" );
        createMenuElement( site, siteProperties, xmlDoc, null );
        final Element siteEl = xmlDoc.getRootElement().getChild( "menu" );
        siteEl.setName( rootElementName );
        return (Element) siteEl.detach();
    }

    private void createMenuElement( SiteEntity site, SiteProperties siteProperties, XMLBuilder xmlDoc,
                                    DefaultSiteAccumulatedAccessRights accessRightsAccumulated )
    {

        xmlDoc.startElement( "menu" );
        xmlDoc.setAttribute( "key", site.getKey().toString() );
        xmlDoc.setAttribute( "language", site.getLanguage().getDescription() );
        xmlDoc.setAttribute( "languagekey", site.getLanguage().getKey().toInt() );
        xmlDoc.setAttribute( "languagecode", site.getLanguage().getCode() );

        xmlDoc.startElement( "name" );
        if ( site.getName() != null )
        {
            xmlDoc.addContent( site.getName() );
        }
        xmlDoc.endElement();

        xmlDoc.startElement( "firstpage" );
        if ( site.getFrontPage() != null )
        {
            xmlDoc.setAttribute( "key", site.getFrontPage().getKey() );
        }
        xmlDoc.endElement();

        xmlDoc.startElement( "loginpage" );
        if ( site.getLoginPage() != null )
        {
            xmlDoc.setAttribute( "key", String.valueOf( site.getLoginPage().getKey() ) );
        }
        xmlDoc.endElement();

        xmlDoc.startElement( "errorpage" );
        if ( site.getErrorPage() != null )
        {
            xmlDoc.setAttribute( "key", site.getErrorPage().getKey() );
        }
        xmlDoc.endElement();

        xmlDoc.startElement( "defaultpagetemplate" );
        if ( site.getPageTemplate() != null )
        {
            xmlDoc.setAttribute( "pagetemplatekey", Integer.toString( site.getPageTemplate().getKey() ) );
        }
        xmlDoc.endElement();

        addMenudataElement( site, xmlDoc );

        xmlDoc.startElement( "details" );
        xmlDoc.setAttribute( "menukey", site.getKey().toString() );
        xmlDoc.endElement();
        if ( includeSiteURLInfo )
        {
            xmlDoc.startElement( "url" );

            if ( siteProperties != null && siteProperties.getSiteURL() != null )
            {

                xmlDoc.addContent( siteProperties.getSiteURL() );
            }
            xmlDoc.endElement();
        }

        xmlDoc.startElement( "statistics" );
        if ( site.getStatisticsUrl() != null )
        {
            xmlDoc.addContent( site.getStatisticsUrl() );
        }
        xmlDoc.endElement();

        if ( includeDeviceClassResolverInfo )
        {
            xmlDoc.startElement( "deviceclassresolver" );
            if ( site.getDeviceClassResolver() != null )
            {
                xmlDoc.setAttribute( "key", site.getDeviceClassResolver().toString() );
            }
            xmlDoc.endElement();
        }

        if ( includeLocalizationInfo )
        {
            xmlDoc.startElement( "defaultlocalizationresource" );
            if ( site.getDefaultLocalizationResource() != null )
            {
                xmlDoc.setAttribute( "key", site.getDefaultLocalizationResource().toString() );
            }
            xmlDoc.endElement();

            xmlDoc.startElement( "localeresolver" );
            if ( site.getLocaleResolver() != null )
            {
                xmlDoc.setAttribute( "key", site.getLocaleResolver().toString() );
            }
            xmlDoc.endElement();
        }

        if ( includePathToPublicHome )
        {
            xmlDoc.startElement( "path-to-public-home-resources" );
            if ( site.getPathToPublicResources() != null )
            {
                xmlDoc.setAttribute( "key", site.getPathToPublicResources().toString() );
            }
            xmlDoc.endElement();
        }

        if ( includePathToHome )
        {
            xmlDoc.startElement( "path-to-home-resources" );
            if ( site.getPathToResources() != null )
            {
                xmlDoc.setAttribute( "key", site.getPathToResources().toString() );
            }
            xmlDoc.endElement();
        }

        if ( includeRunAs )
        {
            UserEntity runAsUser = site.resolveDefaultRunAsUser();
            if ( runAsUser != null )
            {
                xmlDoc.startElement( "run-as" );
                UserXmlCreator userXmlCreator = new UserXmlCreator();
                userXmlCreator.setAdminConsoleStyle( userXmlAsAdminConsoleStyle );
                xmlDoc.getCurrentElement().addContent( userXmlCreator.createUserElement( runAsUser, false ) );
                xmlDoc.endElement();
            }
        }

        if ( accessRightsAccumulated != null )
        {
            accessRightsAccumulatedXmlCreator.setUserRightAttributes( xmlDoc.getCurrentElement(), accessRightsAccumulated );
        }

        if ( includeMenuItems() )
        {
            createMenuItemsElement( xmlDoc, site.getTopMenuItems(), true );
        }

        xmlDoc.endElement();
    }

    private void addMenudataElement( SiteEntity site, XMLBuilder xmlDoc )
    {
        final Set<String> allowedPageTypes = site.getAllowedPageTypes();

        final ResourceKey defaultCssKey = site.getDefaultCssKey();

        if ( allowedPageTypes.size() == 0 && defaultCssKey == null )
        {
            xmlDoc.addContentElement( "menudata", "" );
            return;
        }

        xmlDoc.startElement( "menudata" );

        if ( allowedPageTypes.size() > 0 )
        {
            xmlDoc.startElement( "pagetypes" );
            for ( String type : allowedPageTypes )
            {
                xmlDoc.startElement( "allow" );
                xmlDoc.setAttribute( "type", type );
                xmlDoc.endElement();
            }
            xmlDoc.endElement();
        }

        if ( defaultCssKey != null )
        {
            xmlDoc.startElement( "defaultcss" );
            xmlDoc.setAttribute( "key", defaultCssKey.toString() );
            xmlDoc.endElement();
        }

        xmlDoc.endElement();
    }

    public void setIncludeSiteURLInfo( boolean includeSiteURLInfo )
    {
        this.includeSiteURLInfo = includeSiteURLInfo;
    }

    public void setIncludeDeviceClassResolverInfo( boolean includeDeviceClassResolverInfo )
    {
        this.includeDeviceClassResolverInfo = includeDeviceClassResolverInfo;
    }

    public void setIncludeLocalizationInfo( boolean includeLocalizationInfo )
    {
        this.includeLocalizationInfo = includeLocalizationInfo;
    }

    public void setIncludePathToPublicHome( boolean includePathToPublicHome )
    {
        this.includePathToPublicHome = includePathToPublicHome;
    }

    public void setIncludePathToHome( boolean includePathToHome )
    {
        this.includePathToHome = includePathToHome;
    }

    public void setIncludeRunAs( boolean includeRunAs )
    {
        this.includeRunAs = includeRunAs;
    }

    public boolean includeHiddenMenuItems()
    {
        return includeHiddenMenuItems;
    }

    public void setIncludeHiddenMenuItems( boolean includeHiddenMenuItems )
    {
        this.includeHiddenMenuItems = includeHiddenMenuItems;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public MenuItemEntity getActiveMenuItem()
    {
        return activeMenuItem;
    }

    public void setActiveMenuItem( MenuItemEntity activeMenuItem )
    {
        this.activeMenuItem = activeMenuItem;
    }

    public boolean includeMenuItems()
    {
        return includeMenuItems;
    }

    public void setIncludeMenuItems( boolean includeMenuItems )
    {
        this.includeMenuItems = includeMenuItems;
    }

    public MenuItemEntity getMenuItemInBranch()
    {
        return menuItemInBranch;
    }

    public void setMenuItemInBranch( MenuItemEntity menuItemInBranch )
    {
        this.menuItemInBranch = menuItemInBranch;
    }

    public int getMenuItemLevels()
    {
        return menuItemLevels;
    }

    public void setMenuItemLevels( int menuItemLevels )
    {
        this.menuItemLevels = menuItemLevels;
    }

    public int getBranchStartLevel()
    {
        return branchStartLevel;
    }

    public void setBranchStartLevel( int branchStartLevel )
    {
        this.branchStartLevel = branchStartLevel;
    }

    public boolean includeTopLevel()
    {
        return includeTopLevel;
    }

    public void setIncludeTopLevel( boolean includeTopLevel )
    {
        this.includeTopLevel = includeTopLevel;
    }

    public void setUserXmlAsAdminConsoleStyle( boolean userXmlAsAdminConsoleStyle )
    {
        this.userXmlAsAdminConsoleStyle = userXmlAsAdminConsoleStyle;
    }
}

