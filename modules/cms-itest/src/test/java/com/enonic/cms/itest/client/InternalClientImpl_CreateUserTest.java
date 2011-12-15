package com.enonic.cms.itest.client;


import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.api.client.model.CreateUserParams;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class InternalClientImpl_CreateUserTest
    extends AbstractSpringTest
{
    @Autowired
    private InternalClient internalClient;

    @Autowired
    private DomainFixture fixture;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        PortalSecurityHolder.setLoggedInUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void create_user_in_local_userstore()
        throws Exception
    {
        clientLogin( "admin", "password" );

        // exercise:
        CreateUserParams params = new CreateUserParams();
        params.userstore = "testuserstore";
        params.username = "test1";
        params.password = "password";
        params.email = "jvs@enonic.com";
        params.userInfo.setFirstName( "Jorund Vier" );
        params.userInfo.setLastName( "Skriubakken" );
        String userKey = internalClient.createUser( params );

        // verify:
        assertNotNull( userKey );
        UserEntity actualUser = fixture.findUserByName( "test1" );
        assertEquals( "jvs@enonic.com", actualUser.getEmail() );
        assertEquals( "test1", actualUser.getName() );
        assertEquals( "Jorund Vier", actualUser.getUserInfo().getFirstName() );
        assertEquals( "Skriubakken", actualUser.getUserInfo().getLastName() );
    }

    private void clientLogin( String username, String password )
    {
        UserEntity user = fixture.findUserByName( username );
        PortalSecurityHolder.setLoggedInUser( user.getKey() );
        PortalSecurityHolder.setImpersonatedUser( user.getKey() );

        //internalClient.login( username, password );
    }
}
