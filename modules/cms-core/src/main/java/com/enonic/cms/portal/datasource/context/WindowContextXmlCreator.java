/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import java.util.Map;

import org.jdom.Element;

import com.enonic.cms.core.structure.TemplateParameter;
import com.enonic.cms.core.structure.page.Window;
import com.enonic.cms.core.structure.portlet.PortletEntity;

/**
 * May 15, 2009
 */
public class WindowContextXmlCreator
{

    public Element createPortletWindowElement( Window window, boolean isRenderedInline, Element portletDocumentEl )
    {
        PortletEntity portlet = window.getPortlet();

        Element windowEl = new Element( "window" );
        windowEl.setAttribute( "key", window.getKey().toString() );
        windowEl.setAttribute( "is-rendered-inline", Boolean.toString( isRenderedInline ) );
        windowEl.setAttribute( "region", window.getRegion().getName() );
        windowEl.addContent( new Element( "name" ).setText( portlet.getName() ) );
        windowEl.addContent( createPortletElement( portlet, portletDocumentEl ) );

        return windowEl;
    }

    private Element createPortletElement( PortletEntity portlet, Element portletDocumentEl )
    {
        Element portletEl = new Element( "portlet" );

        portletEl.setAttribute( "key", portlet.getPortletKey().toString() );
        portletEl.addContent( new Element( "name" ).setText( portlet.getName() ) );

        Map<String, TemplateParameter> templateParameters = portlet.getTemplateParameters();
        Element paramatersEl = new Element( "paramaters" );
        for ( TemplateParameter templateParameter : templateParameters.values() )
        {
            Element paramaterEl = new Element( "paramater" );

            Element nameEl = new Element( "name" );
            Element valueEl = new Element( "value" );

            nameEl.setText( templateParameter.getName() );
            valueEl.setText( templateParameter.getValue() );

            paramaterEl.addContent( nameEl );
            paramaterEl.addContent( valueEl );

            paramatersEl.addContent( paramaterEl );
        }
        portletEl.addContent( paramatersEl );

        if ( portletDocumentEl != null )
        {
            portletEl.addContent( portletDocumentEl );
        }

        return portletEl;
    }

}