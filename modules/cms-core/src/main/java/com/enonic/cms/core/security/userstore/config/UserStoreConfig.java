/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserFieldType;

public class UserStoreConfig
{
    private final Collection<UserStoreUserFieldConfig> userFieldConfigs = new TreeSet<UserStoreUserFieldConfig>();

    public Collection<UserStoreUserFieldConfig> getUserFieldConfigs()
    {
        return userFieldConfigs;
    }

    public void addUserFieldConfig( UserStoreUserFieldConfig value )
    {
        userFieldConfigs.add( value );
    }

    public void setUserFieldConfigs( final Collection<UserStoreUserFieldConfig> value )
    {
        userFieldConfigs.clear();
        userFieldConfigs.addAll( value );
    }

    public Collection<UserStoreUserFieldConfig> getRemoteOnlyUserFieldConfigs()
    {
        return getUserFieldConfigs( true );
    }

    public Collection<UserStoreUserFieldConfig> getLocalOnlyUserFieldConfigs()
    {
        return getUserFieldConfigs( false );
    }

    public Collection<UserFieldType> getRemoteOnlyUserFieldTypes()
    {
        return getUserFieldTypes( true );
    }

    public Collection<UserFieldType> getLocalOnlyUserFieldTypes()
    {
        return getUserFieldTypes( false );
    }

    public UserStoreUserFieldConfig getUserFieldConfig( UserFieldType type )
    {
        for ( UserStoreUserFieldConfig config : userFieldConfigs )
        {
            if ( config.getType().equals( type ) )
            {
                return config;
            }
        }
        return null;
    }

    public void removeReadOnlyFields( final UserFieldMap userFieldMap )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( userFieldConfig.isReadOnly() && userFieldMap.hasField( userFieldConfig.getType() ) )
            {
                userFieldMap.remove( userFieldConfig.getType() );
            }
        }
    }

    public void validateUserFieldMap( final UserFieldMap userFieldMap )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( userFieldConfig.isRequired() && !userFieldMap.hasField( userFieldConfig.getType() ) )
            {
                throw new IllegalArgumentException(
                    "Invalid user field map. Missing required user info: " + userFieldConfig.getType().getName() );
            }

            if ( userFieldConfig.isReadOnly() && userFieldMap.hasField( userFieldConfig.getType() ) )
            {
                throw new IllegalArgumentException(
                    "Invalid user field map. Read only user info found: " + userFieldConfig.getType().getName() );
            }
        }
    }

    private Collection<UserStoreUserFieldConfig> getUserFieldConfigs( final boolean remoteFlagValue )
    {
        final Collection<UserStoreUserFieldConfig> fieldConfigs = new HashSet<UserStoreUserFieldConfig>();

        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( userFieldConfig.isRemote() == remoteFlagValue )
            {
                fieldConfigs.add( userFieldConfig );
            }
        }
        return fieldConfigs;
    }

    private Collection<UserFieldType> getUserFieldTypes( final boolean remoteFlagValue )
    {
        final Collection<UserFieldType> fieldTypes = new HashSet<UserFieldType>();

        for ( final UserStoreUserFieldConfig userFieldConfig : getUserFieldConfigs( remoteFlagValue ) )
        {
            fieldTypes.add( userFieldConfig.getType() );
        }

        return fieldTypes;
    }
}
