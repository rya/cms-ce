/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public enum PreferenceScopeType
{

    GLOBAL( "GLOBAL", 0 ),
    SITE( "SITE", 1 ),
    // scopeKey: siteKey
    PAGE( "PAGE", 2 ),
    // scopeKey: menuItemKey
    PORTLET( "PORTLET", 3 ),
    // scopeKey: contentObjectKey
    WINDOW( "WINDOW", 4 );       // scopeKey: menuItemKey:contentObjectKey

    private String name;

    private int priority;

    PreferenceScopeType( String name, int priority )
    {
        this.name = name;
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getName()
    {
        return name;
    }

    public static List<PreferenceScopeType> parseScopes( String commaSeparatedScopeNames )
    {

        List<PreferenceScopeType> scopeTypes = new ArrayList<PreferenceScopeType>();
        StringTokenizer st = new StringTokenizer( commaSeparatedScopeNames, "," );
        while ( st.hasMoreTokens() )
        {
            PreferenceScopeType scopeType = parse( st.nextToken() );
            if ( scopeType != null )
            {
                scopeTypes.add( scopeType );
            }
        }
        return scopeTypes;
    }

    public static PreferenceScopeType parse( String scopeName )
    {

        for ( PreferenceScopeType x : values() )
        {
            if ( x.getName().equalsIgnoreCase( scopeName ) )
            {
                return x;
            }
        }
        return null;
    }
}
