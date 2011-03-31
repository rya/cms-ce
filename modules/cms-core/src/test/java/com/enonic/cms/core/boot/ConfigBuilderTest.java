/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ConfigBuilderTest
{
    private File homeDir;

    private ConfigBuilder builder;

    private ClassLoader classLoader;

    @Before
    public void setUp()
        throws Exception
    {
        this.homeDir = new File( SystemUtils.getJavaIoTmpDir(), "tmp-" + UUID.randomUUID().toString() );

        this.classLoader = Mockito.mock( ClassLoader.class );

        this.builder = new ConfigBuilder( this.homeDir );
        this.builder.setSystemProperties( new Properties() );
        this.builder.setClassLoader( this.classLoader );
    }

    private void setupSystemProperties()
    {
        Properties props = new Properties();
        props.setProperty( "system.param", "value" );
        props.setProperty( "override", "system" );
        this.builder.setSystemProperties( props );
    }

    private void setupHomeProperties()
        throws Exception
    {
        Properties props = new Properties();
        props.setProperty( "home.param", "value" );
        props.setProperty( "override", "home" );

        File file = new File( this.homeDir, "config/cms.properties" );
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream( file );
        props.store( out, "" );
        out.close();
    }

    private void setupClassPathProperties()
        throws Exception
    {
        Properties props = new Properties();
        props.setProperty( "classpath.param", "value" );
        props.setProperty( "override", "classpath" );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store( out, "" );
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
        Mockito.when( this.classLoader.getResourceAsStream( "com/enonic/vertical/default.properties" ) ).thenReturn( in );
    }

    @After
    public void tearDown()
    {
        this.homeDir.delete();
    }

    @Test
    public void testDefault()
        throws Exception
    {
        Properties props = this.builder.loadProperties();
        assertNotNull( props );
        assertEquals( 2, props.size() );
        assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
    }

    @Test
    public void testSystemProperties()
        throws Exception
    {
        setupSystemProperties();

        Properties props = this.builder.loadProperties();
        assertNotNull( props );
        assertEquals( 4, props.size() );
        assertEquals( "value", props.getProperty( "system.param" ) );
        assertEquals( "system", props.getProperty( "override" ) );
    }

    @Test
    public void testClassLoader()
        throws Exception
    {
        setupClassPathProperties();

        Properties props = this.builder.loadProperties();
        assertNotNull( props );
        assertEquals( 4, props.size() );
        assertEquals( "value", props.getProperty( "classpath.param" ) );
        assertEquals( "classpath", props.getProperty( "override" ) );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupHomeProperties();

        Properties props = this.builder.loadProperties();
        assertNotNull( props );
        assertEquals( 4, props.size() );
        assertEquals( "value", props.getProperty( "home.param" ) );
        assertEquals( "home", props.getProperty( "override" ) );
    }

    @Test
    public void testCombining()
        throws Exception
    {
        setupClassPathProperties();
        setupHomeProperties();

        Properties props = this.builder.loadProperties();
        assertNotNull( props );
        assertEquals( 5, props.size() );
        assertEquals( "value", props.getProperty( "classpath.param" ) );
        assertEquals( "value", props.getProperty( "home.param" ) );
        assertEquals( "home", props.getProperty( "override" ) );
    }
}
