/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import org.junit.Test;

import com.enonic.cms.core.AbstractEqualsTest;

public class SiteEntityEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return createSite( 1 );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{createSite( 2 )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return createSite( 1 );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return createSite( 1 );
    }

    private SiteEntity createSite( int key )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( key );
        return site;
    }
}