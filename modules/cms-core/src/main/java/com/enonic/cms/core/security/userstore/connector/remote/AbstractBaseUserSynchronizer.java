/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import org.apache.commons.lang.StringUtils;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.security.userstore.connector.remote.plugin.RemoteUserStorePlugin;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.security.userstore.UserStorageService;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;

import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserInfoTransformer;
import com.enonic.cms.domain.user.remote.RemoteGroup;
import com.enonic.cms.domain.user.remote.RemoteUser;

public abstract class AbstractBaseUserSynchronizer
{
    protected UserStorageService userStorageService;

    protected final UserStoreEntity userStore;

    protected final boolean syncMemberships;

    protected RemoteUserStorePlugin remoteUserStorePlugin;

    protected UserDao userDao;

    protected GroupDao groupDao;

    protected TimeService timeService;

    protected UserStoreConnectorConfig connectorConfig;

    protected UserStoreConfig userStoreConfig;

    protected SynchronizeStatus status = new SynchronizeStatus( SynchronizeUserStoreType.USERS_ONLY );

    protected final boolean syncUser;

    public void setStatusCollector( final SynchronizeStatus value )
    {
        status = value;
    }

    protected AbstractBaseUserSynchronizer( final UserStoreEntity userStore, final boolean syncUser, final boolean syncMemberships )
    {
        this.userStore = userStore;
        this.syncMemberships = syncMemberships;
        this.userStoreConfig = userStore.getConfig();
        this.syncUser = syncUser;
    }

    protected UserStoreKey getUserStoreKey()
    {
        return userStore.getKey();
    }

    protected boolean updateAndResurrectLocalUser( final UserEntity localUser, final RemoteUser remoteUser, final MemberCache memberCache )
    {
        boolean resurrected = false;
        boolean modified = false;
        // force resurrection
        if ( localUser.isDeleted() )
        {
            resurrected = true;

            localUser.setDeleted( false );
            if ( localUser.getUserGroup() != null )
            {
                localUser.getUserGroup().setDeleted( 0 );
            }
            modified = true;
        }

        if ( updateUserModifyableProperties( localUser, remoteUser ) )
        {
            modified = true;
        }

        if ( modified )
        {
            localUser.setTimestamp( timeService.getNowAsDateTime() );
        }
        return resurrected;
    }

    protected String getNameToVerify( final UserEntity localUser, final RemoteUser remoteUser )
    {
        final String remoteName = remoteUser != null ? remoteUser.getId() : null;
        if ( StringUtils.isNotBlank( remoteName ) )
        {
            return remoteName;
        }
        final String localName = localUser != null ? localUser.getName() : null;
        if ( StringUtils.isNotBlank( localName ) )
        {
            return localName;
        }
        return null;
    }

    protected String getEmailToVerify( final UserEntity localUser, final RemoteUser remoteUser )
    {
        final String remoteEmail = remoteUser != null ? remoteUser.getEmail() : null;
        if ( StringUtils.isNotBlank( remoteEmail ) )
        {
            return remoteEmail;
        }
        final String localEmail = localUser != null ? localUser.getEmail() : null;
        if ( StringUtils.isNotBlank( localEmail ) )
        {
            return localEmail;
        }
        return null;
    }

    protected boolean nameAlreadyUsedByOtherUser( final UserStoreKey userStoreKey, final String name, final UserEntity localUser )
    {
        if ( name == null )
        {
            return false;
        }
        final UserSpecification userByEmailSpec = new UserSpecification();
        userByEmailSpec.setName( name );
        userByEmailSpec.setUserStoreKey( userStoreKey );
        userByEmailSpec.setDeletedStateNotDeleted();

        return otherThanMeFound( userByEmailSpec, localUser );
    }

    protected boolean emailAlreadyUsedByOtherUser( final UserStoreKey userStoreKey, final String email, final UserEntity localUser )
    {
        if ( email == null )
        {
            return false;
        }

        final UserSpecification userByEmailSpec = new UserSpecification();
        userByEmailSpec.setEmail( email );
        userByEmailSpec.setUserStoreKey( userStoreKey );
        userByEmailSpec.setDeletedStateNotDeleted();

        return otherThanMeFound( userByEmailSpec, localUser );
    }

    private boolean otherThanMeFound( final UserSpecification specification, final UserEntity me )
    {
        final List<UserEntity> users = userDao.findBySpecification( specification );

        if ( me != null )
        {
            final boolean oneEntityFoundAndItsMe = users.size() == 1 && me.equals( users.get( 0 ) );
            if ( oneEntityFoundAndItsMe )
            {
                return false;
            }
        }
        return users.size() > 0;
    }

    private boolean updateUserModifyableProperties( final UserEntity localUser, final RemoteUser remoteUser )
    {
        boolean modified = false;

        if ( !equals( localUser.getEmail(), remoteUser.getEmail() ) )
        {
            localUser.setEmail( remoteUser.getEmail() );
            modified = true;
        }

        final DisplayNameResolver displayNameResolver = new DisplayNameResolver( userStoreConfig );
        final boolean displayNameManuallyEdited = displayNameManuallyEdited( displayNameResolver, localUser );

        final UserFieldMap userFieldMap = remoteUser.getUserFields();
        userFieldMap.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );

        final UserInfo userInfo = localUser.getUserInfo();
        final UserInfoTransformer transformer = new UserInfoTransformer();
        transformer.updateUserInfo( userInfo, userFieldMap );
        final boolean modifiedUserFields = localUser.updateUserInfo( userInfo );

        if ( !displayNameManuallyEdited )
        {
            localUser.setDisplayName(
                displayNameResolver.resolveDisplayName( localUser.getName(), localUser.getDisplayName(), localUser.getUserInfo() ) );
        }

        modified |= modifiedUserFields;

        return modified;
    }

    private boolean displayNameManuallyEdited( final DisplayNameResolver displayNameResolver, UserEntity user )
    {
        final String displayNameGeneratedFromExistingUser =
            displayNameResolver.resolveDisplayName( user.getName(), user.getDisplayName(), user.getUserInfo() );

        final String existingDisplayName = user.getDisplayName();

        return !displayNameGeneratedFromExistingUser.equals( existingDisplayName );
    }

    protected void syncUserMemberships( final UserEntity localUser, final RemoteUser remoteUser, final MemberCache memberCache )
    {
        final List<RemoteGroup> remoteMemberships = remoteUserStorePlugin.getMemberships( remoteUser );

        removeLocalUserMembershipsNotExistingRemote( localUser, remoteMemberships );

        final GroupEntity userGroup = localUser.getUserGroup();

        for ( final RemoteGroup remoteMembership : remoteMemberships )
        {
            syncGroupMembershipOfTypeGroup( userGroup, remoteMembership, memberCache );
        }

    }

    private void syncGroupMembershipOfTypeGroup( final GroupEntity localGroup, final RemoteGroup remoteGroupMember,
                                                 final MemberCache memberCache )
    {
        final GroupSpecification spec = new GroupSpecification();
        spec.setUserStoreKey( getUserStoreKey() );
        spec.setName( remoteGroupMember.getId() );
        spec.setSyncValue( remoteGroupMember.getSync() );

        GroupEntity existingMember = memberCache.getMemberOfTypeGroup( spec );
        if ( existingMember == null )
        {
            existingMember = groupDao.findSingleBySpecification( spec );
            if ( existingMember != null )
            {
                memberCache.addMemeberOfTypeGroup( existingMember );
            }
        }

        if ( existingMember == null )
        {
            // skip creation - only supported in full sync
        }
        else
        {
            if ( localGroup.hasMembership( existingMember ) )
            {
                // all is fine
                status.userMembershipVerified();
            }
            else
            {
                localGroup.addMembership( existingMember );
                status.userMembershipCreated();
            }
        }
    }

    protected void removeLocalUserMembershipsNotExistingRemote( final UserEntity localUser, final List<RemoteGroup> remoteMemberships )
    {
        // Gather remote users in a map for fast and easy access
        final Map<String, RemoteGroup> remoteMembershipsMap = new HashMap<String, RemoteGroup>();
        for ( final RemoteGroup remoteMembership : remoteMemberships )
        {
            remoteMembershipsMap.put( remoteMembership.getId() + "-" + remoteMembership.getSync(), remoteMembership );
        }

        final GroupEntity userGroup = localUser.getUserGroup();

        // Gather local memberships that does not exist remote
        final Set<GroupEntity> localMembershipsToRemove = new HashSet<GroupEntity>();
        for ( final GroupEntity localMembership : userGroup.getMemberships( false ) )
        {
            // We're not removing memberships in built-in or global groups
            if ( !localMembership.isBuiltIn() && !localMembership.isGlobal() )
            {
                final RemoteGroup remoteMembership =
                    remoteMembershipsMap.get( localMembership.getName() + "-" + localMembership.getSyncValue() );
                if ( remoteMembership == null )
                {
                    localMembershipsToRemove.add( localMembership );
                }
            }
        }

        // Remove local memberships that does not exist remote
        for ( final GroupEntity localMembershipToRemove : localMembershipsToRemove )
        {
            userGroup.removeMembership( localMembershipToRemove );
            status.userMembershipDeleted();
        }
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

    public void setRemoteUserStorePlugin( final RemoteUserStorePlugin value )
    {
        this.remoteUserStorePlugin = value;
    }

    public void setTimeService( final TimeService value )
    {
        this.timeService = value;
    }

    public void setUserDao( final UserDao value )
    {
        this.userDao = value;
    }

    public void setGroupDao( final GroupDao value )
    {
        this.groupDao = value;
    }

    public void setConnectorConfig( final UserStoreConnectorConfig value )
    {
        this.connectorConfig = value;
    }

    public void setUserStorageService( UserStorageService value )
    {
        this.userStorageService = value;
    }
}
