/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.core.security.userstore.status.LocalUsersStatus;

import com.enonic.cms.core.security.userstore.connector.remote.MemberCache;

import com.enonic.cms.core.security.userstore.status.LocalGroupsStatus;

import com.enonic.cms.core.security.group.AddMembershipsCommand;
import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.RemoveMembershipsCommand;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.DeleteUserStoreCommand;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.domain.user.remote.RemoteGroup;
import com.enonic.cms.domain.user.remote.RemoteUser;

public interface UserStoreService
{
    UserKey storeNewUser( final StoreNewUserCommand command );

    void updateUser( final UpdateUserCommand command );

    void deleteUser( final DeleteUserCommand command );

    void deleteUserStore( final DeleteUserStoreCommand command );

    GroupKey storeNewGroup( final StoreNewGroupCommand command );

    UserStoreKey storeNewUserStore( final StoreNewUserStoreCommand command );

    void updateUserStore( final UpdateUserStoreCommand command );

    void updateGroup( final UpdateGroupCommand command );

    void deleteGroup( final DeleteGroupCommand command );

    List<GroupEntity> removeMembershipsFromGroup( final RemoveMembershipsCommand command );

    List<GroupEntity> addMembershipsToGroup( final AddMembershipsCommand command );

    void authenticateUser( final UserStoreKey userStoreKey, final String uid, final String password );

    void changePassword( final UserStoreKey userStoreKey, final String uid, final String newPassword );

    UserEntity synchronizeUser( final UserSpecification userSpec );

    UserKey synchronizeUser( UserStoreKey userStoreKey, String uid )
        throws UserNotFoundException;

    void synchronizeUsers( final SynchronizeStatus status, final UserStoreKey userStoreKey, final List<RemoteUser> remoteUsers,
                           final boolean syncMemberships, final MemberCache memberCache );

    void synchronizeUserMemberships( final SynchronizeStatus status, final UserStoreKey userStoreKey, final RemoteUser remoteUser,
                                     final MemberCache memberCache );

    void deleteUsersLocally( final LocalUsersStatus status, final List<UserKey> users );

    GroupEntity synchronizeGroup( final GroupKey groupKey );

    void synchronizeGroups( final SynchronizeStatus status, final UserStoreKey userStoreKey, final List<RemoteGroup> remoteGroups,
                            final boolean syncMemberships, final boolean syncMembers, final MemberCache memberCache );

    void synchronizeGroupMemberships( final SynchronizeStatus status, final UserStoreKey userStoreKey, final RemoteGroup remoteGroup,
                                      final MemberCache memberCache );

    void deleteGroupsLocally( final LocalGroupsStatus status, final List<GroupKey> groups );

    UserStoreEntity getDefaultUserStore();

    UserStoreEntity getUserStore( final UserStoreKey userStoreKey );

    boolean isUserStoreAdministrator( final UserKey userKey );

    boolean isUserStoreAdministrator( final UserKey userKey, final UserStoreKey userStoreKey );

    User getUserByKey( final UserKey userKey );

    Map<String, UserStoreConnectorConfig> getUserStoreConnectorConfigs();

    boolean canSynchronizeUsers( final UserStoreKey userStoreKey );

    boolean canSynchronizeGroups( final UserStoreKey userStoreKey );

    List<UserEntity> getUsers( final UserStoreKey userStoreKey );

    Multimap<String, UserEntity> getUsersAsMapByName( final UserStoreKey userStoreKey );

    List<GroupEntity> getGroups( final GroupSpecification groupSpec );

    void verifyUserStoreConnector( final String connectorName );

    void verifyUserStoreConnectorConfig( final UserStoreConfig config, final String connectorName );

    boolean canCreateUser( final UserStoreKey userStoreKey );

    boolean canUpdateUser( final UserStoreKey userStoreKey );

    boolean canUpdateUserPassword( final UserStoreKey userStoreKey );

    boolean canDeleteUser( final UserStoreKey userStoreKey );

    boolean canCreateGroup( final UserStoreKey userStoreKey );

    boolean canReadGroup( final UserStoreKey userStoreKey );

    boolean canUpdateGroup( final UserStoreKey userStoreKey );

    boolean canDeleteGroup( final UserStoreKey userStoreKey );

    public void verifyUniqueEmailAddress( String email, UserStoreKey userStoreKey );

    void initializeUserStores();
}
