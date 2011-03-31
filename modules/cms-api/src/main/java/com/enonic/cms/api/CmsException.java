/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api;

/**
 * This class implements the cms exception.
 */
public final class CmsException
    extends RuntimeException
{
    /**
     * Serial version UID.
     */
    private final static long serialVersionUID = 1L;

    /**
     * Construct the exception.
     */
    public CmsException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public CmsException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public CmsException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
