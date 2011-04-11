/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.structure.SiteService;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SitePath;

/**
 * Apr 17, 2009
 */
public abstract class AbstractPortalController
    extends AbstractController
{
    private SitePathResolver sitePathResolver;

    private SiteService siteService;

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
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
        SitePath currentSitePath = sitePathResolver.resolveSitePath( request );
        request.setAttribute( Attribute.CURRENT_SITEPATH, currentSitePath );

        return handleRequestInternal( request, response, currentSitePath );
    }

    protected abstract ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response,
                                                           SitePath sitePathAndParams )
        throws Exception;


    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }
}
