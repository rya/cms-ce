/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import org.junit.Test;

import junit.framework.Assert;

public class TIntObjectHashMapTest
{
    @Test
    public void testKeys()
    {
        final TIntObjectHashMap map = new TIntObjectHashMap();

        int[] keys = map.keys();
        Assert.assertNotNull( keys );
        Assert.assertEquals( 0, keys.length );

        map.put( 1, "2" );

        keys = map.keys();
        Assert.assertNotNull( keys );
        Assert.assertEquals( 1, keys.length );
        Assert.assertEquals( 1, keys[0] );
    }

    @Test
    public void testSize()
    {
        final TIntObjectHashMap map = new TIntObjectHashMap();
        Assert.assertEquals( 0, map.size() );

        map.put( 1, "2" );
        map.put( 1, "3" );
        Assert.assertEquals( 1, map.size() );
    }

    @Test
    public void testContains()
    {
        final TIntObjectHashMap map = new TIntObjectHashMap();
        Assert.assertFalse( map.contains( "1" ) );

        map.put( 1, "2" );
        Assert.assertTrue( map.contains( "2" ) );
    }

    @Test
    public void testContainsKey()
    {
        final TIntObjectHashMap map = new TIntObjectHashMap();
        Assert.assertFalse( map.containsKey( 1 ) );

        map.put( 1, "2" );
        Assert.assertTrue( map.containsKey( 1 ) );
    }

    @Test
    public void testGet()
    {
        final TIntObjectHashMap map = new TIntObjectHashMap();
        map.put( 1, "2" );
        map.put( 2, "3" );

        Assert.assertEquals( "2", map.get( 1 ) );
        Assert.assertEquals( "3", map.get( 2 ) );
    }
}