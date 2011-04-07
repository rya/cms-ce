/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.store.dao.CategoryAccessDao;
import com.enonic.cms.store.dao.ContentAccessDao;
import com.enonic.cms.store.dao.DefaultSiteAccessDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemAccessDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.DisplayNameResolver;
import com.enonic.cms.domain.security.user.StoreNewUserCommand;
import com.enonic.cms.domain.security.user.UpdateUserCommand;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserNotFoundException;
import com.enonic.cms.domain.security.user.UserSpecification;
import com.enonic.cms.domain.security.user.UserType;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.user.UserInfo;

/**
 * Jun 24, 2009
 */
public class UserStorageService
{
    private UserStoreDao userStoreDao;

    private GroupDao groupDao;

    private UserDao userDao;

    private TimeService timeService;

    private MenuItemAccessDao menuItemAccessDao;

    private CategoryAccessDao categoryAccessDao;

    private ContentAccessDao contentAccessDao;

    private DefaultSiteAccessDao defaultSiteAccessDao;

    public UserKey storeNewUser( StoreNewUserCommand command, DisplayNameResolver displayNameResolver )
    {
        UserStoreEntity userStore = userStoreDao.findByKey( command.getUserStoreKey() );

        final UserEntity newUser = new UserEntity();
        newUser.setDeleted( 0 );
        newUser.setDisplayName( command.getDisplayName() );
        newUser.setType( command.getType() );
        newUser.setTimestamp( timeService.getNowAsDateTime() );
        newUser.setName( command.getUsername() );
        newUser.setUserStore( userStore );
        newUser.encodePassword( command.getPassword() );
        final String syncValue = command.getSyncValue();
        newUser.setSyncValue( syncValue == null ? "NA" : syncValue );
        newUser.setEmail( command.getEmail() );
        newUser.updateUserInfo( command.getUserInfo() );

        if ( command.getDisplayName() == null ||
            !displayNameManuallyEdited( displayNameResolver, command.getUsername(), command.getDisplayName(), command.getUserInfo() ) )
        {
            newUser.setDisplayName(
                displayNameResolver.resolveDisplayName( newUser.getName(), newUser.getDisplayName(), newUser.getUserInfo() ) );
        }

        userDao.storeNew( newUser );

        if ( command.getType() == UserType.ANONYMOUS )
        {
            GroupEntity anonymousUserGroup = groupDao.findSingleByGroupType( GroupType.ANONYMOUS );
            anonymousUserGroup.setUser( newUser );
            newUser.setUserGroup( anonymousUserGroup );
        }
        else if ( command.getType() == UserType.ADMINISTRATOR )
        {
            GroupEntity eaUserGroup = groupDao.findSingleByGroupType( GroupType.ENTERPRISE_ADMINS );
            eaUserGroup.setUser( newUser );
            newUser.setUserGroup( eaUserGroup );
        }
        else
        {
            GroupEntity newUserGroup = new GroupEntity();
            newUserGroup.setDeleted( 0 );
            newUserGroup.setDescription( null );
            newUserGroup.setName( "userGroup" + newUser.getKey() );
            newUserGroup.setSyncValue( newUser.getSync() );
            newUserGroup.setUser( newUser );
            newUserGroup.setUserStore( userStore );
            newUserGroup.setType( GroupType.USER );
            newUserGroup.setRestricted( 1 );
            groupDao.storeNew( newUserGroup );
            newUser.setUserGroup( newUserGroup );

            if ( command.getMemberships() != null )
            {
                for ( GroupKey groupKey : command.getMemberships() )
                {
                    GroupEntity group = groupDao.find( groupKey.toString() );
                    if ( group != null )
                    {
                        if ( group.getUserStore() != null && !newUserGroup.getUserStore().equals( group.getUserStore() ) )
                        {
                            throw new IllegalArgumentException(
                                newUser.getQualifiedName() + " cannot be member of group " + group.getQualifiedName() +
                                    ". The user is not located in the same userstore as the group." );
                        }
                        newUserGroup.addMembership( group );
                    }
                }
            }
        }

        return newUser.getKey();
    }

    public void updateUser( final UpdateUserCommand command )
    {
        UserEntity userToUpdate = userDao.findSingleBySpecification( command.getSpecification() );

        if ( userToUpdate == null )
        {
            throw new UserNotFoundException( command.getSpecification() );
        }

        boolean modified = updateUserModifyableValues( command, userToUpdate );
        if ( modified )
        {
            userToUpdate.setTimestamp( timeService.getNowAsDateTime() );
        }

        if ( command.syncMemberships() )
        {
            addGroupsFromCommand( command, userToUpdate );
            removeGroupsNotInCommand( command, userToUpdate );
        }
    }

    private void addGroupsFromCommand( final UpdateUserCommand command, final UserEntity userToUpdate )
    {
        GroupEntity userGroup = userToUpdate.getUserGroup();

        for ( GroupKey groupKey : command.getMemberships() )
        {
            GroupEntity groupToAdd = groupDao.find( groupKey.toString() );

            boolean alreadyExists = userGroup.isMemberOf( groupToAdd, false );

            if ( groupToAdd != null && !alreadyExists )
            {
                if ( groupToAdd.getUserStore() != null && !userToUpdate.getUserStore().equals( groupToAdd.getUserStore() ) )
                {
                    throw new IllegalArgumentException(
                        userToUpdate.getQualifiedName() + " cannot be member of group " + groupToAdd.getQualifiedName() +
                            ". The user is not located in the same userstore as the group." );
                }

                userGroup.addMembership( groupToAdd );
            }
        }
    }

    private void removeGroupsNotInCommand( UpdateUserCommand command, UserEntity userToUpdate )
    {
        GroupEntity userGroup = userToUpdate.getUserGroup();

        List<GroupEntity> groupsToRemove = new ArrayList<GroupEntity>();

        for ( GroupEntity existingMembership : userGroup.getMemberships( false ) )
        {
            boolean removeThisGroup = !command.getMemberships().contains( existingMembership.getGroupKey() );

            if ( removeThisGroup )
            {
                groupsToRemove.add( existingMembership );
            }
        }

        for ( GroupEntity groupToRemove : groupsToRemove )
        {
            userGroup.removeMembership( groupToRemove );
        }
    }

    private boolean updateUserModifyableValues( UpdateUserCommand command, UserEntity userToUpdate )
    {
        boolean replaceAll = command.getUpdateStrategy().equals( UpdateUserCommand.UpdateStrategy.REPLACE_ALL );
        boolean modified = false;

        final String displayName = command.getDisplayName();
        if ( displayName != null || replaceAll )
        {
            modified |= equals( userToUpdate.getDisplayName(), displayName );
            userToUpdate.setDisplayName( displayName );
        }

        final String email = command.getEmail();
        if ( email != null || replaceAll )
        {
            modified |= equals( userToUpdate.getEmail(), email );
            userToUpdate.setEmail( email );
        }

        UserInfo userInfo = command.getUserInfo();
        if ( userInfo == null && replaceAll )
        {
            userInfo = new UserInfo();
        }

        if ( userInfo != null )
        {
            boolean userInfoModified;

            if ( replaceAll )
            {
                userInfoModified = userToUpdate.updateUserInfo( userInfo );
            }
            else
            {
                userInfoModified = userToUpdate.updateUserInfoNewOnly( userInfo );
            }

            modified = modified || userInfoModified;
        }
        return modified;
    }

    public void deleteUser( final UserSpecification userSpec )
    {
        final UserEntity userToDelete = userDao.findSingleBySpecification( userSpec );

        if ( userToDelete == null )
        {
            return;
        }

        Preconditions.checkArgument( !userToDelete.isBuiltIn(), "Cannot delete a built-in user" );

        userToDelete.setDeleted( true );
        userToDelete.setTimestamp( timeService.getNowAsDateTime() );

        final GroupEntity userGroup = userToDelete.getUserGroup();
        if ( userGroup != null )
        {
            userGroup.setDeleted( true );
            final GroupKey groupKey = userGroup.getGroupKey();
            defaultSiteAccessDao.deleteByGroupKey( groupKey );
            menuItemAccessDao.deleteByGroupKey( groupKey );
            contentAccessDao.deleteByGroupKey( groupKey );
            categoryAccessDao.deleteByGroupKey( groupKey );
        }
    }

    public void changePassword( UserStoreKey userStoreKey, final String uid, final String newPassword )
    {
        UserEntity user = userDao.findByUserStoreKeyAndUsername( userStoreKey, uid );
        user.encodePassword( newPassword );
    }

    private boolean equals( Object a, Object b )
    {
        if ( a == null && b == null )
        {
            return true;
        }
        else if ( a == null || b == null )
        {
            return false;
        }
        return a.equals( b );
    }

    private boolean displayNameManuallyEdited( final DisplayNameResolver displayNameResolver, String userName, String displayName,
                                               UserInfo userInfo )
    {
        final String displayNameGeneratedFromExistingUser = displayNameResolver.resolveDisplayName( userName, displayName, userInfo );

        return !displayNameGeneratedFromExistingUser.equals( displayName );
    }

    @Autowired
    public void setUserStoreDao( UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public void setMenuItemAccessDao( MenuItemAccessDao menuItemAccessDao )
    {
        this.menuItemAccessDao = menuItemAccessDao;
    }

    @Autowired
    public void setCategoryAccessDao( CategoryAccessDao categoryAccessDao )
    {
        this.categoryAccessDao = categoryAccessDao;
    }

    @Autowired
    public void setContentAccessDao( ContentAccessDao contentAccessDao )
    {
        this.contentAccessDao = contentAccessDao;
    }

    @Autowired
    public void setDefaultSiteAccessDao( DefaultSiteAccessDao defaultSiteAccessDao )
    {
        this.defaultSiteAccessDao = defaultSiteAccessDao;
    }
}
