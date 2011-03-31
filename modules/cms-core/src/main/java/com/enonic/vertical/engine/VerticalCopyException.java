/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalCopyException
    extends VerticalEngineException
{
    /**
     * Construct the exception.
     */
    public VerticalCopyException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalCopyException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalCopyException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}