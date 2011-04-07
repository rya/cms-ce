/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.enonic.cms.domain.security.user.UserKey;

public final class SecurityHolderAdmin
{
    private static final String USER_KEY = "user";

    public static UserKey getUser()
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            return (UserKey) attr.getAttribute( USER_KEY, RequestAttributes.SCOPE_SESSION );
        }
        return null;
    }

    public static void setUser( UserKey userKey )
    {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if ( attr != null )
        {
            attr.setAttribute( USER_KEY, userKey, RequestAttributes.SCOPE_SESSION );
        }
    }
}