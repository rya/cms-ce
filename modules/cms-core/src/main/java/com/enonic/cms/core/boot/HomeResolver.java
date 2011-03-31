/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HomeResolver
        implements SharedConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( HomeResolver.class );

    private Properties systemProperties;

    private Map<String, String> environment;

    private File defaultHome;

    public HomeResolver()
    {
        setSystemProperties( System.getProperties() );
        setEnvironment( System.getenv() );
        setDefaultHome( new File( SystemUtils.getUserHome(), "cms-home" ) );
    }

    public void setSystemProperties( final Properties props )
    {
        this.systemProperties = props;
    }

    public void setEnvironment( final Map<String, String> env )
    {
        this.environment = env;
    }

    public void setDefaultHome( final File dir )
    {
        this.defaultHome = dir;
    }

    public File resolve()
    {
        return validatePath( resolvePath() );
    }

    private String resolvePath()
    {
        String path = this.systemProperties.getProperty( CMS_HOME );
        if ( !StringUtils.isEmpty( path ) )
        {
            return path;
        }

        path = this.environment.get( CMS_HOME_ENV );
        if ( !StringUtils.isEmpty( path ) )
        {
            return path;
        }

        return this.defaultHome.getAbsolutePath();
    }

    private File validatePath( final String path )
    {
        File dir = new File( path );

        try
        {
            dir = dir.getCanonicalFile();
        }
        catch ( IOException e )
        {
            // Do nothing
        }

        if ( dir.exists() && !dir.isDirectory() )
        {
            throw new IllegalArgumentException( "Invalid home directory: [" + path + "] is not a directory" );
        }

        if ( !dir.exists() && dir.mkdirs() )
        {
            LOG.debug( "Missing home directory was created in [" + dir.getAbsolutePath() + "]" );
        }

        LOG.info( "Home directory is set to [" + dir.getAbsolutePath() + "]" );
        return dir;
    }
}