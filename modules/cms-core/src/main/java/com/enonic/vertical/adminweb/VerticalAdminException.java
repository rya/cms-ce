/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.vertical.VerticalException;

public class VerticalAdminException
    extends VerticalException
{

    /**
     * Construct the exception.
     */
    public VerticalAdminException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
