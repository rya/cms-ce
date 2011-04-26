/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import org.springframework.beans.factory.FactoryBean;

public final class CacheFacadeFactory
    implements FactoryBean<CacheFacade>
{
    private CacheManager cacheManager;

    private String cacheName;

    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    public void setCacheName( String cacheName )
    {
        this.cacheName = cacheName;
    }

    public CacheFacade getObject()
    {
        return this.cacheManager.getOrCreateCache( this.cacheName );
    }

    public Class getObjectType()
    {
        return CacheFacade.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
