/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache;

import java.util.Properties;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

/**
 * This class implements the hibernate cache provider.
 */
public final class HibernateCacheProvider
    implements CacheProvider
{
    /**
     * Builds the cache,
     */
    public Cache buildCache( String region, Properties properties )
    {
        return new HibernateCache( region, HibernateCacheBootstrap.getInstance().getCache() );
    }

    /**
     * @see org.hibernate.cache.CacheProvider#nextTimestamp()
     */
    public long nextTimestamp()
    {
        return Timestamper.next();
    }

    /**
     * @see org.hibernate.cache.CacheProvider#isMinimalPutsEnabledByDefault()
     */
    public boolean isMinimalPutsEnabledByDefault()
    {
        return false;
    }

    /**
     * @see org.hibernate.cache.CacheProvider#stop()
     */
    public void stop()
    {
        // Do nothing
    }

    /**
     * @see org.hibernate.cache.CacheProvider#start(java.util.Properties)
     */
    public void start( Properties hibernateSystemProperties )
    {
        // Do nothing
    }
}
