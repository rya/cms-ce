package com.enonic.vertical.adminweb;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

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
            infoModel.setSystemProperties( System.getProperties() );
            infoModel.setDatasourceProperties( this.dataSourceInfoResolver.getInfo( false ) );
            infoModel.setConfigurationProperties( getConfigurationProperties() );
        }
        catch ( Exception e )
        {
            throw new VerticalAdminException( "Not able to create properties-model", e );
        }

        return infoModel;
    }

    private Map<Object, Object> getConfigurationProperties()
    {
        final Properties strippedConfiguration = stripPasswords( this.configurationProperties );

        final Map<Object, Object> filteredPropertiesMap = Maps.filterKeys( strippedConfiguration, new Predicate<Object>()
        {
            public boolean apply( Object o )
            {
                String key = (String) o;
                if ( StringUtils.startsWith( key, "cms." ) )
                {
                    return true;
                }

                return false;
            }
        } );

        return filteredPropertiesMap;
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
