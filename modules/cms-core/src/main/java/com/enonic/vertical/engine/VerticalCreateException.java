/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public class VerticalCreateException
    extends VerticalEngineException
{

    public VerticalCreateException( String message )
    {
        super( message );
    }

    public VerticalCreateException()
    {
        super( "dummmy" );
    }
}
