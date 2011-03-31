/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

/**
 * This class implements a coded exception.
 */
public abstract class CodedException
    extends NormalizedException
{
    /**
     * Error code.
     */
    private final String errorCode;

    /**
     * Construct the exception.
     */
    public CodedException( String errorCode, String message )
    {
        super( message );
        this.errorCode = errorCode;
    }

    /**
     * Construct the exception.
     */
    public CodedException( String errorCode, Throwable cause )
    {
        super( cause );
        this.errorCode = errorCode;
    }

    /**
     * Construct the exception.
     */
    public CodedException( String errorCode, String message, Throwable cause )
    {
        super( message, cause );
        this.errorCode = errorCode;
    }

    /**
     * Return the error code.
     */
    public String getErrorCode()
    {
        return this.errorCode;
    }
}
