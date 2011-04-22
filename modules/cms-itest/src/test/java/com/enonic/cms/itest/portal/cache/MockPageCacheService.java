/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.portal.cache;

import com.enonic.cms.portal.cache.PageCacheService;

import com.enonic.cms.domain.CacheObjectSettings;
import com.enonic.cms.domain.CachedObject;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.portal.rendering.PageCacheKey;
import com.enonic.cms.portal.rendering.WindowCacheKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

/**
 * Mar 4, 2010
 */
public class MockPageCacheService
    implements PageCacheService
{
    public CachedObject cachePage( PageCacheKey key, Object page, CacheObjectSettings settings )
    {
        return null;
    }

    public CachedObject cachePortletWindow( WindowCacheKey key, Object object, CacheObjectSettings settings )
    {
        return null;
    }

    public CachedObject getCachedPage( PageCacheKey key )
    {
        return null;
    }

    public CachedObject getCachedPortletWindow( WindowCacheKey key )
    {
        return null;
    }

    public void removeEntriesBySite()
    {

    }

    public void removePageEntriesBySite()
    {

    }

    public void removePortletWindowEntriesBySite()
    {

    }

    public void removeEntriesByMenuItem( MenuItemKey menuItemKey )
    {

    }

    public void clearCache()
    {

    }

    public SiteKey getSiteKey()
    {
        return null;
    }

    public boolean isEnabled()
    {
        return false;
    }

    public int getDefaultTimeToLive()
    {
        return 0;
    }
}
