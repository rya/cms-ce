/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.access;

import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.AbstractAccessResolver;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupMembershipSearcher;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

public class MenuItemAccessResolver
    extends AbstractAccessResolver<MenuItemEntity, MenuItemAccessType>
{
    public MenuItemAccessResolver( GroupDao groupDao )
    {
        super( groupDao );
    }

    public boolean hasAccess( UserEntity executor, MenuItemEntity menuItem, MenuItemAccessType accessType )
    {
        return doHasAccess( executor, menuItem, accessType );
    }

    @Override
    protected boolean hasAccess( final MenuItemEntity menuItem, final GroupEntity group, final MenuItemAccessType menuItemAccessType,
                                 final boolean checkMemberships )
    {
        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        if ( menuItem.hasAccess( group, menuItemAccessType ) )
        {
            return true;
        }

        if ( !checkMemberships )
        {
            return false;
        }

        // Check through all memberships of userGroup
        GroupMembershipSearcher searcher = new GroupMembershipSearcher()
        {
            public boolean isGroupFound( GroupEntity traversedGroup )
            {
                return menuItem.hasAccess( traversedGroup, menuItemAccessType );
            }
        };
        return searcher.startSearch( group );
    }
}
