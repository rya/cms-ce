/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.base;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.config.CacheConfig;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

/**
 * This class implements the abstract cache.
 */
public abstract class AbstractCacheFacade
    implements CacheFacade
{
    /**
     * Name of cache.
     */
    private final String name;

    private final String implementationName;

    /**
     * Cache configuration.
     */
    private final CacheConfig config;

    /**
     * Hit count.
     */
    private int hitCount = 0;

    /**
     * Miss count.
     */
    private int missCount = 0;

    /**
     * Construct the cache facade.
     */
    protected AbstractCacheFacade( String name, String implementationName, CacheConfig config )
    {
        this.name = name;
        this.implementationName = implementationName;
        this.config = config;
    }

    /**
     * @inherit
     */
    public final String getName()
    {
        return this.name;
    }

    /**
     * @inherit
     */
    public final boolean getDiskOverflow()
    {
        return this.config.getDiskOverflow();
    }

    /**
     * @inherit
     */
    public final int getMemoryCapacity()
    {
        return this.config.getMemoryCapacity();
    }

    public int getDiskCapacity()
    {
        return this.config.getDiskCapacity();
    }

    /**
     * @inherit
     */
    public final int getTimeToLive()
    {
        return this.config.getTimeToLive();
    }

    /**
     * @inherit
     */
    public final int getHitCount()
    {
        return this.hitCount;
    }

    /**
     * @inherit
     */
    public final int getMissCount()
    {
        return this.missCount;
    }

    /**
     * @inherit
     */
    public final void clearStatistics()
    {
        this.hitCount = 0;
        this.missCount = 0;
    }

    /**
     * @inherit
     */
    public final Object get( String group, String key )
    {
        String compositeKey = createCompositeKey( group, key );
        Object value = doGet( compositeKey );

        if ( value != null )
        {
            this.hitCount++;
        }
        else
        {
            this.missCount++;
        }

        return value;
    }

    /**
     * @inherit
     */
    public final void put( String group, String key, Object value )
    {
        put( group, key, value, this.config.getTimeToLive() );
    }

    /**
     * @inherit
     */
    public final void put( String group, String key, Object value, int timeToLive )
    {
        if ( value == null )
        {
            remove( group, key );
        }
        else
        {
            timeToLive = timeToLive < -1 ? this.config.getTimeToLive() : timeToLive;
            String compositeKey = createCompositeKey( group, key );
            doPut( group, compositeKey, value, timeToLive );
        }
    }

    /**
     * @inherit
     */
    public final void remove( String group, String key )
    {
        String compositeKey = createCompositeKey( group, key );
        doRemove( compositeKey );
    }

    /**
     * @inherit
     */
    public final void removeGroup( String group )
    {
        doRemoveGroup( group );
    }

    public void removeGroupByPrefix( String prefix )
    {
        doRemoveGroupByPrefix( prefix );
    }

    /**
     * Clear the cache.
     */
    public final void removeAll()
    {
        doRemoveAll();
    }


    public abstract Object doGet( String compositeKey );

    public abstract void doPut( String group, String compositeKey, Object value, int timeToLive );

    public abstract void doRemove( String compositeKey );

    public abstract void doRemoveGroup( String groupName );

    public abstract void doRemoveGroupByPrefix( String prefix );

    public abstract void doRemoveAll();


    /**
     * @inherit
     */
    public final XMLDocument getInfoAsXml()
    {
        Element root = new Element( "cache" );

        root.setAttribute( "name", getName() );
        root.setAttribute( "implementationName", implementationName );
        root.setAttribute( "memoryCapacity", String.valueOf( getMemoryCapacity() ) );
        root.setAttribute( "diskCapacity", String.valueOf( getDiskCapacity() ) );
        root.setAttribute( "diskOverflow", String.valueOf( getDiskOverflow() ) );
        root.setAttribute( "timeToLive", String.valueOf( getTimeToLive() ) );

        Element statsElem = new Element( "statistics" );
        statsElem.setAttribute( "objectCount", String.valueOf( getCount() ) );
        statsElem.setAttribute( "cacheHits", String.valueOf( getHitCount() ) );
        statsElem.setAttribute( "cacheMisses", String.valueOf( getMissCount() ) );

        root.addContent( statsElem );
        return XMLDocumentFactory.create( new Document( root ) );
    }

    private String createCompositeKey( String group, String key )
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
}
