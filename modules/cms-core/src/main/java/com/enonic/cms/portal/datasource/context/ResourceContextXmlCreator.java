/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import org.jdom.Element;

import com.enonic.cms.portal.datasource.DatasourceExecutorContext;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.portal.PageRequestType;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;

/**
 * Jul 28, 2009
 */
public class ResourceContextXmlCreator
{

    private PageTemplateEntity pageTemplate;

    private MenuItemEntity menuItem;

    private ContentEntity contentFromRequest;

    private PageRequestType pageRequestType;

    private PathContextXmlCreator pathContextXmlCreator;

    public ResourceContextXmlCreator( DatasourceExecutorContext datasourceExecutorContext )
    {
        this.menuItem = datasourceExecutorContext.getMenuItem();
        this.contentFromRequest = datasourceExecutorContext.getContentFromRequest();
        this.pageRequestType = datasourceExecutorContext.getPageRequestType();
        this.pageTemplate = datasourceExecutorContext.getPageTemplate();

        pathContextXmlCreator = new PathContextXmlCreator();
    }

    public Element createResourceElement()
    {
        Element resourceEl = new Element( "resource" );
        resourceEl.setAttribute( "key", resolveKey() );
        resourceEl.setAttribute( "type", resolveResourceType() );

        resourceEl.addContent(
            new Element( "name" ).setText( ResourceNameResolver.resolveName( pageRequestType, contentFromRequest, menuItem ) ) );
        Element menuNameEl =
            new Element( "menu-name" ).setText( asEmptyIfNull( ResourceNameResolver.resolveMenuName( pageRequestType, menuItem ) ) );
        resourceEl.addContent( menuNameEl );

        resourceEl.addContent( new Element( "display-name" ).setText(
            ResourceNameResolver.resolveDisplayName( pageRequestType, contentFromRequest, menuItem ) ) );

        if ( menuItem != null )
        {
            resourceEl.addContent( new Element( "show-in-menu" ).setText( menuItem.getHidden() ? "false" : "true" ) );
            resourceEl.addContent( new Element( "keywords" ).setText( asEmptyIfNull( menuItem.getKeywords() ) ) );
            resourceEl.addContent( new Element( "description" ).setText( asEmptyIfNull( menuItem.getDescription() ) ) );
        }

        resourceEl.addContent( new Element( "type" ).setText( resolveType() ) );

        if ( menuItem != null && pageRequestType == PageRequestType.MENUITEM )
        {
            Element documentEl = menuItem.getDocumentElementAsClonedJDOMElement();
            if ( documentEl != null )
            {
                resourceEl.addContent( documentEl );
            }
        }

        // Menuitem can be null when rendering a newsletter
        if ( menuItem != null )
        {
            resourceEl.addContent( pathContextXmlCreator.createPathElement( menuItem ) );
        }

        return resourceEl;
    }


    private String resolveKey()
    {
        if ( PageRequestType.CONTENT.equals( pageRequestType ) )
        {
            return contentFromRequest.getKey().toString();
        }
        else if ( menuItem != null )
        {
            return menuItem.getMenuItemKey().toString();
        }
        else
        {
            return pageTemplate.getPageTemplateKey().toString();
        }
    }

    private String resolveResourceType()
    {
        if ( PageRequestType.CONTENT.equals( pageRequestType ) )
        {
            return "content";
        }
        else if ( menuItem != null )
        {
            return "menuitem";
        }
        else
        {
            return "page-template";
        }
    }

    private String resolveType()
    {
        if ( contentFromRequest != null )
        {
            return contentFromRequest.getContentType().getName();
        }
        else if ( menuItem != null )
        {
            return menuItem.getType().getName();
        }

        return "";
    }

    private String asEmptyIfNull( final String value )
    {
        return value != null ? value : "";
    }
}
