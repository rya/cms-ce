/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.RelatedContentKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class RelatedContentKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new RelatedContentKey( new ContentVersionKey( 1 ), new ContentKey( 1 ) );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new RelatedContentKey( new ContentVersionKey( 1 ), new ContentKey( 2 ) ),
            new RelatedContentKey( new ContentVersionKey( 2 ), new ContentKey( 1 ) )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new RelatedContentKey( new ContentVersionKey( 1 ), new ContentKey( 1 ) );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new RelatedContentKey( new ContentVersionKey( 1 ), new ContentKey( 1 ) );
    }
}
