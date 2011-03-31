/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.wizard;

import com.enonic.vertical.adminweb.VerticalAdminException;

public class WizardException
    extends VerticalAdminException
{
    /**
     * Construct the exception.
     */
    public WizardException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public WizardException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public WizardException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
