/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.joda.time.DateTime;

/**
 * Apr 23, 2009
 */
public class RenderedWindowResult
    implements Serializable
{
    private String contentEncoding = "UTF-8";

    private String content;

    private String httpContentType;

    private DateTime expirationTimeInCache;

    private String outputMethod;

    public boolean isErrorFree()
    {
        return !( this instanceof ErrorRenderPortletResult );
    }

    public void setContent( String value )
    {
        this.content = value;
    }

    public String getContent()
    {
        return content;
    }

    public String getOutputMethod()
    {
        return outputMethod;
    }

    public void setOutputMethod( String outputMethod )
    {
        this.outputMethod = outputMethod;
    }

    public byte[] getContentAsBytes()
        throws UnsupportedEncodingException
    {
        return content.getBytes( contentEncoding );
    }

    public String getHttpContentType()
    {
        return httpContentType;
    }

    public void setHttpContentType( String httpContentType )
    {
        this.httpContentType = httpContentType;
    }

    public DateTime getExpirationTimeInCache()
    {
        return expirationTimeInCache;
    }

    public void setExpirationTimeInCache( DateTime expirationTimeInCache )
    {
        this.expirationTimeInCache = expirationTimeInCache;
    }

    public void setContentEncoding( String value )
    {
        this.contentEncoding = value;
    }

    public String getContentEncoding()
    {
        return contentEncoding;
    }

    public void stripXHTMLNamespaces()
    {
        if ( content == null )
        {
            return;
        }

        content = stripNamespaces( content, "http://www.w3.org/1999/xhtml" );
    }

    private String stripNamespaces( String content, String namespace )
    {
        String regexp = "\\s+(\\w+:)?xmlns=\"" + namespace + "\"";
        return content.replaceAll( regexp, "" );
    }

    public RenderedWindowResult clone()
    {
        RenderedWindowResult clone = new RenderedWindowResult();
        clone.setContent( content );
        clone.setContentEncoding( contentEncoding );
        clone.setExpirationTimeInCache( expirationTimeInCache );
        clone.setHttpContentType( httpContentType );
        clone.setOutputMethod( outputMethod );

        return clone;
    }


}
