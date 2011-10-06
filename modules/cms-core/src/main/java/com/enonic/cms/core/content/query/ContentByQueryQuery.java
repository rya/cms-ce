/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

/**
 * Jul 27, 2010
 */
public class ContentByQueryQuery
    extends AbstractContentArchiveQuery
{
    @Override
    public void validate()
    {
        super.validate();

        if ( !hasQuery() )
        {
            throw new InvalidContentByQueryQueryException( "Required query missing" );
        }
    }
}
