/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import org.junit.Test;

import junit.framework.Assert;

public class TIntArrayListTest
{
    @Test
    public void testGet()
    {
        final TIntArrayList list = new TIntArrayList();

        list.add( 1, 2, 3 );
        Assert.assertEquals( 3, list.get( 2 ) );
    }

    @Test
    public void testSize()
    {
        final TIntArrayList list = new TIntArrayList();
        Assert.assertEquals( 0, list.size() );

        list.add( 1, 2, 3 );
        Assert.assertEquals( 3, list.size() );
    }

    @Test
    public void testContains()
    {
        final TIntArrayList list = new TIntArrayList();
        Assert.assertFalse( list.contains( 1 ) );

        list.add( 1 );
        Assert.assertTrue( list.contains( 1 ) );
    }

    @Test
    public void testToArray()
    {
        final TIntArrayList list = new TIntArrayList();

        int[] values = list.toArray();
        Assert.assertNotNull( values );
        Assert.assertEquals( 0, values.length );

        list.add( 1 );

        values = list.toArray();
        Assert.assertNotNull( values );
        Assert.assertEquals( 1, values.length );
        Assert.assertEquals( 1, values[0] );

        list.add( 2, 3 );

        values = list.toArray();
        Assert.assertNotNull( values );
        Assert.assertEquals( 3, values.length );
        Assert.assertEquals( 1, values[0] );
        Assert.assertEquals( 2, values[1] );
        Assert.assertEquals( 3, values[2] );
    }
}
