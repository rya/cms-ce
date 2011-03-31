/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.VerticalException;

public abstract class VerticalEngineException
    extends VerticalException
{
    /**
     * Construct the exception.
     */
    public VerticalEngineException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalEngineException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalEngineException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
