/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import java.util.Set;

import junit.framework.TestCase;

public class StandardCacheTest
    extends TestCase
{
    private StandardCache cache;

    public void testGetMaxSize()
    {
        cache = new StandardCache( 3 );

        cache.put( createCacheEntry( "A", "1", Long.MAX_VALUE ) );

        assertEquals( 3, cache.getMaxEntries() );
    }

    public void testMaxSizeEnsured()
    {
        cache = new StandardCache( 3 );

        cache.put( createCacheEntry( "A", "1", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "B", "2", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "C", "3", Long.MAX_VALUE ) );

        assertEquals( 3, cache.numberOfEntries() );

        cache.put( createCacheEntry( "D", "4", Long.MAX_VALUE ) );

        assertEquals( 3, cache.numberOfEntries() );

    }

    public void testEvictionWhenMaxSizeIsReached()
    {
        cache = new StandardCache( 3 );
        cache.put( createCacheEntry( "A", "1", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "B", "2", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "C", "3", Long.MAX_VALUE ) );

        assertEquals( 3, cache.numberOfEntries() );

        cache.put( createCacheEntry( "D", "4", Long.MAX_VALUE ) );

        Set<String> set = cache.getKeys();
        assertEquals( 3, set.size() );
        assertTrue( set.contains( "C" ) );
        assertTrue( set.contains( "B" ) );
        assertTrue( set.contains( "D" ) );
        assertEquals( 3, cache.numberOfEntries() );

        //make sure B stays in cache, before adding yet another one.
        //B should now be 'last used'
        // SRS - new concurrentlinkedhashmap is not strictly LRU
        // does not reorder before 64 operations
        for ( int i = 0; i < 70; i++ )
        {
            cache.get( "B" );
        }

        cache.put( createCacheEntry( "E", "4", Long.MAX_VALUE ) );

        assertEquals( 3, cache.numberOfEntries() );
        set = cache.getKeys();
        assertEquals( 3, set.size() );
        assertTrue( set.contains( "B" ) );
        assertTrue( set.contains( "D" ) );
        assertTrue( set.contains( "E" ) );
    }

//    /**
//     * Tests that entry order is updated when element is accessed. A read entry is expected to be me moved to the end
//     * of the "list".
//     */
//    public void testAccessOrder()
//    {
//        cache = new StandardCache(3);
//        cache.put(createCacheEntry("A", "1", Long.MAX_VALUE));
//        cache.put(createCacheEntry("B", "2", Long.MAX_VALUE));
//        cache.put(createCacheEntry("C", "3", Long.MAX_VALUE));
//        assertEquals(3, cache.numberOfEntries());
//
//        // make entry with key A move to the last position of cache
//        cache.get("A");
//
//        // add new cache entry
//        cache.put(createCacheEntry("D", "4", Long.MAX_VALUE));
//
//        // B should not longer be in cache at this time
//        assertNull(cache.get("B"));
//
//        // verify order
//        Iterator<String> it = cache.getKeys().iterator();
//        assertEquals("C", it.next());
//        assertEquals("A", it.next());
//        assertEquals("D", it.next());
//        // verify size
//        assertEquals(3, cache.numberOfEntries());
//    }

    public void testRemoveAll()
    {

        cache = new StandardCache( 100 );
        cache.put( createCacheEntry( "A", "1", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "B", "2", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "C", "3", Long.MAX_VALUE ) );
        assertEquals( 3, cache.numberOfEntries() );

        cache.removeAll();

        assertEquals( 0, cache.numberOfEntries() );
    }

    public void testRemove()
    {

        cache = new StandardCache( 100 );
        cache.put( createCacheEntry( "A", "1", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "B", "2", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "C", "3", Long.MAX_VALUE ) );
        assertEquals( 3, cache.numberOfEntries() );

        cache.remove( "B" );

        assertEquals( 2, cache.numberOfEntries() );
    }

    public void testRemoveGroup()
    {

        cache = new StandardCache( 100 );
        cache.put( createCacheEntry( "group1:A", "1", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "group1:B", "2", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "group2:A", "101", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "group2:B", "201", Long.MAX_VALUE ) );
        assertEquals( 4, cache.numberOfEntries() );

        cache.removeGroup( "group2" );

        assertEquals( 2, cache.numberOfEntries() );
    }

    public void testTimeToLive()
    {

        cache = new StandardCache( 100 );
        cache.put( createCacheEntry( "group1:A", "1", 200 ) );

        try
        {
            Thread.sleep( 250 );
        }
        catch ( InterruptedException e )
        {
            fail( "Exception while running test: " + e.getMessage() );
        }

        assertNull( "Expected no cache entry", cache.get( "group1:A" ) );
    }

    @SuppressWarnings({"unchecked"})
    private CacheEntry createCacheEntry( String key, Object value, long timeToLive )
    {

        return new CacheEntry( key, value, timeToLive );
    }
}
