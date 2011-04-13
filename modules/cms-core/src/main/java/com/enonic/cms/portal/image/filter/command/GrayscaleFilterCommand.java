/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image.filter.command;

import com.jhlabs.image.GrayscaleFilter;

import com.enonic.cms.portal.image.filter.BuilderContext;

public final class GrayscaleFilterCommand
    extends FilterCommand
{
    public GrayscaleFilterCommand()
    {
        super( "grayscale" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new GrayscaleFilter();
    }
}