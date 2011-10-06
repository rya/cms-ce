/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.business.core.structure.MenuItemAccessRightAccumulator;

import com.enonic.cms.core.content.ContentEntity;

import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemAndUserAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

/**
 * Jan 7, 2010
 */
public class ContentEditFormModelFactory
{
    private SecurityService securityService;

    private ContentDao contentDao;

    private MenuItemAccessRightAccumulator menuItemAccessRightAccumulator;

    public ContentEditFormModelFactory( ContentDao contentDao, SecurityService securityService,
                                        MenuItemAccessRightAccumulator menuItemAccessRightAccumulator )
    {
        this.contentDao = contentDao;
        this.securityService = securityService;
        this.menuItemAccessRightAccumulator = menuItemAccessRightAccumulator;
    }

    public ContentEditFormModel createContentEditFormModel( ContentKey contentKey, UserEntity executor )
    {
        ContentEntity content = contentDao.findByKey( contentKey );
        ContentLocationSpecification contentLocationSpecificaiton = new ContentLocationSpecification();
        contentLocationSpecificaiton.setIncludeInactiveLocationsInSection( true );
        ContentLocations contentLocations = content.getLocations( contentLocationSpecificaiton );

        ContentEditFormModel contentEditFormModel = new ContentEditFormModel();
        contentEditFormModel.setContentLocations( contentLocations );

        Map<MenuItemKey, MenuItemAndUserAccessRights> menuItemAndUserAccessRightsMapByMenuItemKey =
            new HashMap<MenuItemKey, MenuItemAndUserAccessRights>();

        for ( MenuItemEntity menuItem : contentLocations.getMenuItems() )
        {
            MenuItemAndUserAccessRights menuItemAndUserAccessRights =
                new MenuItemAndUserAccessRights( menuItem, resolveUserRights( menuItem, executor ),
                                                 resolveUserRights( menuItem, securityService.getAnonymousUser() ) );

            menuItemAndUserAccessRightsMapByMenuItemKey.put( menuItem.getMenuItemKey(), menuItemAndUserAccessRights );
        }
        contentEditFormModel.setMenuItemAndUserAccessRightsMapByMenuItemKey( menuItemAndUserAccessRightsMapByMenuItemKey );
        return contentEditFormModel;
    }

    private MenuItemAccumulatedAccessRights resolveUserRights( final MenuItemEntity menuItem, final UserEntity user )
    {
        return menuItemAccessRightAccumulator.getAccessRightsAccumulated( menuItem, user );
    }
}
