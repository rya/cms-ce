/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * Response wrapper that check the status code.
 */
public class HttpServletResponseWrapper
    extends javax.servlet.http.HttpServletResponseWrapper
{
    /**
     * If true, sendError() called.
     */
    private boolean error;

    /**
     * Status code.
     */
    private int status;

    /**
     * Status message.
     */
    private String message = null;

    private String redirectURL = null;

    /**
     * Construct the wrapper.
     */
    public HttpServletResponseWrapper( HttpServletResponse res )
    {
        super( res );
    }

    /**
     * Return the status.
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * Return the status message.
     */
    public String getStatusMessage()
    {
        return this.message;
    }

    /**
     * Send error code.
     */
    public void sendError( int sc )
        throws IOException
    {
        sendError( sc, null );
    }

    /**
     * Send error code.
     */
    public void sendError( int sc, String message )
        throws IOException
    {
        this.error = true;
        this.status = sc;
        this.message = message;
    }

    /**
     * Commit the error status.
     */
    public void commitError()
        throws IOException
    {
        if ( this.error )
        {
            if ( this.message != null )
            {
                super.sendError( this.status, this.message );
            }
            else if ( this.status != SC_OK )
            {
                super.sendError( this.status );
            }
        }
    }

    public void sendRedirect( String redirectURL )
        throws IOException
    {
        this.redirectURL = redirectURL;
        super.sendRedirect( redirectURL );
    }

    public String getRedirectURL()
    {
        return redirectURL;
    }
}
