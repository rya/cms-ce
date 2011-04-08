/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.client;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.client.ClientHttpServletRequest;

import com.enonic.cms.api.client.model.RenderContentParams;
import com.enonic.cms.api.client.model.RenderPageParams;
import com.enonic.cms.api.client.model.RenderParams;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SectionContentDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.portal.PortalRequestService;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.portal.PathToContentResolver;
import com.enonic.cms.domain.portal.PortalRequest;
import com.enonic.cms.domain.portal.PortalResponse;
import com.enonic.cms.domain.portal.ShoppingCart;
import com.enonic.cms.domain.portal.VerticalSession;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * Sep 3, 2009
 */
public class InternalClientRenderService
{
    @Autowired
    private PortalRequestService portalRequestService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private SectionContentDao sectionContentDao;

    public Document renderPage( final RenderPageParams params )
    {

        checkCommonRenderParams( params );

        if ( params.menuItemKey < 0 )
        {
            throw new IllegalArgumentException( "Illegal value for required parameter 'menuItemKey' : " + params.menuItemKey );
        }

        final MenuItemEntity menuItem = menuItemDao.findByKey( params.menuItemKey );

        if ( menuItem == null )
        {
            throw new IllegalArgumentException( "Unknown menu item for key : " + params.menuItemKey );
        }

        SitePath sitePath = setupSitePath( params, menuItem.getSite().getKey(), menuItem.getPath() );
        setupHttpRequestForRenderCall( params, sitePath );

        // setupPortalRequest
        PortalRequest portalRequest = setupPortalRequestForRenderCall( params, sitePath );

        if ( params.languageCode != null )
        {
            LanguageEntity language = languageDao.findByCode( params.languageCode );
            LanguageKey languageKey = language.getKey();
            portalRequest.setOverridingLanguage( languageKey != null ? languageKey.toInt() : -1 );
        }

        return processRequest( portalRequest );

    }

    private SitePath setupSitePath( final RenderParams params, final SiteKey siteKey, Path path )
    {
        SitePath sitePath = new SitePath( siteKey, path );
        Map<String, Object> requestParameters = compileRequestParameters( params.parameters );
        for ( String key : requestParameters.keySet() )
        {
            sitePath.addParam( key, (String) requestParameters.get( key ) );
        }
        return sitePath;
    }

    private void setupHttpRequestForRenderCall( final RenderParams params, SitePath sitePath )
    {
        String uriAsString = "/site/" + sitePath.getSiteKey() + sitePath.getLocalPath().getPathAsString();
        ClientHttpServletRequest httpRequest =
            new ClientHttpServletRequest( params.serverName, params.portNumber, uriAsString, params.basePath );

        if ( !params.basePath.startsWith( "/" ) )
        {
            params.basePath = "/" + params.basePath;
        }

        if ( !params.basePath.endsWith( "/" ) )
        {
            params.basePath = params.basePath + "/";
        }

        httpRequest.setAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, params.basePath );
        httpRequest.setParameters( compileRequestParameters( params.parameters ) );

        ServletRequestAccessor.setRequest( httpRequest );
    }

    private void checkCommonRenderParams( final RenderParams params )
    {
        if ( params.serverName == null || params.serverName.equals( "" ) )
        {
            throw new IllegalArgumentException( "Illegal value for required parameter 'serverName' : " + params.serverName );
        }

        if ( params.basePath == null || params.basePath.equals( "" ) || params.basePath.contains( "://" ) )
        {
            throw new IllegalArgumentException( "Illegal value for required parameter 'basePath' : " + params.basePath );
        }
    }

    private Document processRequest( final PortalRequest portalRequest )
    {

        PortalResponse portalResponse = portalRequestService.processRequest( portalRequest );

        Element markup = new Element( "markup" );
        markup.addContent( new CDATA( portalResponse.getContent() ) );
        return new Document( markup );
    }

    public Document renderContent( final RenderContentParams params )
    {

        checkCommonRenderParams( params );

        if ( params.siteKey < 0 )
        {
            throw new IllegalArgumentException( "Illegal value for required parameter 'siteKey' : " + params.siteKey );
        }

        if ( params.contentKey < 0 )
        {
            throw new IllegalArgumentException( "Illegal value for required parameter 'contentKey' : " + params.contentKey );
        }

        final SiteEntity siteEntity = siteDao.findByKey( params.siteKey );

        if ( siteEntity == null )
        {
            throw new IllegalArgumentException( "Unknown site for key : " + params.siteKey );
        }

        Path localPath = resolveContentUrlLocalPath( new ContentKey( params.contentKey ), siteEntity.getKey() );

        SitePath sitePath = setupSitePath( params, siteEntity.getKey(), localPath );
        setupHttpRequestForRenderCall( params, sitePath );

        PortalRequest portalRequest = setupPortalRequestForRenderCall( params, sitePath );

        return processRequest( portalRequest );

    }

    private PortalRequest setupPortalRequestForRenderCall( final RenderParams params, SitePath sitePath )
    {

        HttpServletRequest httpRequest = ServletRequestAccessor.getRequest();
        final User loggedInPortalUser = securityService.getLoggedInPortalUser();

        // setupPortalRequest
        PortalRequest portalRequest = new PortalRequest();

        if ( params.profile != null )
        {
            portalRequest.setProfile( params.profile );
        }

        portalRequest.setRequestParams( compileRequestParameters( params.parameters ) );
        portalRequest.setEncodeURIs( params.encodeURIs );
        portalRequest.setOriginalSitePath( sitePath );
        portalRequest.setSitePath( sitePath );
        portalRequest.setRequester( loggedInPortalUser.getKey() );
        portalRequest.setTicketId( httpRequest.getSession().getId() );
        portalRequest.setRequestTime( new DateTime() );
        portalRequest.setOriginalUrl( httpRequest.getRequestURL().toString() );
        portalRequest.setShoppingCart( getAndEnsureShoppingCartOnHttpSession( httpRequest.getSession() ) );
        portalRequest.setVerticalSession( getAndEnsureVerticalSessionOnHttpSession( httpRequest.getSession() ) );

        return portalRequest;
    }

    // FIXME duplicate code

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

    // FIXME duplicate code

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

    private HashMap<String, Object> compileRequestParameters( String[] params )
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();

        if ( params == null )
        {
            return parameters;
        }

        for ( String param : params )
        {
            if ( !param.contains( "=" ) )
            {
                throw new IllegalArgumentException( "Param on wrong format, should be <key>=<value>: " + param );
            }

            String[] nameAndValue = param.split( "=" );
            parameters.put( nameAndValue[0], nameAndValue[1] );
        }

        return parameters;
    }

    private Path resolveContentUrlLocalPath( final ContentKey contentKey, final SiteKey siteKey )
    {
        ContentEntity content = contentDao.findByKey( contentKey );
        if ( content == null || content.isDeleted() )
        {
            return new Path( contentKey + ".cms" );
        }

        PathToContentResolver pathToContentResolver = new PathToContentResolver( sectionContentDao );

        return pathToContentResolver.resolveContentUrlLocalPath( content, siteKey );
    }
}
