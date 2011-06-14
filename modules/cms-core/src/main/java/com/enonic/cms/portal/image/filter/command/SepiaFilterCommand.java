/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image.filter.command;

import com.enonic.cms.portal.image.filter.BuilderContext;
import com.enonic.cms.portal.image.filter.effect.SepiaFilter;

public final class SepiaFilterCommand
    extends FilterCommand
{
    public SepiaFilterCommand()
    {
        super( "sepia" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        int d = getIntArg( args, 0, 20 );
        return new SepiaFilter( d );
    }
}