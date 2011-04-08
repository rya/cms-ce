/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.cache;

import com.enonic.cms.domain.SiteKey;

public interface SiteCachesService
{

    void setUpSiteCachesService( SiteKey siteKey );

    void tearDownSiteCachesService( SiteKey siteKey );

    PageCacheService getPageCacheService( SiteKey siteKey );

    void clearCaches( SiteKey siteKey );

    void clearCache( String cacheName );

    void clearCacheStatistics( String cacheName );
}
