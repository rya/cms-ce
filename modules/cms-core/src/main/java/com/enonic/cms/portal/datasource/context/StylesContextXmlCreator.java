/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import org.jdom.CDATA;
import org.jdom.Element;

import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;

/**
 * Apr 21, 2009
 */
public class StylesContextXmlCreator
{
    private ResourceService resourceService;

    public StylesContextXmlCreator( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public Element createStylesElement( ResourceKey[] cssKeys, boolean stylecontextOn )
    {
        Element stylesEl = new Element( "styles" );

        for ( ResourceKey cssKey : cssKeys )
        {
            Element styleEl = new Element( "style" );
            stylesEl.addContent( styleEl );
            styleEl.setAttribute( "key", cssKey.toString() );
            styleEl.setAttribute( "name", cssKey.toString() );
            styleEl.setAttribute( "path", cssKey.toString() );
            styleEl.setAttribute( "type", "text/css" );

            ResourceFile cssResource = resourceService.getResourceFile( cssKey );
            if ( cssResource == null )
            {
                styleEl.setAttribute( "missing", "true" );
                continue;
            }
            String css = cssResource.getDataAsString();
            if ( css == null )
            {
                styleEl.setAttribute( "missing", "true" );
                continue;
            }

            if ( stylecontextOn )
            {
                styleEl.addContent( new CDATA( css ) );
            }
        }

        return stylesEl;
    }
}
