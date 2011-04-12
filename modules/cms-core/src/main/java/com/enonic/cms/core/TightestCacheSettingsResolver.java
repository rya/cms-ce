/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;


import java.util.List;

import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.domain.CacheSettings;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public class TightestCacheSettingsResolver
{
    @Autowired
    private SitePropertiesService sitePropertiesService;

    public CacheSettings resolveTightestCacheSettingsForPage( MenuItemEntity menuItem, Regions regions, PageTemplateEntity pageTemplate )
    {
        if ( menuItem == null )
        {
            throw new IllegalArgumentException( "Given menuItem cannot be null" );
        }

        SiteKey siteKey = menuItem.getSite().getKey();
        boolean pageCacheEnabledForSite = sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE, siteKey );
        if ( !pageCacheEnabledForSite )
        {
            return new CacheSettings( false, CacheSettings.TYPE_DEFAULT, 0 );
        }
        int defaultSecondsToLiveForSite = sitePropertiesService.getPropertyAsInteger( SitePropertyNames.PAGE_CACHE_TIMETOLIVE, siteKey );

        CacheSettings menuItemCacheSettings = menuItem.getCacheSettings( defaultSecondsToLiveForSite, pageTemplate );
        if ( menuItemCacheSettings.isDisabled() )
        {
            return menuItemCacheSettings;
        }

        CacheSettings settingWithLeastTime = menuItemCacheSettings;

        // Compare with the portlets cache settings
        final List<PortletEntity> portlets = regions.getPortlets();

        for ( PortletEntity portlet : portlets )
        {
            CacheSettings current = portlet.getCacheSettings( defaultSecondsToLiveForSite );

            if ( current.isTighterThan( settingWithLeastTime ) )
            {
                settingWithLeastTime = current;
            }
        }

        return settingWithLeastTime;
    }
}
