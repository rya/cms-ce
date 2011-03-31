/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache;

import java.util.Properties;

import junit.framework.TestCase;

import com.enonic.cms.framework.cache.config.CacheConfig;
import com.enonic.cms.framework.cache.config.CacheManagerConfig;
import com.enonic.cms.framework.cache.config.PropertiesCacheManagerConfig;

public class ConfigCacheTest
    extends TestCase
{
    private CacheManagerConfig managerConfig1;

    private CacheManagerConfig managerConfig2;

    public void setUp()
    {
        this.managerConfig1 = createConfig( null );
        this.managerConfig2 = createConfig( "some.prefix" );
    }

    public void testConfig1()
    {
        testConfig( this.managerConfig1 );
    }

    public void testConfig2()
    {
        testConfig( this.managerConfig2 );
    }

    private void testConfig( CacheManagerConfig managerConfig )
    {
        assertEquals( managerConfig.getDiskStorePath(), "$diskStorePath$" );

        CacheConfig config1 = managerConfig.getCacheConfig( "cache1" );
        assertEquals( false, config1.getDiskOverflow() );
        assertEquals( 10000, config1.getMemoryCapacity() );
        assertEquals( 0, config1.getDiskCapacity() );
        assertEquals( 60, config1.getTimeToLive() );

        CacheConfig config2 = managerConfig.getCacheConfig( "cache2" );
        assertEquals( true, config2.getDiskOverflow() );
        assertEquals( 20000, config2.getMemoryCapacity() );
        assertEquals( 100000, config2.getDiskCapacity() );
        assertEquals( 120, config2.getTimeToLive() );

        CacheConfig defaultConfig = managerConfig.getDefaultCacheConfig();
        CacheConfig config3 = managerConfig.getCacheConfig( "cache3" );
        assertEquals( defaultConfig.getDiskOverflow(), config3.getDiskOverflow() );
        assertEquals( defaultConfig.getMemoryCapacity(), config3.getMemoryCapacity() );
        assertEquals( defaultConfig.getDiskCapacity(), config3.getDiskCapacity() );
        assertEquals( defaultConfig.getTimeToLive(), config3.getTimeToLive() );
    }

    /**
     * Create configuration.
     */
    private CacheManagerConfig createConfig( String prefix )
    {
        String strPrefix = prefix != null ? ( prefix + "." ) : "";

        Properties props = new Properties();
        props.setProperty( strPrefix + "diskStorePath", "$diskStorePath$" );
        props.setProperty( strPrefix + "cache1.memoryCapacity", "10000" );
        props.setProperty( strPrefix + "cache1.diskCapacity", "0" );
        props.setProperty( strPrefix + "cache1.timeToLive", "60" );
        props.setProperty( strPrefix + "cache2.memoryCapacity", "20000" );
        props.setProperty( strPrefix + "cache2.diskCapacity", "100000" );
        props.setProperty( strPrefix + "cache2.timeToLive", "120" );

        return new PropertiesCacheManagerConfig( props, prefix );
    }
}
