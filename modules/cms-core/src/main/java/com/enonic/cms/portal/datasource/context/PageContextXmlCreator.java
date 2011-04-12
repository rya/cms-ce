/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import com.enonic.cms.core.structure.menuitem.MenuItemRequestParameter;
import com.enonic.cms.core.structure.page.Region;
import com.enonic.cms.core.structure.page.Regions;
import org.jdom.Element;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Window;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * May 15, 2009
 */
public class PageContextXmlCreator
{
    public Element createPageElementForMenuItemRequest( final Regions regions, final MenuItemEntity menuItem,
                                                        final PageTemplateEntity pageTemplate )
    {
        final Element pageEl = new Element( "page" );

        pageEl.addContent( createParametersElement( menuItem ) );

        pageEl.addContent( createPageTemplateElement( pageTemplate ) );

        final Element regionsEl = new Element( "regions" );
        for ( Region region : regions.getRegions() )
        {
            regionsEl.addContent( createRegionElement( region ) );
        }
        pageEl.addContent( regionsEl );

        return pageEl;
    }

    public Element createPageElementForContentRequest( final Regions regions, final PageTemplateEntity pageTemplate )
    {
        final Element pageEl = new Element( "page" );

        pageEl.addContent( createPageTemplateElement( pageTemplate ) );

        final Element regionsEl = new Element( "regions" );
        for ( Region region : regions.getRegions() )
        {
            regionsEl.addContent( createRegionElement( region ) );
        }
        pageEl.addContent( regionsEl );

        return pageEl;
    }

    private Element createParametersElement( MenuItemEntity menuItem )
    {
        Element parametersEl = new Element( "parameters" );
        for ( MenuItemRequestParameter parameter : menuItem.getRequestParameters().values() )
        {
            Element parameterEl = new Element( "parameter" );
            parameterEl.addContent( new Element( "name" ).setText( parameter.getName() ) );
            parameterEl.addContent( new Element( "overrideable" ).setText( parameter.isOverridableByRequest() ? "true" : "false" ) );
            parameterEl.addContent( new Element( "value" ).setText( asEmptyIfNull( parameter.getValue() ) ) );
            parametersEl.addContent( parameterEl );
        }
        return parametersEl;
    }

    private Element createPageTemplateElement( PageTemplateEntity pageTemplate )
    {
        Element pageTemplateEl = new Element( "page-template" );
        pageTemplateEl.addContent( new Element( "name" ).setText( pageTemplate.getName() ) );
        return pageTemplateEl;
    }

    private Element createRegionElement( Region region )
    {
        Element regionEl = new Element( "region" );
        regionEl.addContent( new Element( "name" ).setText( region.getName() ) );

        Element windowsEl = new Element( "windows" );
        for ( Window window : region.getWindows() )
        {
            Element portletWindowEl = createPortletWindowElement( window );
            windowsEl.addContent( portletWindowEl );
        }
        regionEl.addContent( windowsEl );
        return regionEl;
    }

    private Element createPortletWindowElement( Window window )
    {
        PortletEntity portlet = window.getPortlet();

        Element windowEl = new Element( "window" );
        windowEl.setAttribute( "key", window.getKey().toString() );
        windowEl.addContent( new Element( "name" ).setText( portlet.getName() ) );

        windowEl.addContent( createPortletElement( portlet ) );

        return windowEl;
    }

    private Element createPortletElement( PortletEntity portlet )
    {
        Element portletEl = new Element( "portlet" );
        portletEl.setAttribute( "key", portlet.getPortletKey().toString() );
        portletEl.addContent( new Element( "name" ).setText( portlet.getName() ) );
        return portletEl;
    }

    private String asEmptyIfNull( final String value )
    {
        return value != null ? value : "";
    }
}
