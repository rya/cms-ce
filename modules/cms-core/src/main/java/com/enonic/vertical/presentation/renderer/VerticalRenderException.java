/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.presentation.renderer;

/**
 * Render exception.
 */
public final class VerticalRenderException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public VerticalRenderException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalRenderException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
