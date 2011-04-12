/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public class XmlDataEntryParser
{
    public XmlDataEntry parse( final Element containerElement, final DataEntryConfig inputConfig )
    {
        final List list = containerElement.getChildren();
        String value = "";
        if ( !list.isEmpty() )
        {
            final Element xmlRootNode = (Element) list.get( 0 );
            value = elementToString( xmlRootNode );
        }

        if ( StringUtils.isBlank( value ) )
        {
            return new XmlDataEntry( inputConfig, null );
        }
        return new XmlDataEntry( inputConfig, value );
    }

    private String elementToString( final Element xmlRootNode )
    {
        final StringWriter sw = new StringWriter();
        final XMLOutputter outputter = new XMLOutputter();

        try
        {
            outputter.output( xmlRootNode, sw );
            return sw.getBuffer().toString();
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( "Failed to parse element", e );
        }
    }

}
