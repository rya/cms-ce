/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

/**
 * Exception that is used in the xpath evaluation.
 */
public final class IndexPathException
    extends RuntimeException
{
    /**
     * Constructs the exception.
     */
    public IndexPathException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }

    /**
     * Constructs the exception.
     */
    public IndexPathException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
