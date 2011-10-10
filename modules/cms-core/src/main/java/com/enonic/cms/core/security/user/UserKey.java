/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.InvalidKeyException;

public class UserKey
    implements Serializable
{

    private String value;


    public UserKey( String key )
    {

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

        UserKey userKey = (UserKey) o;

        if ( !value.equals( userKey.value ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 463;
        final int multiplierNonZeroOddNumber = 723;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( value ).toHashCode();
    }

    public String toString()
    {
        return value;
    }

    public static Collection<UserKey> parseCollection( String[] keys )
    {
        List<UserKey> list = new ArrayList<UserKey>();
        for ( String key : keys )
        {
            list.add( new UserKey( key ) );
        }
        return list;
    }
}