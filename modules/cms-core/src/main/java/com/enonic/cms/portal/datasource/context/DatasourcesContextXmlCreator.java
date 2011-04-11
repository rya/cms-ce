/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.portal.datasource.DatasourceExecutorContext;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.portal.PageRequestType;
import com.enonic.cms.domain.portal.VerticalSession;
import com.enonic.cms.domain.portal.datasource.Datasources;
import com.enonic.cms.domain.portal.datasource.DatasourcesType;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.SiteProperties;

/**
 * May 15, 2009
 */
public class DatasourcesContextXmlCreator
    implements InitializingBean
{
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Autowired
    private GroupDao groupDao;

    private UserContextXmlCreator userContextXmlCreator;

    private HttpContextXmlCreator httpContextXmlCreator = new HttpContextXmlCreator();

    private CookieContextXmlCreator cookieContextXmlCreator = new CookieContextXmlCreator();

    private QueryStringContextXmlCreator queryStringContextXmlCreator;

    private StylesContextXmlCreator stylesContextXmlCreator;

    private WindowContextXmlCreator windowContextXmlCreator = new WindowContextXmlCreator();

    private PageContextXmlCreator pageContextXmlCreator = new PageContextXmlCreator();

    private void setup()
    {
        stylesContextXmlCreator = new StylesContextXmlCreator( resourceService );
        queryStringContextXmlCreator = new QueryStringContextXmlCreator( siteURLResolver );
        userContextXmlCreator = new UserContextXmlCreator( groupDao );
    }

    public Element createContextElement( Datasources datasources, DatasourceExecutorContext context )
    {
        Element contextElem = new Element( "context" );

        SiteEntity site = context.getSite();

        SiteProperties siteProperties = context.getSiteProperties();

        LanguageEntity language = context.getLanguage();

        // Language context
        contextElem.setAttribute( "languagecode", language.getCode() );

        // Device context
        if ( context.getDeviceClass() != null )
        {
            addElement( "device-class", context.getDeviceClass(), contextElem );
        }

        // Locale context
        if ( context.getLocale() != null )
        {
            addElement( "locale", context.getLocale().toString(), contextElem );
        }

        // User context
        if ( context.getUser() != null && !context.getUser().isAnonymous() )
        {
            contextElem.addContent( userContextXmlCreator.createUserElement( context.getUser() ) );
        }

        // Site context
        if ( site != null )
        {
            SiteContextXmlCreator siteContextXmlCreator = new SiteContextXmlCreator();
            contextElem.addContent( siteContextXmlCreator.createSiteElement( site, siteProperties ) );
        }

        // Resource context
        if ( context.getMenuItem() != null || context.getPageTemplate() != null )
        {
            ResourceContextXmlCreator contentContextXmlCreator = new ResourceContextXmlCreator( context );
            contextElem.addContent( contentContextXmlCreator.createResourceElement() );
        }

        // Datasource call from a page template
        if ( DatasourcesType.PAGETEMPLATE.equals( context.getDatasourcesType() ) )
        {
            final Element pageEl;
            if ( PageRequestType.CONTENT.equals( context.getPageRequestType() ) )
            {
                pageEl = pageContextXmlCreator.createPageElementForContentRequest( context.getRegions(), context.getPageTemplate() );
                contextElem.addContent( pageEl );
            }
            else if ( context.getMenuItem() != null )
            {
                pageEl = pageContextXmlCreator.createPageElementForMenuItemRequest( context.getRegions(), context.getMenuItem(),
                                                                                    context.getPageTemplate() );
                contextElem.addContent( pageEl );
            }
        }
        // Datasource call from a portlet
        else if ( context.getDatasourcesType().equals( DatasourcesType.PORTLET ) && context.getWindow() != null )
        {
            Element portletDocumentEl = null;
            final Document portletDocument = context.getPortletDocument();
            if ( portletDocument != null )
            {
                portletDocumentEl = (Element) portletDocument.getRootElement().detach();
            }
            final Element windowEl =
                windowContextXmlCreator.createPortletWindowElement( context.getWindow(), context.isPortletWindowRenderedInline(),
                                                                    portletDocumentEl );
            contextElem.addContent( windowEl );
        }

        // Profile context
        if ( context.getProfile() != null )
        {
            addElement( "profile", context.getProfile(), contextElem );
        }

        // Querystring context
        Element queryStringElem =
            queryStringContextXmlCreator.createQueryStringElement( context.getHttpRequest(), context.getOriginalSitePath(),
                                                                   context.getRequestParameters() );
        contextElem.addContent( queryStringElem );

        // Http context
        if ( datasources.hasHttpContext() )
        {
            contextElem.addContent( httpContextXmlCreator.createHttpElement( context.getHttpRequest() ) );
        }

        // Session context
        if ( datasources.hasSessionContext() )
        {
            VerticalSession verticalSession = context.getVerticalSession();
            if ( verticalSession != null )
            {
                contextElem.addContent( buildVerticalSessionXml( verticalSession ) );
            }
        }

        // Cookie context
        if ( datasources.hasCookieContext() )
        {
            contextElem.addContent( cookieContextXmlCreator.createCookieElement( context.getHttpRequest() ) );
        }

        // Styles context
        if ( context.hasCssKeys() )
        {
            Element frameworkEl = getOrCreateElement( contextElem, "framework" );
            boolean styleContextOn = datasources.hasStyleContext();
            Element stylesEl = stylesContextXmlCreator.createStylesElement( context.getCssKeys(), styleContextOn );
            frameworkEl.addContent( stylesEl );
        }

        return contextElem;
    }

    private void addElement( String name, String textContent, Element parent )
    {
        parent.addContent( new Element( name ).setText( textContent ) );
    }

    private Element getOrCreateElement( Element parent, String name )
    {
        Element elem = parent.getChild( name );
        if ( elem != null )
        {
            return elem;
        }

        elem = new Element( name );
        parent.addContent( elem );
        return elem;
    }

    private Element buildVerticalSessionXml( VerticalSession session )
    {
        Document doc = XMLDocumentFactory.create( session.toXML() ).getAsJDOMDocument();
        return (Element) JDOMUtil.getFirstElement( doc.getRootElement() ).detach();
    }


    public void afterPropertiesSet()
        throws Exception
    {
        setup();
    }
}
