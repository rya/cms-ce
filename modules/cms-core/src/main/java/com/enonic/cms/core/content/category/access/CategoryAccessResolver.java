/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category.access;

import com.enonic.cms.core.AbstractAccessResolver;
import com.enonic.cms.core.content.category.CategoryAccessRightsAccumulated;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.security.group.GroupMembershipSearcher;
import com.enonic.cms.core.security.user.UserEntity;


public class CategoryAccessResolver
    extends AbstractAccessResolver<CategoryEntity, CategoryAccessType>
{
    public CategoryAccessResolver( GroupDao groupDao )
    {
        super( groupDao );
    }

    public CategoryAccessRightsAccumulated getAccumulatedAccessRights( final UserEntity user, final CategoryEntity category )
    {
        final CategoryAccessRightsAccumulated accumulated = new CategoryAccessRightsAccumulated( false );

        // anonymous group
        doGetAccumulatedAccessRights( accumulated, getAnonymousGroup(), category );
        if ( accumulated.isAllTrue() || user.isAnonymous() )
        {
            return accumulated;
        }

        // user's group
        if ( user.getUserGroup() != null )
        {
            doGetAccumulatedAccessRights( accumulated, user.getUserGroup(), category );
            if ( accumulated.isAllTrue() )
            {
                return accumulated;
            }
        }

        // check "authenticated users" group
        if ( user.getUserStore() != null )
        {
            final GroupEntity authenticatedUsersGroup = getAuthenticatedUsersGroup( user.getUserStore() );
            // NB! All users are always implicit member of authenticated users
            doGetAccumulatedAccessRights( accumulated, authenticatedUsersGroup, category );
            if ( accumulated.isAllTrue() )
            {
                return accumulated;
            }
        }

        // check if user is member of enterprise admin and if so give all rights
        if ( user.isMemberOf( getEnterpriseAdminsGroup(), true ) )
        {
            accumulated.setAllTo( true );
            return accumulated;
        }

        if ( user.isRoot() )
        {
            accumulated.setAllTo( true );
            return accumulated;
        }

        return accumulated;
    }

    public boolean hasAccess( UserEntity user, CategoryEntity category, CategoryAccessType categoryAccessType )
    {
        return doHasAccess( user, category, categoryAccessType );
    }

    public boolean hasAdministrateCategoryAccess( UserEntity user, CategoryEntity category )
    {
        if ( doHasAccess( user, category, CategoryAccessType.ADMINISTRATE ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasAdminBrowseCategoryAccess( UserEntity user, CategoryEntity category )
    {
        if ( doHasAccess( user, category, CategoryAccessType.ADMIN_BROWSE ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasReadCategoryAccess( UserEntity user, CategoryEntity category )
    {
        if ( doHasAccess( user, category, CategoryAccessType.READ ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasCreateContentAccess( UserEntity user, CategoryEntity category )
    {
        if ( doHasAccess( user, category, CategoryAccessType.CREATE ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasApproveContentAccess( UserEntity user, CategoryEntity category )
    {
        if ( doHasAccess( user, category, CategoryAccessType.APPROVE ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasReadContentAccess( UserEntity user, CategoryEntity category )
    {
        if ( doHasAccess( user, category, CategoryAccessType.READ ) )
        {
            return true;
        }

        if ( doHasAccess( user, category, CategoryAccessType.ADMIN_BROWSE ) )
        {
            return true;
        }

        if ( doHasAccess( user, category, CategoryAccessType.APPROVE ) )
        {
            return true;
        }

        return false;
    }

    /**
     * Returns true if given user has given access on the given category or on any of it's descendants.
     */
    public boolean hasAdminBrowseAccessWithDescendantsCheck( UserEntity user, CategoryEntity category )
    {
        return hasAdminBrowseAccessRecursively( user, category );
    }

    protected boolean hasAccess( final CategoryEntity category, final GroupEntity group, final CategoryAccessType categoryAccessType,
                                 final boolean checkMemberships )
    {
        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        if ( category.hasAccess( group, categoryAccessType ) )
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
                return category.hasAccess( traversedGroup, categoryAccessType );
            }
        };
        return searcher.startSearch( group );
    }

    private boolean hasAdminBrowseAccessRecursively( UserEntity user, CategoryEntity category )
    {
        if ( hasAdminBrowseCategoryAccess( user, category ) )
        {
            return true;
        }

        for ( CategoryEntity childCategory : category.getChildren() )
        {
            if ( hasAdminBrowseAccessRecursively( user, childCategory ) )
            {
                return true;
            }
        }

        return false;
    }

    private void doGetAccumulatedAccessRights( final CategoryAccessRightsAccumulated accumulated, final GroupEntity group,
                                               final CategoryEntity category )
    {
        // first, accumulate any rights set for the given group
        category.accumulateAccess( accumulated, group );
        if ( accumulated.isAllTrue() )
        {
            return;
        }

        final GroupMembershipSearcher searcher = new GroupMembershipSearcher()
        {
            public boolean isGroupFound( GroupEntity traversedGroup )
            {
                category.accumulateAccess( accumulated, traversedGroup );
                boolean hasFinishedSearching = accumulated.isAllTrue();
                return hasFinishedSearching;
            }
        };

        searcher.startSearch( group );
    }

}
