/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;
import com.enonic.cms.domain.IntBasedKey;

public class ContentKey
    extends AbstractIntegerBasedKey
    implements Serializable, IntBasedKey
{

    public ContentKey( String contentKey )
    {
        init( contentKey );
    }

    public ContentKey( int contentKey )
    {
        init( contentKey );
    }

    public ContentKey( Integer contentKey )
    {
        init( contentKey );
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

        ContentKey contentKey = (ContentKey) o;

        return intValue == contentKey.intValue;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 755;
        final int multiplierNonZeroOddNumber = 349;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( intValue ).toHashCode();
    }

    public boolean isInList( int[] list )
    {
        if ( list == null || list.length == 0 )
        {
            return false;
        }

        for ( int value : list )
        {
            if ( value == toInt() )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isInCollection( Collection<ContentKey> collection )
    {
        if ( collection == null || collection.size() == 0 )
        {
            return false;
        }

        for ( ContentKey value : collection )
        {
            if ( value.equals( this ) )
            {
                return true;
            }
        }
        return false;
    }

    public static Collection<ContentKey> convertToList( int key )
    {
        Collection<ContentKey> list = new ArrayList<ContentKey>( 1 );
        list.add( new ContentKey( key ) );
        return list;
    }

    public static List<ContentKey> convertToList( ContentKey... keys )
    {
        List<ContentKey> list = new ArrayList<ContentKey>();
        for ( ContentKey key : keys )
        {
            list.add( key );
        }
        return list;
    }

    public static Set<ContentKey> convertToSet( ContentKey... keys )
    {
        Set<ContentKey> set = new HashSet<ContentKey>();
        //noinspection ManualArrayToCollectionCopy
        for ( ContentKey key : keys )
        {
            set.add( key );
        }
        return set;
    }

    public static List<ContentKey> convertToList( int[] array )
    {
        if ( array == null || array.length == 0 )
        {
            return null;
        }

        List<ContentKey> list = new ArrayList<ContentKey>( array.length );
        for ( int value : array )
        {
            list.add( new ContentKey( value ) );

        }
        return list;
    }

    public static List<ContentKey> convertToList( String[] contentKeysStrings )
    {
        List<ContentKey> contentKeys = new ArrayList<ContentKey>();

        for ( String contentKeyString : contentKeysStrings )
        {
            contentKeys.add( new ContentKey( contentKeyString ) );
        }
        return contentKeys;
    }

    public static List<ContentKey> convertToList( Collection<Integer> collection )
    {
        if ( collection == null || collection.size() == 0 )
        {
            return null;
        }

        List<ContentKey> list = new ArrayList<ContentKey>( collection.size() );
        for ( int value : collection )
        {
            list.add( new ContentKey( value ) );

        }
        return list;
    }

    public static Set convertToSet( Collection<ContentKey> contentKeys )
    {
        Set<ContentKey> set = new HashSet<ContentKey>();
        set.addAll( contentKeys );
        return set;
    }
}
