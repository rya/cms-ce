/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import java.util.Properties;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.enonic.cms.framework.cache.base.AbstractCacheManager;
import com.enonic.cms.framework.cache.standard.StandardCacheManager;

/**
 * This class switches between cache managers.
 */
public final class CacheManagerFactory
    implements FactoryBean, InitializingBean, DisposableBean
{
    /**
     * Config location.
     */
    private Properties properties;

    /**
     * Cache manager.
     */
    private AbstractCacheManager cacheManager;

    /**
     * After properties set.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        this.cacheManager = new StandardCacheManager();
        this.cacheManager.setProperties( this.properties );
        this.cacheManager.setPropertyPrefix( "cms.cache" );
        this.cacheManager.afterPropertiesSet();
    }

    /**
     * Destroy the bean.
     */
    public void destroy()
        throws Exception
    {
        if ( this.cacheManager != null )
        {
            this.cacheManager.destroy();
        }
    }

    /**
     * Set the properties.
     */
    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    /**
     * Return the object.
     */
    public Object getObject()
        throws Exception
    {
        return this.cacheManager;
    }

    /**
     * Return the object type.
     */
    public Class getObjectType()
    {
        return CacheManager.class;
    }

    /**
     * Return true if singleton.
     */
    public boolean isSingleton()
    {
        return true;
    }
}
