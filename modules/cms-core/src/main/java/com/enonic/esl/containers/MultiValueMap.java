/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class MultiValueMap
    extends HashMap
{

    private HashMap attributeMap = new HashMap<Object, String>();

    private boolean allowNullValues = false;

    private static class ValueList
        implements List, Serializable
    {

        private List<Object> values;

        public ValueList()
        {
            values = new ArrayList<Object>();
        }

        public ValueList( Collection<Object> c )
        {
            values = new ArrayList<Object>( c );
        }

        public ValueList( int initialCapacity )
        {
            values = new ArrayList<Object>( initialCapacity );
        }

        public ValueList( Object obj )
        {
            values = new ArrayList<Object>();
            values.add( obj );
        }

        public int size()
        {
            return values.size();
        }

        public boolean isEmpty()
        {
            return values.isEmpty();
        }

        public boolean contains( Object o )
        {
            return values.contains( o );
        }

        public Iterator<Object> iterator()
        {
            return values.iterator();
        }

        public Object[] toArray()
        {
            return values.toArray();
        }

        public Object[] toArray( Object[] a )
        {
            return values.toArray( a );
        }

        public boolean add( Object o )
        {
            return values.add( o );
        }

        public boolean remove( Object o )
        {
            return values.remove( o );
        }

        public boolean containsAll( Collection c )
        {
            return values.containsAll( c );
        }

        public boolean addAll( Collection c )
        {
            return values.addAll( c );
        }

        public boolean addAll( int index, Collection c )
        {
            return values.addAll( index, c );
        }

        public boolean removeAll( Collection c )
        {
            return values.removeAll( c );
        }

        public boolean retainAll( Collection c )
        {
            return values.retainAll( c );
        }

        public void clear()
        {
            values.clear();
        }

        public Object get( int index )
        {
            return values.get( index );
        }

        public Object set( int index, Object element )
        {
            return values.set( index, element );
        }

        public void add( int index, Object element )
        {
            values.add( index, element );
        }

        public Object remove( int index )
        {
            return values.remove( index );
        }

        public int indexOf( Object o )
        {
            return values.indexOf( o );
        }

        public int lastIndexOf( Object o )
        {
            return values.lastIndexOf( o );
        }

        public ListIterator<Object> listIterator()
        {
            return values.listIterator();
        }

        public ListIterator<Object> listIterator( int index )
        {
            return values.listIterator( index );
        }

        public List<Object> subList( int fromIndex, int toIndex )
        {
            return values.subList( fromIndex, toIndex );
        }

        public String toString()
        {
            return values.toString();
        }

    }


    public MultiValueMap( int initialCapacity, float loadFactor )
    {
        super( initialCapacity, loadFactor );
    }

    public MultiValueMap( int initialCapacity )
    {
        super( initialCapacity );
    }

    public MultiValueMap()
    {
        super();
    }

    public MultiValueMap( boolean allowNullValues )
    {
        super();
        this.allowNullValues = allowNullValues;
    }

    public MultiValueMap( Map m )
    {
        Iterator iterator = m.entrySet().iterator();
        while ( iterator.hasNext() )
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            put( entry.getKey(), entry.getValue() );
        }
    }

    /**
     * @see java.util.Map#put(java.lang.Object,java.lang.Object)
     */
    public Object put( Object key, Object value )
    {
        return put( key, value, null, false );
    }

    public Object put( Object key, Object value, String attribute )
    {
        return put( key, value, attribute, false );
    }

    public Object put( Object key, int value )
    {
        return put( key, value, null, false );
    }

    public Object put( Object key, int value, String attribute )
    {
        return put( key, value, attribute, false );
    }

    public Object put( Object key, int[] values )
    {
        return put( key, values, null );
    }

    public Object put( Object key, int[] values, String attribute )
    {
        if ( values == null )
        {
            return put( key, null, attribute, false );
        }
        Object obj = null;
        for ( int i = 0; i < values.length; i++ )
        {
            if ( i == 0 )
            {
                obj = put( key, values[0], false );
            }
            else
            {
                put( key, values[i], attribute, false );
            }
        }
        return obj;
    }

    public Object put( Object key, boolean value )
    {
        return put( key, value, null, false );
    }

    public Object put( Object key, boolean value, String attribute )
    {
        return put( key, value, attribute, false );
    }

    public Object put( Object key, Object value, boolean removeOld )
    {
        return put( key, value, null, removeOld );
    }

    public Object put( Object key, Object value, String attribute, boolean removeOld )
    {
        Object obj;
        if ( !removeOld && containsKey( key ) && ( value != null || allowNullValues ) )
        {
            List<Object> values = (List<Object>) get( key );
            if ( value instanceof ValueList )
            {
                values.addAll( (ValueList) value );
            }
            else
            {
                values.add( value );
            }
            obj = values;
        }
        else if ( value != null && value instanceof ValueList )
        {
            obj = super.put( key, value );
        }
        else
        {
            List<Object> values = new ValueList();
            if ( value != null || allowNullValues )
            {
                values.add( value );
            }
            obj = super.put( key, values );
        }

        if ( attribute != null )
        {
            attributeMap.put( key, attribute );
        }

        return obj;
    }

    public List getValueList( Object key )
    {
        return (List) get( key );
    }

    public String getAttribute( Object key )
    {
        return (String) attributeMap.get( key );
    }

    public boolean containsValue( Object value )
    {
        Iterator iterator = super.values().iterator();
        while ( iterator.hasNext() )
        {
            List values = (List) iterator.next();
            if ( values.contains( value ) )
            {
                return true;
            }
        }
        return false;
    }

}
