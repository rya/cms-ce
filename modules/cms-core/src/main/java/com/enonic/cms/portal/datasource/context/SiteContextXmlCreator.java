/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteProperties;
import org.jdom.Element;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserXmlCreator;

/**
 * Apr 21, 2009
 */
public class SiteContextXmlCreator
{
    private UserXmlCreator userXmlCreator;

    private PathContextXmlCreator pathContextXmlCreator;

    public SiteContextXmlCreator()
    {
        userXmlCreator = new UserXmlCreator();
        userXmlCreator.setAdminConsoleStyle( false );
        pathContextXmlCreator = new PathContextXmlCreator();
    }

    public Element createSiteElement( SiteEntity site, SiteProperties siteProperties )
    {
        Element siteEl = new Element( "site" );

        // site key (menu key)
        siteEl.setAttribute( "key", site.getKey().toString() );

        siteEl.addContent( new Element( "name" ).setText( site.getName() ) );

        siteEl.addContent( new Element( "language" ).setText( site.getLanguage().getCode() ) );

        if ( siteProperties != null && siteProperties.getSiteURL() != null )
        {
            siteEl.addContent( new Element( "url" ).setText( siteProperties.getSiteURL() ) );
        }
        else
        {
            siteEl.addContent( new Element( "url" ) );
        }

        Element defaultRunAsUserEl = new Element( "default-run-as-user" );
        siteEl.addContent( defaultRunAsUserEl );
        final UserEntity defaultRunAsUser = site.resolveDefaultRunAsUser();
        if ( defaultRunAsUser != null )
        {
            defaultRunAsUserEl.addContent( userXmlCreator.createUserElement( defaultRunAsUser, false ) );
        }

        Element frontpageEl = new Element( "front-page" );
        if ( site.getFrontPage() != null )
        {
            frontpageEl.addContent( pathContextXmlCreator.createResourceElement( site.getFrontPage() ) );
        }
        siteEl.addContent( frontpageEl );

        Element errorpageEl = new Element( "error-page" );
        if ( site.getErrorPage() != null )
        {
            errorpageEl.addContent( pathContextXmlCreator.createResourceElement( site.getErrorPage() ) );
        }
        siteEl.addContent( errorpageEl );

        Element loginpageEl = new Element( "login-page" );
        if ( site.getLoginPage() != null )
        {
            loginpageEl.addContent( pathContextXmlCreator.createResourceElement( site.getLoginPage() ) );
        }
        siteEl.addContent( loginpageEl );

        siteEl.addContent(
            new Element( "path-to-public-home-resources" ).setText( toStringAsEmptyIfNull( site.getPathToPublicResources() ) ) );
        siteEl.addContent( new Element( "path-to-home-resources" ).setText( toStringAsEmptyIfNull( site.getPathToResources() ) ) );

        return siteEl;
    }

    private String toStringAsEmptyIfNull( Object value )
    {
        if ( value == null )
        {
            return "";
        }
        return String.valueOf( value );
    }
}
