/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

/**
 * Root exception for all Vertical runtime exceptions.
 */
public class VerticalRuntimeException
    extends RuntimeException
{
    /**
     * Message key.
     */
    private int messageKey;

    /**
     * Construct the exception.
     */
    public VerticalRuntimeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalRuntimeException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalRuntimeException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }


    /**
     * Return the message key.
     */
    public int getMessageKey()
    {
        return this.messageKey;
    }

    /**
     * Set message key.
     */
    public void setMessageKey( int messageKey )
    {
        this.messageKey = messageKey;
    }
}
