package com.enonic.cms.core.home;

import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static junit.framework.Assert.*;
import java.io.File;
import java.util.Map;
import java.util.Properties;

public class HomeResolverTest
{
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File defaultHomeDir;

    private File validHomeDir;

    private File invalidHomeDir;

    private File notCreatedHomeDir;

    @Before
    public void setUp()
        throws Exception
    {
        this.defaultHomeDir = this.folder.newFolder( "cms.home" ).getCanonicalFile();
        this.validHomeDir = this.folder.newFolder( "valid.home" ).getCanonicalFile();

        this.invalidHomeDir = new File( this.folder.getRoot(), "invalid.home" ).getCanonicalFile();
        this.notCreatedHomeDir = new File( this.folder.getRoot(), "not.created.home" ).getCanonicalFile();

        FileUtils.touch(this.invalidHomeDir);
    }

    @Test
    public void testDefaultNotExist()
    {
        final HomeDir homeDir = resolve( null, null );
        assertNotNull( homeDir );

        final File file = homeDir.getFile();
        assertNotNull( file );
        assertTrue( file.exists() );
        assertTrue( file.isDirectory() );
        assertEquals( this.defaultHomeDir, file );
    }

    @Test
    public void testSystemProperty()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( this.validHomeDir.getAbsolutePath(), null );
        assertNotNull( homeDir );

        final File file = homeDir.getFile();
        assertTrue( file.exists() );
        assertTrue( file.isDirectory() );
        assertEquals( this.validHomeDir, file );
    }

    @Test
    public void testEnvironment()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( null, this.validHomeDir.getAbsolutePath() );
        assertNotNull( homeDir );

        final File file = homeDir.getFile();
        assertTrue( file.exists() );
        assertTrue( file.isDirectory() );
        assertEquals( this.validHomeDir, file );
    }

    @Test
    public void testCreateHomeDir()
    {
        assertFalse( this.notCreatedHomeDir.exists() );

        final HomeDir homeDir = resolve( this.notCreatedHomeDir.getAbsolutePath(), null );
        assertNotNull( homeDir );

        final File file = homeDir.getFile();
        assertTrue( file.exists() );
        assertTrue( file.isDirectory() );
        assertEquals( this.notCreatedHomeDir, file );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHomeDir()
    {
        resolve( this.invalidHomeDir.getAbsolutePath(), null );
    }

    private HomeDir resolve( String propValue, String envValue )
    {
        final Properties props = new Properties();
        final Map<String, String> envs = Maps.newHashMap();

        if ( propValue != null )
        {
            props.setProperty( "cms.home", propValue );
        }

        if ( envValue != null )
        {
            envs.put( "CMS_HOME", envValue );
        }

        HomeResolver resolver = new HomeResolver();
        resolver.setSystemProperties( props );
        resolver.setEnvironment( envs );
        resolver.setDefaultHome( this.defaultHomeDir );
        return resolver.resolve();
    }
}
