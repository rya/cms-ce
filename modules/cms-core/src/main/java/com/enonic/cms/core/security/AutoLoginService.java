/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.esl.servlet.http.CookieUtil;

import com.enonic.cms.business.login.LoginService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;

public class AutoLoginService
{
    private SecurityService securityService;

    private LoginService loginService;

    public UserEntity autologinWithRemoteUser( HttpServletRequest request )
    {
        UserEntity user = resolveUserFromRequest( request );
        if ( user == null )
        {
            return securityService.getAnonymousUser();
        }
        if ( !user.isAnonymous() )
        {
            SecurityHolder.setUser( user.getKey() );
        }
        return user;
    }

    /**
     * Checks the cookies to see if a user is allready logged in on the site.
     * The login information in the cookie have to match user data in the database.
     *
     * @param siteKey  The site to check if the user is logged in.
     * @param request  The Http Request, containing the cookies.
     * @param response The Http Response, on which the cookie is cleared, if the user has expired.
     * @return The logged in user, if it exists, otherwise, the anonymous user.
     */
    public UserEntity autologinWithCookie( SiteKey siteKey, HttpServletRequest request, HttpServletResponse response )
    {
        UserEntity user = resolveUserFromCookie( siteKey, request, response );
        if ( user == null )
        {
            return securityService.getAnonymousUser();
        }
        if ( !user.isAnonymous() )
        {
            SecurityHolder.setUser( user.getKey() );
            return user;
        }
        return user;
    }

    private UserEntity resolveUserFromCookie( SiteKey siteKey, HttpServletRequest request, HttpServletResponse response )
    {

        String cookieName = "guid-" + siteKey.toInt();
        Cookie cookie = CookieUtil.getCookie( request, cookieName );
        if ( cookie == null || cookie.getValue() == null )
        {
            return null;
        }

        String cookieGUID = cookie.getValue();

        if ( cookieGUID.length() == 0 )
        {
            cookie.setValue( null );
            response.addCookie( cookie );
            return null;
        }

        UserKey userKey = loginService.getRememberedLogin( cookieGUID, siteKey );
        if ( userKey == null )
        {
            cookie.setValue( null );
            response.addCookie( cookie );
            return null;
        }
        return securityService.getUser( userKey );
    }

    private UserEntity resolveUserFromRequest( HttpServletRequest request )
    {

        String remoteUserUID = request.getRemoteUser();
        if ( remoteUserUID == null )
        {
            return null;
        }

        return securityService.getUserFromDefaultUserStore( remoteUserUID );
    }

    public void setLoginService( LoginService loginService )
    {
        this.loginService = loginService;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

}
