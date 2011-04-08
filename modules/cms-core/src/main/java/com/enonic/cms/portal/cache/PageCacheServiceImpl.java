/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.cache;


import org.joda.time.DateTime;

import com.enonic.cms.domain.CacheObjectSettings;
import com.enonic.cms.domain.CachedObject;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.rendering.PageCacheKey;
import com.enonic.cms.domain.portal.rendering.WindowCacheKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;


public class PageCacheServiceImpl
    extends AbstractBaseCacheService
    implements PageCacheService
{
    private static final String TYPE_PAGE = "P";

    private static final String TYPE_OBJECT = "O";

    public PageCacheServiceImpl( SiteKey siteKey )
    {
        super( siteKey );
    }

    public CachedObject cachePage( PageCacheKey key, Object page, CacheObjectSettings settings )
    {
        if ( !isEnabled() )
        {
            return new CachedObject( page, false );
        }

        int secondsToLive = parseSecondsToLive( settings );
        CachedObject cachedObject = new CachedObject( page );
        cachedObject.setExpirationTime( new DateTime().plusSeconds( secondsToLive ) );
        String group = resolveGroupStringForPage( getSiteKey(), key.getMenuItemKey() );
        cacheObject( group, key, cachedObject, secondsToLive );
        return cachedObject;
    }

    public CachedObject cachePortletWindow( WindowCacheKey key, Object object, CacheObjectSettings settings )
    {
        if ( !isEnabled() )
        {
            return new CachedObject( object, false );
        }

        int secondsToLive = parseSecondsToLive( settings );
        CachedObject cachedObject = new CachedObject( object );
        cachedObject.setExpirationTime( new DateTime().plusSeconds( secondsToLive ) );
        String group = resolveGroupStringForObject( getSiteKey(), key.getMenuItemKey() );
        cacheObject( group, key, cachedObject, secondsToLive );
        return cachedObject;
    }

    public CachedObject getCachedPage( PageCacheKey key )
    {
        if ( !isEnabled() )
        {
            return null;
        }

        final String group = resolveGroupStringForPage( getSiteKey(), key.getMenuItemKey() );
        return getCachedObject( group, key );
    }

    public CachedObject getCachedPortletWindow( WindowCacheKey key )
    {
        if ( !isEnabled() )
        {
            return null;
        }

        final String group = resolveGroupStringForObject( getSiteKey(), key.getMenuItemKey() );
        return getCachedObject( group, key );
    }

    public void removeEntriesBySite()
    {
        cacheFacade.removeGroupByPrefix( getSiteKey() + "-" );
    }

    public void removePageEntriesBySite()
    {
        cacheFacade.removeGroupByPrefix( getSiteKey() + "-" + TYPE_PAGE + "-" );
    }

    public void removePortletWindowEntriesBySite()
    {
        cacheFacade.removeGroupByPrefix( getSiteKey() + "-" + TYPE_OBJECT + "-" );
    }

    public void removeEntriesByMenuItem( final MenuItemKey menuItemKey )
    {
        if ( !isEnabled() )
        {
            return;
        }

        String groupForPage = resolveGroupStringForPage( getSiteKey(), menuItemKey );
        cacheFacade.removeGroup( groupForPage );

        String groupForObjects = resolveGroupStringForObject( getSiteKey(), menuItemKey );
        cacheFacade.removeGroup( groupForObjects );
    }

    private String resolveGroupStringForPage( SiteKey siteKey, MenuItemKey menuItemKey )
    {
        StringBuffer s = new StringBuffer();
        s.append( siteKey.toString() ).append( "-" ).append( TYPE_PAGE ).append( "-" ).append( menuItemKey );
        return s.toString();
    }

    private String resolveGroupStringForObject( SiteKey siteKey, MenuItemKey menuItemKey )
    {
        StringBuffer s = new StringBuffer();
        s.append( siteKey.toString() ).append( "-" ).append( TYPE_OBJECT ).append( "-" ).append( menuItemKey );
        return s.toString();
    }

    private int parseSecondsToLive( CacheObjectSettings settings )
    {
        if ( settings.useDefaultSettings() )
        {
            return getDefaultTimeToLive();
        }
        else
        {
            // specified or "live forever"
            return settings.getSecondsToLive();
        }
    }

}
