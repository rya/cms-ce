/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HashMapCache
    implements Cache
{
    private Map cache = new HashMap();

    /* (non-Javadoc)
     * @see com.enonic.esl.cache.Cache#addObject(java.lang.Object, java.lang.Object)
     */

    public void addObject( Object key, Object object )
    {
        cache.put( key, object );
    }

    /* (non-Javadoc)
     * @see com.enonic.esl.cache.Cache#clear()
     */

    public void clear()
    {
        cache.clear();
    }

    /* (non-Javadoc)
     * @see com.enonic.esl.cache.Cache#getObject(java.lang.Object)
     */

    public Object getObject( Object key )
    {
        return cache.get( key );
    }

    /* (non-Javadoc)
     * @see com.enonic.esl.cache.Cache#remove(java.lang.Object)
     */

    public Object remove( Object key )
    {
        return cache.remove( key );
    }

    /* (non-Javadoc)
     * @see com.enonic.esl.cache.Cache#size()
     */

    public int size()
    {
        return cache.size();
    }

    public void removeValues( Object value )
    {
        if ( cache.containsValue( value ) )
        {
            Iterator iter = cache.keySet().iterator();
            while ( iter.hasNext() )
            {
                Object key = iter.next();
                if ( cache.get( key ).equals( value ) )
                {
                    cache.remove( key );
                }
            }
        }
    }

}
