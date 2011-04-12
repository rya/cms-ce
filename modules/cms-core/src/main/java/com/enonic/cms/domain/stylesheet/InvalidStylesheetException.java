/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.stylesheet;

import com.enonic.cms.core.resource.ResourceKey;


public class InvalidStylesheetException
    extends RuntimeException
{

    private ResourceKey stylesheetKey;

    public InvalidStylesheetException( ResourceKey stylesheetKey, Throwable t )
    {
        super( "Invalid stylesheet: " + stylesheetKey, t );
        this.stylesheetKey = stylesheetKey;
    }

    public ResourceKey getStylesheetKey()
    {
        return stylesheetKey;
    }
}
