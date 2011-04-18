/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * Root exception for all Vertical exceptions.
 */
public class VerticalException
        extends RuntimeException
{
    /**
     * Message key.
     */
    private int messageKey;

    /**
     * Construct the exception.
     */
    public VerticalException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalException( Throwable cause )
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
