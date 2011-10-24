/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.vertical.VerticalException;

public class VerticalAdminException
    extends VerticalException
{
    public VerticalAdminException( String message )
    {
        super( message );
    }

    public VerticalAdminException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public VerticalAdminException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
