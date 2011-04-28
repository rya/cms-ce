package com.enonic.cms.itest.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

public class Assertions
{

    public static <T> void assertCollectionWithOneItem( T expected, Collection<T> collection )
    {

        Assert.assertEquals( 1, collection.size() );

        Assert.assertEquals( expected, collection.iterator().next() );
    }

    public static String arrayToString( Object[] a )
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

    public static void assertArrayEquals( Object[] a1, Object[] a2 )
    {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    public static void assertListEquals( Object[] a1, List a2 )
    {
        assertArrayEquals( a1, a2.toArray() );
    }

    public static void assertUnorderedArrayArrayEquals( Object[] a1, Object[] a2 )
    {
        Object[] b1 = a1.clone();
        Object[] b2 = a2.clone();

        Arrays.sort( b1 );
        Arrays.sort( b2 );

        assertArrayEquals( b1, b2 );
    }

    public static void assertUnorderedArrayListEquals( Object[] a1, Collection<?> a2 )
    {
        assertUnorderedArrayArrayEquals( a1, a2.toArray() );
    }

    public static <T> void assertUnorderedListListEquals( Collection<? extends T> a1, Collection<? extends T> a2 )
    {
        assertUnorderedArrayArrayEquals( a1.toArray(), a2.toArray() );
    }

    public static void assertArraysEqual( Object[] array1, Object[] array2 )
    {
        boolean result = Arrays.equals( array1, array2 );
        Assert.assertTrue( result );
    }

    public static <T> void assertListElementsEqual( T[] expected, List<?> was )
    {
        for ( int i = 0; i < Math.min( expected.length, was.size() ); ++i )
        {
            Assert.assertEquals( "Element " + i + " doesn't match", expected[i], was.get( i ) );
        }

        Assert.assertEquals( "List size doesn't match", expected.length, was.size() );
    }

    public static void assertEmpty( Collection<?> collection )
    {
        Assert.assertTrue( "Collection<?> should be empty (has size =0)", collection.isEmpty() );
    }

    public static void assertNonEmpty( Collection<?> collection )
    {
        Assert.assertFalse( "Collection shouldn't be empty", collection.isEmpty() );
    }
}
