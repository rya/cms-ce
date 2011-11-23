/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import java.util.concurrent.atomic.AtomicInteger;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

final class StandardCacheFacade
    implements CacheFacade 
{
    /**
     * Name of cache.
     */
    private final String name;

    /**
     * Peer cache.
     */
    private final StandardCache peer;

    /**
     * Cache configuration.
     */
    private final CacheConfig config;

    /**
     * Hit count.
     */
    private final AtomicInteger hitCount;

    /**
     * Miss count.
     */
    private final AtomicInteger missCount;

    public StandardCacheFacade( final String name, final StandardCache peer, final CacheConfig config )
    {
        this.name = name;
        this.peer = peer;
        this.config = config;
        this.hitCount = new AtomicInteger( 0 );
        this.missCount = new AtomicInteger( 0 );
    }

    public String getName()
    {
        return this.name;
    }

    public int getMemoryCapacity()
    {
        return this.config.getMemoryCapacity();
    }

    public int getTimeToLive()
    {
        return this.config.getTimeToLive();
    }

    public int getHitCount()
    {
        return this.hitCount.get();
    }

    public int getMissCount()
    {
        return this.missCount.get();
    }

    public void clearStatistics()
    {
        this.hitCount.set( 0 );
        this.missCount.set( 0 );
    }

    public Object get( final String group, final String key )
    {
        final String compositeKey = createCompositeKey( group, key );
        final Object value = doGet( compositeKey );

        if ( value != null )
        {
            this.hitCount.incrementAndGet();
        }
        else
        {
            this.missCount.incrementAndGet();
        }

        return value;
    }

    public void put( final String group, final String key, final Object value )
    {
        put( group, key, value, -1 );
    }

    public void put( final String group, final String key, final Object value, final int timeToLive )
    {
        if ( value == null )
        {
            remove( group, key );
        }
        else
        {
            final int realTimeToLive = timeToLive < 0 ? this.config.getTimeToLive() : timeToLive;
            final String compositeKey = createCompositeKey( group, key );
            doPut( compositeKey, value, realTimeToLive );
        }
    }

    public void remove( final String group, final String key )
    {
        final String compositeKey = createCompositeKey( group, key );
        this.peer.remove( compositeKey );
    }

    public void removeGroup( final String group )
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

    public void removeGroupByPrefix( final String prefix )
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

    public void removeAll()
    {
        this.peer.removeAll();
    }

    private Object doGet( final String compositeKey )
    {
        final CacheEntry entry = this.peer.get( compositeKey );

        if ( entry == null )
        {
            return null;
        }

        return entry.getValue();
    }

    private void doPut( final String compositeKey, final Object value, final int timeToLive )
    {
        final CacheEntry entry = new CacheEntry( compositeKey, value, timeToLive > 0 ? timeToLive * 1000L : 0 );
        this.peer.put( entry );
    }

    public int getCount()
    {
        return this.peer.numberOfEntries();
    }

    private String createCompositeKey( final String group, final String key )
    {
        if ( group != null )
        {
            return group + ":" + key;
        }
        else
        {
            return key;
        }
    }

    public XMLDocument getInfoAsXml()
    {
        final Element root = new Element( "cache" );

        root.setAttribute( "name", getName() );
        root.setAttribute( "implementationName", "Standard Cache" );
        root.setAttribute( "memoryCapacity", String.valueOf( getMemoryCapacity() ) );
        root.setAttribute( "timeToLive", String.valueOf( getTimeToLive() ) );

        final Element statsElem = new Element( "statistics" );
        statsElem.setAttribute( "objectCount", String.valueOf( getCount() ) );
        statsElem.setAttribute( "cacheHits", String.valueOf( getHitCount() ) );
        statsElem.setAttribute( "cacheMisses", String.valueOf( getMissCount() ) );

        root.addContent( statsElem );
        return XMLDocumentFactory.create( new Document( root ) );
    }
}
