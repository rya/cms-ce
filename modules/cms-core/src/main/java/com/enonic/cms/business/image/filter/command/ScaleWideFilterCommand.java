/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.image.filter.command;

import com.enonic.cms.business.image.filter.BuilderContext;
import com.enonic.cms.business.image.filter.effect.ScaleWideFilter;

public final class ScaleWideFilterCommand
    extends FilterCommand
{
    public ScaleWideFilterCommand()
    {
        super( "scalewide" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new ScaleWideFilter( getIntArg( args, 0, 0 ), getIntArg( args, 1, 0 ), getFloatArg( args, 2, 0.5f ) );
    }
}
