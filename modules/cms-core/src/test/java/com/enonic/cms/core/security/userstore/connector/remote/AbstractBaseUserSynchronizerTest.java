/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.user.remote.RemoteUser;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Sep 4, 2009
 */
public class AbstractBaseUserSynchronizerTest
{

    AbstractBaseUserSynchronizer abstractBaseUserSynchronizer;

    UserDao userDao;

    UserStoreEntity userStore;

    @Before
    public void setUp()
    {
        userStore = createUserStore( 1 );
        abstractBaseUserSynchronizer = new UserSynchronizer( userStore, false );

        userDao = createMock( UserDao.class );

        abstractBaseUserSynchronizer.setUserDao( userDao );
    }

    private void setUpUserDao( List<UserEntity> matchingUsers )
    {
        expect( userDao.findBySpecification( isA( UserSpecification.class ) ) ).andReturn( matchingUsers ).anyTimes();
        replay( userDao );
    }

    @Test
    public void testVerifyEmailForUpdateIsUnique()
    {
        UserEntity localUser = createUser( "rmy", userStore, false );

        RemoteUser remoteUser = createRemoteUser( "rmy" );

        List<UserEntity> matchingUsers = new ArrayList<UserEntity>();
        matchingUsers.add( localUser );

        setUpUserDao( matchingUsers );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertFalse( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( userStore.getKey(), email, localUser ) );
    }

    @Test
    public void testVerifyEmailForUpdateFoundOtherUser()
    {
        UserEntity localUser = createUser( "rmy", userStore, false );
        UserEntity anotherUser = createUser( "jam", userStore, false );

        RemoteUser remoteUser = createRemoteUser( "rmy" );

        List<UserEntity> matchingUsers = new ArrayList<UserEntity>();
        matchingUsers.add( anotherUser );

        setUpUserDao( matchingUsers );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertTrue( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( userStore.getKey(), email, localUser ) );
    }


    @Test
    public void testVerifyEmailForUpdateSeveralFoundUsers()
    {
        RemoteUser remoteUser = createRemoteUser( "rmy" );

        UserEntity localUser = createUser( "rmy", userStore, false );
        UserEntity anotherUser = createUser( "rmy", userStore, false );

        List<UserEntity> matchingUsers = new ArrayList<UserEntity>();
        matchingUsers.add( localUser );
        matchingUsers.add( anotherUser );

        setUpUserDao( matchingUsers );

        final String email = abstractBaseUserSynchronizer.getEmailToVerify( localUser, remoteUser );
        assertTrue( abstractBaseUserSynchronizer.emailAlreadyUsedByOtherUser( userStore.getKey(), email, localUser ) );
    }

    private UserEntity createUser( String uid, UserStoreEntity userStore, boolean isDeleted )
    {
        UserEntity user = new UserEntity();

        UserKey userKey = new UserKey( uid );
        user.setKey( userKey );
        user.setName( uid );
        user.setEmail( uid + "@enonic.com" );
        user.setUserStore( userStore );
        user.setDeleted( isDeleted );

        return user;
    }

    private RemoteUser createRemoteUser( String id )
    {
        RemoteUser user = new RemoteUser( id );
        user.setEmail( id + "@enonic" );

        return user;
    }

    private UserStoreEntity createUserStore( int key )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( key ) );
        userStore.setName( "myUserStore" + key );
        return userStore;
    }
}
