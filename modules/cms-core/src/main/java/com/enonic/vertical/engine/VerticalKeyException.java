/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalKeyException
    extends VerticalEngineException
{
    public VerticalKeyException( String message )
    {
        super( message );
    }

    public VerticalKeyException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VerticalKeyException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
