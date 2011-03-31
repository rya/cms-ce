/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the base for a normalized exception. The cause of this exception will be normalized to include the last (or inner)
 * exception.
 */
public abstract class NormalizedException
    extends Exception
{
    /**
     * Construct the exception.
     */
    public NormalizedException( String message )
    {
        this( message, null );
    }

    /**
     * Construct the exception.
     */
    public NormalizedException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }

    /**
     * Construct the exception.
     */
    public NormalizedException( String message, Throwable cause )
    {
        super( message, cause != null ? findRealCause( cause ) : null );
    }

    /**
     * Find the real cause.
     */
    private static Throwable findRealCause( Throwable cause )
    {
        if ( cause instanceof InvocationTargetException )
        {
            return findRealCause( ( (InvocationTargetException) cause ).getTargetException() );
        }
        else
        {
            Throwable other = cause.getCause();
            if ( other != null )
            {
                return findRealCause( other );
            }
            else
            {
                return cause;
            }
        }
    }
}
