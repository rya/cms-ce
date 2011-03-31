/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import com.enonic.cms.framework.cache.base.AbstractCacheFacade;
import com.enonic.cms.framework.cache.base.AbstractCacheManager;
import com.enonic.cms.framework.cache.config.CacheConfig;

/**
 * This class implements the cache.
 */
public final class StandardCacheManager
    extends AbstractCacheManager
{
    /**
     * Return the cache.
     */
    protected AbstractCacheFacade doCreateCache( String name, CacheConfig config )
    {
        StandardCache cache = new StandardCache( config.getMemoryCapacity() );
        return new StandardCacheFacade( name, cache, config );
    }
}
