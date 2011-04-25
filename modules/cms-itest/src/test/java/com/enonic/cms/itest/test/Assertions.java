package com.enonic.cms.itest.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import junit.framework.Assert;

public class Assertions
{

    public static <T> void assertCollectionWithOneItem( T expected, Collection<T> collection )
    {

        Assert.assertEquals( 1, collection.size() );

        Assert.assertEquals( expected, collection.iterator().next() );
    }

    public static <T> T get( Collection<? extends T> collection, T pattern, Comparator<? super T> comparator )
    {
        for ( T candidate : collection )
        {
            if ( comparator.compare( pattern, candidate ) > 0 )
            {
                return candidate;
            }
        }

        return null;
    }

    public static <T> boolean assertUnorderedLists( Collection<? extends T> a1, Collection<? extends T> a2 )
    {
        return assertUnorderedArrays( a1.toArray(), a2.toArray() );
    }

    public static boolean assertUnorderedArrays( Object[] a1, Object[] a2 )
    {
        Object[] b1 = a1.clone();
        Object[] b2 = a2.clone();

        Arrays.sort( b1 );
        Arrays.sort( b2 );

        return assertArrayEquals( b1, b2 );
    }

    private static boolean assertArrayEquals( Object[] a1, Object[] a2 )
    {
        return arrayToString( a1 ).equals( arrayToString( a2 ) );
    }

    private static String arrayToString( Object[] a )
    {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ )
        {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }
}
