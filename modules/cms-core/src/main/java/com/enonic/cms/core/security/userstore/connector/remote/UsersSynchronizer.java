/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.security.userstore.connector.EmailAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.NameAlreadyExistsException;

import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.DisplayNameResolver;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldMap;
import com.enonic.cms.domain.user.field.UserInfoTransformer;
import com.enonic.cms.domain.user.remote.RemoteUser;

public class UsersSynchronizer
        extends AbstractBaseUserSynchronizer
{
    private static final Logger LOG = LoggerFactory.getLogger( UsersSynchronizer.class );

    protected UsersSynchronizer( final UserStoreEntity userStore, final boolean syncUser, final boolean syncMemberships )
    {
        super( userStore, syncUser, syncMemberships );
    }

    public void synchronizeUsers( final List<RemoteUser> remoteUsers, final MemberCache memberCache )
    {
        for ( final RemoteUser remoteUser : remoteUsers )
        {
            createUpdateOrResurrectLocalUser( remoteUser, memberCache );
        }
    }

    private void createUpdateOrResurrectLocalUser( final RemoteUser remoteUser, final MemberCache memberCache )
    {
        final UserSpecification spec = new UserSpecification();
        spec.setUserStoreKey( userStore.getKey() );
        spec.setName( remoteUser.getId() );
        spec.setSyncValue( remoteUser.getSync() );
        spec.setDeletedState( UserSpecification.DeletedState.ANY );

        UserEntity localUser = userDao.findSingleBySpecification( spec );

        if ( syncUser )
        {
            if ( !canBeCreatedOrUpdated( localUser, remoteUser ) )
            {
                status.userSkipped();
                return;
            }

            if ( localUser == null )
            {
                localUser = createLocalUser( remoteUser );
                status.userCreated();
            }
            else
            {
                final boolean resurrected = updateAndResurrectLocalUser( localUser, remoteUser, memberCache );
                status.userUpdated( resurrected );
            }
        }
        if ( syncMemberships && localUser != null )
        {
            syncUserMemberships( localUser, remoteUser, memberCache );
        }
    }

    private boolean canBeCreatedOrUpdated( final UserEntity localUser, final RemoteUser remoteUser )
    {
        final String name = getNameToVerify( localUser, remoteUser );
        if ( nameAlreadyUsedByOtherUser( userStore.getKey(), name, localUser ) )
        {
            LOG.warn( NameAlreadyExistsException.createMessage( userStore.getName(), name ) );
            return false;
        }

        final String email = getEmailToVerify( localUser, remoteUser );
        if ( emailAlreadyUsedByOtherUser( userStore.getKey(), email, localUser ) )
        {
            LOG.warn( EmailAlreadyExistsException.createMessage( userStore.getName(), name, email ) );
            return false;
        }
        return true;
    }

    private UserEntity createLocalUser( final RemoteUser remoteUser )
    {
        final UserEntity newLocalUser = new UserEntity();
        newLocalUser.setDeleted( 0 );
        newLocalUser.setUserStore( userStore );
        newLocalUser.setName( remoteUser.getId() );
        newLocalUser.setSyncValue( remoteUser.getSync() );
        newLocalUser.setEmail( remoteUser.getEmail() );
        newLocalUser.setType( UserType.NORMAL );
        newLocalUser.setTimestamp( timeService.getNowAsDateTime() );

        final UserInfoTransformer infoTransformer = new UserInfoTransformer();
        final UserFieldMap userFieldMap = remoteUser.getUserFields();
        userFieldMap.retain( userStoreConfig.getRemoteOnlyUserFieldTypes() );
        final UserInfo userInfo = infoTransformer.toUserInfo( userFieldMap );

        newLocalUser.updateUserInfo( userInfo );
        newLocalUser.setDisplayName(
                new DisplayNameResolver( userStoreConfig ).resolveDisplayName( newLocalUser.getName(), newLocalUser.getDisplayName(),
                                                                               newLocalUser.getUserInfo() ) );

        userDao.storeNew( newLocalUser );

        final GroupEntity newUserGroup = new GroupEntity();
        newUserGroup.setDeleted( 0 );
        newUserGroup.setDescription( null );
        newUserGroup.setName( "userGroup" + newLocalUser.getKey() );
        newUserGroup.setSyncValue( newLocalUser.getSync() );
        newUserGroup.setUser( newLocalUser );
        newUserGroup.setUserStore( userStore );
        newUserGroup.setType( GroupType.USER );
        newUserGroup.setRestricted( 1 );

        groupDao.storeNew( newUserGroup );

        newLocalUser.setUserGroup( newUserGroup );

        userDao.getHibernateTemplate().flush();

        return newLocalUser;
    }
}
