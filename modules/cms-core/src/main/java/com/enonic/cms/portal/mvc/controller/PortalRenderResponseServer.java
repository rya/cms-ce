/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Preconditions;

import com.enonic.esl.util.DigestUtil;

import com.enonic.cms.framework.util.HttpCacheControlSettings;
import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.business.SiteBasePathResolver;
import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.SiteRedirectAndForwardHelper;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteBasePath;
import com.enonic.cms.domain.SiteBasePathAndSitePath;
import com.enonic.cms.domain.SiteBasePathAndSitePathToStringBuilder;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.portal.PortalRequest;
import com.enonic.cms.domain.portal.PortalResponse;
import com.enonic.cms.domain.portal.RedirectInstruction;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;

/**
 * May 26, 2009
 */
public class PortalRenderResponseServer
{
    private static final int SECOND_IN_MILLIS = 1000;

    private SitePropertiesService sitePropertiesService;

    private SiteRedirectAndForwardHelper siteRedirectAndForwardHelper;

    private UserDao userDao;

    private SiteDao siteDao;

    public ModelAndView serveResponse( PortalRequest request, PortalResponse response, HttpServletResponse httpResponse,
                                       HttpServletRequest httpRequest )
        throws IOException
    {
        if ( response.hasRedirectInstruction() )
        {
            return serveRedirect( response, httpResponse, httpRequest );
        }
        else if ( response.isForwardToSitePath() )
        {
            return serveForwardToSitePathResponse( response, httpRequest );
        }
        else
        {
            return servePageResponse( request, response, httpResponse, httpRequest );
        }
    }

    private ModelAndView servePageResponse( PortalRequest request, PortalResponse response, HttpServletResponse httpResponse,
                                            HttpServletRequest httpRequest )
        throws IOException
    {
        HttpServletUtil.setDateHeader( httpResponse, request.getRequestTime().toDate() );

        final SitePath requestedPath = request.getSitePath();
        final SiteKey requestedSiteKey = requestedPath.getSiteKey();
        final UserEntity requester = userDao.findByKey( request.getRequester() );
        final SiteEntity site = siteDao.findByKey( requestedSiteKey );
        final boolean cacheHeadersEnabled = resolveCacheHeadersEnabledForSite( requestedSiteKey );
        boolean forceNoCache = false;

        if ( isInPreviewMode( httpRequest ) || RenderTrace.isTraceOn() )
        {
            forceNoCache = true;
            final DateTime expirationTime = request.getRequestTime();
            setHttpCacheHeaders( requester, request.getRequestTime(), expirationTime, httpResponse, forceNoCache, site );
        }
        else if ( cacheHeadersEnabled )
        {
            forceNoCache = resolveForceNoCacheForSite( requestedSiteKey );
            final DateTime expirationTime = resolveExpirationTime( request.getRequestTime(), response.getExpirationTime() );
            setHttpCacheHeaders( requester, request.getRequestTime(), expirationTime, httpResponse, forceNoCache, site );
        }

        // filter response with any response plugins
        String content = response.getContent();
        response.setContent( content );

        boolean handleEtagLogic = cacheHeadersEnabled && !forceNoCache;
        if ( handleEtagLogic )
        {
            // Handling etag logic if cache headers are enabled
            if ( StringUtils.isEmpty( content ) )
            {
                httpResponse.setContentType( response.getHttpContentType() );
                writeContent( httpResponse, response.getContentAsBytes() );
            }
            else
            {
                final String etagFromContent = resolveEtag( content );
                final boolean contentIsModified = isContentModified( httpRequest, etagFromContent );
                if ( contentIsModified )
                {
                    HttpServletUtil.setEtag( httpResponse, etagFromContent );
                    httpResponse.setContentType( response.getHttpContentType() );
                    writeContent( httpResponse, response.getContentAsBytes() );
                }
                else
                {
                    httpResponse.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
                }
            }
        }
        else
        {
            httpResponse.setContentType( response.getHttpContentType() );
            writeContent( httpResponse, response.getContentAsBytes() );
        }

        return null;
    }

    private String resolveEtag( String content )
    {
        Preconditions.checkArgument( StringUtils.isNotEmpty( content ) );
        return "content_" + DigestUtil.generateSHA( content );
    }

    private boolean isContentModified( HttpServletRequest req, String etagFromContent )
    {
        return HttpServletUtil.isContentModifiedAccordingToIfNoneMatchHeader( req, etagFromContent );
    }

    private void writeContent( HttpServletResponse httpResponse, byte[] content )
        throws IOException
    {
        httpResponse.setContentLength( content.length );

        OutputStream out = httpResponse.getOutputStream();
        out.write( content );
    }

    private void setHttpCacheHeaders( final User requester, final DateTime requestTime, final DateTime expirationTime,
                                      final HttpServletResponse httpResponse, final boolean forceNoCache, final SiteEntity site )
    {
        final Interval maxAge = new Interval( requestTime, expirationTime );

        @SuppressWarnings({"UnnecessaryLocalVariable"}) boolean notCachableByClient = forceNoCache;
        if ( notCachableByClient )
        {
            HttpServletUtil.setCacheControlNoCache( httpResponse );
        }
        else
        {
            HttpCacheControlSettings cacheControlSettings = new HttpCacheControlSettings();

            // To eliminate proxy caching of pages (decided by TSI)
            cacheControlSettings.publicAccess = false;

            boolean setCacheTimeToZero = dynamicResolversEnabled( site );

            if ( setCacheTimeToZero )
            {
                cacheControlSettings.maxAgeSecondsToLive = new Long( 0 );
                HttpServletUtil.setExpiresHeader( httpResponse, requestTime.toDate() );
            }
            else
            {
                cacheControlSettings.maxAgeSecondsToLive = maxAge.toDurationMillis() / SECOND_IN_MILLIS;
                HttpServletUtil.setExpiresHeader( httpResponse, expirationTime.toDate() );
            }

            HttpServletUtil.setCacheControl( httpResponse, cacheControlSettings );
        }
    }

    private boolean dynamicResolversEnabled( SiteEntity site )
    {
        return site.isDeviceClassificationEnabled() || site.isLocalizationEnabled();
    }

    private ModelAndView serveRedirect( PortalResponse response, HttpServletResponse httpResponse, HttpServletRequest httpRequest )
        throws IOException
    {

        RedirectInstruction redirectInstruction = response.getRedirectInstruction();

        int redirectStatus =
            redirectInstruction.isPermanentRedirect() ? HttpServletResponse.SC_MOVED_PERMANENTLY : HttpServletResponse.SC_MOVED_TEMPORARILY;

        if ( redirectInstruction.hasRedirectSitePath() )
        {
            return serveRedirectToSitePath( redirectInstruction.getRedirectSitePath(), redirectStatus, httpResponse, httpRequest );
        }
        else if ( redirectInstruction.hasRedirectUrl() )
        {
            return serveRedirectResponse( redirectInstruction.getRedirectUrl() );
        }
        else
        {
            throw new IllegalStateException( "Redirect must have target url or sitepath set" );
        }
    }


    private ModelAndView serveRedirectToSitePath( SitePath toSitePath, int redirectStatus, HttpServletResponse httpResponse,
                                                  HttpServletRequest httpRequest )
        throws IOException
    {

        SiteBasePath siteBasePath = SiteBasePathResolver.resolveSiteBasePath( httpRequest, toSitePath.getSiteKey() );
        SiteBasePathAndSitePath siteBasePathAndSitePath = new SiteBasePathAndSitePath( siteBasePath, toSitePath );

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( "UTF-8" );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( false );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( true );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );
        String redirectUrl = siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );

        String encodedRedirectUrl = httpResponse.encodeRedirectURL( redirectUrl );

        if ( redirectStatus == HttpServletResponse.SC_MOVED_PERMANENTLY )
        {
            httpResponse.setStatus( redirectStatus );
            httpResponse.setHeader( "Location", encodedRedirectUrl );
            return null;
        }
        else
        {
            httpResponse.setStatus( HttpServletResponse.SC_MOVED_TEMPORARILY );
            httpResponse.setHeader( "Location", encodedRedirectUrl );
            return null;
        }
    }

    private ModelAndView serveForwardToSitePathResponse( PortalResponse response, HttpServletRequest httpRequest )
    {
        return siteRedirectAndForwardHelper.getForwardModelAndView( httpRequest, response.getForwardToSitePath() );
    }

    private ModelAndView serveRedirectResponse( String redirectUrl )
    {
        return new ModelAndView( "redirect:" + redirectUrl );
    }

    private DateTime resolveExpirationTime( DateTime requestTime, DateTime expirationTime )
    {
        if ( expirationTime == null )
        {
            return requestTime;
        }

        if ( expirationTime.isBefore( requestTime ) )
        {
            return requestTime;
        }

        return expirationTime;
    }

    private boolean resolveCacheHeadersEnabledForSite( final SiteKey requestedSiteKey )
    {
        return sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE_HEADERS_ENABLED, requestedSiteKey );
    }

    private boolean resolveForceNoCacheForSite( final SiteKey requestedSiteKey )
    {
        return sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.PAGE_CACHE_HEADERS_FORCENOCACHE, requestedSiteKey );
    }

    private boolean isInPreviewMode( HttpServletRequest httpRequest )
    {
        String previewEnabled = (String) httpRequest.getAttribute( Attribute.PREVIEW_ENABLED );
        return "true".equals( previewEnabled );
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public void setSiteRedirectAndForwardHelper( SiteRedirectAndForwardHelper siteRedirectAndForwardHelper )
    {
        this.siteRedirectAndForwardHelper = siteRedirectAndForwardHelper;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }
}
