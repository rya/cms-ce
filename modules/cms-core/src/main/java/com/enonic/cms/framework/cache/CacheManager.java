/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import com.enonic.cms.framework.xml.XMLDocument;

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
     * Return xml details.
     */
    public XMLDocument getInfoAsXml();
}
