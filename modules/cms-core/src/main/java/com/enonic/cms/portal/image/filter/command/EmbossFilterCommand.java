/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image.filter.command;

import com.jhlabs.image.EmbossFilter;

import com.enonic.cms.portal.image.filter.BuilderContext;

public final class EmbossFilterCommand
    extends FilterCommand
{
    public EmbossFilterCommand()
    {
        super( "emboss" );
    }

    protected Object doBuild( BuilderContext context, Object[] args )
    {
        return new EmbossFilter();
    }
}
