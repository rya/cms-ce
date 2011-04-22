/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.portal.cache;

import com.enonic.cms.portal.cache.PageCacheService;
import com.enonic.cms.portal.cache.SiteCachesService;

import com.enonic.cms.domain.SiteKey;

/**
 * Mar 4, 2010
 */
public class MockSiteCachesService
    implements SiteCachesService
{
    public void setUpSiteCachesService( SiteKey siteKey )
    {
        // do nothing
    }

    public void tearDownSiteCachesService( SiteKey siteKey )
    {
        // do nothing
    }

    public PageCacheService getPageCacheService( SiteKey siteKey )
    {
        return new MockPageCacheService();
    }

    public void clearCaches( SiteKey siteKey )
    {
        // do nothing
    }

    public void clearCache( String cacheName )
    {
        // do nothing
    }

    public void clearCacheStatistics( String cacheName )
    {
        // do nothing
    }
}
