/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.cms.core.resource.ResourceKey;

public class InvalidStylesheetException
    extends RuntimeException
{
    public InvalidStylesheetException( ResourceKey stylesheetKey, Throwable t )
    {
        super( "Invalid stylesheet: " + stylesheetKey, t );
    }
}
