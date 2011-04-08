/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.cache;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.domain.CachedObject;
import com.enonic.cms.domain.SiteKey;

public abstract class AbstractBaseCacheService
    implements BaseCacheService
{

    private SiteKey siteKey;

    protected CacheFacade cacheFacade;

    private boolean enabled = false;

    private Integer timeToLive;


    protected AbstractBaseCacheService( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public void setEnabled( boolean value )
    {
        this.enabled = value;
    }

    public void setCacheFacade( CacheFacade value )
    {
        this.cacheFacade = value;
    }

    public void setTimeToLive( Integer value )
    {
        this.timeToLive = value;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void clearCache()
    {

        cacheFacade.removeAll();
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    protected void cacheObject( String group, Object key, CachedObject obj, int secondsToLive )
    {
        cacheFacade.put( group, key.toString(), obj, secondsToLive );
    }

    protected CachedObject getCachedObject( String group, Object key )
    {
        final CachedObject cachedObject = (CachedObject) cacheFacade.get( group, key.toString() );
        if ( cachedObject != null && cachedObject.isExpired() )
        {
            cacheFacade.remove( siteKey.toString(), key.toString() );
            return null;
        }
        return cachedObject;
    }

    protected boolean isKeyInCache( Object key )
    {
        Object o = cacheFacade.get( siteKey.toString(), key.toString() );
        return o != null;
    }

    protected void removeObjectFromCache( Object key )
    {
        cacheFacade.remove( siteKey.toString(), key.toString() );
    }

    /**
     * Returns the cache default time to live for objects.
     */
    public int getDefaultTimeToLive()
    {

        if ( timeToLive != null )
        {
            return timeToLive;
        }
        return cacheFacade.getTimeToLive();
    }
}
