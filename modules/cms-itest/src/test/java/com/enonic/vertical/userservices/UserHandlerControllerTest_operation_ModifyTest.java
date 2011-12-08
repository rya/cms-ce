package com.enonic.vertical.userservices;

import com.enonic.cms.core.security.PortalSecurityHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;

import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.SiteRedirectHelper;
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
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.UserDao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.easymock.classextension.EasyMock.createMock;

public class UserHandlerControllerTest_operation_ModifyTest
        extends AbstractSpringTest {

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

    private UserHandlerController userHandlerController;

    @Before
    public void setUp() {
        fixture = new DomainFixture(hibernateTemplate);
        factory = new DomainFactory(fixture);

        fixture.initSystemData();

        userHandlerController = new UserHandlerController();
        userHandlerController.setUserDao(userDao);
        userHandlerController.setSecurityService(securityService);
        userHandlerController.setUserStoreService(userStoreService);
        userHandlerController.setUserServicesRedirectHelper(new UserServicesRedirectUrlResolver());

        // just need a dummy of the SiteRedirectHelper
        userHandlerController.setSiteRedirectHelper(createMock(SiteRedirectHelper.class));

        request.setRemoteAddr("127.0.0.1");
        ServletRequestAccessor.setRequest(request);

        PortalSecurityHolder.setAnonUser(fixture.findUserByName("anonymous").getKey());

    }

    @After
    public void after() {
        securityService.logoutPortalUser();
    }

    @Test
    // When modify user with (unrequired) birtdate not sent, the birthdate should be untouched
    public void modify_with_unrequired_birthday_not_sent_should_be_untouched()
            throws Exception {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.FIRST_NAME, "required"));
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.LAST_NAME, "required"));
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.INITIALS, "required"));
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.BIRTHDAY, ""));
        createLocalUserStore("myLocalStore", true, userStoreConfig);

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName("First name");
        userInfo.setLastName("Last name");
        userInfo.setInitials("INI");
        userInfo.setBirthday(DateUtil.parseDate("12.12.2012"));
        createNormalUser("testuser", "myLocalStore", userInfo);

        // verify
        UserInfo resultInfo = fixture.findUserByName("testuser").getUserInfo();
        assertNotNull(resultInfo.getBirthday());
        assertEquals("12.12.2012", DateUtil.formatDate(resultInfo.getBirthday()));
        assertEquals("INI", resultInfo.getInitials());

        // exercise
        request.setAttribute(Attribute.ORIGINAL_SITEPATH, new SitePath(new SiteKey(0), "/_services/user/create"));
        ExtendedMap formItems = new ExtendedMap(true);
        formItems.putString("first_name", "First name changed");
        formItems.putString("last_name", "Last name changed");
        formItems.putString("initials", "Initials changed");

        loginPortalUser("testuser");

        userHandlerController.handlerModify(request, response, formItems);

        // verify
        resultInfo = fixture.findUserByName("testuser").getUserInfo();
        assertNotNull(resultInfo.getBirthday());
        assertEquals("12.12.2012", DateUtil.formatDate(resultInfo.getBirthday()));
        assertEquals("Initials changed", resultInfo.getInitials());
    }

    @Test
    // When modify user with empty birthdate, the birthdate will be emptied
    public void modify_with_birthday_and_empty_value_in_form_will_be_emptied()
            throws Exception {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.FIRST_NAME, "required"));
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.LAST_NAME, "required"));
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.INITIALS, "required"));
        userStoreConfig.addUserFieldConfig(createUserStoreUserFieldConfig(UserFieldType.BIRTHDAY, ""));
        createLocalUserStore("myLocalStore", true, userStoreConfig);

        fixture.flushAndClearHibernateSesssion();

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName("First name");
        userInfo.setLastName("Last name");
        userInfo.setInitials("INI");
        userInfo.setBirthday(DateUtil.parseDate("12.12.2012"));
        createNormalUser("testuser", "myLocalStore", userInfo);

        // verify
        UserInfo resultInfo = fixture.findUserByName("testuser").getUserInfo();
        assertEquals("12.12.2012", DateUtil.formatDate(resultInfo.getBirthday()));
        assertEquals("INI", resultInfo.getInitials());

        // exercise
        request.setAttribute(Attribute.ORIGINAL_SITEPATH, new SitePath(new SiteKey(0), "/_services/user/create"));
        ExtendedMap formItems = new ExtendedMap(true);
        formItems.putString("first_name", "First name changed");
        formItems.putString("last_name", "Last name changed");
        formItems.putString("initials", "Initials changed");
        formItems.putString("birthday", "");

        loginPortalUser("testuser");

        userHandlerController.handlerModify(request, response, formItems);

        // verify
        resultInfo = fixture.findUserByName("testuser").getUserInfo();
        assertNull(resultInfo.getBirthday());
        assertEquals("Initials changed", resultInfo.getInitials());
    }

    private UserStoreUserFieldConfig createUserStoreUserFieldConfig(UserFieldType type, String properties) {
        UserStoreUserFieldConfig fieldConfig = new UserStoreUserFieldConfig(type);
        fieldConfig.setRemote(properties.contains("remote"));
        fieldConfig.setReadOnly(properties.contains("read-only"));
        fieldConfig.setRequired(properties.contains("required"));
        fieldConfig.setIso(properties.contains("iso"));
        return fieldConfig;
    }

    private void loginPortalUser(String userName) {
        PortalSecurityHolder.setImpersonatedUser(fixture.findUserByName(userName).getKey());
        PortalSecurityHolder.setUser(fixture.findUserByName(userName).getKey());
    }

    private UserStoreKey createLocalUserStore(String name, boolean defaultStore, UserStoreConfig config) {
        StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        command.setStorer(fixture.findUserByName("admin").getKey());
        command.setName(name);
        command.setDefaultStore(defaultStore);
        command.setConfig(config);
        return userStoreService.storeNewUserStore(command);
    }

    private UserKey createNormalUser(String userName, String userStoreName, UserInfo userInfo) {
        StoreNewUserCommand command = new StoreNewUserCommand();
        command.setStorer(fixture.findUserByName("admin").getKey());
        command.setUsername(userName);
        command.setUserStoreKey(fixture.findUserStoreByName(userStoreName).getKey());
        command.setAllowAnyUserAccess(true);
        command.setEmail(userName + "@example.com");
        command.setPassword("password");
        command.setType(UserType.NORMAL);
        command.setDisplayName(userName);
        command.setUserInfo(userInfo);

        return userStoreService.storeNewUser(command);
    }
}
