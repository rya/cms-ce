/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.cache;

import java.util.HashMap;

public class TimedHashMapCache
    extends HashMapCache
{

    protected class Entry
    {
        long timestamp;

        Object data;

        Entry( long t, Object o )
        {
            timestamp = t;
            data = o;
        }
    }

    protected HashMap cacheMap = new HashMap();

    protected long cacheTime;

    public TimedHashMapCache( long cacheTime )
    {
        super();
        this.cacheTime = cacheTime;
    }

    public void addObject( Object key, Object object )
    {
        Entry e = new Entry( System.currentTimeMillis() + cacheTime, object );
        super.addObject( key, e );
    }

    public Object remove( Object key )
    {
        Entry e = (Entry) super.remove( key );
        return ( e != null ? e.data : null );
    }

    public Object getObject( Object key )
    {
        long currentTime = System.currentTimeMillis();
        Entry e = (Entry) super.getObject( key );

        if ( e == null )
        {
            return null;
        }

        if ( e.timestamp > currentTime )
        {
            return e.data;
        }

        // cache is invalid. remove it and return null.
        super.remove( key );
        return null;
    }
}
