/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPolicyConfig
{
    private static final String POLICY_SEPERATOR = ",";

    protected List<String> parsePolicies( final String value )
    {
        final List<String> policies = new ArrayList<String>();
        if ( value != null )
        {
            final String[] values = value.split( POLICY_SEPERATOR );
            for ( int i = 0; i < values.length; i++ )
            {
                policies.add( values[i].trim().toLowerCase() );
            }
        }
        return policies;
    }
}
