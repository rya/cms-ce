/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.VerticalException;

public abstract class VerticalEngineException
    extends VerticalException
{
    public VerticalEngineException( String message )
    {
        super( message );
    }

    public VerticalEngineException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VerticalEngineException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
