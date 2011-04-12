/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import org.apache.commons.lang.builder.HashCodeBuilder;


public class PreferenceScope
{

    private PreferenceScopeType type;

    private PreferenceScopeKey key;

    public PreferenceScope( PreferenceScopeType type, PreferenceScopeKey key )
    {
        this.type = type;
        this.key = key;

        if ( type == PreferenceScopeType.GLOBAL && key != null )
        {
            throw new IllegalArgumentException( "Given scopeKey expected to be null when scope is " + type.getName() );
        }
        else if ( type != PreferenceScopeType.GLOBAL && key == null )
        {
            throw new IllegalArgumentException( "Given scopeKey cannot be null when scope is " + type.getName() );
        }
    }

    public PreferenceScopeType getType()
    {
        return type;
    }

    public PreferenceScopeKey getKey()
    {
        return key;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        PreferenceScope that = (PreferenceScope) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }
        if ( !type.equals( that.getType() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 483, 245 ).append( key ).append( type ).toHashCode();
    }
}
