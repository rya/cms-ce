/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

/**
 * Jul 27, 2010
 */
public class ContentByCategoryQuery
    extends AbstractContentArchiveQuery
{
    @Override
    public void validate()
    {
        super.validate();

        if ( !hasCategoryFilter() )
        {
            throw new InvalidContentByCategoryQueryException( "Required category filter missing" );
        }
    }
}
