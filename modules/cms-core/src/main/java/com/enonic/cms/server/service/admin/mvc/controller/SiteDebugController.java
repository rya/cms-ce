/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.business.SitePathResolver;

public abstract class SiteDebugController
    extends AbstractController
{
    private SitePathResolver sitePathResolver;

    protected SecurityService securityService;

    public SiteDebugController()
    {
        setCacheSeconds( 0 );
        setUseCacheControlHeader( false );
        setUseExpiresHeader( false );
    }

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    protected String getAdminBaseUrl( HttpServletRequest request )
    {
        return AdminHelper.getAdminPath( request, true ) + "/";
    }

    protected String getDebugBaseUrlWithHost( HttpServletRequest request, String suffix )
    {
        return AdminHelper.getAdminPath( request, false ) + "/site/" + suffix;
    }

    protected SitePath getSitePath( HttpServletRequest request )
    {
        return sitePathResolver.resolveSitePath( request );
    }

    protected SiteKey getSiteKey( HttpServletRequest request )
    {
        return sitePathResolver.resolveSiteKey( request );
    }
}
