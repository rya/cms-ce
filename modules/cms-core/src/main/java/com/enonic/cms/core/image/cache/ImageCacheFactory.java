/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.cache;

import org.springframework.beans.factory.FactoryBean;

import com.enonic.cms.framework.cache.CacheManager;

public final class ImageCacheFactory
    implements FactoryBean
{
    private String cacheName;

    private CacheManager cacheManager;

    public void setCacheName( String cacheName )
    {
        this.cacheName = cacheName;
    }

    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    public Object getObject()
        throws Exception
    {
        return new WrappedImageCache( this.cacheManager.getOrCreateCache( this.cacheName ) );
    }

    public Class getObjectType()
    {
        return ImageCache.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
