/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public class HtmlAreaDataEntryParser
{
    public HtmlAreaDataEntry parse( final Element containerElement, final DataEntryConfig inputConfig )
    {
        final Attribute hasValueAtr = containerElement.getAttribute( "has-value" );
        if ( hasValueAtr != null && hasValueAtr.getValue().equalsIgnoreCase( "false" ) )
        {
            return new HtmlAreaDataEntry( inputConfig, null );
        }

        final String value = elementContentToString( containerElement );

        return new HtmlAreaDataEntry( inputConfig, value );
    }

    private String elementContentToString( final Element containerElement )
    {
        final StringWriter sw = new StringWriter();
        final XMLOutputter outputter = new XMLOutputter();

        try
        {
            outputter.outputElementContent( containerElement, sw );
            return sw.getBuffer().toString();
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( "Failed to print element", e );
        }
    }


}
