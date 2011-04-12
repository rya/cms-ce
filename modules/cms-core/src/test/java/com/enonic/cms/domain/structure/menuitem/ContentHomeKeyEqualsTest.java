/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.menuitem;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.menuitem.ContentHomeKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.domain.SiteKey;


public class ContentHomeKeyEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 1 ) );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        final ContentHomeKey instance1 = new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 2 ) );
        final ContentHomeKey instance2 = new ContentHomeKey( new SiteKey( 2 ), new ContentKey( 1 ) );

        return new Object[]{instance1, instance2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 1 ) );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 1 ) );
    }
}
