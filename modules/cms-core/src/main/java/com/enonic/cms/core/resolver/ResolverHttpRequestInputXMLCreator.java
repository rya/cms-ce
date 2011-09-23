/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.Map;
import java.util.Set;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

/**
 * Created by rmy - Date: Apr 7, 2009
 */
public class ResolverHttpRequestInputXMLCreator
{
    public final static String REQUEST_ROOT_ELEMENT_NAME = "request";

    protected final static String HEADERS_ROOT_ELEMENT_NAME = "headers";

    protected final static String HEADER_ELEMENT_NAME = "header";

    protected final static String ATTRIBUTE_NAME = "name";

    protected final static String USER_AGENT_ROOT_ELEMENT_NAME = "user-agent";

    protected static final String COOKIES_ROOT_ELEMENT_NAME = "cookies";

    protected static final String COOKIE_ELEMENT_NAME = "cookie";

    protected static final String REFERRER_ELEMENT_NAME = "referrer";

    protected static final String IP_ELEMENT_NAME = "ip";

    protected static final String PARAMETER_ELEMENT_NAME = "parameter";

    protected static final String ACCEPT_ROOT_ELEMENT_NAME = "accept";

    protected static final String QUALITY_ELEMENT_NAME = "quality";

    protected static final String LANGUAGE_ELEMENT_NAME = "language";

    protected static final String VIRTUAL_HOST_ELEMENT_NAME = "virtualhost";

    protected static final String URI_REQUEST_ATTRIBUTE_NAME = "uri";

    protected static final String PARAMETERS_ROOT_ELEMENT_NAME = "parameters";

    protected static final String PORT_ELEMENT_NAME = "port";

    protected static final String RESOURCEPATH_ELEMENT_NAME = "resourcepath";

    protected static final String PROTOCOL_ELEMENT_NAME = "protocol";

    public XMLDocument buildResolverInputXML( ResolverHttpRequestInput resolverHttpRequestInput )
    {
        XMLBuilder xmlDoc = new XMLBuilder();

        xmlDoc.startElement( REQUEST_ROOT_ELEMENT_NAME );

        if ( resolverHttpRequestInput != null )
        {
            addUserAgent( resolverHttpRequestInput, xmlDoc );
            addReferrer( resolverHttpRequestInput, xmlDoc );
            addIP( resolverHttpRequestInput, xmlDoc );
            addURI( resolverHttpRequestInput, xmlDoc );
            addProtocol( resolverHttpRequestInput, xmlDoc );
            addVirtualHostAndPort( resolverHttpRequestInput, xmlDoc );
            addResourcePath( resolverHttpRequestInput, xmlDoc );
            addParameters( resolverHttpRequestInput, xmlDoc );
            addAccept( resolverHttpRequestInput, xmlDoc );
            addCookies( resolverHttpRequestInput, xmlDoc );
            addHttpHeaders( resolverHttpRequestInput, xmlDoc );
        }

        xmlDoc.endElement();

        return xmlDoc.getDocument();
    }

    private void addUserAgent( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        addElement( xmlDoc, USER_AGENT_ROOT_ELEMENT_NAME, resolverHttpRequestInput.getUserAgent() );
    }

    private void addReferrer( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        addElement( xmlDoc, REFERRER_ELEMENT_NAME, resolverHttpRequestInput.getReferrer() );
    }

    private void addIP( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        addElement( xmlDoc, IP_ELEMENT_NAME, resolverHttpRequestInput.getIp() );
    }

    private void addProtocol( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        addElement( xmlDoc, PROTOCOL_ELEMENT_NAME, resolverHttpRequestInput.getProtocol() );
    }

    private void addAccept( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        xmlDoc.startElement( ACCEPT_ROOT_ELEMENT_NAME );

        Map<String, String> acceptLanguageMap = resolverHttpRequestInput.getAcceptLanguages();
        Set<String> acceptLanguages = acceptLanguageMap.keySet();

        for ( String acceptLanguage : acceptLanguages )
        {
            String quality = acceptLanguageMap.get( acceptLanguage );

            if ( quality != null )
            {
                addAttributeElement( xmlDoc, LANGUAGE_ELEMENT_NAME, QUALITY_ELEMENT_NAME, quality, acceptLanguage );
            }
            else
            {
                addElement( xmlDoc, LANGUAGE_ELEMENT_NAME, acceptLanguage );
            }
        }
        xmlDoc.endElement();
    }

    private void addCookies( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        xmlDoc.startElement( COOKIES_ROOT_ELEMENT_NAME );

        Map<String, String> cookieMap = resolverHttpRequestInput.getCookies();
        Set<String> cookieNames = cookieMap.keySet();

        for ( String cookieName : cookieNames )
        {
            addAttributeElement( xmlDoc, COOKIE_ELEMENT_NAME, ATTRIBUTE_NAME, cookieName, cookieMap.get( cookieName ) );
        }

        xmlDoc.endElement();
    }

    private void addHttpHeaders( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        xmlDoc.startElement( HEADERS_ROOT_ELEMENT_NAME );

        Map<String, String> headerMap = resolverHttpRequestInput.getHttpHeaders();
        Set<String> headerNames = headerMap.keySet();

        for ( String headerName : headerNames )
        {
            addAttributeElement( xmlDoc, HEADER_ELEMENT_NAME, ATTRIBUTE_NAME, headerName, headerMap.get( headerName ) );
        }

        xmlDoc.endElement();
    }

    private void addURI( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        addElement( xmlDoc, URI_REQUEST_ATTRIBUTE_NAME, resolverHttpRequestInput.getUri() );
    }

    private void addResourcePath( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {

        addElement( xmlDoc, RESOURCEPATH_ELEMENT_NAME, resolverHttpRequestInput.getResourcePath() );
    }

    private void addVirtualHostAndPort( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        addElement( xmlDoc, VIRTUAL_HOST_ELEMENT_NAME, resolverHttpRequestInput.getVirtualHost() );

        Integer port = resolverHttpRequestInput.getPort();

        if ( port != null )
        {
            addElement( xmlDoc, PORT_ELEMENT_NAME, port.toString() );
        }
    }

    private void addParameters( ResolverHttpRequestInput resolverHttpRequestInput, XMLBuilder xmlDoc )
    {
        xmlDoc.startElement( PARAMETERS_ROOT_ELEMENT_NAME );

        Map<String, String> parameterMap = resolverHttpRequestInput.getParameters();
        Set<String> parameterNames = parameterMap.keySet();

        for ( String parameterName : parameterNames )
        {
            addAttributeElement( xmlDoc, PARAMETER_ELEMENT_NAME, ATTRIBUTE_NAME, parameterName, parameterMap.get( parameterName ) );
        }
        xmlDoc.endElement();
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
}