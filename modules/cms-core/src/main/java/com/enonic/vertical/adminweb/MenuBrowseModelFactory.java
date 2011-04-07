/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.structure.MenuItemAccessRightAccumulator;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.structure.DefaultSiteAccessRightAccumulator;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.DefaultSiteAccumulatedAccessRights;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemAndUserAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

/**
 * Nov 24, 2009
 */
public class MenuBrowseModelFactory
{
    private SecurityService securityService;

    private SiteDao siteDao;

    private MenuItemDao menuItemDao;

    private MenuItemAccessRightAccumulator menuItemAccessRightAccumulator;

    private SitePropertiesService sitePropertiesService;

    public MenuBrowseModelFactory( SecurityService securityService, SiteDao siteDao, MenuItemDao menuItemDao,
                                   SitePropertiesService sitePropertiesService )
    {
        this.securityService = securityService;
        this.siteDao = siteDao;
        this.menuItemDao = menuItemDao;
        this.menuItemAccessRightAccumulator = new MenuItemAccessRightAccumulator( securityService );
        this.sitePropertiesService = sitePropertiesService;
    }

    public MenuBrowseMenuItemsModel createMenuItemModel( UserEntity user, SiteKey siteKey, MenuItemKey selectedMenuItemKey )
    {
        MenuBrowseMenuItemsModel model = new MenuBrowseMenuItemsModel();

        SiteEntity site = siteDao.findByKey( siteKey );
        model.setSite( site );
        model.setSiteProperties( sitePropertiesService.getSiteProperties( site.getKey() ) );

        DefaultSiteAccessRightAccumulator defaultSiteAccessRightAccumulator = new DefaultSiteAccessRightAccumulator( securityService );
        DefaultSiteAccumulatedAccessRights userRightsForSite = defaultSiteAccessRightAccumulator.getAccessRightsAccumulated( site, user );
        model.setUserRightsForSite( userRightsForSite );

        MenuItemEntity selectedMenuItem = null;
        if ( selectedMenuItemKey != null )
        {
            selectedMenuItem = menuItemDao.findByKey( selectedMenuItemKey.toInt() );
            model.setSelectedMenuItem( selectedMenuItem );
            model.setSelectedMenuItemPath( selectedMenuItem.getMenuItemPath() );
            model.setUserRightsForSelectedMenuItem( resolveUserRights( selectedMenuItem, user ) );

            MenuItemEntity parentToSelectedMenuItem = selectedMenuItem.getParent();
            model.setParentToSelectedMenuItem( parentToSelectedMenuItem );
            if ( parentToSelectedMenuItem != null )
            {
                model.setUserRightsForParentToSelectedMenuItem( resolveUserRights( parentToSelectedMenuItem, user ) );
            }
        }

        Collection<MenuItemAndUserAccessRights> menuItemsToList = resolveMenuItemsToList( site, selectedMenuItem, user );
        model.setMenuItemsToList( menuItemsToList );

        return model;
    }

    public MenuBrowseContentModel createContentModel( final UserEntity user, final SiteKey siteKey, final MenuItemKey selectedMenuItemKey )
    {
        final MenuBrowseContentModel model = new MenuBrowseContentModel();

        final SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        model.setSite( site );
        model.setSiteProperties( sitePropertiesService.getSiteProperties( site.getKey() ) );

        final DefaultSiteAccessRightAccumulator defaultSiteAccessRightAccumulator =
            new DefaultSiteAccessRightAccumulator( securityService );
        final DefaultSiteAccumulatedAccessRights userRightsForSite =
            defaultSiteAccessRightAccumulator.getAccessRightsAccumulated( site, user );
        model.setUserRightsForSite( userRightsForSite );

        if ( selectedMenuItemKey != null )
        {
            final MenuItemEntity selectedMenuItem = menuItemDao.findByKey( selectedMenuItemKey.toInt() );
            model.setSelectedMenuItem( selectedMenuItem );
            model.setSelectedMenuItemPath( selectedMenuItem.getMenuItemPath() );
            model.setUserRightsForSelectedMenuItem( resolveUserRights( selectedMenuItem, user ) );

            final MenuItemEntity parentToSelectedMenuItem = selectedMenuItem.getParent();
            model.setParentToSelectedMenuItem( parentToSelectedMenuItem );
            if ( parentToSelectedMenuItem != null )
            {
                model.setUserRightsForParentToSelectedMenuItem( resolveUserRights( parentToSelectedMenuItem, user ) );
            }
        }
        return model;
    }

    private Collection<MenuItemAndUserAccessRights> resolveMenuItemsToList( final SiteEntity site, final MenuItemEntity parentMenuItem,
                                                                            final UserEntity user )
    {
        final UserEntity anonymousUser = securityService.getUser( securityService.getAnonymousUserKey() );

        if ( parentMenuItem == null )
        {
            final List<MenuItemAndUserAccessRights> menuItemsAndUserAccessRights = new ArrayList<MenuItemAndUserAccessRights>();
            final Collection<MenuItemEntity> menuItems = site.getTopMenuItems();
            for ( MenuItemEntity menuItem : menuItems )
            {
                final MenuItemAccumulatedAccessRights accessRightsForAnonymousUser = resolveUserRights( menuItem, anonymousUser );
                final MenuItemAccumulatedAccessRights accessRightsForRunningUser = resolveUserRights( menuItem, user );
                menuItemsAndUserAccessRights.add(
                    new MenuItemAndUserAccessRights( menuItem, accessRightsForRunningUser, accessRightsForAnonymousUser ) );
            }
            return menuItemsAndUserAccessRights;
        }
        else
        {
            final List<MenuItemAndUserAccessRights> menuItemsAndUserAccessRights = new ArrayList<MenuItemAndUserAccessRights>();
            final Collection<MenuItemEntity> menuItems = parentMenuItem.getChildren();

            for ( MenuItemEntity menuItem : menuItems )
            {
                final MenuItemAccumulatedAccessRights accessRightsForAnonymousUser = resolveUserRights( menuItem, anonymousUser );
                final MenuItemAccumulatedAccessRights accessRightsForRunningUser = resolveUserRights( menuItem, user );
                menuItemsAndUserAccessRights.add(
                    new MenuItemAndUserAccessRights( menuItem, accessRightsForRunningUser, accessRightsForAnonymousUser ) );
            }
            return menuItemsAndUserAccessRights;
        }
    }

    private MenuItemAccumulatedAccessRights resolveUserRights( final MenuItemEntity menuItem, final UserEntity user )
    {
        return menuItemAccessRightAccumulator.getAccessRightsAccumulated( menuItem, user );
    }

}
