/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.ArrayList;
import java.util.List;

public final class ArrayUtil
{

    /**
     * Private constructor.
     */
    private ArrayUtil()
    {
    }

    public static boolean arrayContains( Object obj, Object[] array )
    {
        for ( int i = 0; i < array.length; ++i )
        {
            if ( array[i].equals( obj ) )
            {
                return true;
            }
        }

        return false;
    }

    public static boolean contains( int[] array, int value )
    {
        if ( array == null )
        {
            return false;
        }

        for ( int i = 0; i < array.length; i++ )
        {
            if ( array[i] == value )
            {
                return true;
            }
        }

        return false;
    }

    public static boolean contains( String[] array, String value )
    {
        if ( array == null )
        {
            return false;
        }

        for ( int i = 0; i < array.length; i++ )
        {
            if ( array[i].equals( value ) )
            {
                return true;
            }
        }

        return false;
    }

    public static String[] filter( String[] sourceValues, String[] excludeValues )
    {
        if ( sourceValues == null || sourceValues.length == 0 )
        {
            return sourceValues;
        }

        List<String> filtered = new ArrayList<String>();

        for ( int i = 0; i < sourceValues.length; i++ )
        {
            if ( !contains( excludeValues, sourceValues[i] ) )
            {
                filtered.add( sourceValues[i] );
            }
        }
        return filtered.toArray( new String[filtered.size()] );
    }
}
