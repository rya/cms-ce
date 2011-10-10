/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.stylesheet;

import com.enonic.cms.core.resource.ResourceKey;


public class StylesheetNotFoundException
    extends RuntimeException
{

    private ResourceKey stylesheetKey;

    public StylesheetNotFoundException( ResourceKey stylesheetKey )
    {
        super( "Stylesheet not found: " + stylesheetKey );
        this.stylesheetKey = stylesheetKey;
    }

    public ResourceKey getStylesheetKey()
    {
        return stylesheetKey;
    }
}
