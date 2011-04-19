/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import java.util.Collection;

import org.jdom.Document;

/**
 * This interface defines the cache manager.
 */
public interface CacheManager
{
    /**
     * Return the cache by name, if no cache was found return null.
     */
    public CacheFacade getCache( String name );

    /**
     * Create a cache by name with default configuration.
     */
    public CacheFacade getOrCreateCache( String name );

    /**
     * Return names of all caches.
     */
    public Collection<String> getCacheNames();

    /**
     * Return the disk store path.
     */
    public String getDiskStorePath();

    /**
     * Return xml details.
     */
    public Document getInfoAsXml();
}
