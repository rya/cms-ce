/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;

public class BinaryDataKey
    extends AbstractIntegerBasedKey
    implements Serializable
{

    public BinaryDataKey( String key )
    {
        init( key );
    }

    public BinaryDataKey( int key )
    {
        init( key );
    }

    public BinaryDataKey( Integer key )
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

        BinaryDataKey key = (BinaryDataKey) o;

        return intValue == key.intValue;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 413, 179 ).append( intValue ).toHashCode();
    }


    public static List<BinaryDataKey> convertToList( int[] array )
    {

        if ( array == null || array.length == 0 )
        {
            return new ArrayList<BinaryDataKey>();
        }

        List<BinaryDataKey> list = new ArrayList<BinaryDataKey>( array.length );
        for ( int value : array )
        {
            list.add( new BinaryDataKey( value ) );

        }
        return list;
    }

    public static List<BinaryDataKey> convertList( List<BinaryDataAndBinary> binaries )
    {
        if ( binaries == null || binaries.size() == 0 )
        {
            return new ArrayList<BinaryDataKey>();
        }

        List<BinaryDataKey> list = new ArrayList<BinaryDataKey>();
        for ( BinaryDataAndBinary binary : binaries )
        {
            list.add( binary.getBinaryData().getBinaryDataKey() );
        }
        return list;
    }
}
