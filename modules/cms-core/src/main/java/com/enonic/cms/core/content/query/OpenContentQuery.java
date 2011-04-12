/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

/**
 * Jul 27, 2010
 */
public class OpenContentQuery
    extends AbstractContentArchiveQuery
{
    @Override
    public void validate()
    {
        super.validate();

        if ( !hasCategoryFilter() && !hasContentFilter() && !hasQuery() && getCount() > 100 )
        {
            throw new InvalidOpenQueryException( "An open query cannot query more than 100 content at a time" );
        }
    }
}
