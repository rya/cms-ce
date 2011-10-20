/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalRemoveException
    extends VerticalEngineException
{
    public VerticalRemoveException( String message )
    {
        super( message );
    }

    public VerticalRemoveException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
