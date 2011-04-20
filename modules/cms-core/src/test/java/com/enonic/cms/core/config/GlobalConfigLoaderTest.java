/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.config;

import com.enonic.cms.core.home.HomeDir;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GlobalConfigLoaderTest
{
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;

    private GlobalConfigLoader loader;

    private ClassLoader classLoader;

    @Before
    public void setUp()
        throws Exception
    {
        this.homeDir = this.folder.newFolder("cms-home");

        this.classLoader = Mockito.mock( ClassLoader.class );
        
        this.loader = new GlobalConfigLoader( new HomeDir(this.homeDir) );
        this.loader.setSystemProperties(new Properties());
        this.loader.setClassLoader(this.classLoader);
    }

    private void setupSystemProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "system.param", "value" );
        props.setProperty( "override", "system" );
        this.loader.setSystemProperties( props );
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "value" );
        props.setProperty( "override", "home" );

        final File file = new File( this.homeDir, "config/cms.properties" );
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream( file );
        props.store( out, "" );
        out.close();
    }

    private void setupClassPathProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "classpath.param", "value" );
        props.setProperty( "override", "classpath" );

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store( out, "" );
        ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
        Mockito.when( this.classLoader.getResourceAsStream("com/enonic/cms/core/config/default.properties") ).thenReturn( in );
    }

    @Test
    public void testDefault()
        throws Exception
    {
        final Properties props = this.loader.loadProperties();
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

        final Properties props = this.loader.loadProperties();
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

        final Properties props = this.loader.loadProperties();
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

        final Properties props = this.loader.loadProperties();
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

        final Properties props = this.loader.loadProperties();
        assertNotNull( props );
        assertEquals( 5, props.size() );
        assertEquals( "value", props.getProperty( "classpath.param" ) );
        assertEquals( "value", props.getProperty( "home.param" ) );
        assertEquals( "home", props.getProperty( "override" ) );
    }
}
