/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.synchronize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.remote.MemberCache;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;

import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.user.remote.RemoteGroup;
import com.enonic.cms.domain.user.remote.RemoteUser;

public class SynchronizeUserStoreJobImpl
    implements SynchronizeUserStoreJob
{
    private final UserStoreKey userStoreKey;

    private final SynchronizeUserStoreType type;

    private final int batchSize;

    private UserStoreService userStoreService;

    private RemoteUserStoreConnector userStoreConnector;

    private MemberCache memberCache;

    private SynchronizeStatus status;

    public SynchronizeUserStoreJobImpl( final UserStoreKey userStoreKey, final SynchronizeUserStoreType type, final int batchSize )
    {
        this.userStoreKey = userStoreKey;
        this.type = type;
        this.batchSize = batchSize;
        this.memberCache = new MemberCache();
        this.status = new SynchronizeStatus( type );
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
        switch ( type )
        {
            case USERS_ONLY:
            {
                final List<RemoteUser> remoteUsers = userStoreConnector.getAllUsers();
                status.setTotalRemoteUserCount( remoteUsers.size() );

                synchronizeAllUsers( remoteUsers, false );
                synchronizeAllUserMemberships( remoteUsers );
                break;
            }
            case GROUPS_ONLY:
            {
                final List<RemoteGroup> remoteGroups = userStoreConnector.getAllGroups();
                status.setTotalRemoteGroupCount( remoteGroups.size() );

                synchronizeAllGroups( remoteGroups, false, false );
                synchronizeAllGroupMemberships( remoteGroups );
                break;
            }
            case USERS_AND_GROUPS:
            {
                final List<RemoteUser> remoteUsers = userStoreConnector.getAllUsers();
                final List<RemoteGroup> remoteGroups = userStoreConnector.getAllGroups();
                status.setTotalRemoteUserCount( remoteUsers.size() );
                status.setTotalRemoteGroupCount( remoteGroups.size() );

                synchronizeAllUsers( remoteUsers, false );
                synchronizeAllGroups( remoteGroups, false, false );
                synchronizeAllUserMemberships( remoteUsers );
                synchronizeAllGroupMemberships( remoteGroups );
                break;
            }
        }
    }

    private void synchronizeAllUsers( final List<RemoteUser> remoteUsers, final boolean syncMemberships )
    {
        Set<String> usedUserNames = new HashSet<String>();
        for ( int i = 0; i < remoteUsers.size(); i++ )
        {
            RemoteUser remoteUser = remoteUsers.get( i );
            boolean existingUserName = !usedUserNames.add( remoteUser.getId().toLowerCase() );
            if ( existingUserName )
            {
                status.userSkipped();
                remoteUsers.remove( i );
                --i;
            }
        }

        final BatchedList<RemoteUser> batchedList = new BatchedList<RemoteUser>( remoteUsers, batchSize );
        while ( batchedList.hasMoreBatches() )
        {
            userStoreService.synchronizeUsers( status, userStoreKey, batchedList.getNextBatch(), syncMemberships, memberCache );
        }

        final Multimap<String, UserEntity> allUsersMapByName = userStoreService.getUsersAsMapByName( userStoreKey );

        final List<UserKey> allUsersToDelete = resolveUsersToDelete( allUsersMapByName, remoteUsers );

        status.setTotalLocalUserCount( allUsersToDelete.size() );

        final BatchedList<UserKey> usersToDeleteAsBatchedList = new BatchedList<UserKey>( allUsersToDelete, batchSize );
        while ( usersToDeleteAsBatchedList.hasMoreBatches() )
        {
            userStoreService.deleteUsersLocally( status.getLocalUsersStatus(), usersToDeleteAsBatchedList.getNextBatch() );
        }
    }

    private List<UserKey> resolveUsersToDelete( final Multimap<String, UserEntity> usersMapByName,
                                                final Collection<RemoteUser> remoteUsers )
    {
        // Remove all remote users from usersMapByName , so that we are only left with users not existing remote.
        final List<UserEntity> usersToRemoveFromMap = new ArrayList<UserEntity>();
        for ( final RemoteUser remoteUser : remoteUsers )
        {
            final Collection<UserEntity> candidates = usersMapByName.get( remoteUser.getId() );
            for ( final UserEntity candidate : candidates )
            {
                if ( candidate != null && remoteUser.getSync().equals( candidate.getSync() ) )
                {
                    usersToRemoveFromMap.add( candidate );
                }
            }
        }
        for ( final UserEntity userToRemoveFromMap : usersToRemoveFromMap )
        {
            usersMapByName.remove( userToRemoveFromMap.getName(), userToRemoveFromMap );
        }

        final List<UserKey> usersToDelete = new ArrayList<UserKey>();
        for ( final UserEntity userToDelete : usersMapByName.values() )
        {
            usersToDelete.add( userToDelete.getKey() );
        }
        return usersToDelete;
    }

    private void synchronizeAllGroups( final List<RemoteGroup> remoteGroups, final boolean syncMemberships, final boolean syncMembers )
    {
        final BatchedList<RemoteGroup> batchedList = new BatchedList<RemoteGroup>( remoteGroups, batchSize );
        while ( batchedList.hasMoreBatches() )
        {
            userStoreService.synchronizeGroups( status, userStoreKey, batchedList.getNextBatch(), syncMemberships, syncMembers,
                                                memberCache );
        }

        final Multimap<String, GroupEntity> allUsersMapByUid = getGroupsAsMapByName( userStoreKey );

        final List<GroupKey> allGroupsToDelete = resolveGroupsToDelete( allUsersMapByUid, remoteGroups );

        status.setTotalLocalGroupCount( allGroupsToDelete.size() );

        final BatchedList<GroupKey> groupsToDeleteAsBatchedList = new BatchedList<GroupKey>( allGroupsToDelete, batchSize );
        while ( groupsToDeleteAsBatchedList.hasMoreBatches() )
        {
            userStoreService.deleteGroupsLocally( status.getLocalGroupsStatus(), groupsToDeleteAsBatchedList.getNextBatch() );
        }
    }

    private Multimap<String, GroupEntity> getGroupsAsMapByName( final UserStoreKey userStoreKey )
    {
        final GroupSpecification groupSpec = new GroupSpecification();
        groupSpec.setUserStoreKey( userStoreKey );
        groupSpec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        groupSpec.setType( GroupType.USERSTORE_GROUP );

        final List<GroupEntity> groups = userStoreService.getGroups( groupSpec );
        final Multimap<String, GroupEntity> groupMapByName = HashMultimap.create();
        for ( final GroupEntity group : groups )
        {
            groupMapByName.put( group.getName(), group );
        }
        return groupMapByName;
    }

    private List<GroupKey> resolveGroupsToDelete( final Multimap<String, GroupEntity> groupsMapByName,
                                                  final Collection<RemoteGroup> remoteGroups )
    {
        // Remove all remote groups from groupsMapByName, so that we are only left with groups not existing remote.
        final List<GroupEntity> groupsToRemoveFromMap = new ArrayList<GroupEntity>();
        for ( final RemoteGroup remoteGroup : remoteGroups )
        {
            final Collection<GroupEntity> candidates = groupsMapByName.get( remoteGroup.getId() );
            for ( final GroupEntity candidate : candidates )
            {
                if ( candidate != null && remoteGroup.getSync().equals( candidate.getSyncValue() ) )
                {
                    groupsToRemoveFromMap.add( candidate );
                }
            }
        }
        for ( final GroupEntity groupToRemoveFromMap : groupsToRemoveFromMap )
        {
            groupsMapByName.remove( groupToRemoveFromMap.getName(), groupToRemoveFromMap );
        }

        final List<GroupKey> groupsToDelete = new ArrayList<GroupKey>();
        for ( final GroupEntity groupToDelete : groupsMapByName.values() )
        {
            groupsToDelete.add( groupToDelete.getGroupKey() );
        }
        return groupsToDelete;
    }

    private void synchronizeAllUserMemberships( final List<RemoteUser> remoteUsers )
    {
        status.setTotalUserMembershipsCount( remoteUsers.size() );
        for ( final RemoteUser remoteUser : remoteUsers )
        {
            userStoreService.synchronizeUserMemberships( status, userStoreKey, remoteUser, memberCache );
            status.nextUserMemberships();
        }
    }

    private void synchronizeAllGroupMemberships( final List<RemoteGroup> remoteGroups )
    {
        status.setTotalGroupMembershipsCount( remoteGroups.size() );
        for ( final RemoteGroup remoteGroup : remoteGroups )
        {
            userStoreService.synchronizeGroupMemberships( status, userStoreKey, remoteGroup, memberCache );
            status.nextGroupMemberships();
        }
    }

    public SynchronizeStatus getStatus()
    {
        return status;
    }

    public void setUserStoreService( final UserStoreService value )
    {
        userStoreService = value;
    }

    public void setUserStoreConnector( final RemoteUserStoreConnector value )
    {
        userStoreConnector = value;
    }
}