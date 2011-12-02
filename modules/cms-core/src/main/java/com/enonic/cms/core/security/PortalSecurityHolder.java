/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.enonic.cms.core.security.user.UserKey;

/**
 * This class holds the logged in user per thread basis or in session.
 */
public final class PortalSecurityHolder
{
    /**
     * Name for user in session.
     */
    private final static String LOGGED_IN_USER_KEY = "vertical_user";

    /**
     * Name for user in session.
     */
    private final static String IMPERSONATED_USER_KEY = "vertical_user_run_as";

    private final static ThreadLocal<UserKey> LOGGED_IN_USER_BY_THREAD = new ThreadLocal<UserKey>();

    private final static ThreadLocal<UserKey> IMPERSONATED_USER_BY_THREAD = new ThreadLocal<UserKey>();

    private static UserKey ANON_USER;

    public static UserKey getUser()
    {
        UserKey user = doGetUser( false );
        return user != null ? user : ANON_USER;
    }

    public static UserKey getImpersonatedUser()
    {
        UserKey impersonatedUser = doGetUser( true );
        return impersonatedUser != null ? impersonatedUser : getUser();
    }

    public static UserKey getAnonUser()
    {
        return ANON_USER;
    }

    public static void setUser( UserKey user )
    {
        doSetUser( user, false );
    }

    public static void setImpersonatedUser( UserKey user )
    {
        doSetUser( user, true );
    }

    public static void setAnonUser( UserKey user )
    {
        ANON_USER = user;
    }

    /**
     * Return the user in thread or session.
     */
    private static UserKey doGetUser( boolean impersonated )
    {
        if ( isInSession() )
        {
            return doGetUserInSession( impersonated );
        }
        else
        {
            return doGetUserInThread( impersonated );
        }
    }

    private static UserKey doGetUserInThread( boolean impersonated )
    {
        if ( impersonated )
        {
            return IMPERSONATED_USER_BY_THREAD.get();
        }
        else
        {
            return LOGGED_IN_USER_BY_THREAD.get();
        }
    }

    private static UserKey doGetUserInSession( boolean impersonated )
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            String key = impersonated ? IMPERSONATED_USER_KEY : LOGGED_IN_USER_KEY;
            return (UserKey) attr.getAttribute( key, RequestAttributes.SCOPE_SESSION );
        }
        else
        {
            return null;
        }
    }

    private static void doSetUser( UserKey user, boolean impersonated )
    {
        if ( isInSession() )
        {
            doSetUserInSession( user, impersonated );
        }
        else
        {
            doSetUserInThread( user, impersonated );
        }
    }

    private static void doSetUserInThread( UserKey user, boolean impersonated )
    {
        if ( impersonated )
        {
            IMPERSONATED_USER_BY_THREAD.set( user );
        }
        else
        {
            LOGGED_IN_USER_BY_THREAD.set( user );
        }
    }

    private static void doSetUserInSession( UserKey user, boolean impersonated )
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            String key = impersonated ? IMPERSONATED_USER_KEY : LOGGED_IN_USER_KEY;
            attr.setAttribute( key, user, RequestAttributes.SCOPE_SESSION );
        }
    }

    private static boolean isInSession()
    {
        return RequestContextHolder.getRequestAttributes() != null;
    }
}
