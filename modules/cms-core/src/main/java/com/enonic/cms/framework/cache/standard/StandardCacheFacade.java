/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import com.enonic.cms.framework.cache.base.AbstractCacheFacade;
import com.enonic.cms.framework.cache.config.CacheConfig;

public final class StandardCacheFacade
    extends AbstractCacheFacade
{
    /**
     * Peer cache.
     */
    private final StandardCache peer;

    /**
     * Construct the cache.
     */
    public StandardCacheFacade( String name, StandardCache peer, CacheConfig config )
    {
        super( name, "Enonic LinkedHashMap-based Cache", config );
        this.peer = peer;
    }

    /**
     * @inherit
     */
    public Object doGet( String key )
    {
        CacheEntry entry = this.peer.get( key );

        if ( entry == null )
        {
            return null;
        }

        return entry.getValue();
    }

    /**
     * @inherit
     */
    public void doPut( String group, String key, Object value, int timeToLive )
    {
        CacheEntry entry = new CacheEntry( key, value, timeToLive > 0 ? timeToLive * 1000L : 0 );
        this.peer.put( entry );
    }

    /**
     * @inherit
     */
    public void doRemove( String key )
    {
        this.peer.remove( key );
    }

    /**
     * @inherit
     */
    public void doRemoveGroup( String group )
    {
        if ( group != null )
        {
            this.peer.removeGroup( group );
        }
        else
        {
            this.peer.removeAll();
        }
    }

    public void doRemoveGroupByPrefix( String prefix )
    {
        if ( prefix != null )
        {
            this.peer.removeGroupByPrefix( prefix );
        }
        else
        {
            this.peer.removeAll();
        }
    }

    /**
     * @inherit
     */
    public void doRemoveAll()
    {
        this.peer.removeAll();
    }

    /**
     * @inherit
     */
    public int getCount()
    {
        return this.peer.numberOfEntries();
    }
}
