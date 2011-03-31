/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.cache.config.CacheConfig;
import com.enonic.cms.framework.cache.config.CacheManagerConfig;
import com.enonic.cms.framework.cache.config.PropertiesCacheManagerConfig;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

/**
 * This class implements the abstract cache manager.
 */
public abstract class AbstractCacheManager
    implements CacheManager, InitializingBean, DisposableBean
{
    /**
     * Map of caches.
     */
    private final Map<String, AbstractCacheFacade> cacheMap;

    /**
     * Config location.
     */
    private Properties properties;

    /**
     * Property prefix.
     */
    private String propertyPrefix;

    /**
     * Cache manager config.
     */
    private CacheManagerConfig config;

    /**
     * Construct the manager.
     */
    public AbstractCacheManager()
    {
        this.cacheMap = new HashMap<String, AbstractCacheFacade>();
    }

    /**
     * Return the cache manager config.
     */
    protected final CacheManagerConfig getConfig()
    {
        return this.config;
    }

    /**
     * Return the disk store path.
     */
    public String getDiskStorePath()
    {
        return this.config.getDiskStorePath();
    }

    /**
     * Return all caches.
     */
    public final Collection<String> getCacheNames()
    {
        return this.cacheMap.keySet();
    }

    /**
     * Set the properties.
     */
    public final void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    /**
     * Set the property prefix.
     */
    public final void setPropertyPrefix( String propertyPrefix )
    {
        this.propertyPrefix = propertyPrefix;
    }

    /**
     * Return the cache by name, if no cache was found return null.
     */
    public final CacheFacade getCache( String name )
    {
        synchronized ( this.cacheMap )
        {
            return this.cacheMap.get( name );
        }
    }

    /**
     * Create a cache by name with default configuration.
     */
    public final CacheFacade getOrCreateCache( String name )
    {
        synchronized ( this.cacheMap )
        {
            AbstractCacheFacade cache = this.cacheMap.get( name );
            if ( cache == null )
            {
                cache = doCreateCache( name );
                this.cacheMap.put( name, cache );
            }

            return cache;
        }
    }

    /**
     * Create the cache.
     */
    private AbstractCacheFacade doCreateCache( String name )
    {
        return doCreateCache( name, this.config.getCacheConfig( name ) );
    }

    /**
     * Create the cache.
     */
    protected abstract AbstractCacheFacade doCreateCache( String name, CacheConfig config );

    /**
     * Configure the manager.
     */
    public final void afterPropertiesSet()
        throws Exception
    {
        doInitialize();
    }

    /**
     * Destroy the manager.
     */
    public final void destroy()
        throws Exception
    {
        doDestroy();
    }

    /**
     * Initialize.
     */
    protected void doInitialize()
        throws Exception
    {
        this.config = new PropertiesCacheManagerConfig( this.properties, this.propertyPrefix );
    }

    /**
     * Initialize.
     */
    protected void doDestroy()
        throws Exception
    {
        this.cacheMap.clear();
    }

    /**
     * @Inherit
     */
    public XMLDocument getInfoAsXml()
    {
        Element root = new Element( "caches" );
        for ( AbstractCacheFacade cache : this.cacheMap.values() )
        {
            root.addContent( cache.getInfoAsXml().getAsJDOMDocument().getRootElement().detach() );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }
}
