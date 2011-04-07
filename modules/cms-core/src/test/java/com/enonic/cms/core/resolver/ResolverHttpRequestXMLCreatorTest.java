/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.resolver.deviceclass.UserAgentTestEnums;

import com.enonic.cms.domain.resolver.ResolverHttpRequestInput;
import com.enonic.cms.domain.resolver.ResolverHttpRequestInputXMLCreator;

import static org.junit.Assert.*;


/**
 * Created by rmy - Date: Apr 8, 2009
 */
public class ResolverHttpRequestXMLCreatorTest
    extends ResolverHttpRequestInputXMLCreator
{

    ResolverHttpRequestInputXMLCreator httpRequestInputXMLCreator;

    ResolverHttpRequestInput resolverHttpRequestInput;

    XMLDocument xmlDoc;

    private final static int NUMBER_OF_COOKIES = 5;

    private final static int NUMBER_OF_PARAMETERS = 10;

    @Before
    public void setUp()
    {
        resolverHttpRequestInput = new ResolverHttpRequestInput();

        httpRequestInputXMLCreator = new ResolverHttpRequestInputXMLCreator();
    }

    @Test
    public void testNull()
    {
        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( null );
        assertEquals( "request", getRootElementName() );
        assertEquals( 1, getNumberOfElements( xmlDoc.getAsJDOMDocument().getRootElement() ) );
    }

    @Test
    public void testFullResolverInput()
    {
        setupFullRequest();
        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( resolverHttpRequestInput );

        Element rootElement = xmlDoc.getAsJDOMDocument().getRootElement();

        assertEquals( 34, getNumberOfElements( rootElement ) );

        assertEquals( 3, findElementsNumberOfChildren( rootElement, ResolverHttpRequestInputXMLCreator.ACCEPT_ROOT_ELEMENT_NAME ) );
        assertEquals( NUMBER_OF_COOKIES,
                      findElementsNumberOfChildren( rootElement, ResolverHttpRequestInputXMLCreator.COOKIES_ROOT_ELEMENT_NAME ) );
        assertEquals( NUMBER_OF_PARAMETERS,
                      findElementsNumberOfChildren( rootElement, ResolverHttpRequestInputXMLCreator.PARAMETERS_ROOT_ELEMENT_NAME ) );
    }

    @Test
    public void testMissingCookies()
    {
        setupFullRequest();

        resolverHttpRequestInput.setCookies( new HashMap<String, String>() );

        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( resolverHttpRequestInput );

        Element rootElement = xmlDoc.getAsJDOMDocument().getRootElement();

        assertEquals( 29, getNumberOfElements( rootElement ) );
        assertEquals( 1, getNumberOfElements( findElement( rootElement, "cookies" ) ) );
    }

    @Test
    public void testNoParameters()
    {
        setupFullRequest();

        resolverHttpRequestInput.setParameters( new HashMap<String, String>() );

        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( resolverHttpRequestInput );

        Element rootElement = xmlDoc.getAsJDOMDocument().getRootElement();

        assertEquals( 24, getNumberOfElements( rootElement ) );
        assertEquals( 0, findElementsNumberOfChildren( rootElement, ResolverHttpRequestInputXMLCreator.PARAMETERS_ROOT_ELEMENT_NAME ) );
    }

    @Test
    public void testMissingIP()
    {
        setupFullRequest();

        resolverHttpRequestInput.setIp( null );

        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( resolverHttpRequestInput );
        Element rootElement = xmlDoc.getAsJDOMDocument().getRootElement();

        assertTrue( "Ip should be empty",
                    StringUtils.isEmpty( findElement( rootElement, ResolverHttpRequestInputXMLCreator.IP_ELEMENT_NAME ).getValue() ) );
    }

    @Test
    public void testNoAcceptLanguages()
    {
        addUserAgent();
        addReferrer();
        addIp();
        addProtocol();
        addServerAndPort();
        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( resolverHttpRequestInput );

        Element rootElement = xmlDoc.getAsJDOMDocument().getRootElement();
        assertEquals( 0, findElementsNumberOfChildren( rootElement, ResolverHttpRequestInputXMLCreator.ACCEPT_ROOT_ELEMENT_NAME ) );
    }


    @Test
    public void testNoHeaders()
    {
        addIp();
        addProtocol();
        addServerAndPort();

        xmlDoc = httpRequestInputXMLCreator.buildResolverInputXML( resolverHttpRequestInput );

        Element rootElement = xmlDoc.getAsJDOMDocument().getRootElement();
        assertEquals( 0, findElementsNumberOfChildren( rootElement, ResolverHttpRequestInputXMLCreator.HEADERS_ROOT_ELEMENT_NAME ) );
    }

    private int findElementsNumberOfChildren( Element rootElement, String elementName )
    {
        return getNumberOfElements( findElement( rootElement, elementName ) ) - 1;
    }

    private void setupFullRequest()
    {
        addHttpHeaders();
        addUserAgent();
        addReferrer();
        addIp();
        addProtocol();
        addServerAndPort();
        addUri();
        addResourcePath();
        addParameters( NUMBER_OF_PARAMETERS );
        addAcceptLanguages();
        addCookies( NUMBER_OF_COOKIES );
    }

    private void addHttpHeaders()
    {
        resolverHttpRequestInput.addHttpHeader( "header1", "value1" );
        resolverHttpRequestInput.addHttpHeader( "header2", "value2" );
        resolverHttpRequestInput.addHttpHeader( "header3", "value3" );
    }

    private void addUri()
    {
        resolverHttpRequestInput.setUri( "http://localhost:8080/test/site/1?param1=value1" );
    }

    private void addResourcePath()
    {
        resolverHttpRequestInput.setResourcePath( "/test/site/1" );

    }

    private void addCookies( int numberOfCookies )
    {
        for ( int i = 1; i <= numberOfCookies; i++ )
        {
            resolverHttpRequestInput.addCookie( "cookie" + i, "value" + i );
        }
    }

    private void addProtocol()
    {
        resolverHttpRequestInput.setProtocol( "protocol" );
    }

    private void addIp()
    {
        resolverHttpRequestInput.setIp( "remoteAddress" );
    }

    private void addServerAndPort()
    {
        resolverHttpRequestInput.setVirtualHost( "serverName" );
        resolverHttpRequestInput.setPort( 0 );
    }

    private void addReferrer()
    {
        resolverHttpRequestInput.setReferrer( "referrer" );
    }

    private void addUserAgent()
    {
        resolverHttpRequestInput.setUserAgent( UserAgentTestEnums.IPHONE.userAgent );
    }

    private void addParameters( int numberOfParameters )
    {
        for ( int i = 0; i < numberOfParameters; i++ )
        {
            resolverHttpRequestInput.addParameter( "param" + i, "value" + i );
        }
    }

    private void addAcceptLanguages()
    {
        resolverHttpRequestInput.addAcceptLanguage( "us-en", null );
        resolverHttpRequestInput.addAcceptLanguage( "no", "0.5" );
        resolverHttpRequestInput.addAcceptLanguage( "gb-en", "0.8" );
    }

    private String getRootElementName()
    {
        return xmlDoc.getAsJDOMDocument().getRootElement().getName();
    }

    private int getNumberOfElements( Element element )
    {
        int numberOfElements = 1;

        Element[] elements = JDOMUtil.getElements( element );

        if ( elements != null )
        {
            for ( Element e : elements )
            {
                numberOfElements = numberOfElements + getNumberOfElements( e );
            }
        }
        return numberOfElements;
    }


    private Element findElement( Element root, String elementName )
    {
        if ( root.getName().equals( elementName ) )
        {
            return root;
        }

        Element[] elements = JDOMUtil.getElements( root );

        if ( elements != null )
        {
            for ( Element e : elements )
            {
                Element found = findElement( e, elementName );
                if ( found != null )
                {
                    return found;
                }
            }
        }
        return null;
    }

}