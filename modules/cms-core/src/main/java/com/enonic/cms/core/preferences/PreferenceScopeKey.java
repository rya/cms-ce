/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.InvalidKeyException;


public class PreferenceScopeKey
{

    private Integer firstKey;

    private Integer secondKey;

    private String keyAsString;

    public PreferenceScopeKey( String key )
    {

        if ( key == null )
        {
            throw new IllegalArgumentException( "Given key cannot be null" );
        }

        if ( key.indexOf( ":" ) > -1 )
        {
            StringTokenizer st = new StringTokenizer( key, ":" );

            try
            {
                firstKey = Integer.valueOf( st.nextToken() );
            }
            catch ( NumberFormatException e )
            {
                throw new InvalidKeyException( key, this.getClass(), "first key not a number" );
            }

            try
            {
                secondKey = Integer.valueOf( StringUtils.substringBefore( st.nextToken(), "." ) );
            }
            catch ( NumberFormatException e )
            {
                throw new InvalidKeyException( key, this.getClass(), "second key not a number" );
            }

            keyAsString = firstKey + ":" + secondKey;
        }
        else
        {
            try
            {
                firstKey = Integer.valueOf( key );
            }
            catch ( NumberFormatException e )
            {
                throw new InvalidKeyException( key, this.getClass(), "first key not a number" );
            }

            keyAsString = firstKey.toString();
        }

    }

    public Integer getFirstKey()
    {
        return firstKey;
    }

    public Integer getSecondKey()
    {
        return secondKey;
    }

    public boolean isDoubleKey()
    {
        return firstKey != null && secondKey != null;
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

        PreferenceScopeKey that = (PreferenceScopeKey) o;

        if ( !keyAsString.equals( that.keyAsString ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 473, 343 ).append( keyAsString ).toHashCode();
    }

    public String toString()
    {
        return keyAsString;
    }
}
