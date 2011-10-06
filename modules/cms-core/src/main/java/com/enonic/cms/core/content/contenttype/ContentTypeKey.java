/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;
import com.enonic.cms.domain.InvalidKeyException;

public class ContentTypeKey
    extends AbstractIntegerBasedKey
    implements Serializable
{
    public ContentTypeKey( String key )
    {
        init( key );
    }

    public ContentTypeKey( int key )
    {
        init( key );
    }

    public ContentTypeKey( Integer key )
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

        ContentTypeKey key = (ContentTypeKey) o;

        return intValue == key.intValue;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 253, 843 ).append( intValue ).toHashCode();
    }

    public static List<ContentTypeKey> convertToList( int[] array )
        throws InvalidKeyException
    {

        if ( ( array == null ) || ( array.length == 0 ) )
        {
            return null;
        }

        List<ContentTypeKey> list = new ArrayList<ContentTypeKey>( array.length );
        for ( int value : array )
        {
            list.add( new ContentTypeKey( value ) );
        }
        return list;
    }

    public static Set<ContentTypeKey> convertToSet( int[] array )
        throws InvalidKeyException
    {

        if ( ( array == null ) || ( array.length == 0 ) )
        {
            return null;
        }

        Set<ContentTypeKey> set = new HashSet<ContentTypeKey>( array.length );
        for ( int value : array )
        {
            set.add( new ContentTypeKey( value ) );
        }
        return set;
    }
}