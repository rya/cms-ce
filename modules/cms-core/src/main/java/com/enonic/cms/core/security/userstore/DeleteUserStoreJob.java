/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.DeleteUserStoreCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;

public class DeleteUserStoreJob
{
    private final UserStoreService userStoreService;

    private final DeleteUserStoreCommand command;

    private final int batchSize;

    private final DeleteUserStoreStatus status;

    public DeleteUserStoreJob( final UserStoreService userStoreService, final DeleteUserStoreCommand command, final int batchSize )
    {
        this.userStoreService = userStoreService;
        this.command = command;
        this.batchSize = batchSize;
        this.status = new DeleteUserStoreStatus();
    }

    public DeleteUserStoreStatus getStatus()
    {
        return status;
    }

    public void start()
    {
        try
        {
            doStart();
        }
        finally
        {
            status.setCompleted();
        }
    }

    private void doStart()
    {
        userStoreService.deleteUserStore( command );

        deleteUserStoreUsers();

        deleteUserStoreGroups();
    }

    private void deleteUserStoreUsers()
    {
        final List<UserKey> allUserKeys = getUserKeys( userStoreService.getUsers( command.getKey() ) );
        status.setTotalLocalUserCount( allUserKeys.size() );

        final BatchedList<UserKey> usersToDeleteAsBatchedList = new BatchedList<UserKey>( allUserKeys, batchSize );
        while ( usersToDeleteAsBatchedList.hasMoreBatches() )
        {
            userStoreService.deleteUsersLocally( status.getLocalUsersStatus(), usersToDeleteAsBatchedList.getNextBatch() );
        }
    }

    private void deleteUserStoreGroups()
    {
        final GroupSpecification groupSpec = new GroupSpecification();
        groupSpec.setUserStoreKey( command.getKey() );
        groupSpec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );

        final List<GroupKey> allGroupKeys = getGroupKeys( userStoreService.getGroups( groupSpec ) );
        status.setTotalLocalGroupCount( allGroupKeys.size() );

        final BatchedList<GroupKey> groupsToDeleteAsBatchedList = new BatchedList<GroupKey>( allGroupKeys, batchSize );
        while ( groupsToDeleteAsBatchedList.hasMoreBatches() )
        {
            userStoreService.deleteGroupsLocally( status.getLocalGroupsStatus(), groupsToDeleteAsBatchedList.getNextBatch() );
        }
    }

    private List<UserKey> getUserKeys( final List<UserEntity> users )
    {
        final List<UserKey> keys = new ArrayList<UserKey>( users.size() );
        for ( final UserEntity user : users )
        {
            keys.add( user.getKey() );
        }
        return keys;
    }

    private List<GroupKey> getGroupKeys( final List<GroupEntity> groups )
    {
        final List<GroupKey> keys = new ArrayList<GroupKey>( groups.size() );
        for ( final GroupEntity group : groups )
        {
            keys.add( group.getGroupKey() );
        }
        return keys;
    }
}
