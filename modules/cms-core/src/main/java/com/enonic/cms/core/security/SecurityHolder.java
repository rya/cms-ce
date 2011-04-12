/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import javax.security.auth.Subject;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.enonic.cms.core.security.user.UserKey;

/**
 * This class holds the logged in user per thread basis or in session.
 */
public final class SecurityHolder
{
    /**
     * Name for user in session.
     */
    private final static String USER_KEY = "vertical_user";

    /**
     * Name for user in session.
     */
    private final static String RUN_AS_USER_KEY = "vertical_user_run_as";

    /**
     * Name for subject in session.
     */
    private final static String SUBJECT_KEY = "cms:subject";

    private final static ThreadLocal<UserKey> USER = new ThreadLocal<UserKey>();

    private final static ThreadLocal<UserKey> RUN_AS_USER = new ThreadLocal<UserKey>();

    private final static ThreadLocal<Subject> SUBJECT = new ThreadLocal<Subject>();

    private static UserKey ANON_USER;

    public static UserKey getUser()
    {
        UserKey user = doGetUser( false );
        return user != null ? user : ANON_USER;
    }

    public static UserKey getRunAsUser()
    {
        UserKey runAsUser = doGetUser( true );
        return runAsUser != null ? runAsUser : getUser();
    }

    public static UserKey getAnonUser()
    {
        return ANON_USER;
    }

    public static void setUser( UserKey user )
    {
        doSetUser( user, false );
    }

    public static void setRunAsUser( UserKey user )
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
    private static UserKey doGetUser( boolean runAs )
    {
        if ( isInSession() )
        {
            return doGetUserInSession( runAs );
        }
        else
        {
            return doGetUserInThread( runAs );
        }
    }

    private static UserKey doGetUserInThread( boolean runAs )
    {
        if ( runAs )
        {
            return RUN_AS_USER.get();
        }
        else
        {
            return USER.get();
        }
    }

    private static UserKey doGetUserInSession( boolean runAs )
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            String key = runAs ? RUN_AS_USER_KEY : USER_KEY;
            return (UserKey) attr.getAttribute( key, RequestAttributes.SCOPE_SESSION );
        }
        else
        {
            return null;
        }
    }

    private static void doSetUser( UserKey user, boolean runAs )
    {
        if ( isInSession() )
        {
            doSetUserInSession( user, runAs );
        }
        else
        {
            doSetUserInThread( user, runAs );
        }
    }

    private static void doSetUserInThread( UserKey user, boolean runAs )
    {
        if ( runAs )
        {
            RUN_AS_USER.set( user );
        }
        else
        {
            USER.set( user );
        }
    }

    private static void doSetUserInSession( UserKey user, boolean runAs )
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            String key = runAs ? RUN_AS_USER_KEY : USER_KEY;
            attr.setAttribute( key, user, RequestAttributes.SCOPE_SESSION );
        }
    }

    private static boolean isInSession()
    {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    public static Subject getSubject()
    {
        if ( isInSession() )
        {
            return doGetSubjectInSession();
        }
        else
        {
            return doGetSubjectInThread();
        }
    }

    private static Subject doGetSubjectInThread()
    {
        return SUBJECT.get();
    }

    private static Subject doGetSubjectInSession()
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            return (Subject) attr.getAttribute( SUBJECT_KEY, RequestAttributes.SCOPE_SESSION );
        }
        else
        {
            return null;
        }
    }

    /**
     * Set the subject in thread and session.
     */
    public static void setSubject( Subject subject )
    {
        if ( isInSession() )
        {
            doSetSubjectInSession( subject );
        }
        else
        {
            doSetSubjectInThread( subject );
        }
    }

    private static void doSetSubjectInThread( Subject subject )
    {
        SUBJECT.set( subject );
    }

    private static void doSetSubjectInSession( Subject subject )
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            attr.setAttribute( SUBJECT_KEY, subject, RequestAttributes.SCOPE_SESSION );
        }
    }
}
