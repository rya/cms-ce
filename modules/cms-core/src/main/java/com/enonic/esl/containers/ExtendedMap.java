/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;
import org.apache.commons.fileupload.FileItem;

import com.enonic.esl.net.URL;

public class ExtendedMap
    extends HashMap
    implements Map
{

    private final static long serialVersionUID = 100000000L;

    private boolean allowNullValues = false;

    private int fileItemCount;

    public ExtendedMap( int initialCapacity, float loadFactor, boolean allowNullValues )
    {
        super( initialCapacity, loadFactor );
        this.allowNullValues = allowNullValues;
    }

    public ExtendedMap( int initialCapacity )
    {
        super( initialCapacity );
    }

    public ExtendedMap()
    {
        super();
    }

    public ExtendedMap( boolean allowNullValues )
    {
        super();

        this.allowNullValues = allowNullValues;
    }

    public ExtendedMap( Map t )
    {
        super( t );
    }

    public ExtendedMap( Multimap multi )
    {
        this( multi, false );
    }

    public ExtendedMap( Multimap multi, boolean allowNullValues )
    {
        super();
        this.allowNullValues = allowNullValues;

        Iterator iter = multi.keySet().iterator();
        while ( iter.hasNext() )
        {
            Object key = iter.next();
            List values = new ArrayList(multi.get( key ));
            if ( values.size() > 1 )
            {
                if ( values.get( 0 ) instanceof FileItem )
                {
                    FileItem[] valuesArray = new FileItem[values.size()];
                    for ( int i = 0; i < valuesArray.length; i++ )
                    {
                        valuesArray[i] = (FileItem) values.get( i );
                    }
                    put( key, valuesArray );
                }
                else
                {
                    String[] valuesArray = new String[values.size()];
                    for ( int i = 0; i < valuesArray.length; i++ )
                    {
                        Object value = values.get( i );
                        if ( value != null )
                        {
                            valuesArray[i] = String.valueOf( value );
                        }
                        else
                        {
                            valuesArray[i] = null;
                        }
                    }
                    put( key, valuesArray );
                }
            }
            else if ( values.size() == 1 )
            {
                put( key, values.get( 0 ) );
            }
        }
    }

    public ExtendedMap( Map t, boolean allowNullValues )
    {
        super( t );

        this.allowNullValues = allowNullValues;
    }

    public Object get( Object key )
    {
        if ( containsKey( key ) )
        {
            return super.get( key );
        }
        else
        {
            throw new IllegalArgumentException( "Failed to get value with key [" + key + "]" );
        }
    }

    public Object get( Object key, Object defaultValue )
    {
        if ( containsKey( key ) )
        {
            return super.get( key );
        }
        else
        {
            return defaultValue;
        }
    }

    public Object put( Object key, Object value )
    {
        if ( value != null && value instanceof FileItem )
        {
            this.fileItemCount++;
        }
        if ( allowNullValues )
        {
            return super.put( key, value );
        }
        else
        {
            if ( value != null )
            {
                return super.put( key, value );
            }
            else
            {
                return remove( key );
            }
        }
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove( Object key )
    {
        Object value = super.remove( key );
        if ( value != null && value instanceof FileItem )
        {
            this.fileItemCount--;
        }
        return value;
    }

    public Object put( Object key, URL value )
    {
        if ( value != null )
        {
            return put( key, value.toString() );
        }
        else
        {
            return put( key, (Object) null );
        }
    }

    public String getString( Object key )
    {
        Object obj = get( key );
        if ( obj instanceof String[] )
        {
            return ( (String[]) obj )[0];
        }
        else if ( obj instanceof FileItem )
        {
            return ( (FileItem) obj ).getName();
        }
        else if ( obj == null )
        {
            return null;
        }
        else
        {
            return obj.toString();
        }
    }

    public String getString( Object key, String defaultValue )
    {
        if ( containsKey( key ) )
        {
            Object obj = get( key );
            if ( obj == null )
            {
                return null;
            }
            else
            {
                return getString( key );
            }
        }
        else
        {
            return defaultValue;
        }
    }

    public Object putString( Object key, String value )
    {
        if ( allowNullValues )
        {
            return put( key, value );
        }
        else
        {
            if ( value != null && value.length() > 0 )
            {
                return put( key, value );
            }
            else
            {
                return remove( key );
            }
        }
    }

    public byte getByte( Object key )
    {
        try
        {
            return (Byte) get( key );
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a byte." );
        }
    }

    public byte getByte( Object key, byte defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return (Byte) get( key );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a byte." );
        }
    }

    public Object putByte( Object key, byte value )
    {
        return put( key, new Byte( value ) );
    }

    public short getShort( Object key )
    {
        try
        {
            return (Short) get( key );
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a short." );
        }
    }

    public short getShort( Object key, short defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return (Short) get( key );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a short." );
        }
    }

    public Object putShort( Object key, short value )
    {
        return put( key, new Short( value ) );
    }

    public int getInt( Object key )
    {
        try
        {
            return Integer.parseInt( get( key ).toString() );
        }
        catch ( NumberFormatException nfe )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not an integer." );
        }
    }

    public int getInt( Object key, int defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return Integer.parseInt( getString( key ) );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( NumberFormatException nfe )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not an integer." );
        }
    }

    public Object putInt( Object key, int value )
    {
        return put( key, new Integer( value ) );
    }

    public Object put( Object key, int value )
    {
        return put( key, new Integer( value ) );
    }

    public long getLong( Object key )
    {
        try
        {
            return (Long) get( key );
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a long." );
        }
    }

    public long getLong( Object key, long defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return (Long) get( key );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a long." );
        }
    }

    public Object putLong( Object key, long value )
    {
        return put( key, value );
    }

    public float getFloat( Object key )
    {
        try
        {
            return (Float) get( key );
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a float." );
        }
    }

    public float getFloat( Object key, float defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return (Float) get( key );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a float." );
        }
    }

    public Object putFloat( Object key, float value )
    {
        return put( key, value );
    }

    public double getDouble( Object key )
    {
        try
        {
            return (Double) get( key );
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a double." );
        }
    }

    public double getDouble( Object key, double defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return (Double) get( key );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a double." );
        }
    }

    public Object putDouble( Object key, double value )
    {
        return put( key, value );
    }

    public char getChar( Object key )
    {
        try
        {
            return (Character) get( key );
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a char." );
        }
    }

    public char getChar( Object key, char defaultValue )
    {
        try
        {
            if ( containsKey( key ) )
            {
                return (Character) get( key );
            }
            else
            {
                return defaultValue;
            }
        }
        catch ( ClassCastException cce )
        {
            throw new IllegalArgumentException( "Value with key [" + key + "] is not a char." );
        }
    }

    public Object putChar( Object key, char value )
    {
        return put( key, new Character( value ) );
    }

    public FileItem getFileItem( Object key )
    {
        return ( (FileItem) get( key ) );
    }

    public FileItem getFileItem( Object key, FileItem defaultValue )
    {
        if ( containsKey( key ) )
        {
            return ( (FileItem) get( key ) );
        }
        else
        {
            return defaultValue;
        }
    }

    public FileItem[] getFileItems()
    {
        ArrayList<Object> fileItems = new ArrayList<Object>();
        Iterator iter = entrySet().iterator();
        while ( iter.hasNext() )
        {
            Object obj = ( (Map.Entry) iter.next() ).getValue();
            if ( obj instanceof FileItem )
            {
                fileItems.add( obj );
            }
            else if ( obj instanceof FileItem[] )
            {
                FileItem[] fileItemArray = (FileItem[]) obj;
                for ( int i = 0; i < fileItemArray.length; i++ )
                {
                    fileItems.add( fileItemArray[i] );
                }
            }
        }
        return fileItems.toArray( new FileItem[fileItems.size()] );
    }

    public boolean getBoolean( Object key, boolean defaultValue )
    {
        if ( containsKey( key ) )
        {
            return getBoolean( key );
        }
        else
        {
            return defaultValue;
        }
    }

    public boolean getBoolean( Object key )
    {
        String value = get( key ).toString();
        if ( "true".equals( value ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Object putBoolean( Object key, boolean value )
    {
        return put( key, ( value ? Boolean.TRUE : Boolean.FALSE ) );
    }

    public Object put( Object key, boolean value )
    {
        return put( key, ( value ? Boolean.TRUE : Boolean.FALSE ) );
    }

    /**
     * Verify that the map contains a collection of keys.
     */
    public boolean containsKeys( Collection coll, boolean allowNull )
    {
        boolean result = true;

        Iterator iter = coll.iterator();
        while ( iter.hasNext() )
        {
            Object key = iter.next();

            if ( !containsKey( key ) )
            {
                result = false;
            }
            else if ( !allowNull && get( key ) == null )
            {
                result = false;
            }

            if ( !result )
            {
                break;
            }
        }

        return result;
    }

    /**
     * Verify that the map contains a collection of keys.
     *
     * @param keys      A list of the required keys.
     * @param allowNull Set to true if null-values should be allowed.
     */
    public boolean containsKeys( Object[] keys, boolean allowNull )
    {
        boolean result = true;

        for ( int i = 0; i < keys.length; ++i )
        {
            Object key = keys[i];

            if ( !containsKey( key ) )
            {
                result = false;
            }
            else if ( !allowNull && get( key ) == null )
            {
                result = false;
            }

            if ( !result )
            {
                break;
            }
        }

        return result;
    }

    /**
     * Retrieve a String array from the map. If the key only has one associated value, an array is constructed. An empty array is returned
     * if the key does not exist. <p/> <p> It is assumed that the programmer knows what he's doing, as no type checking is performed.</p>
     *
     * @param key
     * @return
     */
    public String[] getStringArray( Object key )
    {
        String[] array;

        if ( containsKey( key ) )
        {
            Object obj = get( key );
            if ( obj == null )
            {
                array = new String[0];
            }
            else if ( obj.getClass() == String[].class )
            {
                array = (String[]) get( key );
            }
            else
            {
                array = new String[]{obj.toString()};
            }
        }
        else
        {
            array = new String[0];
        }

        return array;
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        super.clear();
        this.fileItemCount = 0;
    }

    public boolean hasFileItems()
    {
        return this.fileItemCount > 0;
    }
}
