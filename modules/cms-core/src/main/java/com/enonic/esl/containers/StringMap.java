/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StringMap
    implements Map
{

    private Map<String, Object> internalMap;

    private boolean caseSensitive;

    /**
     *
     */
    public StringMap( boolean caseSensitive )
    {
        internalMap = new HashMap<String, Object>();
        this.caseSensitive = caseSensitive;
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey( Object key )
    {
        if ( key != null && !( key instanceof String ) )
        {
            throw new OnlyStringKeysSupported( key );
        }
        else
        {
            return containsKey( (String) key );
        }
    }

    /**
     * @param key
     * @return
     */
    public boolean containsKey( String key )
    {
        if ( key != null )
        {
            if ( caseSensitive )
            {
                return internalMap.containsKey( key );
            }
            else
            {
                String lowerCaseKey = key.toLowerCase();
                return internalMap.containsKey( lowerCaseKey );
            }
        }
        return false;
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get( Object key )
    {
        if ( key != null && !( key instanceof String ) )
        {
            throw new OnlyStringKeysSupported( key );
        }
        else
        {
            return get( (String) key );
        }
    }

    /**
     * @param key
     * @return
     */
    public Object get( String key )
    {
        if ( key != null )
        {
            if ( caseSensitive )
            {
                return internalMap.get( key );
            }
            else
            {
                String lowerCaseKey = key.toLowerCase();
                return internalMap.get( lowerCaseKey );
            }
        }
        return null;
    }

    /**
     * @see java.util.Map#put(java.lang.Object,java.lang.Object)
     */
    public Object put( Object key, Object value )
    {
        if ( key != null && !( key instanceof String ) )
        {
            throw new OnlyStringKeysSupported( key );
        }
        return put( (String) key, value );
    }

    public Object put( String key, Object value )
    {
        if ( key != null )
        {
            if ( caseSensitive )
            {
                return internalMap.put( key, value );
            }
            else
            {
                String lowerCaseKey = key.toLowerCase();
                return internalMap.put( lowerCaseKey, value );
            }
        }
        return internalMap.put( null, value );
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove( Object key )
    {
        if ( key != null && !( key instanceof String ) )
        {
            throw new OnlyStringKeysSupported( key );
        }
        return remove( (String) key );
    }

    public Object remove( String key )
    {
        if ( key != null )
        {
            if ( caseSensitive )
            {
                return internalMap.remove( key );
            }
            else
            {
                String lowerCaseKey = key.toLowerCase();
                return internalMap.remove( lowerCaseKey );
            }
        }
        return internalMap.remove( null );
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear()
    {
        internalMap.clear();
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue( Object value )
    {
        return internalMap.containsValue( value );
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet()
    {
        return internalMap.entrySet();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty()
    {
        return internalMap.isEmpty();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set<String> keySet()
    {
        return internalMap.keySet();
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll( Map t )
    {
        if ( t != null )
        {
            for ( Iterator iter = t.entrySet().iterator(); iter.hasNext(); )
            {
                Entry entry = (Entry) iter.next();
                put( entry.getKey(), entry.getValue() );
            }
        }
    }

    /**
     * @see java.util.Map#size()
     */
    public int size()
    {
        return internalMap.size();
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection<Object> values()
    {
        return internalMap.values();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return internalMap.toString();
    }

}
