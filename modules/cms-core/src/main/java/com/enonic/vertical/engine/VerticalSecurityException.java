/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalSecurityException
    extends VerticalEngineException
{
    /**
     * Construct the exception.
     */
    public VerticalSecurityException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalSecurityException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalSecurityException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
