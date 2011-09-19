/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl;

/**
 * A modification of the overcomplicated ESL exception in previous releases.
 */
public final class ESLException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public ESLException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public ESLException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
