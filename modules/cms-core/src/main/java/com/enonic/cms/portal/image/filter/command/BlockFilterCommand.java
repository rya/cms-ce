/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image.filter.command;

import com.enonic.cms.portal.image.filter.BuilderContext;
import com.jhlabs.image.BlockFilter;

public final class BlockFilterCommand
    extends FilterCommand
{
    public BlockFilterCommand()
    {
        super( "block" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        BlockFilter filter = new BlockFilter();
        filter.setBlockSize( getIntArg( args, 0, 2 ) );
        return filter;
    }
}
