/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.transform.Source;

import org.apache.commons.codec.digest.DigestUtils;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.StringSource;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

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
     * Digest.
     */
    private String digest;

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
     * Return the digest.
     */
    public String getDigest()
    {
        if ( this.digest == null )
        {
            this.digest = DigestUtils.shaHex( this.content );
        }

        return this.digest;
    }

    /**
     * Return the source.
     */
    public Source getAsSource()
    {
        return new StringSource( this.content, getUri() );
    }

    /**
     * Return as jdom.
     */
    public XMLDocument getAsDocument()
    {
        return XMLDocumentFactory.create( this.content, getUri() );
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
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
