/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.trace;

/**
 * This class implements the recursion exception.
 */
public final class RecursionException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public RecursionException( String message )
    {
        super( message );
    }
}
