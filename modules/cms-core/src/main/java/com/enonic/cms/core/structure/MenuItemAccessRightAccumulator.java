/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemAccumulatedAccessRights;

/**
 * Nov 19, 2009
 */
public class MenuItemAccessRightAccumulator
{
    private SecurityService securityService;

    public MenuItemAccessRightAccumulator( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public MenuItemAccumulatedAccessRights getAccessRightsAccumulated( final MenuItemEntity menuItem, final UserEntity user )
    {
        if ( user.isRoot() )
        {
            return new MenuItemAccumulatedAccessRights( true, user.getKey(), menuItem.getMenuItemKey() );
        }

        final MenuItemAccumulatedAccessRights accumulated =
            new MenuItemAccumulatedAccessRights( false, user.getKey(), menuItem.getMenuItemKey() );

        accumulateUserGroupRights( menuItem, user, accumulated );
        if ( accumulated.isAllTrue() )
        {
            return accumulated;
        }

        accumulateAnonymousRights( menuItem, accumulated );
        if ( accumulated.isAllTrue() )
        {
            return accumulated;
        }

        accumulateAuthenticatedUsersRights( menuItem, user, accumulated );
        if ( accumulated.isAllTrue() )
        {
            return accumulated;
        }

        accumulateUsersMembershipsRights( menuItem, user, accumulated );
        return accumulated;
    }

    private void accumulateUserGroupRights( MenuItemEntity menuItem, UserEntity user, MenuItemAccumulatedAccessRights accumulated )
    {
        accumulateGroupAccess( menuItem, accumulated, user.getUserGroup() );
    }

    private void accumulateAnonymousRights( MenuItemEntity menuItem, MenuItemAccumulatedAccessRights accumulated )
    {
        UserEntity anonymousUser = securityService.getUser( securityService.getAnonymousUserKey() );
        if ( anonymousUser != null )
        {
            accumulateGroupAccess( menuItem, accumulated, anonymousUser.getUserGroup() );
        }
    }

    private void accumulateAuthenticatedUsersRights( MenuItemEntity menuItem, UserEntity user, MenuItemAccumulatedAccessRights accumulated )
    {
        final UserStoreEntity userstore = user.getUserStore();
        if ( !user.isAnonymous() && userstore != null )
        {
            GroupEntity group = securityService.getAuthenticatedUsersGroup( userstore );
            accumulateGroupAccess( menuItem, accumulated, group );
        }
    }

    private void accumulateUsersMembershipsRights( MenuItemEntity menuItem, UserEntity user, MenuItemAccumulatedAccessRights accumulated )
    {
        for ( final GroupEntity group : user.getAllMembershipsGroups() )
        {
            if ( group.getType() == GroupType.ENTERPRISE_ADMINS )
            {
                accumulated.setAllTo( true );
            }
            else
            {
                accumulateGroupAccess( menuItem, accumulated, group );
            }
        }
    }

    private void accumulateGroupAccess( MenuItemEntity menuItem, MenuItemAccumulatedAccessRights accumulated, GroupEntity group )
    {
        final MenuItemAccessEntity access = menuItem.getAccess( group.getGroupKey() );
        if ( access != null )
        {
            accumulated.accumulate( access );
        }
    }
}
