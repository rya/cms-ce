/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.structure.DefaultSiteAccessEntity;
import com.enonic.cms.domain.structure.DefaultSiteAccumulatedAccessRights;
import com.enonic.cms.domain.structure.SiteEntity;

/**
 * Nov 19, 2009
 */
public class DefaultSiteAccessRightAccumulator
{
    private SecurityService securityService;

    public DefaultSiteAccessRightAccumulator( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public DefaultSiteAccumulatedAccessRights getAccessRightsAccumulated( final SiteEntity site, final UserEntity user )
    {
        if ( user.isRoot() )
        {
            return new DefaultSiteAccumulatedAccessRights( true, user.getKey(), site.getKey() );
        }

        final DefaultSiteAccumulatedAccessRights accumulated =
            new DefaultSiteAccumulatedAccessRights( false, user.getKey(), site.getKey() );

        accumulateUserGroupRights( site, user, accumulated );
        if ( accumulated.isAllTrue() )
        {
            return accumulated;
        }

        accumulateAnonymousRights( site, accumulated );
        if ( accumulated.isAllTrue() )
        {
            return accumulated;
        }

        accumulateAuthenticatedUsersRights( site, user, accumulated );
        if ( accumulated.isAllTrue() )
        {
            return accumulated;
        }

        accumulateUsersMembershipsRights( site, user, accumulated );
        return accumulated;
    }

    private void accumulateUserGroupRights( final SiteEntity site, UserEntity user, DefaultSiteAccumulatedAccessRights accumulated )
    {
        accumulateGroupAccess( site, accumulated, user.getUserGroup() );
    }

    private void accumulateAnonymousRights( final SiteEntity site, DefaultSiteAccumulatedAccessRights accumulated )
    {
        UserEntity anonymousUser = securityService.getUser( securityService.getAnonymousUserKey() );
        if ( anonymousUser != null )
        {
            accumulateGroupAccess( site, accumulated, anonymousUser.getUserGroup() );
        }
    }

    private void accumulateAuthenticatedUsersRights( final SiteEntity site, UserEntity user,
                                                     DefaultSiteAccumulatedAccessRights accumulated )
    {
        final UserStoreEntity userstore = user.getUserStore();
        if ( !user.isAnonymous() && userstore != null )
        {
            GroupEntity group = securityService.getAuthenticatedUsersGroup( userstore );
            accumulateGroupAccess( site, accumulated, group );
        }
    }

    private void accumulateUsersMembershipsRights( final SiteEntity site, UserEntity user, DefaultSiteAccumulatedAccessRights accumulated )
    {
        for ( final GroupEntity group : user.getAllMembershipsGroups() )
        {
            if ( group.getType() == GroupType.ENTERPRISE_ADMINS )
            {
                accumulated.setAllTo( true );
            }
            else
            {
                accumulateGroupAccess( site, accumulated, group );
            }
        }
    }

    private void accumulateGroupAccess( final SiteEntity site, DefaultSiteAccumulatedAccessRights accumulated, GroupEntity group )
    {
        final DefaultSiteAccessEntity access = site.getAccess( group.getGroupKey() );
        if ( access != null )
        {
            accumulated.accumulate( access );
        }
    }
}