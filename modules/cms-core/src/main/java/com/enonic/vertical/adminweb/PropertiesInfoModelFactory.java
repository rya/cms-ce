package com.enonic.vertical.adminweb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import com.enonic.cms.server.service.tools.DataSourceInfoResolver;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 8/17/11
 * Time: 8:32 AM
 */
public class PropertiesInfoModelFactory
{
    private File homeDir;

    private DataSourceInfoResolver dataSourceInfoResolver;

    private Properties configurationProperties;

    @Value("${cms.home}")
    public void setHomeDir( File homeDir )
    {
        this.homeDir = homeDir;
    }

    public PropertiesInfoModelFactory( DataSourceInfoResolver dataSourceInfoResolver, Properties configurationProperties )
    {
        this.dataSourceInfoResolver = dataSourceInfoResolver;
        this.configurationProperties = configurationProperties;

        if ( homeDir == null )
        {
            homeDir = new File( System.getProperty( "cms.home" ) );
        }

    }

    public PropertiesInfoModel createSystemPropertiesModel()
    {

        PropertiesInfoModel infoModel = new PropertiesInfoModel();

        try
        {
            infoModel.setConfigFilesProperties( createConfigFileProperties() );
            infoModel.setSystemProperties( System.getProperties() );
            infoModel.setDatasourceProperties( this.dataSourceInfoResolver.getInfo( false ) );
            infoModel.setConfigurationProperties( stripPasswords( this.configurationProperties ) );
            infoModel.setConfigFiles( getConfigFiles() );
        }
        catch ( Exception e )
        {
            throw new VerticalAdminException( "Not able to create properties-model", e );
        }

        return infoModel;
    }

    private Properties createConfigFileProperties()
    {
        Properties configFilesProperties = new Properties();

        configFilesProperties.setProperty( "Home", getHomeDirPath() );
        configFilesProperties.setProperty( "Config", getConfigDirPath() );

        return configFilesProperties;
    }

    private String getHomeDirPath()
    {
        if ( homeDir.exists() )
        {
            return homeDir.getPath();
        }

        return "Home directory not found";
    }


    private String getConfigDirPath()
    {
        try
        {
            for ( File file : homeDir.listFiles() )
            {
                if ( file.getName().equals( "config" ) )
                {
                    return file.getPath();
                }
            }
        }
        catch ( Exception e )
        {
            throw new VerticalAdminException( "Config directory not found: ", e );
        }

        return "Config directory not found";
    }


    private File getConfigDir()
        throws Exception
    {
        for ( File file : homeDir.listFiles() )
        {
            if ( file.getName().equals( "config" ) )
            {
                return file;
            }
        }

        throw new IllegalStateException( "Config directory not found" );
    }

    private File getConfigFile( String name )
        throws Exception
    {
        for ( File file : getConfigDir().listFiles() )
        {
            if ( file.getName().equals( name ) )
            {
                return file;
            }
        }

        throw new IllegalStateException( "Config file [" + name + "] not found" );
    }

    private List<String> getConfigFiles()
    {

        ArrayList<String> files = null;
        try
        {
            files = new ArrayList<String>();
            for ( File children : getConfigDir().listFiles() )
            {
                String name = children.getName();
                if ( name.endsWith( ".xml" ) || ( name.endsWith( ".properties" ) ) )
                {
                    files.add( name );
                }
            }
        }
        catch ( Exception e )
        {
            throw new VerticalAdminException( "Could not list config-files", e );
        }

        return files;
    }

    private Properties stripPasswords( Properties secretProperties )
    {
        Properties publicProperties = new Properties();
        for ( Map.Entry<Object, Object> prop : secretProperties.entrySet() )
        {
            if ( prop.getKey() instanceof String )
            {
                String key = (String) prop.getKey();
                if ( key.matches( ".*[Pp][Aa][Ss][Ss][Ww][Oo][Rr][Dd]$" ) )
                {
                    publicProperties.put( key, "****" );
                }
                else
                {
                    publicProperties.put( key, prop.getValue() );
                }
            }
            else
            {
                publicProperties.put( prop.getKey(), prop.getValue() );
            }
        }

        return publicProperties;
    }


}
