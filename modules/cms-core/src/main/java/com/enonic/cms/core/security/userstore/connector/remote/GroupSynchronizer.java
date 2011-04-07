/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import org.springframework.util.Assert;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.user.remote.RemoteGroup;

public class GroupSynchronizer
    extends AbstractBaseGroupSynchronizer
{
    public GroupSynchronizer( final UserStoreEntity userStore, final boolean syncMemberships, final boolean syncMembers )
    {
        super( userStore, true, syncMemberships, syncMembers );
    }

    public void synchronize( final GroupEntity localGroupToSync, final MemberCache memberCache )
    {
        Assert.notNull( localGroupToSync );

        status.setTotalRemoteGroupCount( 1 );

        final RemoteGroup remoteGroup = remoteUserStorePlugin.getGroup( localGroupToSync.getName() );
        if ( remoteGroup == null )
        {
            deleteGroup( localGroupToSync );
        }
        else if ( !remoteGroup.getSync().equals( localGroupToSync.getSyncValue() ) )
        {
            // No matcing sync value - group no longer in userstore , we delete it
            deleteGroup( localGroupToSync );
        }
        else
        {
            final boolean resurrected = localGroupToSync.isDeleted() == true;

            // force resurrection
            localGroupToSync.setDeleted( 0 );

            status.groupUpdated( resurrected );

            if ( syncMembers )
            {
                syncGroupMembers( localGroupToSync, remoteGroup, memberCache );
            }
            if ( syncMemberships )
            {
                syncGroupMemberships( localGroupToSync, remoteGroup, memberCache );
            }
        }
    }

    private void deleteGroup( final GroupEntity localGroup )
    {
        if ( !localGroup.isDeleted() )
        {
            status.setTotalLocalUserCount( 1 );
            localGroup.setDeleted( 1 );
            status.groupDeleted();
        }
    }
}
