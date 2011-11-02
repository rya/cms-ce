/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.portal.support.LoginPagePathResolverService;

public class LoginController
    extends AbstractSiteController
{
    private LoginPagePathResolverService loginPagePathResolverService;

    public void setLoginPagePathResolverService( LoginPagePathResolverService value )
    {
        this.loginPagePathResolverService = value;
    }

    @Override
    protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response,
                                                 final SitePath sitePath)
        throws Exception
    {
        // Get check and eventually set original sitePath
        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( originalSitePath == null )
        {
            originalSitePath = sitePathResolver.resolveSitePath( request );
            siteService.checkSiteExist( originalSitePath.getSiteKey() );
            request.setAttribute( Attribute.ORIGINAL_SITEPATH, originalSitePath );
        }

        // Get and set the current sitePath
        final SitePath currentSitePath = sitePathResolver.resolveSitePath( request );
        request.setAttribute( Attribute.CURRENT_SITEPATH, currentSitePath );

        final SitePath sitePathAndParams = loginPagePathResolverService.resolvePathToDefaultPageInMenu( currentSitePath );
        return siteRedirectAndForwardHelper.getForwardModelAndView( request, sitePathAndParams );
    }
}
