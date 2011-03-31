/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.wizard;

import com.enonic.vertical.adminweb.VerticalAdminRuntimeException;

public class WizardRuntimeException
    extends VerticalAdminRuntimeException
{
    /**
     * Construct the exception.
     */
    public WizardRuntimeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public WizardRuntimeException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public WizardRuntimeException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
