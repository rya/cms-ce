package com.enonic.vertical.userservices;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.SiteRedirectHelper;
import com.enonic.cms.core.portal.httpservices.UserServicesException;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.StoreNewUserCommand;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.UserDao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.easymock.classextension.EasyMock.createMock;

public class UserHandlerControllerTest_operation_UpdateTest
    extends AbstractSpringTest
{

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreService userStoreService;


    @Autowired
    private DomainFixture fixture;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockHttpSession session = new MockHttpSession();

    private UserHandlerController userHandlerController;

    @Before
    public void setUp()
    {
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

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( "anonymous" ).getKey() );

    }

    @After
    public void after()
    {
        securityService.logoutPortalUser();
    }

    @Test
    // When update user with (unrequired) birthdate not sent (in formItems), the birthdate will be updated to empty
    public void modify_with_unrequired_birthday_not_sent_becomes_empty()
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
        userInfo.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // verify
        UserInfo resultInfo = fixture.findUserByName( "testuser" ).getUserInfo();
        assertNotNull( resultInfo.getBirthday() );
        assertEquals( "12.12.2012", DateUtil.formatDate( resultInfo.getBirthday() ) );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "first_name", "First name changed" );
        formItems.putString( "last_name", "Last name changed" );
        formItems.putString( "initials", "Initials changed" );
        formItems.putString( "email", "test@test.com" );

        loginPortalUser( "testuser" );

        userHandlerController.handlerUpdate( request, response, session, formItems, null, null );

        // verify
        resultInfo = fixture.findUserByName( "testuser" ).getUserInfo();
        assertNull( resultInfo.getBirthday() );
    }

    @Test
    // When update user with birthdate not sent and birthdate required, there will be an exception
    public void modify_with_required_birthday_not_sent_birthday_required_trows_exception()
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.FIRST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.LAST_NAME, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.INITIALS, "required" ) );
        userStoreConfig.addUserFieldConfig( createUserStoreUserFieldConfig( UserFieldType.BIRTHDAY, "required" ) );
        createLocalUserStore( "myLocalStore", true, userStoreConfig );

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName( "First name" );
        userInfo.setLastName( "Last name" );
        userInfo.setInitials( "INI" );
        userInfo.setBirthday( DateUtil.parseDate( "12.12.2012" ) );
        createNormalUser( "testuser", "myLocalStore", userInfo );

        // verify
        Date birthday = fixture.findUserByName( "testuser" ).getUserInfo().getBirthday();
        assertNotNull( birthday );
        assertEquals( "12.12.2012", DateUtil.formatDate( birthday ) );

        // exercise
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/user/create" ) );
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "first_name", "First name changed" );
        formItems.putString( "last_name", "Last name changed" );
        formItems.putString( "initials", "Initials changed" );
        formItems.putString( "email", "test@test.com" );

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
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( userName ).getKey() );
        PortalSecurityHolder.setUser( fixture.findUserByName( userName ).getKey() );
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
}
