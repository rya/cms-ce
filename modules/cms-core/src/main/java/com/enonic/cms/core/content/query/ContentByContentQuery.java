/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

/**
 * Jul 27, 2010
 */
public class ContentByContentQuery
    extends AbstractContentArchiveQuery
{
    @Override
    public void validate()
    {
        super.validate();

        if ( !hasContentFilter() )
        {
            throw new InvalidContentByContentQueryException( "Required content filter missing" );
        }
    }
}
