/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**
 * This class implements the XML document parser methods.
 */
public final class XMLDocumentParser
{
    /**
     * Instance of document parser.
     */
    private final static XMLDocumentParser INSTANCE = new XMLDocumentParser();

    /**
     * Construct the parser.
     */
    private XMLDocumentParser()
    {
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public Document parseDocument( String doc )
            throws XMLException, IOException, JDOMException
    {
        return parseDocument(new StringReader( doc ) );
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public Document parseDocument( Reader input )
            throws XMLException, JDOMException, IOException
    {
        final InputSource source = new InputSource();
        source.setCharacterStream( input );
        return new SAXBuilder().build(source);
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public Document parseDocument( InputStream input )
            throws XMLException, JDOMException, IOException
    {
        return parseDocument( new InputStreamReader( input ) );
    }

    /**
     * Return the instance.
     */
    public static XMLDocumentParser getInstance()
    {
        return INSTANCE;
    }
}
