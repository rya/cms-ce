/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.config;

/**
 * This interface defines the cache manager configuration.
 */
public interface CacheManagerConfig
{
    /**
     * Return the disk store path.
     */
    public String getDiskStorePath();

    /**
     * Return the default cache config.
     */
    public CacheConfig getDefaultCacheConfig();

    /**
     * Return cache config for cache name.
     */
    public CacheConfig getCacheConfig( String name );
}
