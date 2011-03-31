/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalKeyException
    extends VerticalEngineException
{
    /**
     * Construct the exception.
     */
    public VerticalKeyException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalKeyException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalKeyException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
