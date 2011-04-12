/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index;


import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.IllegalQueryException;
import junit.framework.TestCase;

public class ContentIndexQueryTest
    extends TestCase
{

    public void testFullTextValidation()
    {

        // one char illegal
        try
        {
            new ContentIndexQuery( "fulltext CONTAINS \"A\"" );
            fail( "Expected Exception" );
        }
        catch ( IllegalQueryException e )
        {
            // expected
        }

        // two chars illegal
        try
        {
            new ContentIndexQuery( "fulltext CONTAINS \"AB\"" );
            fail( "Expected Exception" );
        }
        catch ( IllegalQueryException e )
        {
            // expected
        }

        // three chars are legal 
        ContentIndexQuery query = new ContentIndexQuery( "fulltext CONTAINS \"ABC\"" );
        assertNotNull( query );

    }
}
