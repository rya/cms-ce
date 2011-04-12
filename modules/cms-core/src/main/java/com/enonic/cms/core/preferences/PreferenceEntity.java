/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class PreferenceEntity
    implements Serializable
{

    private PreferenceKey key;

    private String value;

    public PreferenceKey getKey()
    {
        return key;
    }

    public void setKey( PreferenceKey value )
    {
        if ( value == null )
        {
            throw new IllegalArgumentException( "Given value cannot be null" );
        }
        this.key = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PreferenceEntity ) )
        {
            return false;
        }

        PreferenceEntity that = (PreferenceEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 377, 481 ).append( key ).toHashCode();
    }
}
