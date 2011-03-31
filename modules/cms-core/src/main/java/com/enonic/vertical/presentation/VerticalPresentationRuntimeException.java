/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.presentation;

import com.enonic.vertical.VerticalRuntimeException;

public class VerticalPresentationRuntimeException
    extends VerticalRuntimeException
{
    /**
     * Construct the exception.
     */
    public VerticalPresentationRuntimeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalPresentationRuntimeException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
