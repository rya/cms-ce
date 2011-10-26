/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

public abstract class AbstractCache
    implements Cache
{
    @Autowired
    @Qualifier("cacheFacadeManager")
    private CacheManager cacheManager;

    private String cacheName;

    public AbstractCache( String cacheName )
    {
        this.cacheName = cacheName;
    }

    @ManagedAttribute
    public long getTimeToLiveInSeconds()
    {
        return getCache().getTimeToLive();
    }

    @ManagedAttribute
    public long getMaxElementsInMemory()
    {
        return getCache().getMemoryCapacity();
    }

    @ManagedAttribute
    public long getObjectCount()
    {
        return getCache().getCount();
    }

    @ManagedAttribute
    public long getCacheHits()
    {
        return getCache().getHitCount();
    }

    @ManagedAttribute
    public long getCacheMisses()
    {
        return getCache().getMissCount();
    }

    @ManagedOperation
    public void clearCache()
    {
        getCache().removeAll();
    }

    @ManagedOperation
    public void clearStatistics()
    {
        getCache().clearStatistics();
    }

    private CacheFacade getCache()
    {
        return this.cacheManager.getOrCreateCache( cacheName );
    }
}
