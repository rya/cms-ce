/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

/**
 * This class implements the bootstrap for hibernate cache. It finds the cache manager to use and holds it in a static way.
 */
public final class HibernateCacheBootstrap
{
    /**
     * Default cache name.
     */
    private final static String DEFAULT_CACHE_NAME = "hibernate";

    /**
     * Instance.
     */
    private static HibernateCacheBootstrap INSTANCE;

    /**
     * Cache manager.
     */
    private CacheManager cacheManager;

    /**
     * Cache name.
     */
    private String cacheName;

    /**
     * Construct.
     */
    public HibernateCacheBootstrap()
    {
        INSTANCE = this;
    }

    /**
     * Return the cache name.
     */
    public String getCacheName()
    {
        return this.cacheName != null ? this.cacheName : DEFAULT_CACHE_NAME;
    }

    /**
     * Return the cache manager.
     */
    public CacheManager getCacheManager()
    {
        return this.cacheManager;
    }

    /**
     * Set the cache name.
     */
    public void setCacheName( String cacheName )
    {
        this.cacheName = cacheName;
    }

    /**
     * Set the cache manager.
     */
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    /**
     * Return the cache.
     */
    public CacheFacade getCache()
    {
        return this.cacheManager.getOrCreateCache( getCacheName() );
    }

    /**
     * Return the cache manager.
     */
    public static HibernateCacheBootstrap getInstance()
    {
        return INSTANCE;
    }
}
