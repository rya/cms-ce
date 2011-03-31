/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.config;

/**
 * This class implements the cache configuration.
 */
public final class CacheConfig
{
    /**
     * Memory capacity.
     */
    private final int memoryCapacity;

    /**
     * Time to live. 0 is eternal;
     */
    private final int timeToLive;

    /**
     * Disk capacity.
     */
    private final int diskCapacity;

    /**
     * Construct the config.
     */
    public CacheConfig( int memoryCapacity, int diskCapacity, int timeToLive )
    {
        this.memoryCapacity = memoryCapacity;
        this.diskCapacity = diskCapacity;
        this.timeToLive = timeToLive;
    }

    /**
     * Return true if overflow to disk.
     */
    public boolean getDiskOverflow()
    {
        return this.diskCapacity > 0;
    }

    /**
     * Return the disk capacity.
     */
    public int getDiskCapacity()
    {
        return this.diskCapacity;
    }

    /**
     * Return memory capacity.
     */
    public int getMemoryCapacity()
    {
        return this.memoryCapacity;
    }

    /**
     * Return time to live in seconds.
     */
    public int getTimeToLive()
    {
        return timeToLive;
    }
}
