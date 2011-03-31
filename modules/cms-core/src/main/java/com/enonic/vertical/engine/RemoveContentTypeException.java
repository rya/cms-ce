/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.VerticalException;

public class RemoveContentTypeException
    extends VerticalException
{
    /**
     * Construct the exception.
     */
    public RemoveContentTypeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public RemoveContentTypeException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public RemoveContentTypeException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
