/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalRemoveException
    extends VerticalEngineException
{
    /**
     * Construct the exception.
     */
    public VerticalRemoveException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalRemoveException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalRemoveException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
