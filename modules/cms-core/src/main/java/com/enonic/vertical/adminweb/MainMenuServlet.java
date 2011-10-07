/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.MenuAccessRight;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.resource.ResourceXmlCreator;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.business.DeploymentPathResolver;

import com.enonic.cms.core.resource.ResourceFolder;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public class MainMenuServlet
    extends AdminHandlerBaseServlet
{

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        int selectedUnitKey = formItems.getInt( "selectedunitkey", -1 );
        int selectedMenuKey = formItems.getInt( "selectedmenukey", -1 );
        int topCategoryKey = formItems.getInt( "topcategorykey", -1 );
        if ( topCategoryKey != -1 )
        {
            selectedUnitKey = admin.getUnitKey( topCategoryKey );
        }

        try
        {
            // Get user:
            User oldUser = securityService.getLoggedInAdminConsoleUser();
            UserEntity user = securityService.getUser( oldUser );

            Document docSite = XMLTool.domparse( "<sites/>" );

            final UserStoreXmlCreator userStoreXmlCreator = new UserStoreXmlCreator( userStoreService.getUserStoreConnectorConfigs() );
            Collection<UserStoreEntity> allUsertores = userStoreDao.findAll();
            org.jdom.Element userstoresEl = new org.jdom.Element( "userstores" );
            for ( UserStoreEntity userStore : allUsertores )
            {
                if ( user.isEnterpriseAdmin() || userStoreService.isUserStoreAdministrator( user.getKey(), userStore.getKey() ) )
                {
                    org.jdom.Element userstoreEl = userStoreXmlCreator.createUserStoreElement( userStore );

                    userstoresEl.addContent( userstoreEl );
                }
            }

            XMLDocument userstoresXmlDoc = XMLDocumentFactory.create( new org.jdom.Document( userstoresEl ) );
            XMLTool.mergeDocuments( docSite, userstoresXmlDoc.getAsDOMDocument(), true );

            if ( topCategoryKey == -1 )
            {
                Cookie c = CookieUtil.getCookie( request, oldUser.getKey() + "topcategorykey" );
                if ( c != null )
                {
                    topCategoryKey = Integer.parseInt( c.getValue() );
                }
            }

            if ( selectedMenuKey == -1 )
            {
                Cookie c = CookieUtil.getCookie( request, oldUser.getKey() + "selectedmenukey" );
                if ( c != null )
                {
                    selectedMenuKey = Integer.parseInt( c.getValue() );
                }
            }
            if ( selectedMenuKey != -1 )
            {
                session.setAttribute( "selectedmenukey", String.valueOf( selectedMenuKey ) );
            }

            // Hent valgt unit...

            if ( selectedUnitKey != -1 )
            {
                session.setAttribute( "selectedunitkey", String.valueOf( selectedUnitKey ) );
            }
            else if ( request.getParameter( "chooseunit" ) == null )
            {
                String tmp = (String) session.getAttribute( "selectedunitkey" );
                if ( tmp != null )
                {
                    selectedUnitKey = Integer.parseInt( tmp );
                }

                if ( selectedUnitKey == -1 )
                {
                    Cookie c = CookieUtil.getCookie( request, oldUser.getKey() + "selectedunitkey" );
                    if ( c != null )
                    {
                        selectedUnitKey = Integer.parseInt( c.getValue() );
                        session.setAttribute( "selectedunitkey", String.valueOf( selectedUnitKey ) );
                    }
                }
            }

            // set selected keys in cookie:
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, oldUser.getKey() + "selectedunitkey", String.valueOf( selectedUnitKey ), -1, deploymentPath );
            CookieUtil.setCookie( response, oldUser.getKey() + "selectedmenukey", String.valueOf( selectedMenuKey ), -1, deploymentPath );
            CookieUtil.setCookie( response, oldUser.getKey() + "topcategorykey", String.valueOf( topCategoryKey ), -1, deploymentPath );

            Element sitesElem = docSite.getDocumentElement();
            //Document docUnits = XMLTool.domparse(xmlDataUnits);
            //Document docResourceTypes = XMLTool.domparse(xmlDataResourceTypes);
            //XMLTool.mergeDocuments(docSite, docResourceTypes, true);

            // Get menus for the selected site.
            Document newMenus = XMLTool.domparse( admin.getAdminMenu( oldUser, selectedMenuKey ) );
            Element[] menuElems = XMLTool.getElements( newMenus.getDocumentElement() );
            for ( int i = 0; i < menuElems.length; i++ )
            {
                int key = Integer.parseInt( menuElems[i].getAttribute( "key" ) );
                if ( key == selectedMenuKey )
                {
                    Element[] menuItemElems = XMLTool.getElements( menuElems[i] );
                    Element menuTop = XMLTool.createElement( menuElems[i], "menutop" );
                    for ( int j = 0; j < menuItemElems.length; j++ )
                    {
                        XMLTool.moveNode( menuItemElems[j], menuElems[i], menuTop );
                    }

                    MenuAccessRight menuAccessRight = admin.getMenuAccessRight( oldUser, selectedMenuKey );
                    if ( menuAccessRight.getAdministrate() )
                    {
                        XMLTool.createElement( menuElems[i], "objects" );
                        XMLTool.createElement( menuElems[i], "pagetemplates" );
                    }
                }
            }
            sitesElem.appendChild( docSite.importNode( newMenus.getDocumentElement(), true ) );

            // get categories
            Document categoriesDoc = XMLTool.domparse( admin.getCategoryMenu( oldUser, topCategoryKey, null, true ) );
            XMLTool.mergeDocuments( docSite, categoriesDoc, true );

            if ( resourceAccessResolver.hasAccessToResourceTree( user ) )
            {
                // get resource tree xml
                ResourceFolder root = resourceService.getResourceRoot();

                ResourceXmlCreator xmlCreator = new ResourceXmlCreator();
                xmlCreator.setIncludeFullPath( true );
                xmlCreator.setListFolders( true );
                xmlCreator.setListResources( false );
                XMLDocument resourcesDoc = xmlCreator.createResourceTreeXml( root );
                XMLTool.mergeDocuments( docSite, resourcesDoc.getAsDOMDocument(), true );
            }

            DOMSource xmlSource = new DOMSource( docSite );

            Source xslSource = AdminStore.getStylesheet( session, "mainmenu.xsl" );

            ExtendedMap parameters = new ExtendedMap();
            // Disse tre neste linjene må være her!
            parameters.put( "selectedunitkey", String.valueOf( selectedUnitKey ) );
            parameters.put( "selectedmenukey", String.valueOf( selectedMenuKey ) );
            UserStoreKey userStoreKey = userStoreService.getDefaultUserStore().getKey();

            if ( userStoreKey != null )
            {
                parameters.put( "defaultuserstorekey", userStoreService.getDefaultUserStore().getKey().toString() );
            }
            addCommonParameters( admin, oldUser, request, parameters, selectedUnitKey, -1 );
            addAccessLevelParameters( oldUser, parameters );

            String tmp = formItems.getString( "loadmainstartpage", "false" );
            if ( tmp != null && tmp.length() > 0 )
            {
                parameters.put( "loadmainstartpage", tmp );
            }

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException te )
        {
            String MESSAGE_02 = "Failed to transform XML document: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 2, MESSAGE_02, te );
        }
        catch ( IOException ioe )
        {
            String MESSAGE_03 = "I/O error occured: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 3, MESSAGE_03, ioe );
        }
    }
}
