/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.resource;

import com.enonic.cms.core.resource.ResourceKey;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;


public class ResourceKeyEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return new ResourceKey( "ABC" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{new ResourceKey( "CBA" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return new ResourceKey( "ABC" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return new ResourceKey( "ABC" );
    }
}
