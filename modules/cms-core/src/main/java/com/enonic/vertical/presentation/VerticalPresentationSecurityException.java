/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.presentation;

public class VerticalPresentationSecurityException
    extends VerticalPresentationException
{
    /**
     * Construct the exception.
     */
    public VerticalPresentationSecurityException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalPresentationSecurityException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalPresentationSecurityException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
