/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.portal.cache.PageCacheService;
import com.enonic.cms.portal.cache.SiteCachesService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentLocation;
import com.enonic.cms.domain.content.ContentLocationSpecification;
import com.enonic.cms.domain.content.ContentLocations;
import com.enonic.cms.domain.content.ContentVersionEntity;


public class PageCacheInvalidatorForContent
{
    private SiteCachesService siteCachesService;

    public PageCacheInvalidatorForContent( SiteCachesService siteCachesService )
    {
        this.siteCachesService = siteCachesService;
    }

    public void invalidateForContent( ContentVersionEntity version )
    {
        invalidateForContent( version.getContent() );
    }

    public void invalidateForContent( ContentEntity content )
    {
        ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
        contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
        final ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
        {
            PageCacheService pageCacheService = siteCachesService.getPageCacheService( contentLocation.getSiteKey() );
            pageCacheService.removeEntriesByMenuItem( contentLocation.getMenuItemKey() );
        }
    }
}
