/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

public abstract class AbstractBaseError
    extends RuntimeException
{
    private Integer statusCode;

    protected AbstractBaseError( int statusCode, String message, Throwable cause )
    {
        super( message, cause );
        this.statusCode = new Integer( statusCode );
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }
}
