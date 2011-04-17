/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.util.HashMap;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.store.hibernate.cache.HibernateCacheBootstrap;

/**
 * This class implements the hibernate configurator.
 */
public final class HibernateConfigurator
    extends LocalSessionFactoryBean
{
    /**
     * Cache manager.
     */
    private CacheManager cacheManager;

    /**
     * Set the cache manager.
     */
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    /**
     * After set properties.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        HibernateCacheBootstrap cacheBootstrap = new HibernateCacheBootstrap();
        cacheBootstrap.setCacheName( "entity" );
        cacheBootstrap.setCacheManager( this.cacheManager );

        final HashMap<String, Object> listenerMap = new HashMap<String, Object>();
        listenerMap.put( "post-delete", EntityChangeListenerHub.getInstance() );
        listenerMap.put( "post-insert", EntityChangeListenerHub.getInstance() );
        listenerMap.put( "post-update", EntityChangeListenerHub.getInstance() );
        setEventListeners( listenerMap );

        super.afterPropertiesSet();
    }
}
