/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import org.springframework.util.Assert;

import com.enonic.cms.core.security.userstore.connector.EmailAlreadyExistsException;
import com.enonic.cms.core.security.userstore.connector.NameAlreadyExistsException;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.domain.user.remote.RemoteUser;

public class UserSynchronizer
    extends AbstractBaseUserSynchronizer
{
    protected UserSynchronizer( final UserStoreEntity userStore, final boolean syncMemberships )
    {
        super( userStore, true, syncMemberships );
    }

    public void synchronizeUser( final UserEntity localUser, final MemberCache memberCache )
    {
        Assert.notNull( localUser );
        Assert.isTrue( localUser.getUserStoreKey().equals( userStore.getKey() ) );

        status.setTotalRemoteUserCount( 1 );

        final RemoteUser remoteUser = remoteUserStorePlugin.getUser( localUser.getName() );

        if ( remoteUser == null )
        {
            deleteUser( localUser );
        }
        else if ( !remoteUser.getSync().equals( localUser.getSync() ) )
        {
            // No matcing sync value - user no longer in userstore , we delete it
            deleteUser( localUser );
        }
        else
        {
            final String name = getNameToVerify( localUser, remoteUser );
            if ( nameAlreadyUsedByOtherUser( userStore.getKey(), name, localUser ) )
            {
                throw new NameAlreadyExistsException( userStore.getName(), name );
            }

            final String email = getEmailToVerify( localUser, remoteUser );
            if ( emailAlreadyUsedByOtherUser( userStore.getKey(), email, localUser ) )
            {
                throw new EmailAlreadyExistsException( userStore.getName(), name, email );
            }

            final boolean resurrected = updateAndResurrectLocalUser( localUser, remoteUser, memberCache );
            status.userUpdated( resurrected );

            if ( syncMemberships )
            {
                syncUserMemberships( localUser, remoteUser, memberCache );
            }
        }
    }

    private void deleteUser( final UserEntity localUser )
    {
        if ( !localUser.isDeleted() )
        {
            status.setTotalLocalUserCount( 1 );
            final UserSpecification userToDeleteSpec = new UserSpecification();
            userToDeleteSpec.setKey( localUser.getKey() );
            userStorageService.deleteUser( userToDeleteSpec );
            status.userDeleted();
        }
    }
}
