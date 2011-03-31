/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.vertical.VerticalRuntimeException;

public class VerticalAdminRuntimeException
    extends VerticalRuntimeException
{
    /**
     * Construct the exception.
     */
    public VerticalAdminRuntimeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public VerticalAdminRuntimeException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public VerticalAdminRuntimeException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}