/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;

public class ContentHandlerHandlerServlet
    extends AdminHandlerBaseServlet
{

    public String buildContentHandlerXML( ExtendedMap formItems )
        throws ClassNotFoundException
    {
        StringWriter sw = new StringWriter();

        Document doc = XMLTool.createDocument();

        // Create unit element
        Element contentHandler = XMLTool.createRootElement( doc, "contenthandler" );

        if ( formItems.containsKey( "key" ) )
        {
            contentHandler.setAttribute( "key", formItems.getString( "key" ) );
        }

        XMLTool.createElement( doc, contentHandler, "name", formItems.getString( "name" ) );
        String className = formItems.getString( "class", "" );

        this.getClass().getClassLoader().loadClass( className );

        XMLTool.createElement( doc, contentHandler, "class", className );
        String description = formItems.getString( "description", "" );
        if ( description == null || description.length() == 0 )
        {
            XMLTool.createElement( doc, contentHandler, "description" );
        }
        else
        {
            XMLTool.createElement( doc, contentHandler, "description", description );
        }

        Element xmlConfig = XMLTool.createElement( doc, contentHandler, "xmlconfig" );

        // <config>
        String config = formItems.getString( "config", "" );
        if ( config == null || config.length() == 0 )
        {
            XMLTool.createElement( doc, xmlConfig, "config" );
        }
        else
        {
            Document temp = XMLTool.domparse( config );
            Element root = temp.getDocumentElement();
            if ( root.getNodeName().equals( "config" ) )
            {
                xmlConfig.appendChild( doc.importNode( root, true ) );
            }
            else
            {
                XMLTool.createElement( doc, xmlConfig, "config" );
            }
        }

        // <indexparameters>
        String indexparameters = formItems.getString( "indexparameters", "" );
        if ( indexparameters == null || indexparameters.length() == 0 )
        {
            XMLTool.createElement( doc, xmlConfig, "indexparameters" );
        }
        else
        {
            Document temp = XMLTool.domparse( indexparameters );
            Element root = temp.getDocumentElement();
            if ( root.getNodeName().equals( "indexparameters" ) )
            {
                xmlConfig.appendChild( doc.importNode( root, true ) );
            }
            else
            {
                XMLTool.createElement( doc, xmlConfig, "indexparameters" );
            }
        }

        // <ctydefault>
        String ctydefault = formItems.getString( "ctydefault", "" );
        if ( ctydefault == null || ctydefault.length() == 0 )
        {
            XMLTool.createElement( doc, xmlConfig, "ctydefault" );
        }
        else
        {
            Document temp = XMLTool.domparse( ctydefault );
            Element root = temp.getDocumentElement();
            if ( root.getNodeName().equals( "ctydefault" ) )
            {
                xmlConfig.appendChild( doc.importNode( root, true ) );
            }
            else
            {
                XMLTool.createElement( doc, xmlConfig, "ctydefault" );
            }
        }
        XMLTool.printDocument( sw, doc );
        return sw.toString();
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document doc = XMLTool.domparse( admin.getContentHandlers() );

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        addCommonParameters( admin, user, request, parameters, -1, -1 );
        addSortParamteres( "name", "ascending", formItems, session, parameters );
        parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );

        transformXML( request, response, doc, "contenthandler_browse.xsl", parameters );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        boolean createHandler = false;
        Document doc;
        String xmlData;
        User user = securityService.getLoggedInAdminConsoleUser();

        String keyStr = request.getParameter( "key" );
        if ( keyStr == null || keyStr.length() == 0 )
        {
            // Blank form, make dummy document
            doc = XMLTool.createDocument();
            createHandler = true;
        }
        else
        {
            // Edit content handler
            xmlData = admin.getContentHandler( Integer.parseInt( keyStr ) );
            doc = XMLTool.domparse( xmlData );
        }

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        addCommonParameters( admin, user, request, parameters, -1, -1 );
        addSortParamteres( "name", "ascending", formItems, session, parameters );
        parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
        if ( createHandler )
        {
            parameters.put( "create", "1" );
        }
        else
        {
            parameters.put( "create", "0" );
        }

        transformXML( request, response, doc, "contenthandler_form.xsl", parameters );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {
            User user = securityService.getLoggedInAdminConsoleUser();
            String xmlData = buildContentHandlerXML( formItems );
            admin.updateContentHandler( user, xmlData );

            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "op", "browse" );
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
        catch ( ClassNotFoundException cnfe )
        {
            String message = "Could not find class: %t";
            VerticalAdminLogger.errorAdmin(message, cnfe );
        }
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {
            User user = securityService.getLoggedInAdminConsoleUser();
            String xmlData = buildContentHandlerXML( formItems );
            admin.createContentHandler( user, xmlData );

            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "op", "browse" );
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
        catch ( ClassNotFoundException cnfe )
        {
            String message = "Could not find class: %t";
            VerticalAdminLogger.errorAdmin(message, cnfe );
        }
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        admin.removeContentHandler( user, key );
        redirectClientToReferer( request, response );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( "regenerateindex".equals( operation ) )
        {
            int contentHandlerKey = formItems.getInt( "contenthandlerkey" );

            admin.regenerateIndexForContentHandler( contentHandlerKey );
            redirectClientToReferer( request, response );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }
}
