/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache;

import java.util.Map;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;

import com.enonic.cms.framework.cache.CacheFacade;

/**
 * This class implements hibernate cache provider.
 */
public final class HibernateCache
    implements Cache
{
    /**
     * Group name.
     */
    private final String groupName;

    /**
     * Shared cache.
     */
    private final CacheFacade cache;

    /**
     * Construct the cache.
     */
    public HibernateCache( String groupName, CacheFacade cache )
    {
        this.groupName = groupName;
        this.cache = cache;
    }

    /**
     * @see org.hibernate.cache.Cache#get(java.lang.Object)
     */
    public Object get( Object key )
    {
        return this.cache.get( this.groupName, key.toString() );
    }

    /**
     * @see org.hibernate.cache.Cache#put(java.lang.Object, java.lang.Object)
     */
    public void put( Object key, Object value )
    {
        this.cache.put( this.groupName, key.toString(), value );
    }

    /**
     * @see org.hibernate.cache.Cache#remove(java.lang.Object)
     */
    public void remove( Object key )
    {
        //("Removing one entry ("+key+") in group: " + this.groupName);
        this.cache.remove( this.groupName, key.toString() );
    }

    /**
     * @see org.hibernate.cache.Cache#clear()
     */
    public void clear()
    {
        //("Removing all cache entries in group: " + this.groupName);
        this.cache.removeGroup( this.groupName );
    }

    /**
     * @see org.hibernate.cache.Cache#destroy()
     */
    public void destroy()
    {
        // Do nothing
    }

    /**
     * @see org.hibernate.cache.Cache#lock(java.lang.Object)
     */
    public void lock( Object key )
    {
        // Do nothing
    }

    /**
     * @see org.hibernate.cache.Cache#unlock(java.lang.Object)
     */
    public void unlock( Object key )
    {
        // Do nothing
    }

    /**
     * @see org.hibernate.cache.Cache#nextTimestamp()
     */
    public long nextTimestamp()
    {
        return Timestamper.next();
    }

    /**
     * @see org.hibernate.cache.Cache#getTimeout()
     */
    public int getTimeout()
    {
        return Timestamper.ONE_MS * 60000;
    }

    /**
     * @see org.hibernate.cache.Cache#toMap()
     */
    @SuppressWarnings("unchecked")
    public Map toMap()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.hibernate.cache.Cache#getElementCountOnDisk()
     */
    public long getElementCountOnDisk()
    {
        return -1;
    }

    /**
     * @see org.hibernate.cache.Cache#getElementCountInMemory()
     */
    public long getElementCountInMemory()
    {
        return -1;
    }

    /**
     * @see org.hibernate.cache.Cache#getSizeInMemory()
     */
    public long getSizeInMemory()
    {
        return -1;
    }

    /**
     * @see org.hibernate.cache.Cache#getRegionName()
     */
    public String getRegionName()
    {
        return this.cache.getName();
    }

    /**
     * @see org.hibernate.cache.Cache#update(java.lang.Object, java.lang.Object)
     */
    public void update( Object key, Object value )
        throws CacheException
    {
        put( key, value );
    }

    /**
     * @see org.hibernate.cache.Cache#read(java.lang.Object)
     */
    public Object read( Object key )
        throws CacheException
    {
        return get( key );
    }
}
