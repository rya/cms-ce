/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.portal.mvc.view.SiteCustomForwardView;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;

/**
 * This class implements a file controller that returns the actual referenced file in the servlet context.
 */
public class SitePreviewController
    extends AbstractController
{

    private SitePathResolver sitePathResolver;

    private SecurityService securityService;

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    private void loginAdminWebUser( HttpServletRequest request )
    {
        HttpSession session = request.getSession( false );
        if ( session != null )
        {
            User adminUser = securityService.getLoggedInAdminConsoleUser();
            if ( adminUser != null )
            {
                PortalSecurityHolder.setLoggedInUser( adminUser.getKey() );
            }
        }
    }

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        final User originalLoggedInPortalUser = securityService.getLoggedInPortalUser();
        if ( originalLoggedInPortalUser.isAnonymous() )
        {
            loginAdminWebUser( request );
            if ( securityService.getLoggedInPortalUser().isAnonymous() )
            {
                // User is not logged in, redirect to admin login
                return new ModelAndView( "redirect:" + AdminHelper.getAdminPath( request, false ) );
            }
        }
        else if ( !originalLoggedInPortalUser.equals( securityService.getLoggedInAdminConsoleUser() ) )
        {
            loginAdminWebUser( request );
            if ( securityService.getLoggedInPortalUser().isAnonymous() )
            {
                // User is not logged in, redirect to admin login
                return new ModelAndView( "redirect:" + AdminHelper.getAdminPath( request, false ) );
            }
        }

        SitePath sitePath = sitePathResolver.resolveSitePath( request );
        String url = "/site" + sitePath.asString();
        // We need to url-encode the path again,
        // since forwarding to an decoded url fails in some application servers (Oracle)
        url = UrlPathEncoder.encodeUrlPath( url, VerticalProperties.getVerticalProperties().getUrlCharacterEncoding() );

        request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

        Map<String, Object> model = new HashMap<String, Object>();
        model.put( "path", url );
        model.put( "requestParams", sitePath.getParams() );
        return new ModelAndView( new SiteCustomForwardView(), model );
    }

}
