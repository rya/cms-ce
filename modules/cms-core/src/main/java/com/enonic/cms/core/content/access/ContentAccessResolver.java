/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.access;

import com.enonic.cms.core.content.ContentAccessType;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupMembershipSearcher;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.AbstractAccessResolver;

import com.enonic.cms.core.content.ContentAccessRightsAccumulated;
import com.enonic.cms.core.security.user.UserEntity;


public class ContentAccessResolver
    extends AbstractAccessResolver<ContentEntity, ContentAccessType>
{
    private CategoryAccessResolver categoryAccessResolver;

    public ContentAccessResolver( GroupDao groupDao )
    {
        super( groupDao );
        categoryAccessResolver = new CategoryAccessResolver( groupDao );
    }

    public ContentAccessRightsAccumulated getAccumulatedAccessRights( final UserEntity user, final ContentEntity content )
    {
        final ContentAccessRightsAccumulated accumulated = new ContentAccessRightsAccumulated( false );

        // anonymous group
        doGetAccumulatedAccessRights( accumulated, getAnonymousGroup(), content );
        if ( accumulated.isAllTrue() || user.isAnonymous() )
        {
            return accumulated;
        }

        // user's group
        if ( user.getUserGroup() != null )
        {
            doGetAccumulatedAccessRights( accumulated, user.getUserGroup(), content );
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
            doGetAccumulatedAccessRights( accumulated, authenticatedUsersGroup, content );
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

    public boolean hasUpdateDraftVersionAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.UPDATE ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasCreateContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasCreateNewVersionAsDraftAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.UPDATE ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasCreateContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasCreateSnapshotAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.UPDATE ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasCreateContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasCreateNewVersionAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.UPDATE ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasApproveContentAccess( UserEntity executor, ContentEntity content )
    {
        if ( content.getCategory().getAutoMakeAvailableAsBoolean() )
        {
            if ( doHasAccess( executor, content, ContentAccessType.UPDATE ) )
            {
                return true;
            }

            if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
            {
                return true;
            }
        }
        else
        {
            if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasDeleteContentAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.DELETE ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasApproveContentAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        return false;
    }


    public boolean hasDeleteApprovedOrArchivedContentVersionAccess( UserEntity executor, ContentEntity content )
    {
        if ( categoryAccessResolver.hasAdministrateCategoryAccess( executor, content.getCategory() ) )
        {
            return true;
        }

        return false;
    }

    public boolean hasDeleteDraftContentVersionAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.DELETE ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasCreateContentAccess( executor, content.getCategory() ) )

        {
            return true;
        }

        return false;
    }

    public boolean hasReadContentAccess( UserEntity executor, ContentEntity content )
    {
        if ( doHasAccess( executor, content, ContentAccessType.READ ) )
        {
            return true;
        }

        if ( categoryAccessResolver.hasReadContentAccess( executor, content.getCategory() ) )

        {
            return true;
        }

        return false;
    }

    protected boolean hasAccess( final ContentEntity content, final GroupEntity group, final ContentAccessType accessType,
                                 final boolean checkMemberships )
    {
        if ( group == null )
        {
            throw new IllegalArgumentException( "Given group cannot be null" );
        }

        if ( content.hasAccessRightSet( group, accessType ) )
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
                return content.hasAccessRightSet( traversedGroup, accessType );
            }
        };
        return searcher.startSearch( group );
    }

    private void doGetAccumulatedAccessRights( final ContentAccessRightsAccumulated accumulated, final GroupEntity group,
                                               final ContentEntity content )
    {
        // first, accumulate any rights set for the given group
        content.accumulateAccess( accumulated, group );
        if ( accumulated.isAllTrue() )
        {
            return;
        }

        final GroupMembershipSearcher searcher = new GroupMembershipSearcher()
        {
            public boolean isGroupFound( GroupEntity traversedGroup )
            {
                content.accumulateAccess( accumulated, traversedGroup );
                boolean hasFinishedSearching = accumulated.isAllTrue();
                return hasFinishedSearching;
            }
        };

        searcher.startSearch( group );
        return;
    }

}
