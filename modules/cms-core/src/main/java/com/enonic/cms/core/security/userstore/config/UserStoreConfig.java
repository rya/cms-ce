/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.security.user.MissingRequiredUserFieldException;
import com.enonic.cms.core.security.user.ReadOnlyUserFieldPolicyException;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldMap;
import com.enonic.cms.core.user.field.UserFieldType;

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

    public void validateNoRequiredFieldsAreBlank( final UserFieldMap userFieldMap )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( userFieldConfig.isRequired() && isFieldPresentButBlank( userFieldMap, userFieldConfig.getType() ) )
            {
                throw new MissingRequiredUserFieldException( userFieldConfig.getType() );
            }
        }
    }

    private boolean isFieldPresentButBlank( UserFieldMap userFieldMap, UserFieldType userFieldType )
    {
        if ( !userFieldMap.hasField( userFieldType ) )
        {
            return false;
        }

        UserField userField = userFieldMap.getField( userFieldType );
        Object value = userField.getValue();
        if ( value instanceof String )
        {
            return StringUtils.isBlank( (String) value );
        }
        else if ( value instanceof byte[] )
        {
            return ( (byte[]) value ).length == 0;
        }

        return value == null;
    }

    public void validateAllRequiredFieldsArePresent( final UserFieldMap userFieldMap )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( userFieldConfig.isRequired() && isFieldMissingOrEmpty( userFieldMap, userFieldConfig.getType() ) )
            {
                throw new MissingRequiredUserFieldException( userFieldConfig.getType() );
            }
        }
    }

    private boolean isFieldMissingOrEmpty( final UserFieldMap userFieldMap, final UserFieldType userFieldType )
    {
        if ( !userFieldMap.hasField( userFieldType ) )
        {
            return true;
        }

        UserField userField = userFieldMap.getField( userFieldType );
        Object value = userField.getValue();
        if ( value instanceof String )
        {
            return StringUtils.isBlank( (String) value );
        }
        else if ( value instanceof byte[] )
        {
            return ( (byte[]) value ).length == 0;
        }

        return value == null;
    }

    public void validateReadOnlyFieldsNotExists( final UserFieldMap userFieldMap )
    {
        for ( final UserStoreUserFieldConfig userFieldConfig : userFieldConfigs )
        {
            if ( userFieldConfig.isReadOnly() && userFieldMap.hasField( userFieldConfig.getType() ) )
            {
                throw new ReadOnlyUserFieldPolicyException( userFieldConfig.getType() );
            }
        }
    }

    public void validateUserFieldMap( final UserFieldMap userFieldMap )
    {
        validateAllRequiredFieldsArePresent( userFieldMap );
        validateReadOnlyFieldsNotExists( userFieldMap );
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
