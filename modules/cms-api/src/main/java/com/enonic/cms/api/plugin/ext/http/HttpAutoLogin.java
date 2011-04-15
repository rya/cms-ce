package com.enonic.cms.api.plugin.ext.http;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines the auto login plugin.
 */
public abstract class HttpAutoLogin
    extends HttpProcessor
{
    /**
     * Return the user name of person who should be logged in.
     */
    public abstract String getAuthenticatedUser( HttpServletRequest request )
        throws Exception;


    /**
     * Checks if the current logged in user is the same as the autologin user
     * If the autologin user is different than current user, the current user will be logged out before
     * logging in the new autologin user
     *
     * @return false if the currentuser is not equal to the autologin user
     */
    public boolean validateCurrentUser( String currentUser, String usertoreName, HttpServletRequest request )
    {
        return true;
    }
}
