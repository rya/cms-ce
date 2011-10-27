/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.portal.cache.PageCacheService;
import com.enonic.cms.core.portal.cache.SiteCachesService;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;


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
        ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

        invalidateForContentLocations( contentLocations );
    }

    public void invalidateForContentLocations( ContentLocations contentLocations )
    {
        for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
        {
            PageCacheService pageCacheService = siteCachesService.getPageCacheService( contentLocation.getSiteKey() );
            pageCacheService.removeEntriesByMenuItem( contentLocation.getMenuItemKey() );

            cleanPageCache( contentLocation.getMenuItem().getParent(), pageCacheService );
        }
    }

    private void cleanPageCache( MenuItemEntity menuItem, PageCacheService pageCacheService )
    {
        if ( menuItem != null )
        {
            if ( menuItem.isRenderable() )
            {
                pageCacheService.removeEntriesByMenuItem( menuItem.getMenuItemKey() );
            }
            else
            {
                cleanPageCache( menuItem.getParent(), pageCacheService );
            }
        }
    }
}
