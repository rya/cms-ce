/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.SiteRedirectHelper;
import com.enonic.cms.core.portal.httpservices.UserServicesException;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.*;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.esl.containers.ExtendedMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.easymock.classextension.EasyMock.createMock;

public class UserHandlerControllerTest
    extends AbstractSpringTest
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreService userStoreService;


    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockHttpSession session = new MockHttpSession();

    private UserHandlerController userHandlerController;

    @Before
    public void setUp()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        userHandlerController = new UserHandlerController();
        userHandlerController.setUserDao( userDao );
        userHandlerController.setSecurityService( securityService );
        userHandlerController.setUserStoreService( userStoreService );
        userHandlerController.setUserServicesRedirectHelper( new UserServicesRedirectUrlResolver() );

        // just need a dummy of the SiteRedirectHelper
        userHandlerController.setSiteRedirectHelper( createMock( SiteRedirectHelper.class ) );

        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        SecurityHolder.setAnonUser( fixture.findUserByName( "anonymous" ).getKey() );

    }

    @After
    public void after()
    {
        securityService.logoutPortalUser();
    }

    @Test
    public void testAddGroupsFromSetGroupsConfig()
    {
        ExtendedMap formItems = new ExtendedMap();

        formItems.put( UserHandlerController.ALLGROUPKEYS, "1,2,3,4,5" );
        formItems.put( UserHandlerController.JOINGROUPKEY, new String[]{"2", "3", "6"} );

        UpdateUserCommand updateUserCommand = new UpdateUserCommand( null, null );

        MyUserEntityMock user = new MyUserEntityMock();

        userHandlerController.addGroupsFromSetGroupsConfig( formItems, updateUserCommand, user );

        List<GroupKey> expectedEntries = generateGroupKeyList( new String[]{"2", "3", "6", "7"} );

        assertEquals( "Should have 4 groups", updateUserCommand.getMemberships().size(), 4 );
        assertTrue( "Should contain groupKeys: 2, 3, 6, 7", updateUserCommand.getMemberships().containsAll( expectedEntries ) );
    }

    @Test
    public void modify()
        throws RemoteException
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.MIDDLE_NAME, "" ) );

        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "Qhawe" );
        userInfo.setLastName( "Skriubakken" );
        createNormalUser( "qhawe", "myLocalStore", userInfo );

        loginPortalUser( "qhawe" );

        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/modify" ) );

        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "first_name", "Vier" );

        userHandlerController.handlerModify( request, response, formItems );

        assertEquals( "Vier", fixture.findUserByName( "qhawe" ).getUserInfo().getFirstName() );
    }

    @Test
    public void create_without_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "username", "testcreate" );
        formItems.putString( "password", "password" );
        formItems.putString( "email", "test@test.com" );
        formItems.putString( "first_name", "First name" );
        formItems.putString( "last_name", "Last name" );

        try
        {
            userHandlerController.handlerCreate( request, response, session, formItems, null, null );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }

    }

    @Test
    public void update_without_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "username", "testcreate" );
        formItems.putString( "password", "password" );
        formItems.putString( "email", "test@test.com" );
        formItems.putString( "first_name", "First name changed" );
        formItems.putString( "last_name", "Last name changed" );

        loginPortalUser( "testuser" );

        try
        {
            userHandlerController.handlerUpdate( request, response, session, formItems, null, null );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }

    }

    @Test
    public void modify_without_required_fields_on_local_user_store()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "initials", "ABC" );

        loginPortalUser( "testuser" );

        userHandlerController.handlerModify( request, response, formItems );

        // verify
        assertEquals( "ABC", fixture.findUserByName( "testuser" ).getUserInfo().getInitials() );
    }

    @Test
    public void create_with_empty_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "username", "testcreate" );
        formItems.putString( "password", "password" );
        formItems.putString( "email", "test@test.com" );
        formItems.putString( "first_name", "First name" );
        formItems.putString( "last_name", "Last name" );
        formItems.putString( "initials", "" ); // field set but empty

        try
        {
            userHandlerController.handlerCreate( request, response, session, formItems, null, null );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }

    }

    @Test
    public void create_with_blank_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "username", "testcreate" );
        formItems.putString( "password", "password" );
        formItems.putString( "email", "test@test.com" );
        formItems.putString( "first_name", "First name" );
        formItems.putString( "last_name", "Last name" );
        formItems.putString( "initials", "  " ); // field set but blank

        try
        {
            userHandlerController.handlerCreate( request, response, session, formItems, null, null );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }

    }

    @Test
    public void update_with_empty_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "username", "testcreate" );
        formItems.putString( "password", "password" );
        formItems.putString( "email", "test@test.com" );
        formItems.putString( "first_name", "First name changed" );
        formItems.putString( "last_name", "Last name changed" );
        formItems.putString( "initials", "" ); // field set but empty

        loginPortalUser( "testuser" );

        try
        {
            userHandlerController.handlerUpdate( request, response, session, formItems, null, null );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }

    }

    @Test
    public void update_with_blank_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "username", "testcreate" );
        formItems.putString( "password", "password" );
        formItems.putString( "email", "test@test.com" );
        formItems.putString( "first_name", "First name changed" );
        formItems.putString( "last_name", "Last name changed" );
        formItems.putString( "initials", " " ); // field set but blank

        loginPortalUser( "testuser" );

        try
        {
            userHandlerController.handlerUpdate( request, response, session, formItems, null, null );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }

    }

    @Test
    public void modify_with_empty_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "initials", "" ); // field set but empty
        formItems.putString( "last_name", "Last name changed" );
        loginPortalUser( "testuser" );

        try
        {
            userHandlerController.handlerModify( request, response, formItems );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }
    }

    @Test
    public void modify_with_blank_required_fields_on_local_user_store_throws_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "initials", "  " ); // field set but blank
        formItems.putString( "last_name", "Last name changed" );
        loginPortalUser( "testuser" );

        try
        {
            userHandlerController.handlerModify( request, response, formItems );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserServicesException );
            assertEquals( "Error in userservices, error code: 400", e.getMessage() );
        }
    }

    private UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreUserFieldConfig fieldConfig = new UserStoreUserFieldConfig( type );
        fieldConfig.setRemote( properties.contains( "remote" ) );
        fieldConfig.setReadOnly( properties.contains( "read-only" ) );
        fieldConfig.setRequired( properties.contains( "required" ) );
        fieldConfig.setIso( properties.contains( "iso" ) );
        return fieldConfig;
    }

    private void loginPortalUser( String userName )
    {
        SecurityHolder.setRunAsUser( fixture.findUserByName( userName ).getKey() );
        SecurityHolder.setUser( fixture.findUserByName( userName ).getKey() );
    }

    private UserStoreKey createLocalUserStore( String name, boolean defaultStore, UserStoreConfig config )
    {
        StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setName( name );
        command.setDefaultStore( defaultStore );
        command.setConfig( config );
        return userStoreService.storeNewUserStore( command );
    }

    private UserKey createNormalUser( String userName, String userStoreName, UserInfo userInfo )
    {
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setStorer( fixture.findUserByName( "admin" ).getKey() );
        command.setUsername( userName );
        command.setUserStoreKey( fixture.findUserStoreByName( userStoreName ).getKey() );
        command.setAllowAnyUserAccess( true );
        command.setEmail( userName + "@example.com" );
        command.setPassword( "password" );
        command.setType( UserType.NORMAL );
        command.setDisplayName( userName );
        command.setUserInfo( userInfo );

        return userStoreService.storeNewUser( command );
    }

    private List<GroupKey> generateGroupKeyList( String[] keys )
    {
        List<GroupKey> groupKeys = new ArrayList<GroupKey>();

        for ( String key : keys )
        {
            groupKeys.add( new GroupKey( key ) );
        }

        return groupKeys;
    }

    private class MyUserEntityMock
        extends UserEntity
    {
        @Override
        public Set<GroupEntity> getDirectMemberships()
        {

            GroupEntity group1 = new GroupEntity();
            group1.setKey( "1" );

            GroupEntity group2 = new GroupEntity();
            group2.setKey( "2" );

            GroupEntity group7 = new GroupEntity();
            group7.setKey( "7" );

            Set<GroupEntity> groupEntities = new HashSet<GroupEntity>();

            groupEntities.add( group1 );

            groupEntities.add( group2 );

            groupEntities.add( group7 );

            return groupEntities;
        }
    }
}
