package com.enonic.cms.itest.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.api.client.model.GetUsersParams;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.api.client.model.user.UserInfo;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.*;

public class InternalClientImpl_getUsersTest
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    @Qualifier("localClient")
    private InternalClient internalClient;

    @Autowired
    private GroupDao groupDao;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture = new DomainFixture( hibernateTemplate, groupDao );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        // custom user fields creation
        final UserInfo userInfo = new UserInfo();
        final Date birthday = new Date();
        userInfo.setBirthday( birthday );
        userInfo.setCountry( "country_value" );
        userInfo.setDescription( "description_value" );
        userInfo.setFax( "fax_value" );
        userInfo.setFirstName( "firstname_value" );
        userInfo.setGender( Gender.MALE );
        userInfo.setGlobalPosition( "globalposition_value" );
        userInfo.setHomePage( "homepage_value" );
        userInfo.setHtmlEmail( true );
        userInfo.setInitials( "initials_value" );
        userInfo.setLastName( "lastname_value" );
        userInfo.setLocale( Locale.ENGLISH );
        userInfo.setMemberId( "memberid_value" );
        userInfo.setMiddleName( "middlename_value" );
        userInfo.setMobile( "mobile_value" );
        userInfo.setNickName( "nickname_value" );
        userInfo.setOrganization( "organization_value" );
        userInfo.setPersonalId( "personalid_value" );
        userInfo.setPhone( "phone_value" );
        userInfo.setPhoto( new byte[]{0x23, 0x24, 0x25} );
        userInfo.setPrefix( "prefix_value" );
        userInfo.setSuffix( "suffix_value" );
        userInfo.setTimezone( TimeZone.getTimeZone( "GMT" ) );
        userInfo.setTitle( "title_value" );
        //

        fixture.createAndStoreNormalUserWithAllValuesAndWithUserGroup( "testuser", "Test user", "testuserstore", userInfo );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void get_users_with_custom_fields()
        throws Exception
    {
        // exercise:
        GetUsersParams params = new GetUsersParams();
        params.userStore = "testuserstore";
        params.includeCustomUserFields = true;

        final Date birthday = new Date();

        Document users = internalClient.getUsers( params );

        assertNotNull( users );

        assertSingleXPathValueEquals( "users/user[1]/birthday", users, new SimpleDateFormat( "yyyy-MM-dd" ).format( birthday ) );
        assertSingleXPathValueEquals( "users/user[1]/country", users, "country_value" );
        assertSingleXPathValueEquals( "users/user[1]/description", users, "description_value" );
        assertSingleXPathValueEquals( "users/user[1]/fax", users, "fax_value" );
        assertSingleXPathValueEquals( "users/user[1]/first-name", users, "firstname_value" );
        assertSingleXPathValueEquals( "users/user[1]/gender", users, "male" );
        assertSingleXPathValueEquals( "users/user[1]/global-position", users, "globalposition_value" );
        assertSingleXPathValueEquals( "users/user[1]/home-page", users, "homepage_value" );
        assertSingleXPathValueEquals( "users/user[1]/html-email", users, "true" );
        assertSingleXPathValueEquals( "users/user[1]/initials", users, "initials_value" );
        assertSingleXPathValueEquals( "users/user[1]/locale", users, "en" );
        assertSingleXPathValueEquals( "users/user[1]/member-id", users, "memberid_value" );
        assertSingleXPathValueEquals( "users/user[1]/middle-name", users, "middlename_value" );
        assertSingleXPathValueEquals( "users/user[1]/mobile", users, "mobile_value" );
        assertSingleXPathValueEquals( "users/user[1]/nick-name", users, "nickname_value" );
        assertSingleXPathValueEquals( "users/user[1]/organization", users, "organization_value" );
        assertSingleXPathValueEquals( "users/user[1]/personal-id", users, "personalid_value" );
        assertSingleXPathValueEquals( "users/user[1]/photo/@exists", users, "true" );
        assertSingleXPathValueEquals( "users/user[1]/prefix", users, "prefix_value" );
        assertSingleXPathValueEquals( "users/user[1]/suffix", users, "suffix_value" );
        assertSingleXPathValueEquals( "users/user[1]/time-zone", users, "GMT" );

    }

    @Test
    public void get_users_without_custom_fields()
        throws Exception
    {
        // exercise:
        GetUsersParams params = new GetUsersParams();
        params.userStore = "testuserstore";
        params.includeCustomUserFields = false;

        Document users = internalClient.getUsers( params );

        assertNotNull( users );

        assertSingleXPathValueEquals( "users/user[1]/birthday", users, null );
        assertSingleXPathValueEquals( "users/user[1]/country", users, null );
        assertSingleXPathValueEquals( "users/user[1]/description", users, null );
        assertSingleXPathValueEquals( "users/user[1]/fax", users, null );
        assertSingleXPathValueEquals( "users/user[1]/first-name", users, null );
        assertSingleXPathValueEquals( "users/user[1]/gender", users, null );
        assertSingleXPathValueEquals( "users/user[1]/global-position", users, null );
        assertSingleXPathValueEquals( "users/user[1]/home-page", users, null );
        assertSingleXPathValueEquals( "users/user[1]/html-email", users, null );
        assertSingleXPathValueEquals( "users/user[1]/initials", users, null );
        assertSingleXPathValueEquals( "users/user[1]/locale", users, null );
        assertSingleXPathValueEquals( "users/user[1]/member-id", users, null );
        assertSingleXPathValueEquals( "users/user[1]/middle-name", users, null );
        assertSingleXPathValueEquals( "users/user[1]/mobile", users, null );
        assertSingleXPathValueEquals( "users/user[1]/nick-name", users, null );
        assertSingleXPathValueEquals( "users/user[1]/organization", users, null );
        assertSingleXPathValueEquals( "users/user[1]/personal-id", users, null );
        assertSingleXPathValueEquals( "users/user[1]/photo/@exists", users, null );
        assertSingleXPathValueEquals( "users/user[1]/prefix", users, null );
        assertSingleXPathValueEquals( "users/user[1]/suffix", users, null );
        assertSingleXPathValueEquals( "users/user[1]/time-zone", users, null );

    }

    private void assertSingleXPathValueEquals( final String xpathString, final Document doc, final String expectedValue )
    {
        final String actualValue = JDOMUtil.evaluateSingleXPathValueAsString( xpathString, doc );
        Assert.assertEquals( xpathString, expectedValue, actualValue );
    }
}
