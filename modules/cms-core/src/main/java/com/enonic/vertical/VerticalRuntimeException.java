/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import java.lang.reflect.Constructor;

import org.slf4j.LoggerFactory;

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


    public static void error( Class logger, Class exception, String message )
    {
        error(logger, exception, message);
    }

    public static void error( Class logger, Class exception, String message, String xc ) {

    }

    public static void error( Class logger, Class exception, String message, Throwable cause )
    {
        if ( cause != null )
        {
            LoggerFactory.getLogger( logger.getName() ).error( message, cause );
        }
        else
        {
            LoggerFactory.getLogger( logger.getName() ).error( message );
        }

        RuntimeException runtimeException;

        try
        {
            Constructor constructor = exception.getConstructor( String.class );
            Throwable result = (Throwable) constructor.newInstance( message );
            result.initCause( cause );
            runtimeException = (RuntimeException) result;
        }
        catch ( Exception e )
        {
            throw new VerticalRuntimeException( "Failed to create exception [" + exception.getName() + "]", e );
        }

        throw runtimeException;

    }

}
