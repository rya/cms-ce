/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.enonic.esl.servlet.http.HttpServletRequestWrapper;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.PathAndParams;
import com.enonic.cms.core.PathAndParamsToStringBuilder;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.portal.PageRequestContext;
import com.enonic.cms.domain.portal.PageRequestContextResolver;
import com.enonic.cms.domain.portal.SiteNotFoundException;
import com.enonic.cms.core.structure.SiteEntity;

/**
 * This class implements the debug controller.
 */
public final class SiteDebugViewController
        extends SiteDebugController
{
    private ContentDao contentDao;

    private SiteDao siteDao;

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        SitePath sitePath = getSitePath( request );

        SiteEntity site = resolveSite( sitePath );

        PageRequestContextResolver pageRequestContextResolver = new PageRequestContextResolver( contentDao );
        PageRequestContext pageRequestContext = pageRequestContextResolver.resolvePageRequestContext( site, sitePath );

        PathAndParams redirectIfContentTitleChanged = resolveRedirectIfContentNameChanged( pageRequestContext );
        if ( redirectIfContentTitleChanged != null )
        {
            String debugPath = AdminHelper.getDebugPath( request, sitePath.getSiteKey() );
            PathAndParamsToStringBuilder strBuilder = new PathAndParamsToStringBuilder();
            String pathAndParamsAsString = strBuilder.toString( redirectIfContentTitleChanged );
            if ( pathAndParamsAsString.startsWith( "/" ) )
            {
                pathAndParamsAsString = pathAndParamsAsString.substring( 1 );
            }
            String redirectURL = "redirect:" + debugPath + pathAndParamsAsString;
            return new ModelAndView( redirectURL );
        }

        // We need to url-encode the path again,
        // since forwarding to an decoded url fails in some application servers (Oracle)
        String url = getEncodedUrlForForwarding( sitePath );

        RequestDispatcher dispatcher = request.getRequestDispatcher( url );

        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request, sitePath.getParams() );

        RenderTrace.enter();
        dispatcher.forward( wrappedRequest, response );
        RenderTrace.exit();
        return null;
    }

    private String getEncodedUrlForForwarding( SitePath sitePath )
    {
        final String urlCharacterEncoding = VerticalProperties.getVerticalProperties().getUrlCharacterEncoding();
        String encodedLocalPath = UrlPathEncoder.encodeUrlPathNoParameters( sitePath.getLocalPath().getPathAsString(),
                                                                            urlCharacterEncoding );
        SitePath newSitePath = new SitePath( sitePath.getSiteKey(), encodedLocalPath, sitePath.getParams() );
        return "/site" + newSitePath.asString();
    }

    private PathAndParams resolveRedirectIfContentNameChanged( PageRequestContext pageRequestContext )
    {
        /*
            This was removed prior to the 4.5.0 release

            In previous versions, the content-url contained a key, which could be used to resolve the content
            even if the title was changed.

            In 4.5, title is no longer used for paths, so changing the title will work, but if changing a content name
            the url will no longer be valid, and atm we have no easy solution to support this.

            If this is to be fixed in later version, ICE will probably have to send the content-key so that the debug-controller
            can resolve the content even if the name has been changed.


            RMY, dec 2010
         */

        return null;
    }

    private SiteEntity resolveSite( final SitePath sitePath )
    {
        final SiteEntity site = siteDao.findByKey( sitePath.getSiteKey() );
        if ( site == null )
        {
            throw new SiteNotFoundException( sitePath.getSiteKey() );
        }
        return site;
    }


    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }
}
