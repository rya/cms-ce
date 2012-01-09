package com.enonic.cms.core.security;


import org.junit.After;
import org.junit.Test;

import com.enonic.cms.core.security.user.UserKey;

import static org.junit.Assert.*;

public class PortalSecurityHolderTest
{

    @After
    public void after()
    {
        // Be sure to reset after usage since stuff is stored statically
        PortalSecurityHolder.setImpersonatedUser( null );
        PortalSecurityHolder.setLoggedInUser( null );
    }

    @Test
    public void getLoggedInUser_returns_logged_in_user_when_no_impersonation_is_done()
    {
        PortalSecurityHolder.setLoggedInUser( new UserKey( "ABC" ) );
        assertEquals( new UserKey( "ABC" ), PortalSecurityHolder.getLoggedInUser() );
    }

    @Test
    public void getLoggedInUser_returns_same_logged_in_user_when_impersonation_is_done()
    {
        PortalSecurityHolder.setLoggedInUser( new UserKey( "ABC" ) );
        PortalSecurityHolder.setImpersonatedUser( new UserKey( "CCC" ) );
        assertEquals( new UserKey( "ABC" ), PortalSecurityHolder.getLoggedInUser() );
    }

    @Test
    public void getImpersonatedUser_returns_logged_in_user_when_impersonation_is_not_done()
    {
        PortalSecurityHolder.setLoggedInUser( new UserKey( "ABC" ) );
        assertEquals( new UserKey( "ABC" ), PortalSecurityHolder.getImpersonatedUser() );
    }

    @Test
    public void getImpersonatedUser_returns_impersonated_user_when_impersonation_is_done()
    {
        PortalSecurityHolder.setLoggedInUser( new UserKey( "ABC" ) );
        PortalSecurityHolder.setImpersonatedUser( new UserKey( "CCC" ) );
        assertEquals( new UserKey( "CCC" ), PortalSecurityHolder.getImpersonatedUser() );
    }

    @Test
    public void getImpersonatedUser_returns_logged_in_user_after_impersonation_is_removed()
    {
        PortalSecurityHolder.setLoggedInUser( new UserKey( "ABC" ) );
        PortalSecurityHolder.setImpersonatedUser( new UserKey( "CCC" ) );
        assertEquals( new UserKey( "CCC" ), PortalSecurityHolder.getImpersonatedUser() );
        PortalSecurityHolder.removeImpersonatedUser();
        assertEquals( new UserKey( "ABC" ), PortalSecurityHolder.getImpersonatedUser() );
    }
}

