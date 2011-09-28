/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.captcha;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.core.security.SecurityService;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class CaptchaServiceImpl
    implements CaptchaService
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private SitePropertiesService sitePropertiesService;

    @Autowired
    private CaptchaRepository captchaRepository;

    private final String SITE_PROPERTY_CAPTCHA_ENABLE = "cms.site.httpServices.captchaEnabled";

    /**
     * @inheritDoc
     */
    public Boolean validateCaptcha( Map<String, Object> formItems, HttpServletRequest request, String handler, String operation )
    {
        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        Object captchaResponse = getCaptchaResponse( formItems );
        boolean forceCaptcha = hasCaptchaCheck( originalSitePath.getSiteKey(), handler, operation );

        if ( forceCaptcha || ( captchaResponse != null ) )
        {
            return validateInput( request.getSession( true ), captchaResponse );
        }
        else
        {
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    public boolean hasCaptchaCheck( SiteKey siteKey, String handler, String operation )
    {
        if ( !securityService.getLoggedInPortalUser().isAnonymous() )
        {
            return false;
        }
        String sitePropertyVariable = SITE_PROPERTY_CAPTCHA_ENABLE + "." + handler;
        String sitePropertySetting = sitePropertiesService.getProperty( sitePropertyVariable, siteKey );
        if ( sitePropertySetting == null )
        {
            return false;
        }
        else
        {
            sitePropertySetting = sitePropertySetting.trim();
        }
        return sitePropertySetting.equals( "*" ) || sitePropertySetting.equals( operation );
    }

    private Boolean validateInput( HttpSession session, Object captchaResponse )
    {
        return captchaRepository.validateResponseForID( session.getId(), captchaResponse );
    }

    private Object getCaptchaResponse( Map<String, Object> formItems )
    {
        Object response;
        if ( formItems instanceof ExtendedMap )
        {

            response = ( (ExtendedMap) formItems ).get( FORM_VARIABLE_CAPTCHA_RESPONSE, null );
        }
        else
        {
            response = formItems.get( FORM_VARIABLE_CAPTCHA_RESPONSE );
        }

        return response;
    }

    /**
     * @inheritDoc
     */
    public XMLDocument buildErrorXMLForSessionContext( Map<String, Object> formItems )
    {
        Element root = new Element( "form" );
        Document doc = new Document( root );
        for ( String name : formItems.keySet() )
        {
            if ( ( name.charAt( 0 ) == '_' ) && ( name.charAt( 1 ) != '_' ) )
            {
                // Variables that start with underscore are Vertical Site internal variable that shall not be forwarded.
            }
            else
            {
                Object value = formItems.get( name );
                if ( value instanceof String[] )
                {
                    for ( String arrayValue : (String[]) value )
                    {
                        root.addContent( new Element( "parameter" ).setAttribute( "name", name ).setText( arrayValue ) );
                    }
                }
                else
                {
                    root.addContent( new Element( "parameter" ).setAttribute( "name", name ).setText( String.valueOf( value ) ) );
                }
            }
        }
        return XMLDocumentFactory.create( doc );
    }

    public void setSitePropertiesService( SitePropertiesService service )
    {
        sitePropertiesService = service;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }

    public void setCaptchaRepository( CaptchaRepository repo )
    {
        captchaRepository = repo;
    }
}
