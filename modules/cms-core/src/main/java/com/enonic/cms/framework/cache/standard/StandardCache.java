/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;


/**
 * Our standard cache that uses the LRU eviction method.
 */
final class StandardCache
{

    private final int maxEntries;

    private final ConcurrentLinkedHashMap<String, SoftReference<CacheEntry>> map;

    public StandardCache( final int maxEntries )
    {
        this.map = new Builder<String, SoftReference<CacheEntry>>().maximumWeightedCapacity( maxEntries ).build();
        this.maxEntries = maxEntries;
    }

    public int getMaxEntries()
    {

        return maxEntries;
    }

    public int numberOfEntries()
    {

        return map.size();
    }

    protected Set<String> getKeys()
    {
        // this method is only used in tests
        return map.keySet();
    }

    public CacheEntry get( String key )
    {

        SoftReference referenceWrapper = doGet( key );
        if ( referenceWrapper == null )
        {
            return null;
        }

        CacheEntry entry = (CacheEntry) referenceWrapper.get();

        if ( entry == null || entry.isExpired() )
        {
            doRemove( key );
            return null;
        }

        entry.updateLastAccessTime();

        return entry;
    }

    public void put( CacheEntry entry )
    {

        doPut( entry );
    }

    public void removeAll()
    {

        doRemoveAll();
    }

    public void remove( String key )
    {

        doRemove( key );
    }

    public void removeGroup( String group )
    {

        String prefix = group + ":";
        Set<String> keys = doGetByPrefix( prefix );

        doRemoveKeys( keys );
    }

    public void removeGroupByPrefix( String prefix )
    {
        Set<String> keys = doGetByPrefix( prefix );

        doRemoveKeys( keys );
    }

    private void doRemoveKeys( Collection<String> keys )
    {

        for ( String key : keys )
        {
            doRemove( key );
        }
    }

    private SoftReference doGet( String key )
    {
        return map.get( key );
    }

    private void doPut( CacheEntry entry )
    {
        map.put( entry.getKey(), new SoftReference<CacheEntry>( entry ) );
    }

    private void doRemove( String key )
    {
        map.remove( key );
    }

    private void doRemoveAll()
    {
        map.clear();
    }

    private Set<String> doGetByPrefix( String prefix )
    {

        HashSet<String> keys = new HashSet<String>();

        Map<String, SoftReference<CacheEntry>> mapClone = new HashMap<String, SoftReference<CacheEntry>>( map );
        for ( String key : mapClone.keySet() )
        {
            if ( key.startsWith( prefix ) )
            {
                keys.add( key );
            }
        }

        return keys;
    }
}
