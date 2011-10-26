/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.core.AbstractAccessResolver;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupMembershipSearcher;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;

public class MenuItemAccessResolver
    extends AbstractAccessResolver<MenuItemEntity, MenuItemAccessType>
{
    public MenuItemAccessResolver( GroupDao groupDao )
    {
        super( groupDao );
    }

    public void checkAccessToAddContentToSection( UserEntity executor, MenuItemEntity section, String message )
    {
        if ( !hasAddContentToSectionAccess( executor, section ) )
        {
            throw new MenuItemAccessException( message, executor.getQualifiedName(), MenuItemAccessType.ADD, section.getMenuItemKey() );
        }
    }

    public void checkAccessToApproveContentInSection( UserEntity executor, MenuItemEntity section, String message )
    {
        if ( !hasApproveContentInSectionAccess( executor, section ) )
        {
            throw new MenuItemAccessException( message, executor.getQualifiedName(), MenuItemAccessType.PUBLISH, section.getMenuItemKey() );
        }
    }

    public void checkAccessToUnapproveContentInSection( final UserEntity executor, final MenuItemEntity section, final String message )
    {
        if ( !hasUnapproveContentInSectionAccess( executor, section ) )
        {
            throw new MenuItemAccessException( message, executor.getQualifiedName(), MenuItemAccessType.PUBLISH, section.getMenuItemKey() );
        }
    }

    public void checkAccessToRemoveUnapprovedContentFromSection( UserEntity executor, MenuItemEntity section, String message )
    {
        if ( !hasRemoveUnapprovedContentFromSectionAccess( executor, section ) )
        {
            throw new MenuItemAccessException( message, executor.getQualifiedName(), MenuItemAccessType.ADD, section.getMenuItemKey() );
        }
    }

    public boolean hasAccess( UserEntity executor, MenuItemEntity menuItem, MenuItemAccessType accessType )
    {
        return doHasAccess( executor, menuItem, accessType );
    }

    public boolean hasApproveContentInSectionAccess( UserEntity executor, MenuItemEntity section )
    {
        return doHasAccess( executor, section, MenuItemAccessType.PUBLISH );
    }

    public boolean hasUnapproveContentInSectionAccess( UserEntity executor, MenuItemEntity section )
    {
        return doHasAccess( executor, section, MenuItemAccessType.PUBLISH );
    }

    public boolean hasAddContentToSectionAccess( UserEntity executor, MenuItemEntity section )
    {
        return doHasAccess( executor, section, MenuItemAccessType.ADD );
    }

    public boolean hasRemoveUnapprovedContentFromSectionAccess( UserEntity executor, MenuItemEntity section )
    {
        return doHasAccess( executor, section, MenuItemAccessType.ADD );
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
