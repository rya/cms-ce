/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.VerticalRuntimeException;

public class VerticalEngineRuntimeException
    extends VerticalRuntimeException
{
    /**
     * Construct the exception.
     */
    public VerticalEngineRuntimeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalEngineRuntimeException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalEngineRuntimeException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
