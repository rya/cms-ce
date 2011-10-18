/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.collect.Maps;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.ContentLocationXmlCreator;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SiteXmlCreator;
import com.enonic.cms.core.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.core.structure.menuitem.MenuItemAndUserAccessRights;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemXMLCreatorSetting;
import com.enonic.cms.core.structure.menuitem.MenuItemXmlCreator;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateXmlCreator;

/**
 * Jan 7, 2010
 */
public class ContentEditFormModel
{
    private ContentLocations contentLocations;

    private Map<MenuItemKey, MenuItemAndUserAccessRights> menuItemAndUserAccessRightsMapByMenuItemKey;

    private Map<SiteKey, PageTemplateEntity> pageTemplateBySite;

    private PageTemplateXmlCreator pageTemplateXmlCreator = new PageTemplateXmlCreator();

    public void setContentLocations( ContentLocations value )
    {
        this.contentLocations = value;
    }

    public void setMenuItemAndUserAccessRightsMapByMenuItemKey( Map<MenuItemKey, MenuItemAndUserAccessRights> value )
    {
        this.menuItemAndUserAccessRightsMapByMenuItemKey = value;
    }

    public void setPageTemplateBySite( Map<SiteKey, PageTemplateEntity> pageTemplateBySite )
    {
        this.pageTemplateBySite = pageTemplateBySite;
    }

    XMLDocument locationsToXML()
    {
        return XMLDocumentFactory.create( new Document( createLocationElement() ) );
    }

    XMLDocument locationMenuitemsToXML()
    {
        return XMLDocumentFactory.create( new Document( createLocationMenuitemsElement() ) );
    }

    XMLDocument locationSitesToXML()
    {
        return XMLDocumentFactory.create( new Document( createLocationSitesElement() ) );
    }

    XMLDocument pageTemplateBySiteToXML()
    {
        return XMLDocumentFactory.create( new Document( createPageTemlateBySiteElement() ) );
    }

    private Element createLocationElement()
    {
        ContentLocationXmlCreator contentLocationXmlCreator = new ContentLocationXmlCreator();
        contentLocationXmlCreator.setIncludeUserDefinedSectionHomeInfo( true );
        return contentLocationXmlCreator.createLocationElement( contentLocations, true );
    }

    private Element createLocationMenuitemsElement()
    {
        Element locationMenuitemsEl = new Element( "location-menuitems" );

        MenuItemXMLCreatorSetting menuItemXMLCreatorSetting = new MenuItemXMLCreatorSetting();
        menuItemXMLCreatorSetting.includeChildren = false;
        menuItemXMLCreatorSetting.includeDocumentElement = false;
        menuItemXMLCreatorSetting.includeHiddenMenuItems = true;
        menuItemXMLCreatorSetting.includeTypeSpecificXML = true;
        menuItemXMLCreatorSetting.includeParents = false;
        MenuItemXmlCreator menuItemXmlCreator = new MenuItemXmlCreator( menuItemXMLCreatorSetting, null );
        menuItemXmlCreator.setIncludeAnonynousReadInfo( true );
        menuItemXmlCreator.setIncludeUserAccessRightsInfo( true );

        for ( MenuItemEntity menuItem : contentLocations.getMenuItems() )
        {
            MenuItemAndUserAccessRights menuItemAndUserAccessRights =
                menuItemAndUserAccessRightsMapByMenuItemKey.get( menuItem.getMenuItemKey() );
            final MenuItemAccumulatedAccessRights accessRightsForExecutor = menuItemAndUserAccessRights.getAccessRightsForUser();
            final MenuItemAccumulatedAccessRights accessRightsForAnonymous = menuItemAndUserAccessRights.getAccessrightsForAnonymous();
            locationMenuitemsEl.addContent(
                menuItemXmlCreator.createMenuItemElement( menuItem, accessRightsForExecutor, accessRightsForAnonymous ) );
        }

        return locationMenuitemsEl;
    }

    private Element createLocationSitesElement()
    {
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( null );
        return siteXmlCreator.createSitesElement( contentLocations.getSites(), Maps.<SiteKey, SiteProperties>newHashMap(),
                                                  "location-sites" );
    }

    private Element createPageTemlateBySiteElement()
    {
        Element rootEl = new Element( "page-template-by-site" );
        for ( Map.Entry<SiteKey, PageTemplateEntity> entry : pageTemplateBySite.entrySet() )
        {
            Element siteEl = new Element( "site" );
            siteEl.setAttribute( "key", entry.getKey().toString() );
            siteEl.addContent( pageTemplateXmlCreator.createPageTemlateElement( entry.getValue() ) );
            rootEl.addContent( siteEl );
        }
        return rootEl;
    }

}
