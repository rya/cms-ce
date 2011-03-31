/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.base;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;

import com.google.common.io.Closeables;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;


final class XsltProcessorImpl
    implements XsltProcessor
{
    private final Transformer transformer;

    public XsltProcessorImpl( Transformer transformer )
    {
        this.transformer = transformer;
    }

    public String getOutputMethod()
    {
        return this.transformer.getOutputProperty( OutputKeys.METHOD );
    }

    public String getOutputMediaType()
    {
        return this.transformer.getOutputProperty( OutputKeys.MEDIA_TYPE );
    }

    public String getOutputEncoding()
    {
        return this.transformer.getOutputProperty( OutputKeys.ENCODING );
    }

    public void setOmitXmlDecl( boolean omitXmlDecl )
    {
        this.transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, omitXmlDecl ? "yes" : "no" );
    }

    public String getContentType()
    {
        StringBuffer contentType = new StringBuffer();
        String outputMediaType = getOutputMediaType();
        if ( outputMediaType != null )
        {
            contentType.append( outputMediaType );
        }
        else
        {
            String outputMethod = getOutputMethod();
            if ( "xml".equals( outputMethod ) )
            {
                contentType.append( "text/xml" );
            }
            else if ( "html".equals( outputMethod ) || "xhtml".equals( outputMethod ) )
            {
                contentType.append( "text/html" );
            }
            else
            {
                contentType.append( "text/plain" );
            }
        }

        String outputEncoding = getOutputEncoding();
        contentType.append( "; charset=" );
        contentType.append( outputEncoding != null ? outputEncoding : "utf-8" );
        return contentType.toString();
    }

    /**
     * Process the xml with stylesheet.
     */
    public String process( Source xml )
        throws XsltProcessorException
    {
        StringWriter writer = new StringWriter();

        try {
            process(xml, writer);
        } finally {
            Closeables.closeQuietly( writer );
        }

        return writer.toString();
    }

    public void process( Source xml, Result result )
        throws XsltProcessorException
    {
        XsltProcessorErrors errors = new XsltProcessorErrors();

        try
        {
            this.transformer.setErrorListener( errors );
            this.transformer.transform( xml, result );

            if ( errors.hasErrors() )
            {
                throw new XsltProcessorException( "An error occured during transformation", errors );
            }
        }
        catch ( Exception e )
        {
            throw new XsltProcessorException( e, errors );
        }
    }

    public void process( Source xml, Writer writer )
        throws XsltProcessorException
    {
        StreamResult result = new StreamResult( writer );
        process(xml, result);
    }

    public Object getParameter( String name )
    {
        return this.transformer.getParameter( name );
    }

    public void setParameter( String name, Object value )
    {
        this.transformer.setParameter( name, value );
    }

    public void clearParameters()
    {
        this.transformer.clearParameters();
    }
}
