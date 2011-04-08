/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.enonic.cms.core.structure.SiteService;

import com.enonic.cms.business.SitePathResolver;
import com.enonic.cms.business.SiteRedirectAndForwardHelper;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SitePath;

public abstract class AbstractSiteCommandController
        extends AbstractCommandController
{

    private static final Logger LOG = LoggerFactory.getLogger( AbstractSiteCommandController.class );

    protected SiteService siteService;

    protected SiteRedirectAndForwardHelper redirectAndForwardHelper;

    protected SitePathResolver sitePathResolver;

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper value )
    {
        this.redirectAndForwardHelper = value;
    }

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    protected final ModelAndView handle( HttpServletRequest request, HttpServletResponse response, Object command,
                                         BindException errors )
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

        return logAndReturn( handle( request, response, command, errors, currentSitePath ) );
    }

    private ModelAndView logAndReturn( ModelAndView modelAndView )
    {
        if ( modelAndView != null )
        {
            LOG.debug( modelAndView.getViewName() );
        }
        return modelAndView;
    }

    protected abstract ModelAndView handle( HttpServletRequest request, HttpServletResponse response, Object command,
                                            BindException errors, SitePath sitePath )
            throws Exception;
}
