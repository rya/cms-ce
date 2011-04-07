/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.captcha;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.captcha.CaptchaRepository;
import com.enonic.cms.core.captcha.CaptchaService;
import com.enonic.cms.core.captcha.CaptchaServiceImpl;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.core.security.SecurityService;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

public class CaptchaServiceTest
{
    private SecurityService securityService;

    private MockHttpServletRequest req;

    private Map<String, Object> formItems;

    private SitePropertiesService sitePropsService;

    private CaptchaRepository repo;

    private CaptchaService captchaService;

    @Before
    public void setUp()
    {
        req = new MockHttpServletRequest();
        req.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/userServices/content/create" ) );
        req.setSession( new MockHttpSession( null, "13" ) );
        formItems = new HashMap<String, Object>();
        formItems.put( "abc", "123" );
        formItems.put( "def", "456" );
        formItems.put( "_ghi", "789" );
        formItems.put( "_jkl", new String[]{"EVS", "xsl", "xml", "cms"} );
        formItems.put( "mno", new String[]{"Enonic", "Vertical Site", "Bring", "NAV", "Statens Vegvesen", "Norsk Tipping"} );
        formItems.put( "_captcha_response", "gurba" );

        sitePropsService = createMock( SitePropertiesService.class );
        securityService = createMock( SecurityService.class );
        repo = createMock( CaptchaRepository.class );

        CaptchaServiceImpl service = new CaptchaServiceImpl();
        service.setSecurityService( securityService );
        service.setSitePropertiesService( sitePropsService );
        service.setCaptchaRepository( repo );
        captchaService = service;
    }

    @Test
    public void testHandleNonCaptchaProtectedServices()
    {
        formItems.remove( "_captcha_response" );

        // Setup code for this specific test:
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.dog", new SiteKey( 0 ) ) ).andReturn( null );
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.cat", new SiteKey( 0 ) ) ).andReturn( null );
        replay( sitePropsService );

        expect( securityService.getLoggedInPortalUser() ).andReturn( createUser( "anonymous", UserType.ANONYMOUS ) ).times( 2 );
        replay( securityService );

        // Here starts the real test:
        req.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/dog/create" ) );
        assertNull( captchaService.validateCaptcha( formItems, req, "dog", "create" ) );

        req.setAttribute( Attribute.ORIGINAL_SITEPATH, new SitePath( new SiteKey( 0 ), "/_services/cat/create" ) );
        assertNull( captchaService.validateCaptcha( formItems, req, "cat", "create" ) );

        verify( sitePropsService );
        verify( securityService );
    }

    @Test
    public void testHandleCaptchaAsAdmin()
    {
        formItems.remove( "_captcha_response" );

        // Setup code for this specific test:
        expect( securityService.getLoggedInPortalUser() ).andReturn( createUser( "admin", UserType.ADMINISTRATOR ) ).times( 2 );
        replay( securityService );

        // Here starts the real test:
        assertNull( captchaService.validateCaptcha( formItems, req, "content", "create" ) );
        assertNull( captchaService.validateCaptcha( formItems, req, "content", "create" ) );
        verify( securityService );
    }

    @Test
    public void testHandleCaptchaAsAnonymous()
    {
        // Setup code for this specific test:
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.form", new SiteKey( 0 ) ) ).andReturn( "*" );
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.content", new SiteKey( 0 ) ) ).andReturn( "create" );
        replay( sitePropsService );

        expect( securityService.getLoggedInPortalUser() ).andReturn( createUser( "anonymous", UserType.ANONYMOUS ) ).times( 2 );
        replay( securityService );

        expect( repo.validateResponseForID( "13", "gurba" ) ).andReturn( true );
        expect( repo.validateResponseForID( "13", "gurba" ) ).andReturn( false );
        replay( repo );

        // Here starts the real test:
        assertTrue( captchaService.validateCaptcha( formItems, req, "form", "create" ) );
        assertFalse( captchaService.validateCaptcha( formItems, req, "content", "create" ) );
        XMLDocument xmlDoc = captchaService.buildErrorXMLForSessionContext( formItems );
        assertNotNull( xmlDoc );
        String xml = xmlDoc.getAsString();
        assertTrue( xml.contains( "123" ) );
        assertTrue( xml.contains( "def" ) );
        assertFalse( xml.contains( "ghi" ) );
        assertFalse( xml.contains( "EVS" ) );
        assertTrue( xml.contains( "Norsk Tipping" ) );

        verify( securityService );
        verify( sitePropsService );
        verify( repo );
    }

    @Test
    public void testHasCaptchaStandalone()
    {
        // Setup code for this specific test:
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.form", new SiteKey( 0 ) ) ).andReturn( "update" );
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.content", new SiteKey( 3 ) ) ).andReturn( "*" );
        expect( sitePropsService.getProperty( "cms.site.httpServices.captchaEnabled.gurba", new SiteKey( 58 ) ) ).andReturn( "remove" );
        replay( sitePropsService );

        expect( securityService.getLoggedInPortalUser() ).andReturn( createUser( "anonymous", UserType.ANONYMOUS ) ).times( 3 );
        replay( securityService );

        assertFalse( captchaService.hasCaptchaCheck( new SiteKey( 0 ), "form", "create" ) );
        assertTrue( captchaService.hasCaptchaCheck( new SiteKey( 3 ), "content", "update" ) );
        assertTrue( captchaService.hasCaptchaCheck( new SiteKey( 58 ), "gurba", "remove" ) );

        verify( sitePropsService );
        verify( securityService );
    }

    private UserEntity createUser( String name, UserType type )
    {
        UserEntity user = new UserEntity();
        user.setName( name );
        user.setType( type );
        return user;
    }
}