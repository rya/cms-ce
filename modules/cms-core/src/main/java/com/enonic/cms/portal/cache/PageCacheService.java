/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.cache;

import com.enonic.cms.domain.CacheObjectSettings;
import com.enonic.cms.domain.CachedObject;
import com.enonic.cms.domain.portal.rendering.PageCacheKey;
import com.enonic.cms.domain.portal.rendering.WindowCacheKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

public interface PageCacheService
    extends BaseCacheService
{
    /**
     * Cache page and returns the wrapping CachedObject.
     */
    CachedObject cachePage( PageCacheKey key, Object page, CacheObjectSettings settings );

    /**
     * Cache content object and returns the wrapping CachedObject.
     */
    CachedObject cachePortletWindow( WindowCacheKey key, Object object, CacheObjectSettings settings );

    CachedObject getCachedPage( PageCacheKey key );

    CachedObject getCachedPortletWindow( WindowCacheKey key );

    void removeEntriesBySite();

    void removePageEntriesBySite();

    void removePortletWindowEntriesBySite();

    void removeEntriesByMenuItem( MenuItemKey menuItemKey );
}
