/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.transform.Source;

import com.enonic.cms.framework.xml.StringSource;

/**
 * This class implements the holder for an xslt template.
 */
public class XsltResource
{
    /**
     * Name of template.
     */
    private final String name;

    /**
     * Content of the template.
     */
    private final String content;

    /**
     * Construct the template.
     */
    public XsltResource( String content )
    {
        this( null, content );
    }

    /**
     * Construct the template.
     */
    public XsltResource( String name, String content )
    {
        this.name = name != null ? name : "unknown";
        this.content = content;
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Return the content.
     */
    public String getContent()
    {
        return this.content;
    }

    /**
     * Return the source.
     */
    public Source getAsSource()
    {
        return new StringSource( this.content, getUri() );
    }

    private String getUri()
    {
        if ( this.name.contains( ":/" ) )
        {
            return this.name;
        }

        try
        {
            return "dummy:/" + URLEncoder.encode( this.name, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new AssertionError( e );
        }
    }
}
