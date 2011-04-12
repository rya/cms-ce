/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import org.joda.time.DateTime;

/**
 * This class implements the render result.
 */
public final class RenderedPageResult
{
    private DateTime renderedAt;

    private String contentEncoding = "UTF-8";

    private String content;

    private String redirectPath;

    private String httpContentType;

    private String outputMethod;

    private boolean retrievedFromCache = false;

    private DateTime expirationTime;

    public DateTime getRenderedAt()
    {
        return renderedAt;
    }

    public void setRenderedAt( DateTime renderedAt )
    {
        this.renderedAt = renderedAt;
    }

    public String getContent()
    {
        return content;
    }

    public String getContentEncoding()
    {
        return contentEncoding;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public void setContentEncoding( String value )
    {
        this.contentEncoding = value;
    }

    public String getOutputMethod()
    {
        return outputMethod;
    }

    public void setOutputMethod( String outputMethod )
    {
        this.outputMethod = outputMethod;
    }

    public boolean isRetrievedFromCache()
    {
        return retrievedFromCache;
    }

    public void setRetrievedFromCache( boolean retrievedFromCache )
    {
        this.retrievedFromCache = retrievedFromCache;
    }

    public DateTime getExpirationTime()
    {
        return expirationTime;
    }

    public void setExpirationTime( DateTime value )
    {
        this.expirationTime = value;
    }

    public String getHttpContentType()
    {
        return httpContentType;
    }

    public void setHttpContentType( String value )
    {
        this.httpContentType = value;
    }

    public Object clone()
    {
        RenderedPageResult clone = new RenderedPageResult();
        clone.setContent( content );
        clone.setContentEncoding( contentEncoding );
        clone.setExpirationTime( expirationTime );
        clone.setHttpContentType( httpContentType );
        clone.setOutputMethod( outputMethod );
        clone.setRenderedAt( renderedAt );
        clone.setRetrievedFromCache( retrievedFromCache );
        clone.redirectPath = redirectPath;
        return clone;
    }
}
