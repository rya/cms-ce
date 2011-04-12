/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.menuitem;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.domain.SiteKey;


public class ContentHomeEntityEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        ContentHomeEntity instance = new ContentHomeEntity();
        instance.setKey( new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 1 ) ) );
        return instance;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        ContentHomeEntity instance1 = new ContentHomeEntity();
        instance1.setKey( new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 2 ) ) );

        ContentHomeEntity instance2 = new ContentHomeEntity();
        instance2.setKey( new ContentHomeKey( new SiteKey( 2 ), new ContentKey( 1 ) ) );

        ContentHomeEntity instance3 = new ContentHomeEntity();
        instance2.setKey( new ContentHomeKey( new SiteKey( 4 ), new ContentKey( 5 ) ) );

        return new Object[]{instance1, instance2, instance3};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        ContentHomeEntity instance = new ContentHomeEntity();
        instance.setKey( new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 1 ) ) );
        return instance;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        ContentHomeEntity instance = new ContentHomeEntity();
        instance.setKey( new ContentHomeKey( new SiteKey( 1 ), new ContentKey( 1 ) ) );
        return instance;
    }
}
