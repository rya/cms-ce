/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.security.userstore;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UpdateUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class UserStoreServiceImpl_localUserStoreTest
    extends AbstractSpringTest
{
    private DomainFactory factory;

    @Autowired
    private DomainFixture fixture;

    @Autowired
    private UserStoreService userStoreService;

    @Before
    public void setUp()
        throws Exception
    {

        factory = fixture.getFactory();

        fixture.initSystemData();

        UserStoreEntity userStore = factory.createUserStore( "myLocalUserStore", null, true );
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( factory.createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStore.setConfig( userStoreConfig );
        fixture.save( userStore );
    }

    @Test
    public void update_replaceAll_changing_names()
        throws Exception
    {

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );

        // exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( userStore.getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserInfo userInfoForCreate = new UserInfo();
        userInfoForCreate.setFirstName( "Arn" );
        userInfoForCreate.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserInfo( userInfoForCreate );
        userStoreService.storeNewUser( createCommand );

        fixture.flushAndClearHibernateSesssion();

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( "Arn Wyatt-Skriubakken", fixture.findUserByName( "arn" ).getDisplayName() );

        UserSpecification userToUpdate = new UserSpecification();
        userToUpdate.setKey( fixture.findUserByName( "arn" ).getKey() );
        UpdateUserCommand updateCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdate );
        updateCommand.setUpdateStrategy( UpdateUserCommand.UpdateStrategy.REPLACE_ALL );
        updateCommand.setDisplayName( "Wyatt-Skriubakken" );
        updateCommand.setEmail( "arn@test.com" );
        updateCommand.setPassword( "password" );
        UserInfo userInfoForUpdate = new UserInfo();
        userInfoForUpdate.setFirstName( "Arn Umshlaba" );
        userInfoForUpdate.setLastName( "Wyatt-Zulu-Skriubakken" );
        updateCommand.setUserInfo( userInfoForUpdate );
        userStoreService.updateUser( updateCommand );

        fixture.flushAndClearHibernateSesssion();

        UserEntity actualUser = fixture.findUserByName( "arn" );
        assertEquals( "Wyatt-Skriubakken", actualUser.getDisplayName() );
        assertEquals( "Arn Umshlaba", actualUser.getUserInfo().getFirstName() );
        assertEquals( "Wyatt-Zulu-Skriubakken", actualUser.getUserInfo().getLastName() );
    }

    @Test
    public void update_replaceAll_omitting_names()
        throws Exception
    {

        // verify no users exists before doing the synchronize job
        assertEquals( 0, fixture.countUsersByType( UserType.NORMAL ) );

        UserStoreEntity userStore = fixture.findUserStoreByName( "myLocalUserStore" );

        // exercise
        StoreNewUserCommand createCommand = new StoreNewUserCommand();
        createCommand.setUserStoreKey( userStore.getKey() );
        createCommand.setUsername( "arn" );
        createCommand.setEmail( "arn@test.com" );
        createCommand.setDisplayName( "Arn Wyatt-Skriubakken" );
        createCommand.setStorer( fixture.findUserByName( "admin" ).getKey() );
        createCommand.setPassword( "password" );
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "Arn" );
        userInfo.setLastName( "Wyatt-Skriubakken" );
        createCommand.setUserInfo( userInfo );
        userStoreService.storeNewUser( createCommand );

        fixture.flushAndClearHibernateSesssion();

        // verify created users
        assertEquals( 1, fixture.countUsersByType( UserType.NORMAL ) );
        assertEquals( "Arn Wyatt-Skriubakken", fixture.findUserByName( "arn" ).getDisplayName() );

        UserSpecification userToUpdate = new UserSpecification();
        userToUpdate.setKey( fixture.findUserByName( "arn" ).getKey() );
        UpdateUserCommand updateCommand = new UpdateUserCommand( fixture.findUserByName( "admin" ).getKey(), userToUpdate );
        updateCommand.setUpdateStrategy( UpdateUserCommand.UpdateStrategy.REPLACE_ALL );
        updateCommand.setDisplayName( "Wyatt-Skriubakken" );
        updateCommand.setEmail( "arn@test.com" );
        updateCommand.setPassword( "password" );
        UserInfo userInfoForUpdate = new UserInfo();
        updateCommand.setUserInfo( userInfoForUpdate );
        userStoreService.updateUser( updateCommand );

        fixture.flushAndClearHibernateSesssion();

        UserEntity actualUser = fixture.findUserByName( "arn" );
        assertEquals( "Wyatt-Skriubakken", actualUser.getDisplayName() );
        assertEquals( null, actualUser.getUserInfo().getFirstName() );
        assertEquals( null, actualUser.getUserInfo().getLastName() );
    }

}
