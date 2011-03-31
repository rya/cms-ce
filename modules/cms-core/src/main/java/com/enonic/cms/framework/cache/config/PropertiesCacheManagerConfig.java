/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.config;

import java.util.Properties;

/**
 * This class implements the cache manager configuration.
 */
public final class PropertiesCacheManagerConfig
    implements CacheManagerConfig
{
    /**
     * Default cache config.
     */
    private final CacheConfig defaultConfig;

    /**
     * Properties.
     */
    private final Properties properties;

    /**
     * Key prefix.
     */
    private final String keyPrefix;

    /**
     * Construct the config.
     */
    public PropertiesCacheManagerConfig( Properties properties, String keyPrefix )
    {
        this.properties = properties;
        this.keyPrefix = keyPrefix;
        this.defaultConfig = new CacheConfig( 1000, 0, 0 );
    }

    /**
     * Return the property.
     */
    private String getProperty( String key, String defValue )
    {
        String value;
        if ( this.keyPrefix != null )
        {
            value = this.properties.getProperty( this.keyPrefix + "." + key );
        }
        else
        {
            value = this.properties.getProperty( key );
        }

        return value != null ? value : defValue;
    }

    /**
     * Return integer property.
     */
    private int getIntegerProperty( String key, int defValue )
    {
        String value = getProperty( key, null );
        if ( value != null )
        {
            try
            {
                return Integer.parseInt( value );
            }
            catch ( Exception e )
            {
                return defValue;
            }
        }
        else
        {
            return defValue;
        }
    }

    /**
     * Return the disk store path.
     */
    public String getDiskStorePath()
    {
        return getProperty( "diskStorePath", System.getProperty( "java.io.tmpdir" ) );
    }

    /**
     * Return the default cache config.
     */
    public CacheConfig getDefaultCacheConfig()
    {
        return this.defaultConfig;
    }

    /**
     * Return cache config for cache name.
     */
    public CacheConfig getCacheConfig( String name )
    {
        int memoryCapacity = getIntegerProperty( name + ".memoryCapacity", this.defaultConfig.getMemoryCapacity() );
        int diskCapacity = getIntegerProperty( name + ".diskCapacity", this.defaultConfig.getDiskCapacity() );
        int timeToLive = getIntegerProperty( name + ".timeToLive", this.defaultConfig.getTimeToLive() );
        return new CacheConfig( memoryCapacity, diskCapacity, timeToLive );
    }
}
