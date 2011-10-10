/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.springframework.core.style.ToStringCreator;

public class CacheSettings
{
    public static final String TYPE_FOREVER = "forever";

    public static final String TYPE_DEFAULT = "default";

    public static final String TYPE_SPECIFIED = "specified";

    private boolean enabled;

    private String type;

    private Integer secondsToLive = null;

    public CacheSettings( boolean enabled, String type, Integer secondsToLive )
    {
        this.enabled = enabled;
        this.type = resolveType( type );
        this.secondsToLive = resolveSeconds( this.enabled, this.type, secondsToLive );
    }

    private String resolveType( String type )
    {
        if ( type == null || type.length() == 0 )
        {
            return type;
        }
        return type;
    }

    private Integer resolveSeconds( boolean enabled, String type, Integer secondsToLive )
    {
        if ( !enabled )
        {
            return 0;
        }
        else if ( TYPE_DEFAULT.equals( type ) )
        {
            return secondsToLive;
        }
        else if ( TYPE_SPECIFIED.equals( type ) )
        {
            return secondsToLive;
        }
        else if ( TYPE_FOREVER.equals( type ) )
        {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    public String getType()
    {
        return type;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public boolean isDisabled()
    {
        return !enabled;
    }

    public Integer getSpecifiedSecondsToLive()
    {
        return secondsToLive;
    }

    public boolean isDefault()
    {
        return TYPE_DEFAULT.equals( type );
    }

    public boolean isNotDefault()
    {
        return !TYPE_DEFAULT.equals( type );
    }

    public boolean isSpecified()
    {
        return TYPE_SPECIFIED.equals( type );
    }

    public String toString()
    {
        ToStringCreator s = new ToStringCreator( this );
        s.append( "enabled", isEnabled() );
        s.append( "type", getType() );
        s.append( "secondsToLive", getSpecifiedSecondsToLive() );
        return s.toString();
    }

    public boolean isTighterThan( CacheSettings other )
    {
        if ( this.isDisabled() )
        {
            return true;
        }
        else if ( this.getSpecifiedSecondsToLive() < other.getSpecifiedSecondsToLive() )
        {
            return true;
        }

        return false;
    }
}
