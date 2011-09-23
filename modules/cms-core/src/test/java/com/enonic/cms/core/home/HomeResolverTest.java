/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.home;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.*;

public class HomeResolverTest
{
    private File tmpDir;

    private File defaultHomeDir;

    private File validHomeDir;

    private File invalidHomeDir;

    private File notCreatedHomeDir;

    @Before
    public void setUp()
        throws Exception
    {
        this.tmpDir = new File( SystemUtils.getJavaIoTmpDir(), "tmp-" + UUID.randomUUID().toString() );
        this.defaultHomeDir = new File( this.tmpDir, "cms.home" ).getCanonicalFile();
        this.invalidHomeDir = new File( this.tmpDir, "invalid.home" ).getCanonicalFile();
        this.validHomeDir = new File( this.tmpDir, "valid.home" ).getCanonicalFile();
        this.notCreatedHomeDir = new File( this.tmpDir, "not.created.home" ).getCanonicalFile();

        this.defaultHomeDir.mkdirs();
        this.validHomeDir.mkdirs();
        FileUtils.touch( this.invalidHomeDir );
    }

    @After
    public void tearDown()
    {
        this.tmpDir.delete();
    }

    @Test
    public void testDefaultNotExist()
    {
        final HomeDir homeDir = resolve( null, null );
        assertNotNull( homeDir );

        final File homeDirFile = homeDir.getFile();
        assertNotNull( homeDirFile );

        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.defaultHomeDir, homeDirFile );
    }

    @Test
    public void testSystemProperty()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( this.validHomeDir.getAbsolutePath(), null );
        assertNotNull( homeDir );

        final File homeDirFile = homeDir.getFile();
        assertNotNull( homeDirFile );

        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.validHomeDir, homeDirFile );
    }

    @Test
    public void testEnvironment()
    {
        assertTrue( this.validHomeDir.exists() );

        final HomeDir homeDir = resolve( null, this.validHomeDir.getAbsolutePath() );
        assertNotNull( homeDir );

        final File homeDirFile = homeDir.getFile();
        assertNotNull( homeDirFile );

        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.validHomeDir, homeDirFile );
    }

    @Test
    public void testCreateHomeDir()
    {
        assertFalse( this.notCreatedHomeDir.exists() );

        final HomeDir homeDir = resolve( this.notCreatedHomeDir.getAbsolutePath(), null );
        assertNotNull( homeDir );

        final File homeDirFile = homeDir.getFile();
        assertNotNull( homeDirFile );

        assertTrue( homeDirFile.exists() );
        assertTrue( homeDirFile.isDirectory() );
        assertEquals( this.notCreatedHomeDir, homeDirFile );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHomeDir()
    {
        resolve( this.invalidHomeDir.getAbsolutePath(), null );
    }

    private HomeDir resolve( String propValue, String envValue )
    {
        final Properties props = new Properties();
        final Map<String, String> envs = new HashMap<String, String>();

        if ( propValue != null )
        {
            props.setProperty( "cms.home", propValue );
        }

        if ( envValue != null )
        {
            envs.put( "CMS_HOME", envValue );
        }

        final HomeResolver resolver = new HomeResolver();
        resolver.setSystemProperties( props );
        resolver.setEnvironment( envs );
        resolver.setDefaultHome( this.defaultHomeDir );
        return resolver.resolve();
    }
}
