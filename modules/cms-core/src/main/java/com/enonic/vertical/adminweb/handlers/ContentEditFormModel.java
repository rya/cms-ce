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

import com.enonic.cms.business.core.content.ContentLocationXmlCreator;
import com.enonic.cms.business.core.structure.MenuItemXMLCreatorSetting;
import com.enonic.cms.business.core.structure.MenuItemXmlCreator;
import com.enonic.cms.business.core.structure.SiteXmlCreator;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentLocations;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.SiteProperties;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemAndUserAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

/**
 * Jan 7, 2010
 */
public class ContentEditFormModel
{
    private ContentLocations contentLocations;

    private Map<MenuItemKey, MenuItemAndUserAccessRights> menuItemAndUserAccessRightsMapByMenuItemKey;

    public void setContentLocations( ContentLocations value )
    {
        this.contentLocations = value;
    }

    public void setMenuItemAndUserAccessRightsMapByMenuItemKey( Map<MenuItemKey, MenuItemAndUserAccessRights> value )
    {
        this.menuItemAndUserAccessRightsMapByMenuItemKey = value;
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

        final Iterable<SiteEntity> sites = contentLocations.getSites();

        return siteXmlCreator.createSitesElement( sites, Maps.<SiteKey, SiteProperties>newHashMap(), "location-sites" );
    }
}
