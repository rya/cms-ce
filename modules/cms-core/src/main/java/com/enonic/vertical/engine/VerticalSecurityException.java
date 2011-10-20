/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalSecurityException
    extends VerticalEngineException
{
    public VerticalSecurityException( String message )
    {
        super( message );
    }

    public VerticalSecurityException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VerticalSecurityException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
