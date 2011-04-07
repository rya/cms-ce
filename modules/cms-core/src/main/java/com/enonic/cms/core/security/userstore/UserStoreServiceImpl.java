/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.security.group.GroupStorageService;
import com.enonic.cms.core.security.group.access.GroupAccessResolver;
import com.enonic.cms.core.security.userstore.connector.UserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.remote.MemberCache;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.remote.plugin.RemoteUserStorePlugin;
import com.enonic.cms.core.security.userstore.status.LocalGroupsStatus;
import com.enonic.cms.core.security.userstore.status.LocalUsersStatus;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserEntityDao;
import com.enonic.cms.store.dao.UserStoreDao;

import com.enonic.cms.core.security.userstore.connector.remote.plugin.RemoteUserStoreFactory;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;

import com.enonic.cms.domain.security.group.AddMembershipsCommand;
import com.enonic.cms.domain.security.group.CreateGroupAccessException;
import com.enonic.cms.domain.security.group.DeleteGroupAccessException;
import com.enonic.cms.domain.security.group.DeleteGroupCommand;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.GroupSpecification;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.group.RemoveMembershipsCommand;
import com.enonic.cms.domain.security.group.StoreNewGroupCommand;
import com.enonic.cms.domain.security.group.UpdateGroupAccessException;
import com.enonic.cms.domain.security.group.UpdateGroupCommand;
import com.enonic.cms.domain.security.user.DeleteUserCommand;
import com.enonic.cms.domain.security.user.DeleteUserStoreCommand;
import com.enonic.cms.domain.security.user.StoreNewUserCommand;
import com.enonic.cms.domain.security.user.UpdateUserCommand;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserImpl;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserNotFoundException;
import com.enonic.cms.domain.security.user.UserSpecification;
import com.enonic.cms.domain.security.user.UserStorageExistingEmailException;
import com.enonic.cms.domain.security.user.UserStorageInvalidArgumentException;
import com.enonic.cms.domain.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.domain.security.userstore.UpdateUserStoreCommand;
import com.enonic.cms.domain.security.userstore.UserStoreAccessException;
import com.enonic.cms.domain.security.userstore.UserStoreAccessType;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.security.userstore.config.InvalidUserStoreConfigException;
import com.enonic.cms.domain.security.userstore.config.UserStoreConfig;
import com.enonic.cms.domain.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.domain.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.domain.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.domain.user.field.UserFieldType;
import com.enonic.cms.domain.user.remote.RemoteGroup;
import com.enonic.cms.domain.user.remote.RemoteUser;

public class UserStoreServiceImpl
    implements UserStoreService
{
    @Autowired
    private UserStoreConnectorManager userConnectorStoreManager;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private UserEntityDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private VerticalProperties verticalProperties;

    @Autowired
    private GroupStorageService groupStorageService;

    @Autowired
    private UserStorageService userStorageService;

    @Autowired
    private UserStoreAccessResolver userStoreAccessResolver;

    @Autowired
    private GroupAccessResolver groupAccessResolver;

    @Autowired
    private RemoteUserStoreFactory remoteUserStoreFactory;

    private static final String VALID_EMAIL_PATTERN =
        "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";


    public UserStoreEntity getUserStore( final UserStoreKey userStoreKey )
    {
        return userStoreDao.findByKey( userStoreKey );
    }

    public boolean isUserStoreAdministrator( final UserKey userKey )
    {
        UserEntity user = userDao.findByKey( userKey );
        UserStoreEntity userStore = user.getUserStore();
        return user.isUserstoreAdmin( userStore );
    }

    public boolean isUserStoreAdministrator( final UserKey userKey, final UserStoreKey userStoreKey )
    {
        UserEntity user = userDao.findByKey( userKey );
        UserStoreEntity userStore = getUserStore( userStoreKey );
        return user.isUserstoreAdmin( userStore );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserKey storeNewUser( final StoreNewUserCommand command )
    {
        final UserSpecification storerSpec = new UserSpecification();
        storerSpec.setKey( command.getStorer() );
        storerSpec.setDeletedStateNotDeleted();
        final UserEntity storer = userDao.findSingleBySpecification( storerSpec );

        final UserStoreEntity userStore = userStoreDao.findByKey( command.getUserStoreKey() );

        if ( !userStoreAccessResolver.hasCreateUserAccess( storer, userStore ) && !command.allowAnyUserAccess() )
        {
            throw new UserStoreAccessException( UserStoreAccessType.CREATE_USER, storer.getQualifiedName(), command.getUsername() );
        }

        final UserStoreConnector usc = doGetUSConnector( command.getUserStoreKey() );

        verifyMandatoryFieldsForCreate( command );

        verifyUniqueEmailForCreate( command );

        return usc.storeNewUser( command );
    }

    private void verifyMandatoryFieldsForCreate( StoreNewUserCommand command )
    {
        String email = command.getEmail();

        if ( !isValidEmailAddress( email ) )
        {
            throw new UserStorageInvalidArgumentException( "email" );
        }

        boolean hasUserName = StringUtils.isNotBlank( command.getUsername() );
        boolean hasDisplayName = StringUtils.isNotBlank( command.getDisplayName() );
        boolean hasFirstName = StringUtils.isNotBlank( command.getUserInfo().getFirstName() );
        boolean hasLastName = StringUtils.isNotBlank( command.getUserInfo().getLastName() );

        if ( !hasUserName && !hasDisplayName && !hasFirstName && !hasLastName )
        {
            String[] oneOfRequiredArguments = new String[]{"user_name", "display_name", "first_name", "last_name"};

            throw new UserStorageInvalidArgumentException( Arrays.asList( oneOfRequiredArguments ),
                                                           "Invalid arguments in storage operation, missing one of the following arguments: " );
        }
    }

    public void verifyUniqueEmailAddress( String email, UserStoreKey userStoreKey )
    {
        doVerifyUniqueEmailAdress( email, userStoreKey );
    }

    private void doVerifyUniqueEmailAdress( String email, UserStoreKey userStoreKey )
    {
        if ( StringUtils.isEmpty( email ) )
        {
            return;
        }

        UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );

        List<UserEntity> usersWithThisEmail = findUsersWithEmail( email, userStoreKey );

        if ( usersWithThisEmail.size() > 0 )
        {
            throw new UserStorageExistingEmailException( email, userStore.getName() );
        }

    }


    private boolean isValidEmailAddress( String email )
    {
        return StringUtils.isNotBlank( email ) && email.matches( VALID_EMAIL_PATTERN );
    }


    private void verifyUniqueEmailForCreate( StoreNewUserCommand command )
    {
        doVerifyUniqueEmailAdress( command.getEmail(), command.getUserStoreKey() );
    }

    private void verifyUpdateUserCommand( final UpdateUserCommand command )
    {
        verifyRestrictedGroupAccess( command );

        verifyMandatoryFieldsForUpdate( command );

        verifyUniqueEmailForUpdate( command );
    }

    private void verifyRestrictedGroupAccess( UpdateUserCommand command )
    {
        UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );

        if ( command.syncMemberships() )
        {
            GroupEntity userGroup = userToUpdate.getUserGroup();
            for ( GroupKey groupKey : command.getMemberships() )
            {
                GroupEntity groupToJoin = groupDao.find( groupKey.toString() );
                boolean alreadyExists = userGroup.isMemberOf( groupToJoin, false );

                if ( groupToJoin != null && !alreadyExists )
                {
                    if ( command.isUpdateOpenGroupsOnly() && groupToJoin.isRestricted() )
                    {
                        throw new UserStoreAccessException( UserStoreAccessType.JOIN_GROUP, userToUpdate.getQualifiedName(), groupToJoin,
                                                            "Not allowed to add membership to a restricted group in this context" );
                    }
                }
            }

            for ( GroupEntity existingMembership : userGroup.getMemberships( false ) )
            {
                boolean removeThisGroup = !command.getMemberships().contains( existingMembership.getGroupKey() );

                if ( removeThisGroup )
                {
                    if ( command.isUpdateOpenGroupsOnly() && existingMembership.isRestricted() )
                    {
                        throw new UserStoreAccessException( UserStoreAccessType.LEAVE_GROUP, userToUpdate.getQualifiedName(),
                                                            existingMembership,
                                                            "Not allowed to remove membership from a restricted group in this context" );
                    }

                }
            }
        }
    }


    private void verifyUniqueEmailForUpdate( UpdateUserCommand command )
    {
        String email = command.getEmail();
        if ( email == null )
        {
            return;
        }

        UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );

        List<UserEntity> usersWithThisEmail = findUsersWithEmail( email, userToUpdate.getUserStoreKey() );

        if ( usersWithThisEmail.size() == 0 )
        {
            return;
        }

        boolean oneUserWithMatchingEmailFoundAndItsMe =
            usersWithThisEmail.size() == 1 && userToUpdate.equals( usersWithThisEmail.get( 0 ) );
        if ( oneUserWithMatchingEmailFoundAndItsMe )
        {
            return;
        }

        throw new UserStorageExistingEmailException( email, userToUpdate.getUserStore().getName() );
    }


    private List<UserEntity> findUsersWithEmail( String email, UserStoreKey userStoreKey )
    {
        UserSpecification userByEmailSpec = new UserSpecification();
        userByEmailSpec.setEmail( email );
        userByEmailSpec.setUserStoreKey( userStoreKey );
        userByEmailSpec.setDeletedStateNotDeleted();

        return userDao.findBySpecification( userByEmailSpec );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUser( final UpdateUserCommand command )
    {
        final UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );
        if ( userToUpdate == null )
        {
            throw new IllegalArgumentException( "User does not exists: " + command.getSpecification() );
        }
        final UserSpecification updaterSpec = new UserSpecification();
        updaterSpec.setKey( command.getUpdater() );
        updaterSpec.setDeletedStateNotDeleted();

        final UserEntity updater = userDao.findSingleBySpecification( updaterSpec );

        final UserStoreEntity userStore = userToUpdate.getUserStore();

        boolean hasUpdateAccessOnUser =
            userStoreAccessResolver.hasUpdateUserAccess( updater, userStore, command.allowUpdateSelf(), userToUpdate );

        if ( !hasUpdateAccessOnUser )
        {
            throw new UserStoreAccessException( UserStoreAccessType.UPDATE_USER, updater.getQualifiedName(),
                                                userToUpdate.getQualifiedName() );
        }

        Assert.isTrue( !userToUpdate.isBuiltIn(), "Cannot update a built-in user" );

        final UserStoreKey userStoreKey = userToUpdate.getUserStoreKey();

        final UserStoreConnector usc = doGetUSConnector( userStoreKey );

        if ( command.removePhoto() )
        {
            // Make sure a new photo isn't set
            command.getUserInfo().setPhoto( null );
        }
        else if ( command.getUserInfo().getPhoto() == null )
        {
            // No new photo found - use old photo
            command.getUserInfo().setPhoto( userToUpdate.getPhoto() );
        }

        verifyUpdateUserCommand( command );

        usc.updateUser( command );
    }


    private void verifyMandatoryFieldsForUpdate( final UpdateUserCommand command )
    {
        boolean allowNullsForMandatoryValues = command.getUpdateStrategy() == UpdateUserCommand.UpdateStrategy.REPLACE_NEW;

        String email = command.getEmail();

        boolean emailValid = allowNullsForMandatoryValues ? email == null || isValidEmailAddress( email ) : isValidEmailAddress( email );

        if ( !emailValid )
        {
            throw new UserStorageInvalidArgumentException( "email" );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUser( final DeleteUserCommand command )
    {
        final UserEntity deleter = userDao.findByKey( command.getDeleter() );
        final UserEntity userToDelete = userDao.findSingleBySpecification( command.getSpecification() );
        if ( userToDelete == null )
        {
            // User does not exist.
            return;
        }
        final UserStoreEntity userStore = userToDelete.getUserStore();

        if ( deleter.equals( userToDelete ) )
        {
            throw new UserStoreAccessException( UserStoreAccessType.DELETE_USER, deleter.getQualifiedName(),
                                                userToDelete.getQualifiedName(), "Cannot delete the deleter." );
        }

        if ( !userStoreAccessResolver.hasDeleteUserAccess( deleter, userStore ) )
        {
            throw new UserStoreAccessException( UserStoreAccessType.DELETE_USER, deleter.getQualifiedName(),
                                                userToDelete.getQualifiedName() );
        }

        final UserStoreConnector usc = doGetUSConnector( userStore.getKey() );
        usc.deleteUser( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUserStore( final DeleteUserStoreCommand command )
    {
        final UserStoreEntity userStoreToDelete = userStoreDao.findByKey( command.getKey() );

        if ( userStoreToDelete == null )
        {
            throw new IllegalArgumentException( "UserStore with key=" + command.getKey() + " not found" );
        }
        Assert.isTrue( !userStoreToDelete.isDefaultUserStore(), "Not allowed to delete the default UserStore" );

        final UserEntity deleter = userDao.findByKey( command.getDeleter() );

        if ( !userStoreAccessResolver.hasDeleteUserStoreAccess( deleter ) )
        {
            throw new UserStoreAccessException( UserStoreAccessType.DELETE_USERSTORE, deleter.getQualifiedName(),
                                                userStoreToDelete.getName() );
        }

        userStoreToDelete.setDeleted( true );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GroupKey storeNewGroup( final StoreNewGroupCommand command )
    {
        UserStoreEntity userStore = null;
        if ( command.getUserStoreKey() != null )
        {
            userStore = userStoreDao.findByKey( command.getUserStoreKey() );
        }

        final UserEntity creator = command.getExecutor();

        if ( !groupAccessResolver.hasCreateGroupAccess( creator, command.getType(), userStore ) )
        {
            if ( command.isRespondWithException() )
            {
                throw new CreateGroupAccessException( creator.getQualifiedName(), command.getType() );
            }
            else
            {
                return null;
            }
        }

        if ( command.getType().isGlobal() || command.getType().isBuiltIn() )
        {
            return groupStorageService.storeNewGroup( command );
        }
        else
        {
            final UserStoreConnector usc = doGetUSConnector( command.getUserStoreKey() );
            return usc.storeNewGroup( command );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserStoreKey storeNewUserStore( final StoreNewUserStoreCommand command )
    {
        final UserEntity storer = userDao.findByKey( command.getStorer() );

        if ( !userStoreAccessResolver.hasCreateUserStoreAccess( storer ) )
        {
            throw new UserStoreAccessException( UserStoreAccessType.CREATE_USERSTORE, storer.getQualifiedName(), command.getName() );
        }

        return doStoreNewUserStore( command );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void initializeUserStores()
    {
        List<UserStoreEntity> allUserStores = userStoreDao.findAll();
        if ( allUserStores.size() != 0 )
        {
            return;
        }

        StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        command.setConfig( UserStoreConfigParser.parse( null ) );
        command.setConnectorName( null );
        command.setDefaultStore( true );
        command.setName( "default" );
        command.setDefaultStore( true );
        doStoreNewUserStore( command );
    }

    private UserStoreKey doStoreNewUserStore( final StoreNewUserStoreCommand command )
    {
        Assert.isTrue( StringUtils.isNotEmpty( command.getName() ), "UserStore name is required" );

        final boolean newDefaultUserStore = command.isDefaultStore();

        UserStoreEntity currentDefault = null;

        if ( newDefaultUserStore )
        {
            currentDefault = userStoreDao.findDefaultUserStore();
        }

        final UserStoreEntity userStore = new UserStoreEntity();
        userStore.setName( command.getName() );
        userStore.setConnectorName( command.getConnectorName() );
        userStore.setDefaultStore( command.isDefaultStore() );
        userStore.setDeleted( false );
        userStore.setConfig( command.getConfig() );

        userStoreDao.store( userStore );

        if ( newDefaultUserStore && currentDefault != null )
        {
            currentDefault.setDefaultStore( false );
        }

        final StoreNewGroupCommand storeNewUserStoreAdminGroupCommand = new StoreNewGroupCommand();
        storeNewUserStoreAdminGroupCommand.setUserStoreKey( userStore.getKey() );
        storeNewUserStoreAdminGroupCommand.setName( GroupType.USERSTORE_ADMINS.getName() );
        storeNewUserStoreAdminGroupCommand.setType( GroupType.USERSTORE_ADMINS );
        storeNewUserStoreAdminGroupCommand.setRestriced( true );
        groupStorageService.storeNewGroup( storeNewUserStoreAdminGroupCommand );

        final StoreNewGroupCommand storeNewUserStoreAuthGroupCommand = new StoreNewGroupCommand();
        storeNewUserStoreAuthGroupCommand.setUserStoreKey( userStore.getKey() );
        storeNewUserStoreAuthGroupCommand.setName( GroupType.AUTHENTICATED_USERS.getName() );
        storeNewUserStoreAuthGroupCommand.setType( GroupType.AUTHENTICATED_USERS );
        storeNewUserStoreAuthGroupCommand.setRestriced( true );
        groupStorageService.storeNewGroup( storeNewUserStoreAuthGroupCommand );

        return userStore.getKey();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUserStore( final UpdateUserStoreCommand command )
    {
        if ( command == null )
        {
            throw new IllegalArgumentException( "command cannot be null" );
        }

        Assert.isTrue( command.getKey() != null, "UserStore key is required" );
        Assert.isTrue( StringUtils.isNotEmpty( command.getName() ), "UserStore name is required" );

        final UserEntity storer = userDao.findByKey( command.getUpdater() );

        final UserStoreEntity userStoreToUpdate = userStoreDao.findByKey( command.getKey() );
        if ( !userStoreAccessResolver.hasUpdateUserStoreAccess( storer ) )
        {
            throw new UserStoreAccessException( UserStoreAccessType.UPDATE_USERSTORE, storer.getQualifiedName(), command.getName() );
        }

        final boolean setAsNewDefaultStore = command.getAsNewDefaultStore();

        UserStoreEntity currentDefaultUserStore = null;

        if ( setAsNewDefaultStore )
        {
            currentDefaultUserStore = userStoreDao.findDefaultUserStore();
        }

        if ( userStoreToUpdate == null )
        {
            throw new IllegalArgumentException( "UserStore with key=" + command.getKey() + " not found" );
        }

        userStoreToUpdate.setName( command.getName() );
        userStoreToUpdate.setConnectorName( command.getConnectorName() );
        userStoreToUpdate.setDeleted( command.isDeleted() );
        if ( setAsNewDefaultStore )
        {
            userStoreToUpdate.setDefaultStore( true );
        }

        if ( setAsNewDefaultStore && currentDefaultUserStore != null )
        {
            currentDefaultUserStore.setDefaultStore( false );
        }

        userStoreToUpdate.setConfig( command.getConfig() );

        userStoreDao.getHibernateTemplate().flush();
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateGroup( final UpdateGroupCommand command )
    {
        final GroupEntity groupToBeUpdated = groupDao.findByKey( command.getGroupKey() );
        final UserEntity updater = userDao.findByKey( command.getUpdater() );

        if ( !groupAccessResolver.hasUpdateGroupAccess( updater, groupToBeUpdated ) )
        {
            throw new UpdateGroupAccessException( updater.getQualifiedName(), groupToBeUpdated.getQualifiedName() );
        }

        if ( groupToBeUpdated.isGlobal() || groupToBeUpdated.isBuiltIn() )
        {
            groupStorageService.updateGroup( command );
        }
        else
        {
            final UserStoreConnector usc = doGetUSConnector( groupToBeUpdated.getUserStore().getKey() );
            usc.updateGroup( command );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<GroupEntity> addMembershipsToGroup( final AddMembershipsCommand command )
    {
        final List<GroupEntity> groupsAddedTo = new ArrayList<GroupEntity>();
        final UserEntity executor = userDao.findByKey( command.getExecutor() );
        final GroupEntity groupToAdd = groupDao.findSingleBySpecification( command.getGroupToAdd() );

        for ( final GroupKey groupToAddToKey : command.getGroupsToAddTo() )
        {
            final GroupEntity groupToAddTo = groupDao.findByKey( groupToAddToKey );
            if ( command.isUpdateOpenGroupsOnly() && groupToAddTo.isRestricted() )
            {
                if ( command.isRespondWithException() )
                {
                    throw new UserStoreAccessException( UserStoreAccessType.JOIN_GROUP, executor.getQualifiedName(), groupToAddTo,
                                                        "Not allowed to add membership to a restricted group in this context" );
                }
                else
                {
                    continue;
                }
            }

            if ( groupAccessResolver.hasAddMembershipAccess( executor, groupToAdd, groupToAddTo ) )
            {
                if ( groupToAddTo.isGlobal() )
                {
                    groupStorageService.addMembershipToGroup( groupToAdd, groupToAddTo );
                    groupsAddedTo.add( groupToAddTo );
                }
                else
                {
                    final UserStoreConnector usc = doGetUSConnector( groupToAddTo.getUserStore().getKey() );
                    usc.addMembershipToGroup( groupToAdd, groupToAddTo );
                    groupsAddedTo.add( groupToAddTo );
                }
            }
            else
            {
                if ( command.isRespondWithException() )
                {
                    throw new UserStoreAccessException( UserStoreAccessType.JOIN_GROUP, executor.getQualifiedName(), groupToAddTo );
                }
            }
        }

        return groupsAddedTo;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<GroupEntity> removeMembershipsFromGroup( final RemoveMembershipsCommand command )
    {
        final List<GroupEntity> groupsRemovedFrom = new ArrayList<GroupEntity>();
        final UserEntity executor = userDao.findByKey( command.getExecutor() );
        final GroupEntity groupToRemove = groupDao.findSingleBySpecification( command.getGroupToRemove() );

        for ( final GroupKey groupToRemoveFromKey : command.getGroupsToRemoveFrom() )
        {
            final GroupEntity groupToRemoveFrom = groupDao.findByKey( groupToRemoveFromKey );
            if ( command.isUpdateOpenGroupsOnly() && groupToRemoveFrom.isRestricted() )
            {
                if ( command.isRespondWithException() )
                {
                    throw new UserStoreAccessException( UserStoreAccessType.LEAVE_GROUP, executor.getQualifiedName(), groupToRemoveFrom,
                                                        "Not allowed to remove membership from a restricted group in this context" );
                }
                else
                {
                    continue;
                }
            }

            if ( groupAccessResolver.hasRemoveMembershipAccess( executor, groupToRemove, groupToRemoveFrom ) )
            {
                if ( groupToRemoveFrom.isGlobal() )
                {
                    groupStorageService.removeMembershipFromGroup( groupToRemove, groupToRemoveFrom );
                    groupsRemovedFrom.add( groupToRemoveFrom );
                }
                else
                {
                    final UserStoreConnector usc = doGetUSConnector( groupToRemoveFrom.getUserStore().getKey() );
                    usc.removeMembershipFromGroup( groupToRemove, groupToRemoveFrom );
                    groupsRemovedFrom.add( groupToRemoveFrom );
                }
            }
            else
            {
                if ( command.isRespondWithException() )
                {
                    throw new UserStoreAccessException( UserStoreAccessType.LEAVE_GROUP, executor.getQualifiedName(), groupToRemoveFrom );
                }
            }
        }

        return groupsRemovedFrom;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteGroup( final DeleteGroupCommand command )
    {
        final UserEntity deleter = command.getDeleter();

        final GroupEntity groupToDelete = groupDao.findSingleBySpecification( command.getSpecification() );

        Assert.isTrue( !groupToDelete.isBuiltIn(), "Cannot delete a built-in group" );

        if ( !groupAccessResolver.hasDeleteGroupAccess( deleter, groupToDelete ) )
        {
            if ( command.isRespondWithException() )
            {
                throw new DeleteGroupAccessException( deleter.getQualifiedName(), groupToDelete.getQualifiedName() );
            }
            else
            {
                return;
            }
        }

        if ( groupToDelete.isGlobal() )
        {
            groupStorageService.deleteGroup( command );
        }
        else
        {
            final UserStoreEntity userStore = groupToDelete.getUserStore();

            final UserStoreKey userStoreKey = userStore.getKey();
            final UserStoreConnector usc = doGetUSConnector( userStoreKey );
            usc.deleteGroup( command );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void authenticateUser( final UserStoreKey userStoreKey, final String uid, final String password )
    {
        doGetUSConnector( userStoreKey ).authenticateUser( uid, password );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void changePassword( final UserStoreKey userStoreKey, final String uid, final String newPassword )
    {
        if ( UserEntity.isBuiltInUser( uid ) )
        {
            throw new IllegalArgumentException( "Cannot change password for built in user: " + uid );
        }
        final UserStoreConnector usc = doGetUSConnector( userStoreKey );
        usc.changePassword( uid, newPassword );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserEntity synchronizeUser( final UserSpecification userSpec )
    {
        final UserEntity user = userDao.findSingleBySpecification( userSpec );
        if ( user.isBuiltIn() )
        {
            return user;
        }

        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( user.getUserStore().getKey() );
        if ( rusc != null )
        {
            rusc.synchronizeUser( user, true );
        }
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserKey synchronizeUser( final UserStoreKey userStoreKey, final String uid )
        throws UserNotFoundException
    {
        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );

        if ( rusc != null )
        {
            RemoteUser remoteUser = rusc.getRemoteUser( uid );

            final UserSpecification spec = new UserSpecification();
            spec.setDeletedState( UserSpecification.DeletedState.ANY );
            spec.setUserStoreKey( userStoreKey );
            spec.setName( uid );
            spec.setSyncValue( remoteUser.getSync() );
            UserEntity user = userDao.findSingleBySpecification( spec );
            if ( user == null )
            {
                rusc.createUserNotExistingLocally( uid );
                user = userDao.findSingleBySpecification( spec );
            }

            if ( user.isBuiltIn() )
            {
                throw new IllegalArgumentException( "Cannot synchronize built-in user: " + user.getQualifiedName() );
            }

            rusc.synchronizeUser( user, true );
            return user.getKey();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void synchronizeUsers( final SynchronizeStatus status, final UserStoreKey userStoreKey, final List<RemoteUser> remoteUsers,
                                  final boolean syncMemberships, final MemberCache memberCache )
    {
        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );
        if ( rusc != null )
        {
            rusc.synchronizeUsers( status, remoteUsers, syncMemberships, memberCache );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void synchronizeUserMemberships( final SynchronizeStatus status, final UserStoreKey userStoreKey, final RemoteUser remoteUser,
                                            final MemberCache memberCache )
    {
        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );
        if ( rusc != null && rusc.canReadGroup() )
        {
            rusc.synchronizeUserMemberships( status, remoteUser, memberCache );
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUsersLocally( final LocalUsersStatus status, final List<UserKey> users )
    {
        for ( final UserKey userKey : users )
        {
            final UserSpecification userToDeleteSpec = new UserSpecification();
            userToDeleteSpec.setKey( userKey );
            userToDeleteSpec.setDeletedState( UserSpecification.DeletedState.ANY );
            userStorageService.deleteUser( userToDeleteSpec );
            status.deleted();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public GroupEntity synchronizeGroup( final GroupKey groupKey )
    {
        final GroupEntity group = groupDao.findByKey( groupKey );
        if ( group.isBuiltIn() )
        {
            throw new IllegalArgumentException( "Cannot synchronize built-in group: " + group.getQualifiedName() );
        }
        if ( group.getUserStore() == null )
        {
            throw new IllegalArgumentException( "Can only synchronize groups that belongs to a userstore: " + group.getQualifiedName() );
        }

        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( group.getUserStore().getKey() );
        if ( rusc != null && rusc.canReadGroup() )
        {
            rusc.synchronizeGroup( group, true, false );
        }
        return group;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void synchronizeGroups( final SynchronizeStatus status, final UserStoreKey userStoreKey, final List<RemoteGroup> remoteGroups,
                                   final boolean syncMemberships, final boolean syncMembers, final MemberCache memberCache )
    {
        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );
        if ( rusc != null )
        {
            rusc.synchronizeGroups( status, remoteGroups, syncMemberships, syncMembers, memberCache );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void synchronizeGroupMemberships( final SynchronizeStatus status, final UserStoreKey userStoreKey, final RemoteGroup remoteGroup,
                                             final MemberCache memberCache )
    {
        final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );
        if ( rusc != null )
        {
            rusc.synchronizeGroupMemberships( status, remoteGroup, memberCache );
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteGroupsLocally( final LocalGroupsStatus status, final List<GroupKey> groups )
    {
        for ( final GroupKey groupKey : groups )
        {
            final GroupSpecification groupToDeleteSpec = new GroupSpecification();
            groupToDeleteSpec.setKey( groupKey );
            final DeleteGroupCommand command = new DeleteGroupCommand( null, groupToDeleteSpec );
            groupStorageService.deleteGroup( command );
            status.deleted();
        }
    }

    public User getUserByKey( final UserKey userKey )
    {
        Preconditions.checkNotNull( userKey, "Given userKey cannot be null" );

        final UserEntity userEntity = userDao.findByKey( userKey );

        Preconditions.checkNotNull( userEntity, "User does not exist in database: " + userKey );

        if ( userEntity.isBuiltIn() )
        {
            UserImpl user = UserImpl.createFrom( userEntity );
            if ( user.isEnterpriseAdmin() )
            {
                // hente email fra cms.properties
                user.setEmail( verticalProperties.getAdminEmail() );
            }
            return user;
        }

        final UserStoreConnector usc = doGetUSConnector( userEntity.getUserStore().getKey() );
        return usc.getUserByEntity( userEntity );
    }

    public boolean canSynchronizeUsers( final UserStoreKey userStoreKey )
    {
        try
        {
            final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );
            if ( rusc != null )
            {
                return true;
            }
            return false;
        }
        catch ( final InvalidUserStoreConfigException ex )
        {
            return false;
        }
    }

    public boolean canSynchronizeGroups( final UserStoreKey userStoreKey )
    {
        try
        {
            final RemoteUserStoreConnector rusc = doGetRemoteUSConnector( userStoreKey );
            if ( rusc != null )
            {
                return rusc.canReadGroup();
            }
            return false;
        }
        catch ( final InvalidUserStoreConfigException ex )
        {
            return false;
        }
    }

    public UserStoreEntity getDefaultUserStore()
    {
        UserStoreEntity userStore = userStoreDao.findDefaultUserStore();
        if ( userStore != null )
        {
            return userStore;
        }

        throw new IllegalStateException( "Default userstore is not set" );
    }

    public Map<String, UserStoreConnectorConfig> getUserStoreConnectorConfigs()
    {
        return userConnectorStoreManager.getUserStoreConnectorConfigs();
    }

    private List<UserEntity> doGetUsers( final UserStoreKey userStoreKey )
    {
        final UserSpecification userSpec = new UserSpecification();
        userSpec.setUserStoreKey( userStoreKey );
        userSpec.setDeletedStateNotDeleted();
        return userDao.findBySpecification( userSpec );
    }

    public List<UserEntity> getUsers( final UserStoreKey userStoreKey )
    {
        return doGetUsers( userStoreKey );
    }

    public Multimap<String, UserEntity> getUsersAsMapByName( final UserStoreKey userStoreKey )
    {
        final List<UserEntity> users = doGetUsers( userStoreKey );
        final Multimap<String, UserEntity> userMapByName = HashMultimap.create();
        for ( final UserEntity user : users )
        {
            userMapByName.put( user.getName(), user );
        }
        return userMapByName;
    }

    public List<GroupEntity> getGroups( GroupSpecification groupSpec )
    {
        return groupDao.findBySpecification( groupSpec );
    }

    public void verifyUserStoreConnector( final String connectorName )
    {
        getRemoteUserStorePlugin( connectorName );
    }

    private RemoteUserStorePlugin getRemoteUserStorePlugin( final String connectorName )
    {
        final UserStoreConnectorConfig connectorConfig = userConnectorStoreManager.getUserStoreConnectorConfig( connectorName );
        final String connectorType = connectorConfig.getPluginType();
        final Properties pluginProperties = connectorConfig.getPluginProperties();

        final RemoteUserStorePlugin remoteUserStorePlugin = remoteUserStoreFactory.create( connectorType, pluginProperties );
        remoteUserStorePlugin.initialize();

        return remoteUserStorePlugin;
    }

    public void verifyUserStoreConnectorConfig( final UserStoreConfig config, final String connectorName )
    {
        final RemoteUserStorePlugin remoteUserStorePlugin = getRemoteUserStorePlugin( connectorName );
        final Set<UserFieldType> supportedTypes = remoteUserStorePlugin.getSupportedFieldTypes();
        for ( final UserStoreUserFieldConfig userFieldConfig : config.getRemoteOnlyUserFieldConfigs() )
        {
            if ( !supportedTypes.contains( userFieldConfig.getType() ) )
            {
                throw new InvalidUserStoreConfigException(
                    "Remote plugin '" + connectorName + "' does not support type: " + userFieldConfig.getType().getName() );
            }
        }
    }

    public boolean canCreateUser( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canCreateUser();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canUpdateUser( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canUpdateUser();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canUpdateUserPassword( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canUpdateUserPassword();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canDeleteUser( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canDeleteUser();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canCreateGroup( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canCreateGroup();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canReadGroup( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canReadGroup();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canUpdateGroup( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canUpdateGroup();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    public boolean canDeleteGroup( final UserStoreKey userStoreKey )
    {
        try
        {
            return doGetUSConnector( userStoreKey ).canDeleteGroup();
        }
        catch ( InvalidUserStoreConfigException e )
        {
            return false;
        }
    }

    private UserStoreConnector doGetUSConnector( final UserStoreKey userStoreKey )
    {
        return userConnectorStoreManager.getUserStoreConnector( userStoreKey );
    }

    private RemoteUserStoreConnector doGetRemoteUSConnector( final UserStoreKey userStoreKey )
    {
        return userConnectorStoreManager.getRemoteUserStoreConnector( userStoreKey );
    }
}
