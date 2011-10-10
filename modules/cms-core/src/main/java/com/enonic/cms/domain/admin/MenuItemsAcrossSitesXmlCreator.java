/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.admin;

import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;

/**
 * Oct 1, 2009
 */
public class MenuItemsAcrossSitesXmlCreator
{
    public Document createXmlDocument( MenuItemsAcrossSitesModel model )
    {
        Element rootEl = new Element( "menuitems-across-sites" );
        for ( Map.Entry<SiteEntity, List<MenuItemEntity>> entry : model.getMap().entrySet() )
        {
            rootEl.addContent( doCreateSiteElement( entry ) );
        }
        return new Document( rootEl );
    }

    private Element doCreateSiteElement( Map.Entry<SiteEntity, List<MenuItemEntity>> entry )
    {
        SiteEntity site = entry.getKey();
        Element siteEl = new Element( "site" );
        siteEl.setAttribute( "key", site.getKey().toString() );
        siteEl.addContent( new Element( "name" ).setText( site.getName() ) );

        Element menuitemsEl = new Element( "menuitems" );
        siteEl.addContent( menuitemsEl );
        List<MenuItemEntity> menuItems = entry.getValue();
        for ( MenuItemEntity menuItem : menuItems )
        {
            menuitemsEl.addContent( doCreateMenuItemElement( menuItem ) );
        }
        return siteEl;
    }

    private Element doCreateMenuItemElement( MenuItemEntity menuItem )
    {
        Element menuitemEl = new Element( "menuitem" );
        menuitemEl.setAttribute( "key", menuItem.getMenuItemKey().toString() );

        Element typeEl = new Element( "type" );
        typeEl.setAttribute( "key", menuItem.getType().getKey().toString() );
        typeEl.setText( menuItem.getType().getName() );
        menuitemEl.addContent( typeEl );

        menuitemEl.addContent( new Element( "name" ).setText( menuItem.getName() ) );
        menuitemEl.addContent( new Element( "path" ).setText( menuItem.getPathAsString() ) );
        menuitemEl.addContent( new Element( "show-in-menu" ).setText( Boolean.toString( menuItem.showInMenu() ) ) );

        if ( menuItem.getPage() != null )
        {
            menuitemEl.addContent( doCreatePageElement( menuItem.getPage() ) );
        }
        return menuitemEl;
    }

    private Element doCreatePageElement( PageEntity page )
    {
        Element pageEl = new Element( "page" );
        pageEl.setAttribute( "key", String.valueOf( page.getKey() ) );
        if ( page.getTemplate() != null )
        {
            pageEl.addContent( doCreatePageTemplateElement( page.getTemplate() ) );
        }
        return pageEl;
    }

    private Element doCreatePageTemplateElement( PageTemplateEntity pageTemplate )
    {
        Element pagetemplateEl = new Element( "page-template" );
        pagetemplateEl.setAttribute( "key", String.valueOf( pageTemplate.getKey() ) );

        Element typeEl = new Element( "type" );
        typeEl.setAttribute( "key", String.valueOf( pageTemplate.getType().getKey() ) );
        typeEl.setText( pageTemplate.getType().getName() );
        pagetemplateEl.addContent( typeEl );

        return pagetemplateEl;
    }
}
