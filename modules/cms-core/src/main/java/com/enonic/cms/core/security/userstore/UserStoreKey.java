/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;

public class UserStoreKey
    extends AbstractIntegerBasedKey
    implements Serializable
{


    public UserStoreKey( String key )
    {
        if ( key.startsWith( "#" ) )
        {
            key = key.substring( 1 );
        }

        init( key );
    }

    public UserStoreKey( int key )
    {
        init( key );
    }

    public UserStoreKey( Integer key )
    {
        init( key );
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

        UserStoreKey other = (UserStoreKey) o;

        return intValue == other.intValue;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 551;
        final int multiplierNonZeroOddNumber = 831;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( intValue ).toHashCode();
    }


    public static UserStoreKey parse( String key )
    {
        if ( key == null )
        {
            return null;
        }
        else
        {
            return new UserStoreKey( key );
        }
    }

}