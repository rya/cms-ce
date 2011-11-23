/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import com.enonic.cms.framework.xml.XMLDocument;

/**
 * This interface defines the cache definition.
 */
public interface CacheFacade
{
    /**
     * Name of cache.
     */
    public String getName();
    
    /**
     * Return the memory capacity.
     */
    public int getMemoryCapacity();

    /**
     * Return the time to live in seconds. 0 if eternal cache.
     */
    public int getTimeToLive();

    /**
     * Return object from cache.
     */
    public Object get( String group, String key );

    /**
     * Add object to cache.
     */
    public void put( String group, String key, Object value );

    /**
     * Add object to cache.
     *
     * @param timeToLive number of seconds the object is supposed live
     */
    public void put( String group, String key, Object value, int timeToLive );

    /**
     * Remove object from cache.
     */
    public void remove( String group, String key );

    /**
     * Clear the cache.
     */
    public void removeGroup( String group );

    /**
     * Removes all entries that have a group that starts with given prefix.
     */
    public void removeGroupByPrefix( String prefix );

    /**
     * Clear the cache.
     */
    public void removeAll();

    /**
     * Number of times a requested item was found in cache.
     */
    public int getHitCount();

    /**
     * Number of times a requested item was not found in cache.
     */
    public int getMissCount();

    /**
     * Return the number of objects in cache.
     */
    public int getCount();

    /**
     * Clears the statistics.
     */
    public void clearStatistics();

    /**
     * Return xml details.
     */
    public XMLDocument getInfoAsXml();
}
