/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
    public XMLBytes parseDocument( String doc )
        throws XMLException
    {
        InputSource source = new InputSource();
        source.setCharacterStream( new StringReader( doc ) );
        byte[] byteData = internalParseDocument( source );
        return new XMLBytes( byteData );
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public XMLBytes parseDocument( Reader input )
        throws XMLException
    {
        return parseDocument( XMLDocumentHelper.copyToString( input ) );
    }

    /**
     * Parse document and return the fast infoset byte array.
     */
    public XMLBytes parseDocument( InputStream input )
        throws XMLException
    {
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
