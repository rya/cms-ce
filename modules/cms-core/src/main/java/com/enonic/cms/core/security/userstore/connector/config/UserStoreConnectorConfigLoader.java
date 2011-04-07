/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.domain.security.userstore.config.InvalidUserStoreConfigException;
import com.enonic.cms.domain.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.domain.security.userstore.connector.config.InvalidUserStoreConnectorConfigException;
import com.enonic.cms.domain.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.domain.security.userstore.connector.config.UserStoreConnectorConfig;

public class UserStoreConnectorConfigLoader
{
    private VerticalProperties verticalProperties;

    private Set<String> configNames = null;

    public Set<String> getAllConfigNames()
    {
        return doGetAllConfigNames();
    }

    public Map<String, UserStoreConnectorConfig> getAllConfigs()
    {
        Collection<String> allNames = doGetAllConfigNames();
        Map<String, UserStoreConnectorConfig> configs = new LinkedHashMap<String, UserStoreConnectorConfig>( allNames.size() );

        for ( final String name : allNames )
        {
            configs.put( name, doGetConfig( name, true ) );
        }

        return configs;
    }

    public UserStoreConnectorConfig getConfig( final String configName )
    {
        return doGetConfig( configName, false );
    }

    private UserStoreConnectorConfig doGetConfig( final String configName, final boolean failSilent )
    {
        if ( !doGetAllConfigNames().contains( configName ) )
        {
            //FIXME localization impossible, message shown directly in GUI
            final String errorMessage =
                InvalidUserStoreConnectorConfigException.createMessage( configName, "No configuration found in cms.properties" );
            if ( failSilent )
            {
                final UserStoreConnectorConfig config =
                    new UserStoreConnectorConfig( configName, null, UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE );
                config.addErrorMessage( errorMessage );
                return config;
            }
            else
            {
                throw new InvalidUserStoreConfigException( errorMessage );
            }
        }

        return doLoadConfig( configName );
    }

    private UserStoreConnectorConfig doLoadConfig( final String configName )
    {
        UserPolicyConfig userPolicyConfig;
        String userPolicyConfigErrorMessage = null;
        try
        {
            userPolicyConfig = getUserPolicy( configName );
        }
        catch ( InvalidUserStoreConnectorConfigException e )
        {
            userPolicyConfigErrorMessage = e.getMessage();
            userPolicyConfig = UserPolicyConfig.ALL_FALSE;
        }

        GroupPolicyConfig groupPolicyConfig;
        String groupPolicyConfigErrorMessage = null;
        try
        {
            groupPolicyConfig = getGroupPolicy( configName );
        }
        catch ( Exception e )
        {
            groupPolicyConfigErrorMessage = e.getMessage();
            groupPolicyConfig = GroupPolicyConfig.ALL_FALSE;
        }

        final String pluginType = getPluginType( configName );
        final UserStoreConnectorConfig config = new UserStoreConnectorConfig( configName, pluginType, userPolicyConfig, groupPolicyConfig );
        config.addProperties( getPluginProperties( configName ) );

        if ( userPolicyConfigErrorMessage != null )
        {
            config.addErrorMessage( userPolicyConfigErrorMessage );
        }
        if ( groupPolicyConfigErrorMessage != null )
        {
            config.addErrorMessage( groupPolicyConfigErrorMessage );
        }

        return config;
    }

    private Set<String> doGetAllConfigNames()
    {
        if ( configNames != null )
        {
            return configNames;
        }
        configNames = new HashSet<String>();

        final Properties allProperties = verticalProperties.getSubSet( "cms.userstore.connector." );
        for ( final Object propertyName : allProperties.keySet() )
        {
            final String configName = ( (String) propertyName ).replaceAll( "^(cms\\.userstore\\.)?(.*?)\\..*$", "$2" );
            configNames.add( configName );
        }
        return configNames;
    }

    private String getPluginType( final String configName )
    {
        return verticalProperties.getProperty( String.format( "cms.userstore.connector.%s.plugin", configName ) );
    }

    private Properties getPluginProperties( final String configName )
    {
        return verticalProperties.getSubSet( String.format( "cms.userstore.connector.%s.plugin.", configName ) );
    }

    private UserPolicyConfig getUserPolicy( final String configName )
        throws InvalidUserStoreConnectorConfigException
    {
        return new UserPolicyConfig( configName, verticalProperties.getProperty(
            String.format( "cms.userstore.connector.%s.userPolicy", configName ) ) );
    }

    private GroupPolicyConfig getGroupPolicy( final String configName )
    {
        return new GroupPolicyConfig( configName, verticalProperties.getProperty(
            String.format( "cms.userstore.connector.%s.groupPolicy", configName ) ) );
    }

    @Autowired
    public void setVerticalProperties( VerticalProperties value )
    {
        this.verticalProperties = value;
    }
}
