package com.enonic.vertical.adminweb;

import java.util.Arrays;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.business.core.structure.MenuItemXMLCreatorSetting;
import com.enonic.cms.business.core.structure.MenuItemXmlCreator;
import com.enonic.cms.business.core.structure.SiteXmlCreator;

import com.enonic.cms.domain.structure.DefaultSiteAccumulatedAccessRights;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.SiteProperties;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.page.PageEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;

/**
 * model for edit menu item page
 */
public class MenuItemFormModel
{
    private SiteEntity site;

    private SiteProperties siteProperties;

    private DefaultSiteAccumulatedAccessRights userRightsForSite;

    private List<MenuItemEntity> selectedMenuItemPath;

    public XMLDocument toXML()
    {
        Element modelEl = new Element( "model" );

        modelEl.addContent( createSelectedMenuElement() );
        modelEl.addContent( createSelectedMenuItemPathElement() );

        return XMLDocumentFactory.create( new Document( modelEl ) );
    }

    private Element createSelectedMenuElement()
    {
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( null );
        return siteXmlCreator.createMenuElement( site, siteProperties, userRightsForSite );
    }

    private Element createSelectedMenuItemPathElement()
    {
        Element selectedMenuItemPathEl = new Element( "menuitems" );

        if ( selectedMenuItemPath != null )
        {
            int i = 0;
            for ( MenuItemEntity currMenuItem : selectedMenuItemPath )
            {
                MenuItemXMLCreatorSetting miPathXmlCreatorSetting = new MenuItemXMLCreatorSetting();
                miPathXmlCreatorSetting.includeTypeSpecificXML = false;
                miPathXmlCreatorSetting.includeParents = false;
                miPathXmlCreatorSetting.includeChildren = false;
                MenuItemXmlCreator menuItemPathXmlCreator = new MenuItemXmlCreator( miPathXmlCreatorSetting, null );
                Element currMenuItemEl = menuItemPathXmlCreator.createMenuItemElement( currMenuItem );
                selectedMenuItemPathEl.addContent( currMenuItemEl );

                // do not add last element - it is self
                if ( ++i == selectedMenuItemPath.size() - 1 )
                {
                    break;
                }
            }
        }

        return selectedMenuItemPathEl;
    }


    public int findParentPageTemplateKey()
    {
        int template = -1;

        if ( selectedMenuItemPath != null )
        {
            int self = selectedMenuItemPath.size() - 1;

            for ( int path = self; path >= 0; path-- ) // backward
            {
                PageEntity page = selectedMenuItemPath.get(path).getPage();

                if ( page != null )
                {
                    PageTemplateEntity pageTemplate = page.getTemplate();

                    if ( pageTemplate != null )
                    {
                        template = pageTemplate.getKey();
                        break;
                    }
                }
            }
        }

        return template;
    }

    /* getters and setters */

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

    public void setSelectedMenuItemPath( List<MenuItemEntity> selectedMenuItemPath )
    {
        this.selectedMenuItemPath = selectedMenuItemPath;
    }
}
