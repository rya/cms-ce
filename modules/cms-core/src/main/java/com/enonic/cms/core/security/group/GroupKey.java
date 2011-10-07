/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.domain.StringBasedKey;

public class GroupKey
    implements StringBasedKey, Serializable
{

    private String value;

    public GroupKey( String key )
    {

        if ( key == null )
        {
            throw new IllegalArgumentException( "Given key cannot be null" );
        }

        if ( key.startsWith( "#" ) )
        {
            key = key.substring( 1 );
        }

        init( key );
    }

    private void init( String value )
    {
        if ( value == null )
        {
            throw new InvalidKeyException( value, this.getClass() );
        }

        this.value = value;
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

        GroupKey groupKey = (GroupKey) o;

        return value.equals( groupKey.value );

    }

    public int hashCode()
    {
        return new HashCodeBuilder( 435, 773 ).append( value ).toHashCode();
    }

    public String toString()
    {
        return value;
    }

    public static List<String> convertToStringList( List<GroupKey> groupKeys )
    {
        List<String> stringKeys = new ArrayList<String>();
        for ( GroupKey groupKey : groupKeys )
        {
            stringKeys.add( groupKey.toString() );
        }
        return stringKeys;
    }
}
