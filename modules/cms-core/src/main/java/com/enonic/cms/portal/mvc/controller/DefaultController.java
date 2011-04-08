/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.OriginalUrlResolver;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.core.security.AutoLoginService;
import com.enonic.cms.portal.PortalRequestService;

import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.portal.livetrace.PortalRequestTracer;
import com.enonic.cms.business.preview.PreviewService;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.portal.PortalRequest;
import com.enonic.cms.domain.portal.PortalResponse;
import com.enonic.cms.domain.portal.RedirectInstruction;
import com.enonic.cms.domain.portal.ShoppingCart;
import com.enonic.cms.domain.portal.VerticalSession;
import com.enonic.cms.domain.security.user.User;

/**
 * Apr 17, 2009
 */
public class DefaultController
    extends AbstractPortalController
{
    private static String LOCAL_PREFIX = "/_default";

    private SecurityService securityService;

    private SitePropertiesService sitePropertiesService;

    private PortalRequestService portalRequestService;

    private PortalRenderResponseServer portalRenderResultServer;

    private AutoLoginService autoLoginService;

    private TimeService timeService;

    private PreviewService previewService;

    private LivePortalTraceService livePortalTraceService;

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response, SitePath sitePath )
        throws Exception
    {
        try
        {
            return handleDefaultRequest( request, response, sitePath );
        }
        catch ( Exception e )
        {
            SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
            throw new DefaultRequestException( originalSitePath, request.getHeader( "referer" ), e );
        }
    }

    private ModelAndView handleDefaultRequest( HttpServletRequest httpRequest, HttpServletResponse httpResponse, SitePath sitePath )
        throws Exception
    {
        HttpSession httpSession = httpRequest.getSession( true );

        if ( sitePath.getLocalPath().startsWith( LOCAL_PREFIX ) )
        {
            sitePath = sitePath.createNewInSameSite( sitePath.getLocalPath().subtractPath( LOCAL_PREFIX ), sitePath.getParams() );
        }
        else if ( !sitePath.getLocalPath().startsWithSlash() )
        {
            return redirectToRoot( httpRequest, httpResponse, sitePath );
        }

        String originalUrl = OriginalUrlResolver.get().resolveOriginalUrl( httpRequest );
        SitePath originalSitePath = (SitePath) httpRequest.getAttribute( Attribute.ORIGINAL_SITEPATH );

        final PortalRequestTrace portalRequestTrace = PortalRequestTracer.startTracing( originalUrl, livePortalTraceService );

        try
        {
            PortalRequestTracer.traceMode( portalRequestTrace, previewService );
            PortalRequestTracer.traceHttpRequest( portalRequestTrace, httpRequest );
            PortalRequestTracer.traceRequestedSitePath( portalRequestTrace, sitePath );

            PortalRequest request = new PortalRequest();
            request.setRequestTime( timeService.getNowAsDateTime() );
            request.setSitePath( sitePath );
            request.setRequestParams( getRequestParameters( httpRequest ) );
            request.setTicketId( httpSession.getId() );
            request.setOriginalSitePath( originalSitePath );
            request.setShoppingCart( getAndEnsureShoppingCartOnHttpSession( httpSession ) );
            request.setVerticalSession( getAndEnsureVerticalSessionOnHttpSession( httpSession ) );
            request.setHttpReferer( httpRequest.getHeader( "referer" ) );
            request.setOriginalUrl( originalUrl );

            User loggedInPortalUser = securityService.getLoggedInPortalUser();
            if ( loggedInPortalUser.isAnonymous() )
            {
                if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED,
                                                                 sitePath.getSiteKey() ) )
                {
                    loggedInPortalUser = autoLoginService.autologinWithRemoteUser( httpRequest );
                }
            }
            if ( loggedInPortalUser.isAnonymous() )
            {
                if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_REMEMBER_ME_COOKIE_ENABLED,
                                                                 sitePath.getSiteKey() ) )
                {
                    loggedInPortalUser = autoLoginService.autologinWithCookie( sitePath.getSiteKey(), httpRequest, httpResponse );
                }
            }
            request.setRequester( loggedInPortalUser.getKey() );
            request.setPreviewContext( previewService.getPreviewContext() );

            PortalResponse response = portalRequestService.processRequest( request );

            return portalRenderResultServer.serveResponse( request, response, httpResponse, httpRequest );

        }
        finally
        {
            PortalRequestTracer.stopTracing( portalRequestTrace, livePortalTraceService );
        }
    }

    private ModelAndView redirectToRoot( HttpServletRequest httpRequest, HttpServletResponse httpResponse, SitePath sitePath )
        throws Exception
    {
        sitePath = sitePath.createNewInSameSite( Path.ROOT, sitePath.getParams() );
        PortalRequest request = new PortalRequest();
        request.setRequestTime( new DateTime() );
        request.setSitePath( sitePath );
        request.setRequestParams( getRequestParameters( httpRequest ) );

        RedirectInstruction redirectInstruction = new RedirectInstruction( sitePath );
        redirectInstruction.setPermanentRedirect( true );

        PortalResponse response = PortalResponse.createRedirect( redirectInstruction );
        return portalRenderResultServer.serveResponse( request, response, httpResponse, httpRequest );
    }

    private HashMap<String, Object> getRequestParameters( HttpServletRequest request )
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        Enumeration parameterNames = request.getParameterNames();
        while ( parameterNames.hasMoreElements() )
        {

            String name = (String) parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues( name );

            if ( parameterValues.length == 1 )
            {
                String parameter = parameterValues[0];
                parameters.put( name, parameter );
            }
            else
            {
                parameters.put( name, parameterValues );
            }
        }

        return parameters;
    }

    private ShoppingCart getAndEnsureShoppingCartOnHttpSession( HttpSession httpSession )
    {
        ShoppingCart cart = (ShoppingCart) httpSession.getAttribute( "shoppingcart" );
        if ( cart == null )
        {
            cart = new ShoppingCart();
            httpSession.setAttribute( "shoppingcart", cart );
        }
        return cart;
    }

    private VerticalSession getAndEnsureVerticalSessionOnHttpSession( HttpSession httpSession )
    {
        VerticalSession vsession = (VerticalSession) httpSession.getAttribute( VerticalSession.VERTICAL_SESSION_OBJECT );
        if ( vsession == null )
        {
            vsession = new VerticalSession();
            httpSession.setAttribute( VerticalSession.VERTICAL_SESSION_OBJECT, vsession );
        }
        return vsession;
    }


    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }

    public void setSitePropertiesService( SitePropertiesService service )
    {
        sitePropertiesService = service;
    }

    public void setPortalRequestService( PortalRequestService portalRequestService )
    {
        this.portalRequestService = portalRequestService;
    }

    public void setPortalRenderResultServer( PortalRenderResponseServer portalRenderResultServer )
    {
        this.portalRenderResultServer = portalRenderResultServer;
    }

    public void setAutoLoginService( AutoLoginService autoLoginService )
    {
        this.autoLoginService = autoLoginService;
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }
}
