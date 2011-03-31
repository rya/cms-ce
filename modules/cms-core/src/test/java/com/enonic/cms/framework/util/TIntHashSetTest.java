/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import org.junit.Test;

import junit.framework.Assert;

public class TIntHashSetTest
{
    @Test
    public void testSize()
    {
        final TIntHashSet set = new TIntHashSet();
        Assert.assertEquals( 0, set.size() );

        set.add( 1, 2, 3, 1, 2, 3 );
        Assert.assertEquals( 3, set.size() );
    }

    @Test
    public void testContains()
    {
        final TIntHashSet set = new TIntHashSet();
        Assert.assertFalse( set.contains( 1 ) );

        set.add( 1 );
        Assert.assertTrue( set.contains( 1 ) );
    }

    @Test
    public void testToArray()
    {
        final TIntHashSet set = new TIntHashSet();

        int[] values = set.toArray();
        Assert.assertNotNull( values );
        Assert.assertEquals( 0, values.length );

        set.add( 1 );

        values = set.toArray();
        Assert.assertNotNull( values );
        Assert.assertEquals( 1, values.length );
        Assert.assertEquals( 1, values[0] );

        set.add( 1, 2, 3 );

        values = set.toArray();
        Assert.assertNotNull( values );
        Assert.assertEquals( 3, values.length );
        Assert.assertEquals( 1, values[0] );
        Assert.assertEquals( 2, values[1] );
        Assert.assertEquals( 3, values[2] );
    }
}