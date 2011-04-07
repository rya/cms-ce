/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.synchronize.status;

import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.core.security.userstore.status.LocalGroupsStatus;
import com.enonic.cms.core.security.userstore.status.LocalUsersStatus;

public class SynchronizeStatus
{
    private boolean completed = false;

    private final SynchronizeUserStoreType type;

    private final RemoteUsersStatus remoteUsersStatus = new RemoteUsersStatus();

    private final RemoteGroupsStatus remoteGroupsStatus = new RemoteGroupsStatus();

    private final LocalUsersStatus localUsersStatus = new LocalUsersStatus();

    private final LocalGroupsStatus localGroupsStatus = new LocalGroupsStatus();

    private final GroupMembershipsStatus groupMembershipsStatus = new GroupMembershipsStatus();

    private final UserMembershipsStatus userMembershipsStatus = new UserMembershipsStatus();

    public SynchronizeStatus( final SynchronizeUserStoreType type )
    {
        this.type = type;
    }

    public SynchronizeUserStoreType getType()
    {
        return type;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted()
    {
        completed = true;
    }

    public RemoteUsersStatus getRemoteUsersStatus()
    {
        return remoteUsersStatus;
    }

    public RemoteGroupsStatus getRemoteGroupsStatus()
    {
        return remoteGroupsStatus;
    }

    public LocalUsersStatus getLocalUsersStatus()
    {
        return localUsersStatus;
    }

    public LocalGroupsStatus getLocalGroupsStatus()
    {
        return localGroupsStatus;
    }

    public GroupMembershipsStatus getGroupMembershipsStatus()
    {
        return groupMembershipsStatus;
    }

    public UserMembershipsStatus getUserMembershipsStatus()
    {
        return userMembershipsStatus;
    }

    /* remote users */

    public void setTotalRemoteUserCount( final int value )
    {
        remoteUsersStatus.setTotalCount( value );
    }

    public void userCreated()
    {
        remoteUsersStatus.created();
    }

    public void userUpdated( final boolean resurrected )
    {
        remoteUsersStatus.updated( resurrected );
    }

    public void userSkipped()
    {
        remoteUsersStatus.skipped();
    }

    /* local users */

    public void setTotalLocalUserCount( final int value )
    {
        localUsersStatus.setTotalCount( value );
    }

    public void userDeleted()
    {
        localUsersStatus.deleted();
    }

    /* remote groups */

    public void setTotalRemoteGroupCount( final int value )
    {
        remoteGroupsStatus.setTotalCount( value );
    }

    public void groupCreated()
    {
        remoteGroupsStatus.created();
    }

    public void groupUpdated( final boolean resurrected )
    {
        remoteGroupsStatus.updated( resurrected );
    }

    public void groupSkipped()
    {
        remoteGroupsStatus.skipped();
    }

    /* local groups */

    public void setTotalLocalGroupCount( final int value )
    {
        localGroupsStatus.setTotalCount( value );
    }

    public void groupDeleted()
    {
        localGroupsStatus.deleted();
    }

    /* memberships - users */

    public void setTotalUserMembershipsCount( final int value )
    {
        userMembershipsStatus.setTotalCount( value );
    }

    public void nextUserMemberships()
    {
        userMembershipsStatus.next();
    }

    public void userMembershipCreated()
    {
        userMembershipsStatus.created();
    }

    public void userMembershipVerified()
    {
        userMembershipsStatus.verified();
    }

    public void userMembershipDeleted()
    {
        userMembershipsStatus.deleted();
    }

    /* memberships - groups */

    public void setTotalGroupMembershipsCount( final int value )
    {
        groupMembershipsStatus.setTotalCount( value );
    }

    public void nextGroupMemberships()
    {
        groupMembershipsStatus.next();
    }

    public void groupMembershipCreated()
    {
        groupMembershipsStatus.created();
    }

    public void groupMembershipVerified()
    {
        groupMembershipsStatus.verified();
    }

    public void groupMembershipDeleted()
    {
        groupMembershipsStatus.deleted();
    }
}
