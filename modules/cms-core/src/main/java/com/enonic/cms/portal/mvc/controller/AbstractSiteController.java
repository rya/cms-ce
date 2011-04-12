/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SiteRedirectAndForwardHelper;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SitePath;

public abstract class AbstractSiteController
        extends AbstractController
{

    private static final Logger LOG = LoggerFactory.getLogger( AbstractSiteController.class );

    protected SiteService siteService;

    protected SiteDao siteDao;

    protected SiteRedirectAndForwardHelper redirectAndForwardHelper;

    protected SitePathResolver sitePathResolver;

    protected PresentationService presentationService;

    protected SecurityService securityService;

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    public void setSiteDao( SiteDao value )
    {
        this.siteDao = value;
    }

    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper value )
    {
        this.redirectAndForwardHelper = value;
    }

    public void setPresentationService( PresentationService value )
    {
        this.presentationService = value;
    }

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }


    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    protected final ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
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

        return logAndReturn( handleRequestInternal( request, response, currentSitePath ) );
    }

    private ModelAndView logAndReturn( ModelAndView modelAndView )
    {
        if ( modelAndView != null )
        {
            LOG.debug( modelAndView.getViewName() );
        }
        return modelAndView;
    }


    /**
     * Process the site path and return a {@link org.springframework.web.servlet.ModelAndView}.
     */
    protected abstract ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response,
                                                           SitePath sitePath )
            throws Exception;

    protected void enableHttpCacheHeaders( HttpServletResponse response, SitePath sitePath, DateTime now, Integer siteCacheSettingsMaxAge,
                                           boolean anonymousAccess )
    {
        int maxAge;

        boolean cacheForever = hasTimestampParameter( sitePath );

        if ( cacheForever )
        {
            maxAge = HttpCacheControlSettings.CACHE_FOREVER_SECONDS;
        }
        else
        {
            maxAge = siteCacheSettingsMaxAge;
        }

        final DateTime expirationTime = now.plusSeconds( maxAge );

        final HttpCacheControlSettings cacheControlSettings = new HttpCacheControlSettings();
        cacheControlSettings.maxAgeSecondsToLive = (long) maxAge;
        cacheControlSettings.publicAccess = anonymousAccess;
        HttpServletUtil.setExpiresHeader( response, expirationTime.toDate() );
        HttpServletUtil.setCacheControl( response, cacheControlSettings );
    }

    protected boolean hasTimestampParameter( SitePath sitePath )
    {
        String timestamp = sitePath.getParam( "_ts" );
        return StringUtils.isNotBlank( timestamp );
    }

}
