/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.content.index.IndexValueResult;
import com.enonic.cms.core.content.index.IndexValueResultSet;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.index.AggregatedResult;

/**
 * Includes methods that creates the XML documentes for result sets with index values.
 */
public class ContentIndexValuesXMLCreator
{

    /**
     * Create aggregated index values document.
     *
     * @param path   The path to the result set.  Set as an attribute on the root element.
     * @param result The aggregated data to use in creating the XML.
     * @return A full XML with the results.
     */
    public XMLDocument createIndexValuesDocument( String path, AggregatedResult result )
    {
        Element root = new Element( "index" );
        root.setAttribute( "path", path );

        Element values = new Element( "values" );
        values.setAttribute( "count", String.valueOf( result.getCount() ) );
        values.setAttribute( "min", String.valueOf( result.getMinValue() ) );
        values.setAttribute( "max", String.valueOf( result.getMaxValue() ) );
        values.setAttribute( "sum", String.valueOf( result.getSumValue() ) );
        values.setAttribute( "average", String.valueOf( result.getAverageValue() ) );
        root.addContent( values );

        return XMLDocumentFactory.create( new Document( root ) );
    }

    /**
     * Create index values document.
     *
     * @param path   The path to the result set.  Set as an attribute on the root element.
     * @param result The aggregated data to use in creating the XML.
     * @return A full XML with the results.
     */
    public XMLDocument createIndexValuesDocument( String path, IndexValueResultSet result )
    {
        Element root = new Element( "index" );
        root.setAttribute( "path", path );

        Element values = new Element( "values" );
        values.setAttribute( "count", String.valueOf( result.getCount() ) );
        values.setAttribute( "totalcount", String.valueOf( result.getTotalCount() ) );
        values.setAttribute( "index", String.valueOf( result.getFromIndex() ) );
        root.addContent( values );

        for ( int i = 0; i < result.getCount(); i++ )
        {
            IndexValueResult single = result.getIndexValue( i );
            Element value = new Element( "value" );
            value.setAttribute( "count", "1" );
            value.setAttribute( "contentkey", String.valueOf( single.getContentKey() ) );
            value.setText( single.getValue() );
            values.addContent( value );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }
}
