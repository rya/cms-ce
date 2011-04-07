/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.resolver.deviceclass.UserAgentTestEnums;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.SiteURLResolver;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.resolver.ResolverHttpRequestInput;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 15, 2009
 */
public class ResolverHttpRequestInputCreatorTest
{

    private ResolverHttpRequestInputCreator resolverHttpRequestInputCreator;

    private ResolverHttpRequestInput resolverHttpRequestInput;

    private MockHttpServletRequest request;

    private SiteKey siteKey;

    private SiteURLResolver siteURLResolver;

    private static final int NUMBER_OF_COOKIES = 5;

    private static final int NUMBER_OF_PARAMETERS = 10;

    private static final String ACCEPT_LANGUAGE_PARAMETER_VALUE = "no,en-gb;q=0.5,en-us;q=0.9";

    @Before
    public void setUp()
    {

        resolverHttpRequestInputCreator = new ResolverHttpRequestInputCreator();

        siteKey = new SiteKey( 1 );
        request = new MockHttpServletRequest();
        request.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( siteKey, "/home" ) );
        request.setRequestURI( "/site/" + siteKey.toString() + "/" );

        siteURLResolver = new SiteURLResolver();
        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        sitePropertiesService.setProperty( siteKey, SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, "false" );

        siteURLResolver.setSitePropertiesService( sitePropertiesService );

        resolverHttpRequestInputCreator.setSiteUrlResolver( siteURLResolver );

        ServletRequestAccessor.setRequest( request );

    }

    @Test
    public void testNullRequest()
    {
        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );
        assertEquals( null, resolverHttpRequestInput.getUserAgent() );
    }

    @Test
    public void testFullRequest()
    {
        setupFullRequest();

        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );

        Assert.assertEquals( UserAgentTestEnums.IPHONE.userAgent, resolverHttpRequestInput.getUserAgent() );

        assertBasics();

        assertEquals( NUMBER_OF_COOKIES, resolverHttpRequestInput.getCookies().size() );
        assertEquals( NUMBER_OF_PARAMETERS, resolverHttpRequestInput.getParameters().size() );

        assertEquals( 3, resolverHttpRequestInput.getHttpHeaders().size() );

    }

    @Test
    public void testMissingCookies()
    {
        setupFullRequest();

        request.setCookies();

        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );

        assertBasics();
        assertEquals( 0, resolverHttpRequestInput.getCookies().size() );

    }

    @Test
    public void testNoParameters()
    {
        setupFullRequest();

        request.removeAllParameters();

        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );

        assertBasics();
        assertEquals( 0, resolverHttpRequestInput.getParameters().size() );
    }

    @Test
    public void testMissingIP()
    {
        setupFullRequest();

        request.setRemoteAddr( null );

        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );

        assertNull( resolverHttpRequestInput.getIp() );
    }

    @Test
    public void testNoAcceptLanguages()
    {
        addUserAgent();
        addRefferer();
        addRemoteAddress();
        addProtocol();
        addServerAndPort();

        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );

        assertEquals( 0, resolverHttpRequestInput.getAcceptLanguages().size() );
    }

    @Test
    public void testNoHeaders()
    {
        addRemoteAddress();
        addProtocol();
        addServerAndPort();

        resolverHttpRequestInput = resolverHttpRequestInputCreator.createResolverHttpRequestInput( request );

        assertEquals( 0, resolverHttpRequestInput.getHttpHeaders().size() );
    }

    private void assertBasics()
    {
        assertEquals( "referrer", resolverHttpRequestInput.getReferrer() );
        assertEquals( "remoteAddress", resolverHttpRequestInput.getIp() );
        assertEquals( "protocol", resolverHttpRequestInput.getProtocol() );
    }

    private void setupFullRequest()
    {
        addUserAgent();
        addRefferer();
        addRemoteAddress();
        addProtocol();
        addServerAndPort();
        addParameters( NUMBER_OF_PARAMETERS );
        addAcceptLanguages();
        addCookies( NUMBER_OF_COOKIES );
    }

    private void addCookies( int numberOfCookies )
    {
        Cookie[] cookies = new Cookie[numberOfCookies];
        for ( int i = 0; i < numberOfCookies; i++ )
        {
            cookies[i] = new Cookie( "cookie" + i, "value" + i );
        }
        request.setCookies( cookies );
    }

    private void addProtocol()
    {
        request.setProtocol( "protocol" );
    }

    private void addRemoteAddress()
    {
        request.setRemoteAddr( "remoteAddress" );
    }

    private void addServerAndPort()
    {
        request.setServerName( "serverName" );
        request.setLocalPort( 0 );
    }

    private void addRefferer()
    {
        request.addHeader( ResolverHttpRequestInputCreator.REFERER_HEADER_NAME, "referrer" );
    }

    private void addUserAgent()
    {
        request.addHeader( ResolverHttpRequestInputCreator.USER_AGENT_HEADER_NAME, UserAgentTestEnums.IPHONE.userAgent );
    }

    private void addParameters( int numberOfParameters )
    {
        for ( int i = 0; i < numberOfParameters; i++ )
        {
            request.setParameter( "param" + i, "value" + i );
        }
    }

    private void addAcceptLanguages()
    {
        request.addHeader( ResolverHttpRequestInputCreator.ACCEPT_LANGUAGE_HEADER_NAME, ACCEPT_LANGUAGE_PARAMETER_VALUE );
    }
}
