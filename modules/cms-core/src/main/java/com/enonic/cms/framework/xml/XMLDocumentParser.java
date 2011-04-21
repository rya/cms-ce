/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.*;

import javax.swing.text.Document;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;

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
     * Sax parser factory.
     */
    private final SAXParserFactory saxParserFactory;

    /**
     * Construct the parser.
     */
    private XMLDocumentParser()
    {
        this.saxParserFactory = SAXParserFactory.newInstance();
        this.saxParserFactory.setNamespaceAware( true );
        this.saxParserFactory.setValidating( true );
    }

    /**
     * Return the SAX parser.
     */
    private SAXParser newSAXParser()
        throws XMLException
    {
        try
        {
            return this.saxParserFactory.newSAXParser();
        }
        catch ( Exception e )
        {
            throw new XMLException( "Failed to create XML parser", e );
        }
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public org.jdom.Document parseDocument( String doc )
            throws XMLException, IOException, JDOMException {
        InputSource source = new InputSource();
        source.setCharacterStream( new StringReader( doc ) );
        byte[] byteData = internalParseDocument( source );
        org.jdom.Document jdoc = new SAXBuilder().build(new StringReader(new String(byteData, "UTF-8")));
        return jdoc;
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public org.jdom.Document parseDocument(Reader input)
            throws XMLException, JDOMException, IOException {
        return parseDocument( XMLDocumentHelper.copyToString( input ) );
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public org.jdom.Document parseDocument(InputStream input)
            throws XMLException, JDOMException, IOException {
        return parseDocument( new InputStreamReader( input ) );
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    private byte[] internalParseDocument( InputSource input )
        throws XMLException
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            SAXDocumentSerializer serializer = new SAXDocumentSerializer();
            serializer.setOutputStream( out );

            SAXParser parser = newSAXParser();
            parser.setProperty( "http://xml.org/sax/properties/lexical-handler", serializer );
            parser.parse( input, serializer );

            return out.toByteArray();
        }
        catch ( Exception e )
        {
            throw new XMLException( "Failed to parse document", e );
        }
    }

    /**
     * Return the instance.
     */
    public static XMLDocumentParser getInstance()
    {
        return INSTANCE;
    }
}
