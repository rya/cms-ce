/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.core.security.userstore.UserStorageService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import com.enonic.cms.core.security.group.GroupStorageService;

import com.enonic.cms.domain.security.group.DeleteGroupCommand;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.StoreNewGroupCommand;
import com.enonic.cms.domain.security.group.UpdateGroupCommand;
import com.enonic.cms.domain.security.user.DeleteUserCommand;
import com.enonic.cms.domain.security.user.DisplayNameResolver;
import com.enonic.cms.domain.security.user.StoreNewUserCommand;
import com.enonic.cms.domain.security.user.UpdateUserCommand;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserSpecification;
import com.enonic.cms.domain.security.user.UsernameResolver;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

/**
 * Jun 25, 2009
 */
public abstract class AbstractBaseUserStoreConnector
{
    protected final UserStoreKey userStoreKey;

    protected final String userStoreName;

    protected final String connectorName;

    protected UserDao userDao;

    protected GroupDao groupDao;

    protected UserStoreDao userStoreDao;

    protected GroupStorageService groupStorageService;

    protected UserStorageService userStorageService;

    protected abstract boolean isUsernameUnique( String username );

    protected AbstractBaseUserStoreConnector( UserStoreKey userStoreKey, String userStoreName, String connectorName )
    {
        this.userStoreKey = userStoreKey;
        this.userStoreName = userStoreName;
        this.connectorName = connectorName;
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    protected UserStoreEntity getUserStore()
    {
        return userStoreDao.findByKey( userStoreKey );
    }

    protected void ensureValidUserName( final StoreNewUserCommand command )
    {
        boolean usernameProvided = StringUtils.isNotBlank( command.getUsername() );
        if ( usernameProvided )
        {
            return;
        }

        String resolvedUsername = new UsernameResolver( getUserStore().getConfig() ).resolveUsername( command );

        String createdUniqueUsername = getUniqueUsername( resolvedUsername );

        command.setUsername( createdUniqueUsername );
    }

    private String getUniqueUsername( String suggestedUsername )
    {
        Assert.isTrue( StringUtils.isNotBlank( suggestedUsername ) );

        suggestedUsername = UserHandler.latinToAZ( suggestedUsername ).toLowerCase();
        suggestedUsername.replaceAll( "\\s+", "" );

        int i = 0;

        String baseName = suggestedUsername;

        while ( true )
        {
            if ( isUsernameUnique( suggestedUsername ) )
            {
                return suggestedUsername;
            }
            else
            {
                i++;
                suggestedUsername = baseName + i;
            }

            Assert.isTrue( i < 100, "Not able to resolve user name within 100 attempts to create unique" );
        }
    }

    protected UserEntity getLocalUserWithUsername( String userName )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setUserStoreKey( userStoreKey );
        userSpec.setName( userName );
        userSpec.setDeletedStateNotDeleted();

        return userDao.findSingleBySpecification( userSpec );
    }

    protected UserKey storeNewUserLocally( StoreNewUserCommand command, DisplayNameResolver displayNameResolver )
    {
        return userStorageService.storeNewUser( command, displayNameResolver );
    }

    protected void updateUserLocally( UpdateUserCommand command )
    {
        userStorageService.updateUser( command );
    }

    protected void deleteUserLocally( DeleteUserCommand command )
    {
        userStorageService.deleteUser( command.getSpecification() );
    }

    protected GroupKey storeNewGroupLocally( StoreNewGroupCommand command )
    {
        return groupStorageService.storeNewGroup( command );
    }

    protected void updateGroupLocally( UpdateGroupCommand command )
    {
        groupStorageService.updateGroup( command );
    }

    protected void removeMembershipFromGroupLocally( GroupEntity groupToRemove, GroupEntity groupToRemoveFrom )
    {
        groupStorageService.removeMembershipFromGroup( groupToRemove, groupToRemoveFrom );
    }

    protected void addMembershipToGroupLocally( GroupEntity groupToAdd, GroupEntity groupToAddTo )
    {
        groupStorageService.addMembershipToGroup( groupToAdd, groupToAddTo );
    }

    protected void deleteGroupLocally( DeleteGroupCommand command )
    {
        groupStorageService.deleteGroup( command );
    }

    public void setUserDao( UserDao value )
    {
        this.userDao = value;
    }

    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    public void setGroupStorageService( GroupStorageService value )
    {
        this.groupStorageService = value;
    }

    public void setUserStorageService( UserStorageService value )
    {
        this.userStorageService = value;
    }

    public void setUserStoreDao( UserStoreDao value )
    {
        this.userStoreDao = value;
    }
}
