/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.SecurityHolder;

import com.enonic.cms.core.security.user.User;

public final class LoginCheckInterceptor
    extends HandlerInterceptorAdapter
{
    private SecurityService securityService;

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Override
    public boolean preHandle( HttpServletRequest req, HttpServletResponse res, Object handler )
        throws Exception
    {
        if ( securityService.getLoggedInPortalUser().isAnonymous() )
        {
            loginAdminWebUser( req );
        }

        if ( securityService.getLoggedInPortalUser().isAnonymous() )
        {
            res.sendRedirect( AdminHelper.getAdminPath( req, false ) );
            return false;
        }

        return true;
    }

    private void loginAdminWebUser( HttpServletRequest request )
    {
        HttpSession session = request.getSession( false );
        if ( session != null )
        {
            User adminUser = securityService.getLoggedInAdminConsoleUser();
            if ( adminUser != null )
            {
                SecurityHolder.setUser( adminUser.getKey() );
            }
        }
    }
}
