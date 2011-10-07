/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserStoreConnectorConfig
{
    private String name;

    private List<String> errorMessages = new ArrayList<String>();

    private UserPolicyConfig userPolicy;

    private GroupPolicyConfig groupPolicy;

    private String pluginType;

    private Properties pluginProperties;

    public UserStoreConnectorConfig( String name, String pluginType, UserPolicyConfig userPolicy, GroupPolicyConfig groupPolicy )
    {
        this.name = name;
        this.pluginType = pluginType;
        this.userPolicy = userPolicy;
        this.groupPolicy = groupPolicy;
    }

    public String getName()
    {
        return name;
    }

    public void addErrorMessage( String message )
    {
        errorMessages.add( message );
    }

    public boolean hasErrors()
    {
        return errorMessages.size() > 0;
    }

    public List<String> getErrorMessages()
    {
        return errorMessages;
    }

    public boolean canCreateUser()
    {
        return userPolicy.canCreate();
    }

    public boolean canUpdateUser()
    {
        return userPolicy.canUpdate();
    }

    public boolean canUpdateUserPassword()
    {
        return userPolicy.canUpdatePassword();
    }

    public boolean canDeleteUser()
    {
        return userPolicy.canDelete();
    }

    public boolean canCreateGroup()
    {
        return groupPolicy.canCreate();
    }

    public boolean canReadGroup()
    {
        return groupPolicy.canRead();
    }

    public boolean canUpdateGroup()
    {
        return groupPolicy.canUpdate();
    }

    public boolean canDeleteGroup()
    {
        return groupPolicy.canDelete();
    }

    public boolean groupsStoredLocal()
    {
        return groupPolicy.useLocal();
    }

    public boolean groupsStoredRemote()
    {
        return groupPolicy.useRemote();
    }

    public String getPluginType()
    {
        return pluginType;
    }

    public void setPluginType( final String value )
    {
        pluginType = value;
    }

    public Properties getPluginProperties()
    {
        return pluginProperties;
    }

    public void addProperties( final Properties properties )
    {
        if ( this.pluginProperties == null )
        {
            this.pluginProperties = properties;
        }
        else
        {
            this.pluginProperties.putAll( properties );
        }
    }
}
