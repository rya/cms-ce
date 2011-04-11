/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.SiteContext;
import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.content.ContentParserService;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.portal.SiteRedirectHelper;
import com.enonic.cms.portal.cache.SiteCachesService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public abstract class AbstractPresentationController
    implements Controller, DisposableBean
{

    protected SiteService siteService;

    protected SitePathResolver sitePathResolver;

    protected VerticalProperties verticalProperties;

    protected SiteRedirectHelper siteRedirectHelper;

    protected SiteDao siteDao;

    protected CategoryDao categoryDao;

    protected ContentDao contentDao;

    protected SecurityService securityService;

    protected UserStoreService userStoreService;

    protected SendMailService sendMailService;

    protected ContentParserService contentParserService;

    protected ContentService contentService;

    protected SiteCachesService siteCachesService;

    public void setSiteCachesService( SiteCachesService value )
    {
        this.siteCachesService = value;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setContentParserService( ContentParserService contentParserService )
    {
        this.contentParserService = contentParserService;
    }

    public void setContentService( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setSiteRedirectHelper( SiteRedirectHelper value )
    {
        this.siteRedirectHelper = value;
    }

    public void setVerticalProperties( VerticalProperties value )
    {
        this.verticalProperties = value;
    }

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    public void setSitePathResolver( SitePathResolver value )
    {
        this.sitePathResolver = value;
    }

    public void setCategoryDao( CategoryDao categoryDao )
    {
        this.categoryDao = categoryDao;
    }

    public void setSecurityService( SecurityService value )
    {
        this.securityService = value;
    }

    public void setUserStoreService( UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }

    public void setSendMailService( SendMailService sendMailService )
    {
        this.sendMailService = sendMailService;
    }

    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {

        // Get check and eventually set original sitePath
        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( originalSitePath == null )
        {
            originalSitePath = sitePathResolver.resolveSitePath( request );
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

    protected SiteContext getSiteContext( SiteKey siteKey )
    {
        return siteService.getSiteContext( siteKey );
    }

    protected SitePath getSitePath( HttpServletRequest request )
    {
        SitePath sitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( sitePath == null )
        {
            sitePath = sitePathResolver.resolveSitePath( request );
        }
        return sitePath;
    }


    public void destroy()
    {
        // nothing more to destroy
    }


}
