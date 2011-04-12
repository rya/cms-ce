/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldType;

public class DisplayNameResolverTest
{
    private UserStoreConfig userStoreConfig;

    private DisplayNameResolver displayNameResolver;

    @Before
    public void before()
    {
        userStoreConfig = Mockito.mock( UserStoreConfig.class );

        displayNameResolver = new DisplayNameResolver( userStoreConfig );
        displayNameResolver.displayName = "";
    }

    @Test
    public void testOnlyPrefix()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.PREFIX ) ).thenReturn(
            createUserFieldConfig( UserFieldType.PREFIX, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setPrefix( " prefix " );

        Assert.assertEquals( "prefix", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlyFirstName()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.FIRST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.FIRST_NAME, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( " first name " );

        Assert.assertEquals( "first name", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlyMiddleName()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.MIDDLE_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.MIDDLE_NAME, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setMiddleName( " middle name " );

        Assert.assertEquals( "middle name", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlyLastName()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.LAST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.LAST_NAME, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setLastName( " last name " );

        Assert.assertEquals( "last name", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlySuffix()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.SUFFIX ) ).thenReturn(
            createUserFieldConfig( UserFieldType.SUFFIX, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setSuffix( " suffix " );

        Assert.assertEquals( "suffix", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlyNickName()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.NICK_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.NICK_NAME, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setNickName( " nick name " );

        Assert.assertEquals( "nick name", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlyInitials()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.INITIALS ) ).thenReturn(
            createUserFieldConfig( UserFieldType.INITIALS, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setInitials( " i n i t i a l s " );

        Assert.assertEquals( "i n i t i a l s", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testOnlyUid()
    {
        Assert.assertEquals( "uid", displayNameResolver.resolveDisplayName( " uid ", null, null ) );
    }

    @Test
    public void testAllWithoutDisplayName()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.PREFIX ) ).thenReturn(
            createUserFieldConfig( UserFieldType.PREFIX, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.FIRST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.FIRST_NAME, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.MIDDLE_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.MIDDLE_NAME, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.LAST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.LAST_NAME, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.SUFFIX ) ).thenReturn(
            createUserFieldConfig( UserFieldType.SUFFIX, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.NICK_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.NICK_NAME, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.INITIALS ) ).thenReturn(
            createUserFieldConfig( UserFieldType.INITIALS, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setPrefix( " prefix " );
        userInfo.setFirstName( " first " );
        userInfo.setMiddleName( " middle " );
        userInfo.setLastName( " last " );
        userInfo.setSuffix( " suffix " );
        userInfo.setNickName( " the dude " );
        userInfo.setInitials( " f m l " );

        Assert.assertEquals( "prefix first middle last suffix", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testFirstNameAndLastNameWithoutDisplayName()
    {
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.FIRST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.FIRST_NAME, false ) );
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.LAST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.LAST_NAME, false ) );

        final UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( " first " );
        userInfo.setLastName( " last " );

        Assert.assertEquals( "first last", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    @Test
    public void testUserFieldNotConfiguredNotUsedInResolving()
    {
        // setup: only first name is configured, not last name
        Mockito.when( userStoreConfig.getUserFieldConfig( UserFieldType.FIRST_NAME ) ).thenReturn(
            createUserFieldConfig( UserFieldType.FIRST_NAME, true ) );

        // setup: be sure to give last name a value
        final UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "Arn" );
        userInfo.setLastName( "Skriubakken" );

        // exercise and verify that last name is not used in resolving
        Assert.assertEquals( "Arn", displayNameResolver.resolveDisplayName( null, null, userInfo ) );
    }

    private UserStoreUserFieldConfig createUserFieldConfig( UserFieldType type, boolean readOnly )
    {
        UserStoreUserFieldConfig config = new UserStoreUserFieldConfig( type );
        config.setReadOnly( readOnly );
        return config;
    }
}
