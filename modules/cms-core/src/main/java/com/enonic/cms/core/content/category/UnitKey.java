package com.enonic.cms.core.content.category;


import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.AbstractIntegerBasedKey;


public class UnitKey
    extends AbstractIntegerBasedKey
    implements Serializable
{
    public UnitKey( String key )
    {
        init( key );
    }

    public UnitKey( int key )
    {
        init( key );
    }

    public UnitKey( Integer key )
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

        UnitKey key = (UnitKey) o;

        return toInt() == key.toInt();
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 837, 661 ).append( toInt() ).toHashCode();
    }
}
