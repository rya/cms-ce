/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jvnet.fastinfoset.FastInfosetResult;
import org.jvnet.fastinfoset.FastInfosetSource;

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
    public static Document convertToJDOMDocument( XMLBytes doc )
        throws XMLException
    {
        return convertToJDOMDocument( new FastInfosetSource( new ByteArrayInputStream( doc.getData() ) ) );
    }

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
    public static org.w3c.dom.Document convertToW3CDocument( XMLBytes doc )
        throws XMLException
    {
        return convertToW3CDocument( new FastInfosetSource( new ByteArrayInputStream( doc.getData() ) ) );
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
    public static org.w3c.dom.Document convertToW3CDocument( String doc )
        throws XMLException
    {
        return convertToW3CDocument( new StringSource( doc ) );
    }

    /**
     * Convert to w3c document.
     */
    private static org.w3c.dom.Document convertToW3CDocument( Source source )
        throws XMLException
    {
        DOMResult result = new DOMResult();
        transform( source, result );
        return (org.w3c.dom.Document) result.getNode();
    }

    /**
     * Convert to fast info set document.
     */
    public static XMLBytes convertToDocumentData( Document doc )
        throws XMLException
    {
        return convertToDocumentData( new JDOMSource( doc ) );
    }

    /**
     * Convert to fast info set document.
     */
    public static XMLBytes convertToDocumentData( org.w3c.dom.Document doc )
        throws XMLException
    {
        return convertToDocumentData( new DOMSource( doc ) );
    }

    /**
     * Convert to fast info set document.
     */
    public static XMLBytes convertToDocumentData( String doc )
        throws XMLException
    {
        return convertToDocumentData( new StringSource( doc ) );
    }

    /**
     * Convert to fast info set document.
     */
    private static XMLBytes convertToDocumentData( Source source )
        throws XMLException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastInfosetResult result = new FastInfosetResult( out );
        transform( source, result );
        return new XMLBytes( out.toByteArray() );
    }

    /**
     * Convert to data document.
     */
    public static String convertToString( XMLBytes doc )
        throws XMLException
    {
        return convertToString( new FastInfosetSource( new ByteArrayInputStream( doc.getData() ) ) );
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
    public static String convertToString( org.w3c.dom.Document doc )
        throws XMLException
    {
        return convertToString( new DOMSource( doc ) );
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
