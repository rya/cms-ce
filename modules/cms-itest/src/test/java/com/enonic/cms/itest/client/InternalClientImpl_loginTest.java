package com.enonic.cms.itest.client;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.AssertTool;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_loginTest
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    @Qualifier(value = "localClient")
    private InternalClient localClient;

    private DomainFactory factory;

    private DomainFixture fixture;

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();


    @Before
    public void before()
        throws Exception
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "testuser", "password", "Test user", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "avatar", "password", "Avatar", UserType.NORMAL, "testuserstore" );

        httpServletRequest.setRemoteAddr( "127.0.0.1" );
    }

    @Test
    public void userName_returns_anonymous_user_when_not_logged_in()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise & verify
        assertEquals( "anonymous", localClient.getUserName() );
        assertEquals( "anonymous", localClient.getRunAsUserName() );
    }

    @Test
    public void login_with_normal_user()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore\\testuser", "password" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "testuser", localClient.getRunAsUserName() );
    }

    @Test
    public void getUserName_returns_anonymous_after_logout_after_login()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        localClient.login( "testuserstore\\testuser", "password" );

        // verify setup
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "testuser", localClient.getRunAsUserName() );

        // exercise
        localClient.logout();

        // verify
        assertEquals( "anonymous", localClient.getUserName() );
        assertEquals( "anonymous", localClient.getRunAsUserName() );
    }

    @Test
    public void login_with_admin_user()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "admin", "password" );

        // verify
        assertEquals( "admin", localClient.getUserName() );
        assertEquals( "admin", localClient.getRunAsUserName() );
    }

    @Test
    public void login_with_unknown_userstore_fails()
        throws Exception
    {
        try
        {
            localClient.login( "unknownUserstore:jvs", "mypassword" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains( "Invalid username or password, username: 'unknownUserstore\\jvs'" ) );
        }
    }


    @Test
    public void login_with_no_userstore_specified_logs_in_user_in_default_userstore()
        throws Exception
    {
        // setup
        fixture.save( factory.createUserStore( "otheruserstore" ) );

        // exercise
        localClient.login( "testuser", "password" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "testuser", localClient.getRunAsUserName() );
    }

    @Test
    public void impersonate_on_local_client_as_non_admin_user_is_allowed()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore\\testuser", "password" );
        localClient.impersonate( "testuserstore\\avatar" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "avatar", localClient.getRunAsUserName() );
    }

    @Test
    public void impersonate_with_user_specified_as_key()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore:testuser", "password" );
        localClient.impersonate( "#" + fixture.findUserByName( "avatar" ).getKey() );

        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "avatar", localClient.getRunAsUserName() );
    }

    @Test
    public void impersonate_with_user_specified_as_usertore_name_colon_user_name()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore:testuser", "password" );
        localClient.impersonate( "testuserstore:avatar" );

        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "avatar", localClient.getRunAsUserName() );
    }

    @Test
    public void impersonate_with_user_specified_as_usertore_name_backslash_user_name()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore\\testuser", "password" );
        localClient.impersonate( "testuserstore\\avatar" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "avatar", localClient.getRunAsUserName() );
    }

    @Test
    public void runAs_returns_same_as_user_when_not_impersonating()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore\\testuser", "password" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "testuser", localClient.getUser() );
        assertEquals( "testuser", localClient.getRunAsUserName() );
        assertEquals( "testuser", localClient.getRunAsUser() );
    }

    @Test
    public void runAs_returns_impersonated_user_when_using_local_client_and_impersonating()
        throws Exception
    {
        // setup
        ServletRequestAccessor.setRequest( httpServletRequest );
        RequestContextHolder.setRequestAttributes( new ServletRequestAttributes( httpServletRequest ) );

        // exercise
        localClient.login( "testuserstore\\testuser", "password" );
        localClient.impersonate( "testuserstore\\avatar" );

        // verify
        assertEquals( "testuser", localClient.getUserName() );
        assertEquals( "testuser", localClient.getUser() );
        assertEquals( "avatar", localClient.getRunAsUserName() );
        assertEquals( "avatar", localClient.getRunAsUser() );

        AssertTool.assertSingleXPathValueEquals( "/user/name", localClient.getUserContext(), "testuser" );
        AssertTool.assertSingleXPathValueEquals( "/user/name", localClient.getRunAsUserContext(), "avatar" );
    }

}
