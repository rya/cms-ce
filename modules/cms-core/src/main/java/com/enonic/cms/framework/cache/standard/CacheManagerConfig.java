/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import java.util.Properties;

/**
 * This class implements the cache manager configuration.
 */
final class CacheManagerConfig
{
    private final static String KEY_PREFIX = "cms.cache.";

    private final static int DEFAULT_MEMORY_CAPACITY = 1000;
    private final static int DEFAULT_TIME_TO_LIVE = 0;

    private final Properties properties;

    public CacheManagerConfig( final Properties properties )
    {
        this.properties = properties;
    }

    private String getProperty( final String key, final String defValue )
    {
        final String value = this.properties.getProperty( KEY_PREFIX + key );
        return value != null ? value : defValue;
    }

    private int getIntegerProperty( final String key, final int defValue )
    {
        final String value = getProperty( key, String.valueOf( defValue ));

        try
        {
            return Integer.parseInt( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }

    public CacheConfig getCacheConfig( final String name )
    {
        final int memoryCapacity = getIntegerProperty( name + ".memoryCapacity", DEFAULT_MEMORY_CAPACITY );
        final int timeToLive = getIntegerProperty( name + ".timeToLive", DEFAULT_TIME_TO_LIVE );
        return new CacheConfig( memoryCapacity, timeToLive );
    }
}
