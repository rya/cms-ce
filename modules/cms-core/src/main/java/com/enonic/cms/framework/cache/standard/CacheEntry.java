/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

/**
 * This class implements the cache entry.
 */
final class CacheEntry
{
    /**
     * Cache key.
     */
    private final String key;

    /**
     * Time to live in milliseconds.
     */
    private final long timeToLive;

    /**
     * Cache value.
     */
    private final Object value;

    /**
     * Last access time.
     */
    private long lastAccessTime;

    /**
     * Construct the entry.
     */
    public CacheEntry( final String key, final Object value, final long timeToLive )
    {
        this.key = key;
        this.timeToLive = timeToLive;
        this.value = value;
        updateLastAccessTime();
    }

    /**
     * Return the key.
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Return the value.
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     * Update last access time.
     */
    public void updateLastAccessTime()
    {
        this.lastAccessTime = System.currentTimeMillis();
    }

    /**
     * Return true if expired.
     */
    public boolean isExpired()
    {
        return ( this.timeToLive > 0 ) && ( System.currentTimeMillis() - this.lastAccessTime ) > this.timeToLive;
    }
}
