/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.userstore.config.AbstractPolicyConfig;

public final class GroupPolicyConfig
    extends AbstractPolicyConfig
{
    static final String POLICY_ALL = "all";

    static final String POLICY_NONE = "none";

    static final String POLICY_LOCAL = "local";

    static final String POLICY_READ = "read";

    static final String POLICY_CREATE = "create";

    static final String POLICY_UPDATE = "update";

    static final String POLICY_DELETE = "delete";

    private boolean local = false;

    private boolean read = false;

    private boolean create = false;

    private boolean update = false;

    private boolean delete = false;

    public final static GroupPolicyConfig ALL_FALSE = new GroupPolicyConfig();

    private GroupPolicyConfig()
    {

    }

    public GroupPolicyConfig( final String configName, final String policies )
    {
        final List<String> policyList = parsePolicies( policies );

        if ( policyList.size() == 0 )
        {
            throw new InvalidUserStoreConnectorConfigException( configName, "No group policy found (at least one must be specified)" );
        }

        final List<String> supportedPolicies = getSupportedPolicies();
        for ( final String policy : policyList )
        {
            if ( !supportedPolicies.contains( policy ) )
            {
                throw new InvalidUserStoreConnectorConfigException( configName, "Group policy '" + policy + "' not supported" );
            }
        }

        if ( policyList.contains( POLICY_ALL ) && policyList.size() != 1 )
        {
            throw new InvalidUserStoreConnectorConfigException( configName, "Group policy '" + POLICY_ALL +
                "' cannot be combined with other policies" );
        }

        if ( policyList.contains( POLICY_NONE ) && policyList.size() != 1 )
        {
            throw new InvalidUserStoreConnectorConfigException( configName, "Group policy '" + POLICY_NONE +
                "' cannot be combined with other policies" );
        }

        if ( policyList.contains( POLICY_LOCAL ) )
        {
            if ( policyList.size() != 1 )
            {
                throw new InvalidUserStoreConnectorConfigException( configName, "Group policy '" + POLICY_LOCAL +
                    "' cannot be combined with other policies" );
            }
            local = true;
            create = true;
            update = true;
            delete = true;
        }

        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_READ ) )
        {
            read = true;
        }
        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_CREATE ) )
        {
            create = true;
        }
        if ( policyList.contains( POLICY_ALL ) || policyList.contains( POLICY_UPDATE ) )
        {
            update = true;
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
        supportedPolicies.add( POLICY_NONE );
        supportedPolicies.add( POLICY_LOCAL );
        supportedPolicies.add( POLICY_READ );
        supportedPolicies.add( POLICY_CREATE );
        supportedPolicies.add( POLICY_UPDATE );
        supportedPolicies.add( POLICY_DELETE );
        return supportedPolicies;
    }

    public boolean useLocal()
    {
        return local;
    }

    public boolean useRemote()
    {
        return !local;
    }

    public boolean canRead()
    {
        return read;
    }

    public boolean canCreate()
    {
        return create;
    }

    public boolean canUpdate()
    {
        return update;
    }

    public boolean canDelete()
    {
        return delete;
    }
}
