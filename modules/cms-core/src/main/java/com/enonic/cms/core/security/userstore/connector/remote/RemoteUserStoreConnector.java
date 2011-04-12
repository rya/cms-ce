/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.group.*;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import org.springframework.util.Assert;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.security.userstore.UserStoreConnectorPolicyBrokenException;
import com.enonic.cms.core.security.userstore.connector.AbstractBaseUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.UserAlreadyExistsException;

import com.enonic.cms.core.security.userstore.connector.GroupAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.UserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;

import com.enonic.cms.core.security.userstore.connector.remote.plugin.RemoteUserStorePlugin;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserImpl;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserFieldType;
import com.enonic.cms.domain.user.field.UserInfoTransformer;
import com.enonic.cms.domain.user.remote.RemoteGroup;
import com.enonic.cms.domain.user.remote.RemotePrincipal;
import com.enonic.cms.domain.user.remote.RemoteUser;

public class RemoteUserStoreConnector
    extends AbstractBaseUserStoreConnector
    implements UserStoreConnector
{
    private RemoteUserStorePlugin remoteUserStorePlugin;

    private TimeService timeService;

    private UserStoreConnectorConfig connectorConfig;

    private UserStoreConfig userStoreConfig;

    public RemoteUserStoreConnector( final UserStoreKey userStoreKey, final String userStoreName, final String connectorName )
    {
        super( userStoreKey, userStoreName, connectorName );
    }

    public boolean canCreateUser()
    {
        return connectorConfig.canCreateUser();
    }

    public boolean canUpdateUser()
    {
        return connectorConfig.canUpdateUser();
    }

    public boolean canUpdateUserPassword()
    {
        return connectorConfig.canUpdateUserPassword();
    }

    public boolean canDeleteUser()
    {
        return connectorConfig.canDeleteUser();
    }

    public boolean canCreateGroup()
    {
        return connectorConfig.canCreateGroup();
    }

    public boolean canReadGroup()
    {
        return connectorConfig.canReadGroup();
    }

    public boolean canUpdateGroup()
    {
        return connectorConfig.canUpdateGroup();
    }

    public boolean canDeleteGroup()
    {
        return connectorConfig.canDeleteGroup();
    }

    public UserKey storeNewUser( final StoreNewUserCommand command )
    {
        if ( !connectorConfig.canCreateUser() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to create user without 'create' policy" );
        }

        Assert.isTrue( command.getUserStoreKey().equals( userStoreKey ) );

        ensureValidUserName( command );

        RemoteUser remoteUser = new RemoteUser( command.getUsername() );

        remoteUser.setEmail( command.getEmail() );

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        final UserFieldMap userFieldMap = infoTransformer.toUserFields( command.getUserInfo() );

        userFieldMap.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );

        remoteUser.getUserFields().addAll( userFieldMap.getAll() );

        final boolean success = remoteUserStorePlugin.addPrincipal( remoteUser );
        if ( !success )
        {
            throw new UserAlreadyExistsException( userStoreName, command.getUsername() );
        }

        remoteUser = remoteUserStorePlugin.getUser( command.getUsername() );
        command.setSyncValue( remoteUser.getSync() );

        changePassword( command.getUsername(), command.getPassword() );

        if ( connectorConfig.groupsStoredRemote() )
        {
            addMembershipsRemote( remoteUser, command.getMemberships() );
        }

        return storeNewUserLocally( command, new DisplayNameResolver( getUserStore().getConfig() ) );
    }

    protected boolean isUsernameUnique( final String userName )
    {
        final UserEntity localUser = getLocalUserWithUsername( userName );

        final RemoteUser remoteUser = remoteUserStorePlugin.getUser( userName );

        return localUser == null && remoteUser == null;
    }

    public void updateUser( final UpdateUserCommand command )
    {
        if ( !connectorConfig.canUpdateUser() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to update user without 'update' policy" );
        }

        final UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );

        if ( userToUpdate == null )
        {
            throw new UserNotFoundException( command.getSpecification() );
        }

        final RemoteUser remoteUser = remoteUserStorePlugin.getUser( userToUpdate.getName() );

        if ( remoteUser == null )
        {
            throw new RuntimeException( "User does not found in remote userstore '" + userStoreName + "' from specification: " +
                command.getSpecification().toString() );
        }

        updateUserModifiableValues( command, remoteUser );

        final boolean success = remoteUserStorePlugin.updatePrincipal( remoteUser );
        if ( !success )
        {
            throw new RuntimeException( "User does not exists: " + command.getSpecification().getName() );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            updateMembershipsRemote( userToUpdate, remoteUser, command.getMemberships() );
        }

        updateUserLocally( command );
    }

    private void updateUserModifiableValues( final UpdateUserCommand command, final RemoteUser remoteUser )
    {
        final boolean replaceAll = command.getUpdateStrategy().equals( UpdateUserCommand.UpdateStrategy.REPLACE_ALL );

        final String email = command.getEmail();

        if ( email != null || replaceAll )
        {
            remoteUser.setEmail( email );
        }

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        final UserFieldMap userFieldMap = infoTransformer.toUserFields( command.getUserInfo() );

        userFieldMap.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );

        if ( replaceAll )
        {
            remoteUser.getUserFields().clear();
        }

        // Remove address field on "target" when address field is set
        if ( userFieldMap.hasField( UserFieldType.ADDRESS ) )
        {
            remoteUser.getUserFields().remove( UserFieldType.ADDRESS );
        }

        remoteUser.getUserFields().addAll( userFieldMap.getAll() );
    }

    public void deleteUser( final DeleteUserCommand command )
    {
        if ( !connectorConfig.canDeleteUser() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to delete user without 'delete' policy" );
        }

        final UserEntity userToDelete = userDao.findSingleBySpecification( command.getSpecification() );

        remoteUserStorePlugin.removePrincipal( new RemoteUser( userToDelete.getName() ) );

        deleteUserLocally( command );
    }

    public GroupKey storeNewGroup( final StoreNewGroupCommand command )
    {
        if ( !connectorConfig.canCreateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to create group without 'create' policy" );
        }

        Assert.isTrue( command.getUserStoreKey().equals( userStoreKey ) );

        if ( connectorConfig.groupsStoredRemote() )
        {
            RemoteGroup remoteGroup = new RemoteGroup( command.getName() );
            final boolean success = remoteUserStorePlugin.addPrincipal( remoteGroup );
            if ( !success )
            {
                throw new GroupAlreadyExistsException( userStoreName, command.getName() );
            }
            remoteGroup = getRemoteGroup( command.getName() );
            command.setSyncValue( remoteGroup.getSync() );
            final List<GroupKey> members = command.getMembers();
            if ( members != null && members.size() > 0 )
            {
                addMembersRemote( remoteGroup, members );
            }
        }
        return storeNewGroupLocally( command );
    }

    public void updateGroup( final UpdateGroupCommand command )
    {
        if ( !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to update group without 'update' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            final String groupName = command.getName();
            final RemoteGroup remoteGroup = getRemoteGroup( groupName );
            final GroupEntity groupToUpdate = groupDao.findByKey( command.getGroupKey() );
            updateMembersRemote( groupToUpdate, remoteGroup, command.getMembers() );
        }
        updateGroupLocally( command );
    }

    private List<RemotePrincipal> toPrincipalList( final RemotePrincipal... principals )
    {
        final ArrayList<RemotePrincipal> list = new ArrayList<RemotePrincipal>();
        list.addAll( Arrays.asList( principals ) );
        return list;
    }

    public void addMembershipToGroup( final GroupEntity groupToAdd, final GroupEntity groupToAddTo )
    {
        if ( !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to add membership to group without 'update' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            addMembersToRemoteGroup( groupToAddTo.getName(), toPrincipalList( new RemoteGroup( groupToAdd.getName() ) ) );
        }
        addMembershipToGroupLocally( groupToAdd, groupToAddTo );
    }

    public void removeMembershipFromGroup( final GroupEntity groupToRemove, final GroupEntity groupToRemoveFrom )
    {
        if ( !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to remove membership from group without 'update' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            removeMembersFromRemoteGroup( groupToRemoveFrom.getName(), toPrincipalList( new RemoteGroup( groupToRemove.getName() ) ) );
        }
        removeMembershipFromGroupLocally( groupToRemove, groupToRemoveFrom );
    }

    public void deleteGroup( final DeleteGroupCommand command )
    {
        if ( !connectorConfig.canDeleteGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to delete group without 'delete' policy" );
        }

        if ( connectorConfig.groupsStoredRemote() )
        {
            final GroupEntity groupToDelete = groupDao.findSingleBySpecification( command.getSpecification() );
            remoteUserStorePlugin.removePrincipal( new RemoteGroup( groupToDelete.getName() ) );
        }
        deleteGroupLocally( command );
    }

    public String authenticateUser( final String uid, final String password )
    {
        if ( !remoteUserStorePlugin.authenticate( uid, password ) )
        {
            throw new InvalidCredentialsException( uid );
        }
        return remoteUserStorePlugin.getUser( uid ).getSync();
    }

    public void changePassword( final String uid, final String newPassword )
    {
        if ( !connectorConfig.canUpdateUserPassword() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to change password without 'updatePassword' policy" );
        }

        final boolean success = remoteUserStorePlugin.changePassword( uid, newPassword );
        if ( !success )
        {
            throw new RuntimeException( "Changed password failed" );
        }
    }

    public synchronized void synchronizeUsers( final SynchronizeStatus status, final List<RemoteUser> remoteUsers,
                                               final boolean syncMemberships, final MemberCache memberCache )
    {
        doSynchronizeUsers( status, remoteUsers, true, syncMemberships, memberCache );
    }

    public synchronized void synchronizeUserMemberships( final SynchronizeStatus status, final RemoteUser remoteUser,
                                                         final MemberCache memberCache )
    {
        final List<RemoteUser> remoteUsers = new ArrayList<RemoteUser>( 1 );
        remoteUsers.add( remoteUser );
        doSynchronizeUsers( status, remoteUsers, false, true, memberCache );
    }

    private void doSynchronizeUsers( final SynchronizeStatus status, final List<RemoteUser> remoteUsers, final boolean syncUser,
                                     final boolean syncMemberships, final MemberCache memberCache )
    {
        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );

        final boolean doSyncMemberShips = connectorConfig.groupsStoredRemote() && syncMemberships;

        final UsersSynchronizer synchronizer = new UsersSynchronizer( userStore, syncUser, doSyncMemberShips );
        synchronizer.setUserStorageService( userStorageService );
        synchronizer.setGroupDao( groupDao );
        synchronizer.setUserDao( userDao );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setTimeService( timeService );
        synchronizer.setConnectorConfig( connectorConfig );
        synchronizer.setStatusCollector( status );

        synchronizer.synchronizeUsers( remoteUsers, memberCache );
    }

    public synchronized void synchronizeUser( final UserEntity user, final boolean syncMemberships )
    {
        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );

        final boolean doSyncMemberShips = connectorConfig.groupsStoredRemote() && syncMemberships;

        final UserSynchronizer synchronizer = new UserSynchronizer( userStore, doSyncMemberShips );
        synchronizer.setUserStorageService( userStorageService );
        synchronizer.setGroupDao( groupDao );
        synchronizer.setUserDao( userDao );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setTimeService( timeService );
        synchronizer.setConnectorConfig( connectorConfig );

        synchronizer.synchronizeUser( user, new MemberCache() );
    }

    public RemoteUser getRemoteUser( final String uid )
        throws UserNotFoundException
    {
        RemoteUser user = remoteUserStorePlugin.getUser( uid );
        if ( user == null )
        {
            throw new UserNotFoundException( new QualifiedUsername( this.userStoreName, uid ) );
        }
        return user;
    }

    public synchronized UserKey createUserNotExistingLocally( final String uid )
    {
        final RemoteUser remoteUser = remoteUserStorePlugin.getUser( uid );
        final StoreNewUserCommand command = new StoreNewUserCommand();
        command.setUserStoreKey( userStoreKey );
        command.setUsername( uid );
        command.setSyncValue( remoteUser.getSync() );
        command.setEmail( remoteUser.getEmail() );
        command.setType( UserType.NORMAL );

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        final UserFieldMap userFieldMap = remoteUser.getUserFields();
        userFieldMap.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );
        final UserInfo userInfo = infoTransformer.toUserInfo( userFieldMap );
        command.setUserInfo( userInfo );

        return storeNewUserLocally( command, new DisplayNameResolver( getUserStore().getConfig() ) );
    }

    public synchronized void synchronizeGroup( final GroupEntity group, final boolean syncMemberships, final boolean syncMembers )
    {
        if ( !connectorConfig.canReadGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to synchronize group without 'read' policy" );
        }

        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        final GroupSynchronizer synchronizer = new GroupSynchronizer( userStore, syncMemberships, syncMembers );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setUserDao( userDao );
        synchronizer.setGroupDao( groupDao );

        synchronizer.synchronize( group, new MemberCache() );
    }

    public synchronized void synchronizeGroups( final SynchronizeStatus status, final List<RemoteGroup> remoteGroups,
                                                final boolean syncMemberships, final boolean syncMembers, final MemberCache memberCache )
    {
        if ( !connectorConfig.canReadGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to synchronize groups without 'read' policy" );
        }

        doSynchronizeGroups( status, remoteGroups, true, syncMemberships, syncMembers, memberCache );
    }

    public synchronized void synchronizeGroupMemberships( final SynchronizeStatus status, final RemoteGroup remoteGroup,
                                                          final MemberCache memberCache )
    {
        if ( !connectorConfig.canReadGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to synchronize groups without 'read' policy" );
        }

        final List<RemoteGroup> remoteGroups = new ArrayList<RemoteGroup>( 1 );
        remoteGroups.add( remoteGroup );
        doSynchronizeGroups( status, remoteGroups, false, true, false, memberCache );
    }

    private void doSynchronizeGroups( final SynchronizeStatus status, final List<RemoteGroup> remoteGroups, final boolean syncGroup,
                                      final boolean syncMemberships, final boolean syncMembers, final MemberCache memberCache )
    {
        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        final GroupsSynchronizer synchronizer = new GroupsSynchronizer( userStore, syncGroup, syncMemberships, syncMembers );
        synchronizer.setRemoteUserStorePlugin( remoteUserStorePlugin );
        synchronizer.setUserDao( userDao );
        synchronizer.setGroupDao( groupDao );
        synchronizer.setStatusCollector( status );

        synchronizer.synchronize( remoteGroups, memberCache );
    }

    private void addMembershipsRemote( final RemoteUser remoteUser, final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToAdd = getMembershipsToAddRemote( requestedMembershipKeys );

        final boolean hasMembershipsChanges = membershipsToAdd.size() > 0;
        if ( hasMembershipsChanges && !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to add/remove a user's memberships without group 'update' policy" );
        }
        for ( final GroupEntity membershipToAdd : membershipsToAdd )
        {
            addMembersToRemoteGroup( membershipToAdd.getName(), toPrincipalList( remoteUser ) );
        }
    }

    private void addMembersRemote( final RemoteGroup remoteGroup, final Collection<GroupKey> requestedMemberKeys )
    {
        final List<RemotePrincipal> members = getMembersToAddRemote( requestedMemberKeys );
        remoteUserStorePlugin.addMembers( remoteGroup, members );
    }

    private void updateMembershipsRemote( final UserEntity userToUpdate, final RemoteUser remoteUser,
                                          final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToRemove = getMembershipsToRemoveRemote( userToUpdate, requestedMembershipKeys );
        final Set<GroupEntity> membershipsToAdd = getMembershipsToAddRemote( userToUpdate, requestedMembershipKeys );

        final boolean hasMembershipsChanges = membershipsToRemove.size() > 0 || membershipsToAdd.size() > 0;
        if ( hasMembershipsChanges && !connectorConfig.canUpdateGroup() )
        {
            throw new UserStoreConnectorPolicyBrokenException( userStoreName, connectorName,
                                                               "Trying to add/remove a user's memberships without group 'update' policy" );
        }
        for ( final GroupEntity membershipToAdd : membershipsToAdd )
        {
            addMembersToRemoteGroup( membershipToAdd.getName(), toPrincipalList( remoteUser ) );
        }
        for ( final GroupEntity membershipToRemove : membershipsToRemove )
        {
            removeMembersFromRemoteGroup( membershipToRemove.getName(), toPrincipalList( remoteUser ) );
        }
    }

    private void updateMembersRemote( final GroupEntity groupToUpdate, final RemoteGroup remoteGroup,
                                      final Collection<GroupEntity> requestedMembers )
    {
        final List<RemotePrincipal> membersToRemove = getMembersToRemoveRemote( groupToUpdate, requestedMembers );
        remoteUserStorePlugin.removeMembers( remoteGroup, membersToRemove );

        final List<RemotePrincipal> membersToAdd = getMembersToAddRemote( groupToUpdate, requestedMembers );
        remoteUserStorePlugin.addMembers( remoteGroup, membersToAdd );
    }

    private void addMembersToRemoteGroup( final String groupName, final List<RemotePrincipal> members )
    {
        final RemoteGroup remoteGroup = getRemoteGroup( groupName );
        remoteUserStorePlugin.addMembers( remoteGroup, members );
    }

    private void removeMembersFromRemoteGroup( final String groupName, final List<RemotePrincipal> members )
    {
        final RemoteGroup remoteGroup = getRemoteGroup( groupName );
        remoteUserStorePlugin.removeMembers( remoteGroup, members );
    }

    private Set<GroupEntity> getMembershipsToAddRemote( final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToAdd = new HashSet<GroupEntity>();
        for ( final GroupKey membershipKey : requestedMembershipKeys )
        {
            final GroupEntity membership = groupDao.findByKey( membershipKey );

            if ( membership.getType() == GroupType.USERSTORE_GROUP )
            {
                verifyCorrectUserstore( membership );
                membershipsToAdd.add( membership );
            }
        }
        return membershipsToAdd;
    }

    private Set<GroupEntity> getMembershipsToAddRemote( final UserEntity userToUpdate, final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToAdd = new HashSet<GroupEntity>();
        final Set<GroupEntity> currentMemberships = userToUpdate.getDirectMemberships();

        for ( final GroupKey requestedMembershipKey : requestedMembershipKeys )
        {
            final GroupEntity requestedMembership = groupDao.findByKey( requestedMembershipKey );
            if ( requestedMembership.getType() == GroupType.USERSTORE_GROUP && !currentMemberships.contains( requestedMembership ) )
            {
                verifyCorrectUserstore( requestedMembership );
                membershipsToAdd.add( requestedMembership );
            }
        }
        return membershipsToAdd;
    }

    private Set<GroupEntity> getMembershipsToRemoveRemote( final UserEntity userToUpdate,
                                                           final Collection<GroupKey> requestedMembershipKeys )
    {
        final Set<GroupEntity> membershipsToRemove = new HashSet<GroupEntity>();
        final Set<GroupEntity> currentMemberships = userToUpdate.getDirectMemberships();
        for ( final GroupEntity currentMembership : currentMemberships )
        {
            if ( currentMembership.getType() == GroupType.USERSTORE_GROUP &&
                !requestedMembershipKeys.contains( currentMembership.getGroupKey() ) )
            {
                verifyCorrectUserstore( currentMembership );
                membershipsToRemove.add( currentMembership );
            }
        }
        return membershipsToRemove;
    }

    private List<RemotePrincipal> getMembersToAddRemote( final Collection<GroupKey> requestedMemberKeys )
    {
        final List<RemotePrincipal> membersToAdd = new ArrayList<RemotePrincipal>();

        for ( final GroupKey requestedMemberKey : requestedMemberKeys )
        {
            final GroupEntity requestedMember = groupDao.findByKey( requestedMemberKey );
            if ( requestedMember.getType() == GroupType.USERSTORE_GROUP )
            {
                verifyCorrectUserstore( requestedMember );
                membersToAdd.add( new RemoteGroup( requestedMember.getName() ) );
            }
            if ( requestedMember.getType() == GroupType.USER )
            {
                verifyCorrectUserstore( requestedMember );
                final UserEntity user = requestedMember.getUser();
                verifyCorrectUserstore( user );
                membersToAdd.add( new RemoteUser( user.getName() ) );
            }
        }
        return membersToAdd;
    }

    private List<RemotePrincipal> getMembersToAddRemote( final GroupEntity groupToUpdate, final Collection<GroupEntity> requestedMembers )
    {
        final List<RemotePrincipal> membersToAdd = new ArrayList<RemotePrincipal>();
        final Set<GroupEntity> currentMembers = groupToUpdate.getMembers( false );

        for ( final GroupEntity requestedMember : requestedMembers )
        {
            if ( requestedMember.getType() == GroupType.USERSTORE_GROUP && !currentMembers.contains( requestedMember ) )
            {
                verifyCorrectUserstore( requestedMember );
                membersToAdd.add( new RemoteGroup( requestedMember.getName() ) );
            }
            if ( requestedMember.getType() == GroupType.USER && !currentMembers.contains( requestedMember ) )
            {
                verifyCorrectUserstore( requestedMember );
                final UserEntity user = requestedMember.getUser();
                verifyCorrectUserstore( user );
                membersToAdd.add( new RemoteUser( user.getName() ) );
            }
        }
        return membersToAdd;
    }

    private List<RemotePrincipal> getMembersToRemoveRemote( final GroupEntity groupToUpdate,
                                                            final Collection<GroupEntity> requestedMembers )
    {
        final List<RemotePrincipal> membersToRemove = new ArrayList<RemotePrincipal>();
        final Set<GroupEntity> currentMembers = groupToUpdate.getMembers( false );

        for ( final GroupEntity currentMember : currentMembers )
        {
            if ( currentMember.getType() == GroupType.USERSTORE_GROUP && !requestedMembers.contains( currentMember ) )
            {
                verifyCorrectUserstore( currentMember );
                membersToRemove.add( new RemoteGroup( currentMember.getName() ) );
            }
            if ( currentMember.getType() == GroupType.USER && !requestedMembers.contains( currentMember ) )
            {
                verifyCorrectUserstore( currentMember );
                final UserEntity user = currentMember.getUser();
                verifyCorrectUserstore( user );
                membersToRemove.add( new RemoteUser( user.getName() ) );
            }
        }
        return membersToRemove;
    }

    private RemoteGroup getRemoteGroup( final String groupName )
    {
        final RemoteGroup remoteGroup = remoteUserStorePlugin.getGroup( groupName );
        if ( remoteGroup == null )
        {
            throw new IllegalArgumentException( "Group does not exists in remote user store '" + userStoreName + "': " + groupName );
        }
        return remoteGroup;
    }

    private void verifyCorrectUserstore( final UserEntity user )
    {
        if ( !userStoreKey.equals( user.getUserStoreKey() ) )
        {
            throw new IllegalArgumentException(
                "Illegal userstore. Cannot add user " + user.getQualifiedName() + " to userstore " + userStoreName );
        }
    }

    private void verifyCorrectUserstore( final GroupEntity group )
    {
        if ( !userStoreKey.equals( group.getUserStoreKey() ) )
        {
            throw new IllegalArgumentException(
                "Illegal userstore. Cannot add group " + group.getQualifiedName() + " to userstore " + userStoreName );
        }
    }

    public User getUserByEntity( final UserEntity userEntity )
    {
        return UserImpl.createFrom( userEntity );
    }

    public List<RemoteUser> getAllUsers()
    {
        return remoteUserStorePlugin.getAllUsers();
    }

    public List<RemoteGroup> getAllGroups()
    {
        return remoteUserStorePlugin.getAllGroups();
    }

    public void setRemoteUserStorePlugin( final RemoteUserStorePlugin value )
    {
        remoteUserStorePlugin = value;
    }

    public void setTimeService( final TimeService value )
    {
        timeService = value;
    }

    public void setConnectorConfig( final UserStoreConnectorConfig value )
    {
        connectorConfig = value;
    }

    public void setUserStoreConfig( final UserStoreConfig value )
    {
        userStoreConfig = value;
    }
}