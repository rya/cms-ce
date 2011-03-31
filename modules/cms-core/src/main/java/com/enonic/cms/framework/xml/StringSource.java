/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * A helper class which provides a JAXP {@link Source} from a String which can be read as many times as required.
 */
public final class StringSource
    extends StreamSource
    implements Serializable
{
    /**
     * Default encoding.
     */
    private final static String DEFAULT_ENCODING = "UTF-8";

    /**
     * The text.
     */
    private final String text;

    /**
     * Encoding.
     */
    private final String encoding;

    /**
     * Construct the source.
     */
    public StringSource( String text )
    {
        this( text, null, null );
    }

    /**
     * Construct the source.
     */
    public StringSource( String text, String systemId )
    {
        this( text, systemId, null );
    }

    /**
     * Construct the source.
     */
    public StringSource( String text, String systemId, String encoding )
    {
        this.text = text;
        this.encoding = encoding != null ? encoding : DEFAULT_ENCODING;
        setSystemId( systemId );
    }

    /**
     * Return the input stream. Can be aquired multiple times.
     */
    public InputStream getInputStream()
    {
        try
        {
            return new ByteArrayInputStream( this.text.getBytes( this.encoding ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Return the reader. Can be aquired multiple times.
     *
     * @return
     */
    public Reader getReader()
    {
        return new StringReader( this.text );
    }
}
