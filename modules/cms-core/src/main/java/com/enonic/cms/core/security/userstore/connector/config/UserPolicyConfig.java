/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.security.userstore.config.AbstractPolicyConfig;

public final class UserPolicyConfig
    extends AbstractPolicyConfig
{
    static final String POLICY_ALL = "all";

    static final String POLICY_CREATE = "create";

    static final String POLICY_UPDATE = "update";

    static final String POLICY_UPDATE_PASSWORD = "updatepassword";

    static final String POLICY_DELETE = "delete";

    /**
     * Read is always true for a remote user store connector
     */
    private boolean read = true;

    private boolean create = false;

    private boolean update = false;

    private boolean updatePassword = false;

    private boolean delete = false;

    public static final UserPolicyConfig ALL_FALSE = new UserPolicyConfig();

    private UserPolicyConfig()
    {
    }

    public UserPolicyConfig( final String configName, final String policies )
        throws InvalidUserStoreConnectorConfigException
    {
        final List<String> policyList = parsePolicies( policies );

        final List<String> supportedPolicies = getSupportedPolicies();
        for ( final String policy : policyList )
        {
            if ( StringUtils.isBlank( policy ) )
            {
                continue;
            }

            if ( !supportedPolicies.contains( policy ) )
            {
                throw new InvalidUserStoreConnectorConfigException( configName, "User policy '" + policy + "' not supported" );
            }
        }

        if ( policyList.contains( POLICY_ALL ) && policyList.size() > 1 )
        {
            throw new InvalidUserStoreConnectorConfigException( configName,
                                                                "User policy '" + POLICY_ALL + "' cannot be combined with other policies" );
        }

        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_CREATE ) )
        {
            create = true;
        }
        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_UPDATE ) )
        {
            update = true;
        }
        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_UPDATE_PASSWORD ) )
        {
            updatePassword = true;
        }
        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_DELETE ) )
        {
            delete = true;
        }
    }

    private static List<String> getSupportedPolicies()
    {
        final List<String> supportedPolicies = new ArrayList<String>();
        supportedPolicies.add( POLICY_ALL );
        supportedPolicies.add( POLICY_CREATE );
        supportedPolicies.add( POLICY_UPDATE );
        supportedPolicies.add( POLICY_UPDATE_PASSWORD );
        supportedPolicies.add( POLICY_DELETE );
        return supportedPolicies;
    }

    public boolean canCreate()
    {
        return create;
    }

    public boolean canUpdate()
    {
        return update;
    }

    public boolean canUpdatePassword()
    {
        return updatePassword;
    }

    public boolean canDelete()
    {
        return delete;
    }
}
