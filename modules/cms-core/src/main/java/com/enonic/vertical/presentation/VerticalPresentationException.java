/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.presentation;

public class VerticalPresentationException
    extends com.enonic.vertical.VerticalException
{
    /**
     * Construct the exception.
     */
    public VerticalPresentationException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalPresentationException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalPresentationException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}