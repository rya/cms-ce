/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.util.StringUtil;

import com.enonic.cms.framework.xml.XMLBuilder;

import com.enonic.cms.core.SiteURLResolver;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.resolver.ResolverHttpRequestInput;

/**
 * Created by rmy - Date: Apr 7, 2009
 */
public class ResolverHttpRequestInputCreator
{
    public static final String USER_AGENT_HEADER_NAME = "user-agent";

    public static final String ACCEPT_LANGUAGE_HEADER_NAME = "accept-language";

    public static final String REFERER_HEADER_NAME = "referer";

    public static final String URI_REQUEST_ATTRIBUTE_NAME = "uri";

    private SiteURLResolver siteUrlResolver;

    public ResolverHttpRequestInput createResolverHttpRequestInput( HttpServletRequest request )
    {
        ResolverHttpRequestInput resolverHttpRequestInput = new ResolverHttpRequestInput();

        if ( request != null )
        {
            addUserAgent( request, resolverHttpRequestInput );
            addReferrer( request, resolverHttpRequestInput );
            addIP( request, resolverHttpRequestInput );
            addURI( request, resolverHttpRequestInput );
            addProtocol( request, resolverHttpRequestInput );
            addVirtualHostAndPort( request, resolverHttpRequestInput );
            addResourcePath( request, resolverHttpRequestInput );
            addParameters( request, resolverHttpRequestInput );
            addAccept( request, resolverHttpRequestInput );
            addCookies( request, resolverHttpRequestInput );
            addHttpHeaders( request, resolverHttpRequestInput );
        }

        return resolverHttpRequestInput;
    }

    private void addUserAgent( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        resolverHttpRequestInput.setUserAgent( request.getHeader( USER_AGENT_HEADER_NAME ) );
    }

    private void addReferrer( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        resolverHttpRequestInput.setReferrer( request.getHeader( REFERER_HEADER_NAME ) );
    }

    private void addIP( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        resolverHttpRequestInput.setIp( request.getRemoteAddr() );
    }

    private void addURI( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        String uri = buildUrlForVerticalContextQueryString( request );
        resolverHttpRequestInput.setUri( uri );
    }

    private void addProtocol( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        resolverHttpRequestInput.setProtocol( request.getProtocol() );
    }

    private void addVirtualHostAndPort( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        String server = request.getServerName();

        resolverHttpRequestInput.setVirtualHost( server );

        Integer port = request.getServerPort();

        if ( port != null )
        {
            resolverHttpRequestInput.setPort( port );
        }
    }

    private void addResourcePath( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        String servletPath = buildServletPathForVerticalContextQueryString( request );
        resolverHttpRequestInput.setResourcePath( servletPath );
    }

    private void addParameters( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        Enumeration parameterNames = request.getParameterNames();
        while ( parameterNames.hasMoreElements() )
        {
            String parameterName = (String) parameterNames.nextElement();
            resolverHttpRequestInput.addParameter( parameterName, request.getParameter( parameterName ) );
        }
    }

    private void addAccept( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {

        String acceptLanguage = request.getHeader( ACCEPT_LANGUAGE_HEADER_NAME );
        if ( acceptLanguage != null )
        {
            String[] languages = StringUtil.splitString( acceptLanguage, "," );
            for ( String language : languages )
            {
                if ( language.indexOf( ";" ) > 0 )
                {
                    String quality = language.substring( language.indexOf( ";" ) + 3 );
                    String languageValue = language.substring( 0, language.indexOf( ";" ) );

                    resolverHttpRequestInput.addAcceptLanguage( languageValue, quality );
                }
                else
                {
                    resolverHttpRequestInput.addAcceptLanguage( language, null );
                }
            }
        }
    }

    private void addCookies( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        Cookie[] cookies = request.getCookies();

        if ( cookies != null )
        {
            for ( Cookie cookie : cookies )
            {
                String cookieName = cookie.getName();
                resolverHttpRequestInput.addCookie( cookieName, cookie.getValue() );
            }
        }
    }

    private void addHttpHeaders( HttpServletRequest request, ResolverHttpRequestInput resolverHttpRequestInput )
    {
        Enumeration headerNames = request.getHeaderNames();
        while ( headerNames != null && headerNames.hasMoreElements() )
        {
            String headerName = (String) headerNames.nextElement();
            resolverHttpRequestInput.addHttpHeader( headerName, request.getHeader( headerName ) );
        }
    }

    //FIXME Disse to metodene metodene er helt like, hvorfor?

    private String buildUrlForVerticalContextQueryString( HttpServletRequest request )
    {
        SitePath sitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( sitePath != null )
        {
            return siteUrlResolver.createUrl( request, sitePath, true );
        }
        return "";
    }

    private String buildServletPathForVerticalContextQueryString( HttpServletRequest request )
    {
        SitePath sitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        if ( sitePath != null )
        {
            return siteUrlResolver.createUrl( request, sitePath, true );
        }
        return "";
    }

    protected void addElement( XMLBuilder xmlDoc, String elementName, String value )
    {
        xmlDoc.startElement( elementName );
        xmlDoc.addContent( value );
        xmlDoc.endElement();
    }

    protected void addAttributeElement( XMLBuilder xmlDoc, String element, String attributeName, String elementName, String value )
    {
        xmlDoc.startElement( element );
        xmlDoc.setAttribute( attributeName, elementName );
        xmlDoc.addContent( value );
        xmlDoc.endElement();
    }

    @Autowired
    public void setSiteUrlResolver( SiteURLResolver siteUrlResolver )
    {
        this.siteUrlResolver = siteUrlResolver;
    }


}