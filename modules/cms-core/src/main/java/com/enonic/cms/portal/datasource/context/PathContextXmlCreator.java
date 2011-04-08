/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import java.util.List;

import org.jdom.Element;

import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * Jul 28, 2009
 */
public class PathContextXmlCreator
{

    public Element createPathElement( final MenuItemEntity child )
    {
        final Element pathEl = new Element( "path" );

        final List<MenuItemEntity> menItemPath = child.getMenuItemPath();
        for ( final MenuItemEntity mi : menItemPath )
        {
            pathEl.addContent( createResourceElement( mi ) );
        }

        return pathEl;
    }

    public Element createResourceElement( final MenuItemEntity menuItem )
    {
        final Element objectEl = new Element( "resource" );
        objectEl.setAttribute( "key", menuItem.getMenuItemKey().toString() );
        objectEl.setAttribute( "type", "menuitem" );

        objectEl.addContent( createElement( "name", menuItem.getName() ) );
        objectEl.addContent( createElement( "menu-name", asEmptyIfNull( menuItem.getMenuName() ) ) );
        Element altElem = createElement( "display-name", asEmptyIfNull( menuItem.getDisplayName() ) );
        objectEl.addContent( altElem );

        objectEl.addContent( createElement( "show-in-menu", menuItem.getHidden() ? "false" : "true" ) );
        objectEl.addContent( createElement( "keywords", asEmptyIfNull( menuItem.getKeywords() ) ) );
        objectEl.addContent( createElement( "description", asEmptyIfNull( menuItem.getDescription() ) ) );
        objectEl.addContent( createElement( "type", menuItem.getType().getName() ) );

        return objectEl;
    }

    private Element createElement( final String name, final String text )
    {
        final Element el = new Element( name );
        el.setText( asEmptyIfNull( text ) );
        return el;
    }

    private String asEmptyIfNull( final String value )
    {
        return value != null ? value : "";
    }
}
