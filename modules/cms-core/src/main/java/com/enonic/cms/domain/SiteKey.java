/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class SiteKey
    extends AbstractIntegerBasedKey
    implements Serializable
{
    public SiteKey( String key )
    {
        init( key );
    }

    public SiteKey( int key )
    {
        init( key );
    }

    public SiteKey( Integer key )
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

        SiteKey key = (SiteKey) o;

        return intValue == key.intValue;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 657;
        final int multiplierNonZeroOddNumber = 461;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( intValue ).toHashCode();
    }
}
