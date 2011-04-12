/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.portal.cache.PageCacheService;
import com.enonic.cms.portal.cache.SiteCachesService;


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
