/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.*;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

import com.enonic.cms.framework.util.JDOMUtil;

/**
 * This class implements the XML document helper methods.
 */
public final class XMLDocumentHelper
{
    /**
     * Copy buffer size.
     */
    private final static int COPY_BUFFER_SIZE = 4096;

    /**
     * Transformer factory.
     */
    private final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();


    /**
     * Convert to jdom document.
     */
    public static Document convertToJDOMDocument( org.w3c.dom.Document doc )
        throws XMLException
    {
        return convertToJDOMDocument( new DOMSource( doc ) );
    }

    /**
     * Convert to jdom document.
     */
    public static Document convertToJDOMDocument( String doc )
        throws XMLException
    {
        try
        {
            return JDOMUtil.parseDocument( doc );
        }
        catch ( Exception e )
        {
            throw new XMLException( "Failed to parse document: " + e.getMessage(), e );
        }
    }

    /**
     * Convert to jdom document.
     */
    private static Document convertToJDOMDocument( Source source )
        throws XMLException
    {
        JDOMResult result = new JDOMResult();
        transform( source, result );
        return result.getDocument();
    }


    /**
     * Convert to jdom document.
     */
    public static org.w3c.dom.Document convertToW3CDocument( Document doc )
        throws XMLException
    {
        DOMOutputter outputter = new DOMOutputter();
        try
        {
            return outputter.output( doc );
        }
        catch ( JDOMException e )
        {
            throw new XMLException( e );
        }
    }

    /**
     * Convert to jdom document.
     */
    public static String convertToString( Document doc )
        throws XMLException
    {
        return convertToString( new JDOMSource( doc ) );
    }

    /**
     * Convert to w3c document.
     */
    public static String convertToString( Source source )
        throws XMLException
    {
        StreamResult result = new StreamResult();
        StringWriter writer = new StringWriter();
        result.setWriter( writer );
        transform( source, result );
        return writer.toString();
    }

    /**
     * Transform the source to result.
     */
    private static void transform( Source input, Result output )
        throws XMLException
    {
        try
        {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            transformer.transform( input, output );
        }
        catch ( Exception e )
        {
            throw new XMLException( "Failed to transform xml document", e );
        }
    }

    /**
     * Copy a reader to writer.
     */
    private static void copy( Reader in, Writer out )
        throws IOException
    {
        try
        {
            int bytesRead;
            char[] buffer = new char[COPY_BUFFER_SIZE];

            while ( ( bytesRead = in.read( buffer ) ) != -1 )
            {
                out.write( buffer, 0, bytesRead );
            }

            out.flush();
        }
        finally
        {
            in.close();
            out.close();
        }
    }

    /**
     * Copy reader to string.
     */
    public static String copyToString( Reader in )
        throws XMLException
    {
        try
        {
            StringWriter out = new StringWriter();
            copy( in, out );
            return out.toString();
        }
        catch ( IOException e )
        {
            throw new XMLException( "Failed to read document", e );
        }
    }
}
