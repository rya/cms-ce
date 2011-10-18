package com.enonic.cms.core.config;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

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
    private MutablePropertySources sources;
    private ClassLoader classLoader;

    @Before
    public void setUp()
        throws Exception
    {
        final StandardEnvironment env = new StandardEnvironment();
        this.sources = env.getPropertySources();

        this.sources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        this.sources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);

        this.classLoader = Mockito.mock(ClassLoader.class);

        this.homeDir = this.folder.newFolder("cms-home");
        this.configLoader = new ConfigLoader(this.homeDir, env);
        this.configLoader.setClassLoader(this.classLoader);
    }

    private void setupSystemProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "system.param", "system.value" );

        this.sources.addFirst(new PropertiesPropertySource("system", props));
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "home.value" );
        props.setProperty( "override", "home" );
        props.setProperty( "interpolate", "${home.param} ${system.param}");

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
        props.setProperty( "classpath.param", "classpath.value" );
        props.setProperty("override", "classpath");
        props.setProperty( "interpolate", "${classpath.param} ${system.param}");

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
        assertEquals( 5, props.size() );
        assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        assertEquals( "classpath.value", props.getProperty( "classpath.param" ) );
        assertEquals( "classpath", props.getProperty( "override" ) );
        assertEquals( "classpath.value ${system.param}", props.getProperty( "interpolate" ) );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupClassPathProperties();
        setupHomeProperties();
        
        final Properties props = this.configLoader.load();
        assertNotNull( props );
        assertEquals( 6, props.size() );
        assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        assertEquals( "home.value", props.getProperty( "home.param" ) );
        assertEquals( "classpath.value", props.getProperty( "classpath.param" ) );
        assertEquals( "home", props.getProperty( "override" ) );
        assertEquals( "home.value ${system.param}", props.getProperty( "interpolate" ) );
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
        assertEquals( "classpath.value", props.getProperty( "classpath.param" ) );
        assertEquals( "classpath", props.getProperty( "override" ) );
        assertEquals( "classpath.value system.value", props.getProperty( "interpolate" ) );
    }
}
