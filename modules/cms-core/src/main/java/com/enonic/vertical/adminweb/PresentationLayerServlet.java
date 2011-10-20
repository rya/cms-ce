/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.structure.SiteXmlCreator;
import com.enonic.cms.business.portal.cache.PageCacheService;
import com.enonic.cms.business.portal.cache.SiteCachesService;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.SiteEntity;

public final class PresentationLayerServlet
    extends AdminHandlerBaseServlet
{

    private SiteCachesService siteCachesService;

    private static final String DEFAULT_LOCALIZATIONRESOURCE_FORMITEM = "defaultlocalizationresource";

    private static final String DEVICECLASSRESOLVER_FORMITEM = "deviceclassresolver";

    private static final String LOCALRESOLVER_FORMITEM = "localeresolver";

    @Autowired
    public void setSiteCachesService( SiteCachesService value )
    {
        this.siteCachesService = value;
    }

    protected Document buildXML( HttpSession session, ExtendedMap formItems )
    {
        Document doc = XMLTool.createDocument( "menu" );

        Element rootElem = doc.getDocumentElement();
        XMLTool.createElement( doc, rootElem, "name", formItems.getString( "name" ) );
        rootElem.setAttribute( "key", formItems.getString( "menukey" ) );
        if ( formItems.containsKey( "runas" ) )
        {
            rootElem.setAttribute( "runas", formItems.getString( "runas" ) );
        }
        if ( formItems.containsKey( "pathtopublichome" ) )
        {
            rootElem.setAttribute( "pathtopublichome", formItems.getString( "pathtopublichome" ) );
        }
        if ( formItems.containsKey( "pathtohome" ) )
        {
            rootElem.setAttribute( "pathtohome", formItems.getString( "pathtohome" ) );
        }

        rootElem.setAttribute( "languagekey", formItems.getString( "languagekey" ) );
        rootElem.setAttribute( "debug", "on" );

        Element detailsElem = XMLTool.createElement( doc, rootElem, "details" );
        Element virtualhostElem = XMLTool.createElement( doc, detailsElem, "virtualhost" );
        if ( formItems.containsKey( "virtualhost" ) )
        {
            String virtualHost = formItems.getString( "virtualhost" );
            XMLTool.createTextNode( doc, virtualhostElem, virtualHost );
        }
        Element contextpathElem = XMLTool.createElement( doc, detailsElem, "contextpath" );
        if ( formItems.containsKey( "contextpath" ) )
        {
            String contextPath = formItems.getString( "contextpath" );
            if ( contextPath.startsWith( "/" ) )
            {
                contextPath = contextPath.substring( 1 );
            }
            XMLTool.createTextNode( doc, contextpathElem, contextPath );
        }
        Element statisticsElem = XMLTool.createElement( doc, detailsElem, "statistics" );
        if ( formItems.containsKey( "statistics" ) )
        {
            String statisticsURL = formItems.getString( "statistics" );
            XMLTool.createTextNode( doc, statisticsElem, statisticsURL );
        }

        // caching:
        Element menudataElem = XMLTool.createElement( doc, rootElem, "menudata" );

        Element pageTypesElem = XMLTool.createElement( doc, menudataElem, "pagetypes" );
        if ( formItems.containsKey( "allow_url" ) )
        {
            XMLTool.createElement( doc, pageTypesElem, "allow" ).setAttribute( "type", "url" );
        }
        if ( formItems.containsKey( "allow_label" ) )
        {
            XMLTool.createElement( doc, pageTypesElem, "allow" ).setAttribute( "type", "label" );
        }
        if ( formItems.containsKey( "allow_section" ) )
        {
            XMLTool.createElement( doc, pageTypesElem, "allow" ).setAttribute( "type", "section" );
        }

        // default css
        if ( formItems.containsKey( "csskey" ) )
        {
            Element defaultCSSElem = XMLTool.createElement( doc, menudataElem, "defaultcss" );
            defaultCSSElem.setAttribute( "key", formItems.getString( "csskey" ) );
        }

        // DeviceClassResolver
        if ( formItems.containsKey( DEVICECLASSRESOLVER_FORMITEM ) )
        {
            Element deviceClassResolver = XMLTool.createElement( doc, rootElem, DEVICECLASSRESOLVER_FORMITEM );
            deviceClassResolver.setAttribute( "key", formItems.getString( DEVICECLASSRESOLVER_FORMITEM ) );
        }

        // Default Localization Resrouce
        if ( formItems.containsKey( DEFAULT_LOCALIZATIONRESOURCE_FORMITEM ) )
        {
            Element defaultLocalizationResource = XMLTool.createElement( doc, rootElem, DEFAULT_LOCALIZATIONRESOURCE_FORMITEM );
            defaultLocalizationResource.setAttribute( "key", formItems.getString( DEFAULT_LOCALIZATIONRESOURCE_FORMITEM ) );
        }

        // Locale Resolver
        if ( formItems.containsKey( LOCALRESOLVER_FORMITEM ) )
        {
            Element defaultLocalizationResource = XMLTool.createElement( doc, rootElem, LOCALRESOLVER_FORMITEM );
            defaultLocalizationResource.setAttribute( "key", formItems.getString( LOCALRESOLVER_FORMITEM ) );
        }

        return doc;
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
        SiteEntity site = getSiteEntity( siteKey );
        String menuDataXML = getMenuDataXML( site );

        try
        {
            Document doc = XMLTool.domparse( menuDataXML );

            DOMSource xmlSource = new DOMSource( doc );

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, "site.xsl" );
            // Parameters
            addCommonParameters( admin, user, request, formItems, -1, siteKey.toInt() );
            addAccessLevelParameters( user, formItems );
            formItems.put( "page", request.getParameter( "page" ) );

            // Show button for statistics?
            String statisticsPath = this.getServletContext().getRealPath( "/statistics/" + siteKey );
            if ( statisticsPath != null && new File( statisticsPath ).exists() )
            {
                formItems.put( "showstatistics", "true" );
            }
            else
            {
                formItems.put( "showstatistics", "false" );
            }

            formItems.put( "menuadministrate", String.valueOf( admin.getMenuAccessRight( user, siteKey.toInt() ).getAdministrate() ) );

            /*ResourceKey publicResourcePath = siteDao.findByKey( siteKey.toInt() ).getPathToPublicResources();
            if ( publicResourcePath != null )
            {
                formItems.put( "resourcePathToPublicHome", publicResourcePath.toString() );
            }

            ResourceKey internalResourcePath = siteDao.findByKey( siteKey.toInt() ).getPathToResources();
            if ( internalResourcePath != null )
            {
                formItems.put( "resourcePathToHome", internalResourcePath.toString() );
            }*/

            // add cache settings info
            PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
            formItems.put( "pageCacheEnabled", pageCacheService.isEnabled() );
            formItems.put( "debugpath", MenuHandlerServlet.getSiteUrl( request, siteKey.toInt() ) );
            formItems.put( "defaultCssExist", resourceExists( site.getDefaultCssKey() ) ? "true" : "false" );
            formItems.put( "deviceClassResolverExist", resourceExists( site.getDeviceClassResolver() ) ? "true" : "false" );
            formItems.put( "defaultLocalizationResourceExist", resourceExists( site.getDefaultLocalizationResource() ) ? "true" : "false" );
            formItems.put( "localeResolverExist", resourceExists( site.getLocaleResolver() ) ? "true" : "false" );
            UserEntity runAsUser = site.resolveDefaultRunAsUser();
            formItems.put( "defaultRunAsUser",
                           runAsUser == null ? "" : runAsUser.getDisplayName() + " (" + runAsUser.getQualifiedName() + ")" );

            transformXML( session, response.getWriter(), xmlSource, xslSource, formItems );
        }
        catch ( IOException ioe )
        {
            String MESSAGE_20 = "I/O error.";
            VerticalAdminLogger.errorAdmin(MESSAGE_20, ioe );
        }
        catch ( TransformerConfigurationException tce )
        {
            String MESSAGE_40 = "XSL transformer configuration error.";
            VerticalAdminLogger.errorAdmin(MESSAGE_40, tce );
        }
        catch ( TransformerException te )
        {
            String MESSAGE_50 = "XSL transformer error.";
            VerticalAdminLogger.errorAdmin(MESSAGE_50, te );
        }
    }

    private String getMenuDataXML( SiteEntity site )
    {
        String menuDataXML;
        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( null );
        siteXmlCreator.setIncludeSiteURLInfo( true );
        siteXmlCreator.setIncludeDeviceClassResolverInfo( true );
        siteXmlCreator.setIncludeLocalizationInfo( true );
        siteXmlCreator.setIncludePathToPublicHome( true );
        siteXmlCreator.setIncludePathToHome( true );
        siteXmlCreator.setIncludeRunAs( true );
        menuDataXML =
            siteXmlCreator.createLegacyGetMenuData( site, sitePropertiesService.getSiteProperties( site.getKey() ) ).getAsString();
        return menuDataXML;
    }

    private SiteEntity getSiteEntity( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return site;
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {
        User user = securityService.getLoggedInAdminConsoleUser();

        if ( "listmenus".equals( operation ) )
        {
            listMenus( request, response, session, admin, formItems );
        }
        else if ( "createmenuform".equals( operation ) )
        {
            createMenuForm( request, response, session, admin, formItems );
        }
        else if ( "propagateaccessrights".equals( operation ) )
        {
            handlerPropagateAccessRights( request, response, session, admin, formItems );
        }
        else if ( "clearcachedpages".equals( operation ) )
        {
            SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
            PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
            pageCacheService.removePageEntriesBySite();

            formItems.put( "feedback", "clearedcachedpages" );

            redirect( request, response, formItems );
        }
        else if ( "clearcachedobjects".equals( operation ) )
        {
            SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
            PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
            pageCacheService.removePortletWindowEntriesBySite();

            formItems.put( "feedback", "clearedcachedobjects" );

            redirect( request, response, formItems );
        }
        else if ( "clearcachedpagesandobjects".equals( operation ) )
        {
            SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
            PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
            pageCacheService.removeEntriesBySite();

            formItems.put( "feedback", "clearedcachedpagesandobjects" );

            redirect( request, response, formItems );
        }
        else if ( "createmenu".equals( operation ) )
        {
            Document doc = XMLTool.createDocument( "menu" );
            Element menuElem = doc.getDocumentElement();
            menuElem.setAttribute( "languagekey", formItems.getString( "languagekey" ) );

            Element nameElem = XMLTool.createElement( doc, menuElem, "name" );
            XMLTool.createTextNode( doc, nameElem, formItems.getString( "name" ) );
            XMLTool.createElement( doc, menuElem, "firstpage" );
            XMLTool.createElement( doc, menuElem, "loginpage" );
            XMLTool.createElement( doc, menuElem, "errorpage" );

            Element detailsElem = XMLTool.createElement( doc, menuElem, "details" );
            Element virtualhostElem = XMLTool.createElement( doc, detailsElem, "virtualhost" );
            if ( formItems.containsKey( "virtualhost" ) )
            {
                String virtualHost = formItems.getString( "virtualhost" );
                XMLTool.createTextNode( doc, virtualhostElem, virtualHost );
            }
            Element contextpathElem = XMLTool.createElement( doc, detailsElem, "contextpath" );
            if ( formItems.containsKey( "contextpath" ) )
            {
                String contextPath = formItems.getString( "contextpath" );
                if ( contextPath.startsWith( "/" ) )
                {
                    contextPath = contextPath.substring( 1 );
                }
                XMLTool.createTextNode( doc, contextpathElem, contextPath );
            }
            Element statisticsElem = XMLTool.createElement( doc, detailsElem, "statistics" );
            if ( formItems.containsKey( "statistics" ) )
            {
                String statisticsURL = formItems.getString( "statistics" );
                XMLTool.createTextNode( doc, statisticsElem, statisticsURL );
            }

            // default css
            Element menudataElem = XMLTool.createElement( doc, menuElem, "menudata" );
            if ( formItems.containsKey( "csskey" ) )
            {
                Element defaultCSSElem = XMLTool.createElement( doc, menudataElem, "defaultcss" );
                defaultCSSElem.setAttribute( "key", formItems.getString( "csskey" ) );
            }

            admin.createMenu( user, XMLTool.documentToString( doc ) );

            redirectClientToAdminPath( formItems.getString( "redirect" ), "reload", "true", request, response );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    protected void createMenuForm( javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response,
                                   HttpSession session, AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException
    {

        ExtendedMap xslParams = new ExtendedMap();

        User user = securityService.getLoggedInAdminConsoleUser();

        try
        {
            Document doc = XMLTool.createDocument( "menulanguages" );
            doc.getDocumentElement().appendChild( doc.importNode( XMLTool.domparse( admin.getLanguages() ).getDocumentElement(), true ) );

            Source xslSource = AdminStore.getStylesheet( session, "menu_form.xsl" );

            addCommonParameters( admin, user, request, xslParams, -1, -1 );
            addAccessLevelParameters( user, xslParams );
            xslParams.put( "selecteddomainkey", formItems.getString( "selecteddomainkey", null ) );

            DOMSource xmlSource = new DOMSource( doc );

            if ( formItems.containsKey( "selectedunitkey" ) )
            {
                xslParams.put( "selectedunitkey", formItems.getString( "selectedunitkey" ) );
            }
            xslParams.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            String reload = request.getParameter( "reload" );
            if ( reload != null && reload.length() > 0 )
            {
                xslParams.put( "reload", reload );
            }
            if ( formItems.containsKey( "selectedunitkey" ) )
            {
                xslParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
            }

            // Finn ut om brukeren er en enterpriseadmin
            boolean isEnterpriseAdmin = user.isEnterpriseAdmin();
            boolean isEnterpriseAdminGroupMember = admin.isEnterpriseAdmin( user );
            xslParams.put( "enterpriseadmin", ( isEnterpriseAdmin || isEnterpriseAdminGroupMember ? "true" : "false" ) );

            transformXML( session, response.getWriter(), xmlSource, xslSource, xslParams );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }

    }

    protected void listMenus( javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response,
                              HttpSession session, AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException
    {

        int domainKey = getDomainKey( session );

        ExtendedMap xslParams = new ExtendedMap();

        User user = securityService.getLoggedInAdminConsoleUser();

        try
        {
            Document doc = XMLTool.domparse( admin.getAdminMenu( user, -1 ) );

            Source xslSource = AdminStore.getStylesheet( session, "menus_browse.xsl" );

            addCommonParameters( admin, user, request, xslParams, -1, -1 );
            addAccessLevelParameters( user, xslParams );
            xslParams.put( "selecteddomainkey", String.valueOf( domainKey ) );

            DOMSource xmlSource = new DOMSource( doc );

            if ( formItems.containsKey( "selectedunitkey" ) )
            {
                xslParams.put( "selectedunitkey", formItems.getString( "selectedunitkey" ) );
            }
            xslParams.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            String reload = request.getParameter( "reload" );
            if ( reload != null && reload.length() > 0 )
            {
                xslParams.put( "reload", reload );
            }
            if ( formItems.containsKey( "se3lectedunitkey" ) )
            {
                xslParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
            }

            addAccessLevelParameters( user, xslParams );

            // Finn ut om brukeren er en enterpriseadmin
            boolean isEnterpriseAdmin = user.isEnterpriseAdmin();
            boolean isEnterpriseAdminGroupMember = admin.isEnterpriseAdmin( user );
            xslParams.put( "enterpriseadmin", ( isEnterpriseAdmin || isEnterpriseAdminGroupMember ? "true" : "false" ) );

            transformXML( session, response.getWriter(), xmlSource, xslSource, xslParams );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }

    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        int menuKey = formItems.getInt( "key" );

        SiteKey siteKey = new SiteKey( menuKey );
        SiteEntity site = getSiteEntity( siteKey );
        String menuDataXML = getMenuDataXML( site );

        try
        {
            String defaultAccessRightXML = admin.getAccessRights( user, AccessRight.MENUITEM_DEFAULT, menuKey, true );

            Document menuDataXMLDoc = XMLTool.domparse( menuDataXML );
            Document docAccessRights = XMLTool.domparse( defaultAccessRightXML );

            Element parent = (Element) XMLTool.selectNode( menuDataXMLDoc.getDocumentElement(), "/menus/menu" );
            parent.appendChild( menuDataXMLDoc.importNode( docAccessRights.getDocumentElement(), true ) );

            XMLTool.mergeDocuments( menuDataXMLDoc, XMLTool.domparse( admin.getLanguages() ), true );

            StreamSource xmlSource = new StreamSource( new StringReader( XMLTool.documentToString( menuDataXMLDoc ) ) );
            Source xslSource = AdminStore.getStylesheet( session, "site_form.xsl" );

            // Parameters
            ExtendedMap parameters = new ExtendedMap();
            parameters.put( "page", formItems.getString( "page" ) );
            parameters.put( "returnop", formItems.getString( "returnop", "browse" ) );
            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            addAccessLevelParameters( user, parameters );
            parameters.put( "defaultCssExist", resourceExists( site.getDefaultCssKey() ) ? "true" : "false" );

            parameters.put( "deviceClassResolverExist", resourceExists( site.getDeviceClassResolver() ) ? "true" : "false" );
            parameters.put( "pathToPublicHomeExist", resourceExists( site.getPathToPublicResources() ) ? "true" : "false" );
            parameters.put( "pathToHomeExist", resourceExists( site.getPathToResources() ) ? "true" : "false" );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );

        }
        catch ( TransformerException te )
        {
            String message = "Transformer error while transforming XSL: %t";
            VerticalAdminLogger.errorAdmin(message, te );
        }
        catch ( IOException ioe )
        {
            String message = "I/O error: %t";
            VerticalAdminLogger.errorAdmin(message, ioe );
        }
    }

    private boolean resourceExists( ResourceKey key )
    {
        return key == null ? false : resourceService.getResourceFile( key ) != null;
    }


    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document doc = buildXML( session, formItems );
        String menuDataXML = XMLTool.documentToString( doc );

        // access rights:
        Element rootElem = doc.getDocumentElement();
        if ( formItems.containsKey( "updateaccessrights" ) && !formItems.getString( "propagate", "" ).equals( "true" ) )
        {
            admin.updateAccessRights( user,
                                      buildAccessRightsXML( rootElem.getAttribute( "key" ), formItems, AccessRight.MENUITEM_DEFAULT ) );
        }

        admin.updateMenuData( user, menuDataXML );

        // Redirect to propagate page
        if ( "true".equals( formItems.getString( "propagate" ) ) )
        {
            handlerPropagateAccessRightsPage( request, response, session, admin, formItems );
        }
        else
        {
            redirect( request, response, formItems );
        }
    }

    public void handlerPropagateAccessRightsPage( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                                  AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {

            User user = securityService.getLoggedInAdminConsoleUser();
            int menuItemKey = formItems.getInt( "key", -1 );
            int menuKey = formItems.getInt( "menukey", -1 );

            Document doc = XMLTool.createDocument( "data" );

            Document menuItems = XMLTool.domparse( admin.getMenu( user, menuKey, false ) );
            Document changedAccessRights = buildChangedAccessRightsXML( formItems );
            Document currentAccessRights = XMLTool.domparse( buildAccessRightsXML( formItems ) );

            XMLTool.mergeDocuments( doc, menuItems, true );
            XMLTool.mergeDocuments( doc, changedAccessRights, true );
            XMLTool.mergeDocuments( doc, currentAccessRights, true );

            DOMSource xmlSource = new DOMSource( doc );
            Source xslSource = AdminStore.getStylesheet( session, "site_propagateaccessrights.xsl" );

            // Parameters
            ExtendedMap parameters = new ExtendedMap();
            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            addAccessLevelParameters( user, parameters );
            parameters.putInt( "menuitemkey", menuItemKey );
            parameters.put( "page", formItems.get( "page" ) );
            parameters.putString( "menuitemname", formItems.getString( "name", "" ) );
            parameters.putString( "insertbelow", formItems.getString( "insertbelow", "" ) );
            parameters.putString( "returnop", formItems.getString( "returnop", "browse" ) );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException e )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
        catch ( IOException e )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
    }

    public void handlerPropagateAccessRights( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                              AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // Lagre accessright til default menuitem uansett!
        int menuKey = formItems.getInt( "menukey" );
        String accessRightsXML = buildAccessRightsXML( String.valueOf( menuKey ), formItems, AccessRight.MENUITEM_DEFAULT );
        // Oppdaterer i db
        admin.updateAccessRights( user, accessRightsXML );

        // Propagate
        String subop = formItems.getString( "subop", "" );
        if ( "propagate".equals( subop ) )
        {

            String applyOnlyChanges = formItems.getString( "applyonlychanges", "off" );

            if ( "on".equals( applyOnlyChanges ) )
            {
                // Prepare for apply only changes..
                Hashtable<String, ExtendedMap> removedMenuItemAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> addedMenuItemAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> modifiedMenuItemAccessRights = new Hashtable<String, ExtendedMap>();

                for ( Object o : formItems.keySet() )
                {
                    String paramName = (String) o;
                    if ( paramName.startsWith( "arc[key=" ) )
                    {
                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        String paramValue = formItems.getString( paramName );
                        ExtendedMap categoryAccessRight = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );
                        String diffinfo = categoryAccessRight.getString( "diffinfo" );
                        if ( "removed".equals( diffinfo ) )
                        {
                            removedMenuItemAccessRights.put( paramsInName.getString( "key" ), categoryAccessRight );
                        }
                        else if ( "added".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            addedMenuItemAccessRights.put( groupKey, categoryAccessRight );
                        }
                        else if ( "modified".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            modifiedMenuItemAccessRights.put( groupKey, categoryAccessRight );
                        }
                    }
                }

                // Run through each (selected) menuitem...
                for ( Object o : formItems.keySet() )
                {

                    String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        int curMenuItemKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply, only changes
                        if ( "on".equals( applyOnlyChanges ) )
                        {

                            // Henter ud eksisterende accessrights
                            Document docCurrentCategoryAR =
                                XMLTool.domparse( admin.getAccessRights( user, AccessRight.MENUITEM, curMenuItemKey, true ) );
                            Element root = docCurrentCategoryAR.getDocumentElement();
                            Element userright = XMLTool.getElement( root, "userright" );
                            if ( userright != null )
                            {
                                XMLTool.removeChildFromParent( root, userright );
                            }

                            // Påfører endringer
                            Document docChangedCategoryAR =
                                applyChangesInAccessRights( docCurrentCategoryAR, removedMenuItemAccessRights, modifiedMenuItemAccessRights,
                                                            addedMenuItemAccessRights );
                            // Lagrer
                            admin.updateAccessRights( user, XMLTool.documentToString( docChangedCategoryAR ) );

                        }
                    }
                }
            }
            // Apply accessright as whole
            else
            {
                // Prepare for overwrite accessrights
                Document docNewCategoryAccessRights = buildAccessRightsXML( null, null, formItems, AccessRight.MENUITEM );

                // Run through each (selected) menuitem...
                for ( Object o : formItems.keySet() )
                {

                    String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );

                        int curMenuItemKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply on current category
                        Element categoryAccessrighs = docNewCategoryAccessRights.getDocumentElement();
                        categoryAccessrighs.setAttribute( "key", String.valueOf( curMenuItemKey ) );

                        admin.updateAccessRights( user, XMLTool.documentToString( docNewCategoryAccessRights ) );
                    }
                }
            }
        }

        // Redirect...
        redirect( request, response, formItems );
    }

    private void redirect( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", formItems.get( "returnop", "browse" ) );
        queryParams.put( "parentmi", formItems.get( "insertbelow", "-1" ) );
        queryParams.put( "menukey", formItems.get( "menukey", "" ) );
        queryParams.put( "feedback", formItems.get( "feedback", "" ) );
        if ( formItems.containsKey( "reload" ) )
        {
            queryParams.put( "reload", formItems.get( "reload" ) );
        }
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        admin.removeMenu( user, key );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "listmenus" );
        queryParams.put( "parentmi", formItems.get( "insertbelow", "-1" ) );
        queryParams.put( "menukey", formItems.get( "key", "" ) );
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }


    public void handlerCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        admin.copyMenu( user, key, true );
        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", "851" );
        String returnOp = formItems.getString( "returnop", "listmenus" );
        queryParams.put( "op", returnOp );
        if ( "browse".equals( returnOp ) )
        {
            queryParams.put( "menukey", String.valueOf( key ) );
        }
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

}
