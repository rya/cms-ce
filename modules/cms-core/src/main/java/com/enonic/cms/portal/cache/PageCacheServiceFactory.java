/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.cache;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;

import com.enonic.cms.domain.SiteKey;

public class PageCacheServiceFactory
{

    private CacheManager cacheManager;

    private SitePropertiesService sitePropertiesService;

    public void setCacheManager( CacheManager value )
    {
        this.cacheManager = value;
    }

    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }

    public PageCacheServiceImpl createPageAndObjectCacheService( SiteKey siteKey )
    {

        CacheFacade cacheFacade = cacheManager.getOrCreateCache( "page" );

        PageCacheServiceImpl cacheService = new PageCacheServiceImpl( siteKey );
        cacheService.setCacheFacade( cacheFacade );

        Integer defaultTimeToLive = sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey );
        cacheService.setTimeToLive( defaultTimeToLive );
        return cacheService;
    }
}
