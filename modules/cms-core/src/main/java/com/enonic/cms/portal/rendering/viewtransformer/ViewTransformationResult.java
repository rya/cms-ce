/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

/**
 * Apr 30, 2009
 */
public class ViewTransformationResult
{
    private String content;

    private String httpContentType;

    private String outputMethod;

    private String outputEncoding;

    private String outputMediaType;

    public String getOutputMethod()
    {
        return outputMethod;
    }

    public void setOutputMethod( String outputMethod )
    {
        this.outputMethod = outputMethod;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setHttpContentType( String value )
    {
        this.httpContentType = value;
    }

    public String getHttpContentType()
    {
        return httpContentType;
    }

    public String getOutputEncoding()
    {
        return outputEncoding;
    }

    public void setOutputEncoding( String outputEncoding )
    {
        this.outputEncoding = outputEncoding;
    }

    public String getOutputMediaType()
    {
        return outputMediaType;
    }

    public void setOutputMediaType( String outputMediaType )
    {
        this.outputMediaType = outputMediaType;
    }


}
