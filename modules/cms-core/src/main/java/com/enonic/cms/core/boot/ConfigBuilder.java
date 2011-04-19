/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.PropertiesUtil;

public final class ConfigBuilder
        implements SharedConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( ConfigBuilder.class );

    private final File homeDir;

    private Properties systemProperties;

    private ClassLoader classLoader;

    public ConfigBuilder( final File homeDir )
    {
        this.homeDir = homeDir;
        setSystemProperties( System.getProperties() );
        setClassLoader( getClass().getClassLoader() );
    }

    public void setSystemProperties( Properties props )
    {
        this.systemProperties = props;
    }

    public void setClassLoader( ClassLoader loader )
    {
        this.classLoader = loader;
    }

    public Properties loadProperties()
    {
        Properties props = new Properties();
        props.putAll( this.systemProperties );
        props.putAll( loadFromClassPath("com/enonic/cms/core/default.properties") );
        props.putAll( loadFromHomeDir( "config/cms.properties" ) );
        props.putAll( getHomeDirProperties() );
        return PropertiesUtil.interpolate( props );
    }

    private Properties loadFromClassPath( final String name )
    {
        InputStream in = this.classLoader.getResourceAsStream( name );
        if ( in == null )
        {
            LOG.warn( "Could not find [{}] in classpath", name );
        }
        else
        {
            try
            {
                return loadFromStream( in );
            }
            catch ( IOException e )
            {
                LOG.error( "Failed to load [{}] from classpath", e, name );
            }
        }

        return new Properties();
    }

    private Properties loadFromHomeDir( final String name )
    {
        File file = new File( this.homeDir, name );

        try
        {
            return loadFromStream( new FileInputStream( file ) );
        }
        catch ( FileNotFoundException e )
        {
            LOG.warn( "Could not find [{}] in home directory", name );
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to load [{}] from home directory", e, name );
        }

        return new Properties();
    }

    private Properties loadFromStream( final InputStream in )
            throws IOException
    {
        Properties props = new Properties();
        props.load( in );
        in.close();
        return props;
    }

    private Properties getHomeDirProperties()
    {
        Properties props = new Properties();
        props.setProperty( CMS_HOME, this.homeDir.getAbsolutePath() );
        props.setProperty( CMS_HOME_URI, this.homeDir.toURI().toString() );
        return props;
    }
}
