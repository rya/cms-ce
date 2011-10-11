package com.enonic.cms.core.config;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import java.io.*;
import java.util.Properties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigLoaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;
    private ConfigLoader configLoader;
    private ClassLoader classLoader;

    @Before
    public void setUp()
        throws Exception
    {
        this.classLoader = Mockito.mock(ClassLoader.class);

        this.homeDir = this.folder.newFolder("cms-home");
        this.configLoader = new ConfigLoader(this.homeDir);
        this.configLoader.setClassLoader(this.classLoader);
        this.configLoader.setSystemProperties(new Properties());
    }

    private void setupSystemProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "system.param", "value" );
        props.setProperty( "override", "system" );
        this.configLoader.setSystemProperties(props);
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "value" );
        props.setProperty( "override", "home" );

        final File file = new File( this.homeDir, "config/cms.properties" );
        file.getParentFile().mkdirs();

        final FileOutputStream out = new FileOutputStream( file );
        props.store( out, "" );
        out.close();
    }

    private void setupClassPathProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "classpath.param", "value" );
        props.setProperty("override", "classpath");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store( out, "" );
        out.close();

        final ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
        Mockito.when( this.classLoader.getResourceAsStream("com/enonic/vertical/default.properties") )
                .thenReturn( in );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoDefaultProperties()
        throws Exception
    {
        this.configLoader.load();
    }

    @Test
    public void testDefaultConfig()
        throws Exception
    {
        setupClassPathProperties();
        
        final Properties props = this.configLoader.load();
        assertNotNull( props );
        assertEquals( 4, props.size() );
        assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        assertEquals( "value", props.getProperty( "classpath.param" ) );
        assertEquals( "classpath", props.getProperty( "override" ) );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupClassPathProperties();
        setupHomeProperties();
        
        final Properties props = this.configLoader.load();
        assertNotNull( props );
        assertEquals( 5, props.size() );
        assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        assertEquals( "value", props.getProperty( "home.param" ) );
        assertEquals( "value", props.getProperty( "classpath.param" ) );
        assertEquals( "home", props.getProperty( "override" ) );
    }

    @Test
    public void testSystemProperties()
        throws Exception
    {
        setupSystemProperties();
        setupClassPathProperties();

        final Properties props = this.configLoader.load();
        assertNotNull( props );
        assertEquals( 5, props.size() );
        assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        assertEquals( "value", props.getProperty( "system.param" ) );
        assertEquals( "value", props.getProperty( "classpath.param" ) );
        assertEquals( "classpath", props.getProperty( "override" ) );
    }
}
