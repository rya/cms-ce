/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.Base64Util;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.XDG;

import com.enonic.cms.core.CmsDateAndTimeFormats;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public class LogHandlerServlet
    extends AdminHandlerBaseServlet
{

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        try
        {
            User user = securityService.getLoggedInAdminConsoleUser();

            MultiValueMap adminParams = new MultiValueMap();
            ExtendedMap transformParams = new ExtendedMap();

            // select log entries for only one single entity
            String pathXML;
            int menuKey = -1;
            boolean popup;
            if ( formItems.containsKey( "tablekeyvalue" ) )
            {
                popup = true;
                int tableKeyValue = formItems.getInt( "tablekeyvalue" );
                adminParams.put( "@tablekeyvalue", tableKeyValue );
                transformParams.putInt( "tablekeyvalue", tableKeyValue );
                Table tableKey = Table.parse( formItems.getInt( "tablekey" ) );
                adminParams.put( "@tablekey", tableKey.asInteger() );
                transformParams.putInt( "tablekey", tableKey.asInteger() );

                switch ( tableKey )
                {
                    case CONTENT:
                        int categoryKey = admin.getCategoryKey( tableKeyValue );
                        pathXML = admin.getSuperCategoryNames( categoryKey, false, true );
                        break;
                    case MENUITEM:
                        menuKey = admin.getMenuKeyByMenuItem( new MenuItemKey( tableKeyValue ) );
                        transformParams.putInt( "parentkey", admin.getParentMenuItemKey( tableKeyValue ) );
                        pathXML = admin.getMenu( user, menuKey, false );
                        break;
                    default:
                        pathXML = null;
                }
            }
            else if ( formItems.containsKey( "userkey" ) )
            {
                popup = true;
                String userKey = formItems.getString( "userkey" );
                adminParams.put( "@userkey", userKey );
                transformParams.put( "userkey", userKey );
                /*
                if (siteKey < 0) {
                    domainKey = formItems.getInt("selecteddomainkey", -1);
                    transformParams.putInt("selecteddomainkey", domainKey);
                    transformParams.put("domainname", admin.getDomainName(domainKey));
                }
                */
                pathXML = null;
            }
            else
            {
                popup = false;
                pathXML = null;
            }

            int fromIdx = formItems.getInt( "from", 0 );

            Source xmlSource;
            if ( formItems.containsKey( "key" ) )
            {
                String xmlData = admin.getLogEntry( formItems.getString( "key" ) );
                Document doc = XMLTool.domparse( xmlData );
                Element logentryElem = XMLTool.getFirstElement( doc.getDocumentElement() );
                Element dataElem = XMLTool.getElement( logentryElem, "data" );
                if ( "true".equals( dataElem.getAttribute( "deflated" ) ) )
                {
                    // unzip aggregated entries
                    byte[] docBytes = Base64Util.decode( XMLTool.getElementText( dataElem ) );
                    Document tempDoc = XMLTool.deflatedBytesToDocument( docBytes );

                    Table tableKey = Table.parse( Integer.valueOf( logentryElem.getAttribute( "tablekey" ) ) );
                    transformParams.putInt( "tablekey", tableKey.asInteger() );
                    int tableKeyValue = Integer.valueOf( logentryElem.getAttribute( "tablekeyvalue" ) );
                    transformParams.putInt( "tablekeyvalue", tableKeyValue );
                    switch ( tableKey )
                    {
                        case CONTENT:
                            int categoryKey = admin.getCategoryKey( tableKeyValue );
                            XMLTool.mergeDocuments( tempDoc, XMLTool.domparse( admin.getSuperCategoryNames( categoryKey, false, true ) ),
                                                    true );
                            break;
                        case MENUITEM:
                            menuKey = admin.getMenuKeyByMenuItem( new MenuItemKey( tableKeyValue ) );
                            transformParams.putInt( "parentkey", admin.getParentMenuItemKey( tableKeyValue ) );
                            XMLTool.mergeDocuments( tempDoc, XMLTool.domparse( admin.getMenu( user, menuKey, false ) ), true );
                            break;
                    }

                    xmlSource = new DOMSource( tempDoc );
                }
                else
                {
                    xmlSource = new DOMSource( XMLTool.createDocument( "logentries" ) );
                }
                transformParams.put( "key", formItems.containsKey( "key" ) );
            }
            else
            {
                    if ( formItems.containsKey( "filter" ) )
                    {
                        StringTokenizer filter = new StringTokenizer( formItems.getString( "filter" ), ";" );
                        while ( filter.hasMoreTokens() )
                        {
                            String token = filter.nextToken();
                            String id = token.substring( 0, Math.min( 2, token.length() ) );
                            if ( "si".equals( id ) )
                            {
                                if ( token.charAt( 2 ) == 'a' )
                                {
                                    adminParams.put( "@menukey", null );
                                }
                                else
                                {
                                    int key = Integer.valueOf( token.substring( 2 ) );
                                    adminParams.put( "@menukey", key );
                                }
                            }
                            else if ( "ty".equals( id ) )
                            {
                                int key = Integer.valueOf( token.substring( 2 ) );
                                adminParams.put( "@typekey", key );
                            }
                            else if ( "ta".equals( id ) )
                            {
                                int key = Integer.valueOf( token.substring( 2 ) );
                                adminParams.put( "@tablekey", key );
                            }
                            else if ( "fr".equals( id ) )
                            {
                                Date from = DateUtil.parseDate( token.substring( 2 ) );
                                if ( adminParams.containsKey( "@timestamp" ) )
                                {
                                    Object to = adminParams.get( "@timestamp" );
                                    adminParams.remove( "@timestamp" );
                                    adminParams.put( "@timestamp", from, XDG.OPERATOR_RANGE );
                                    adminParams.put( "@timestamp", to, XDG.OPERATOR_RANGE );
                                }
                                else
                                {
                                    adminParams.put( "@timestamp", from, XDG.OPERATOR_GREATER_OR_EQUAL );
                                }
                            }
                            else if ( "to".equals( id ) )
                            {
                                Date to = DateUtil.parseDate( token.substring( 2 ) );
                                if ( adminParams.containsKey( "@timestamp" ) )
                                {
                                    adminParams.put( "@timestamp", to, XDG.OPERATOR_RANGE );
                                }
                                else
                                {
                                    adminParams.put( "@timestamp", to, XDG.OPERATOR_LESS );
                                }
                            }
                        }
                        transformParams.put( "filter", formItems.getString( "filter" ) );
                    }
                    String xmlData = admin.getLogEntries( user, adminParams, fromIdx, 20, false );
                    if ( pathXML != null )
                    {
                        Document doc = XMLTool.domparse( xmlData );
                        XMLTool.mergeDocuments( doc, XMLTool.domparse( pathXML ), true );
                        xmlSource = new DOMSource( doc );
                    }
                    else
                    {
                        xmlSource = new StreamSource( new StringReader( xmlData ) );
                    }
                }
            Source xslSource = AdminStore.getStylesheet( session, "log_browse.xsl" );

            // Parameters
            transformParams.put( "page", formItems.getString( "page" ) );
            transformParams.put( "from", String.valueOf( fromIdx ) );
            transformParams.putBoolean( "popup", popup );
            addAccessLevelParameters( user, transformParams );
            addCommonParameters( admin, user, request, transformParams, -1, menuKey );
            transformXML( session, response.getWriter(), xmlSource, xslSource, transformParams );
        }
        catch ( ParseException pe )
        {
            String message = "Failed to parse a date: %t";
            VerticalAdminLogger.errorAdmin(message, pe );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get response writer: %t";
            VerticalAdminLogger.errorAdmin(message, ioe );
        }
        catch ( TransformerException te )
        {
            String message = "XSLT error: %t";
            VerticalAdminLogger.errorAdmin(message, te );
        }
    }


    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( operation.equals( "view" ) )
        {
            String key = formItems.getString( "key" );
            handlerView( request, response, session, admin, formItems, key, parameters, user );
        }
        else if ( operation.equals( "filter" ) )
        {
            handlerFilter( request, response, session, admin, formItems, parameters, user, verticalDoc );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
    }

    private void handlerView( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                              ExtendedMap formItems, String key, ExtendedMap parameters, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        Document doc = XMLTool.domparse( admin.getLogEntry( key ) );
        Element logentryElem = XMLTool.getFirstElement( doc.getDocumentElement() );

        // Lookup host name
        String address = logentryElem.getAttribute( "inetaddress" );
        if ( address != null && address.length() > 0 )
        {
            try
            {
                InetAddress inetAddress = InetAddress.getByName( address );
                String host = inetAddress.getHostName();
                if ( host != null && host.length() > 0 )
                {
                    logentryElem.setAttribute( "host", host );
                }
            }
            catch ( UnknownHostException uhe )
            {
                String message = "Failed to get host name of ip address \"{0}\": %t";
                VerticalAdminLogger.warn(message, address, uhe );
            }
        }

        // select log entries for only one single entity
        String pathXML;
        int menuKey = -1;
        boolean popup;
        if ( formItems.containsKey( "tablekeyvalue" ) )
        {
            popup = true;
            int tableKeyValue = formItems.getInt( "tablekeyvalue" );
            parameters.putInt( "tablekeyvalue", tableKeyValue );
            Table tableKey = Table.parse( formItems.getInt( "tablekey" ) );
            parameters.putInt( "tablekey", tableKey.asInteger() );

            switch ( tableKey )
            {
                case CONTENT:
                    int categoryKey = admin.getCategoryKey( tableKeyValue );
                    pathXML = admin.getSuperCategoryNames( categoryKey, false, true );
                    break;
                case MENUITEM:
                    menuKey = admin.getMenuKeyByMenuItem( new MenuItemKey( tableKeyValue ) );
                    parameters.putInt( "parentkey", admin.getParentMenuItemKey( tableKeyValue ) );
                    pathXML = admin.getMenu( user, menuKey, false );
                    break;
                default:
                    pathXML = null;
            }
        }
        else if ( formItems.containsKey( "userkey" ) )
        {
            popup = true;
            String userKey = formItems.getString( "userkey" );
            parameters.put( "userkey", userKey );
            pathXML = null;
        }
        else
        {
            popup = false;
            pathXML = null;
        }
        parameters.put( "popup", String.valueOf( popup ) );
        if ( pathXML != null )
        {
            XMLTool.mergeDocuments( doc, XMLTool.domparse( pathXML ), true );
        }

        // If type is entity create, update, remove or read include entity xml
        LogType typeKey = LogType.parse( Integer.parseInt( logentryElem.getAttribute( "typekey" ) ) );
        if ( typeKey == LogType.ENTITY_CREATED || typeKey == LogType.ENTITY_UPDATED || typeKey == LogType.ENTITY_OPENED )
        {
            Table tableKey = Table.parse( Integer.parseInt( logentryElem.getAttribute( "tablekey" ) ) );
            int tableKeyValue = Integer.parseInt( logentryElem.getAttribute( "tablekeyvalue" ) );
            switch ( tableKey )
            {
                case CONTENT:
                    XMLTool.mergeDocuments( doc, XMLTool.domparse( admin.getContent( user, tableKeyValue, 0, 0, 0 ) ) );
                    break;
                case MENUITEM:
                    XMLTool.mergeDocuments( doc, XMLTool.domparse( admin.getMenuItem( user, tableKeyValue, false ) ) );
                    break;
            }
        }
        addCommonParameters( admin, user, request, parameters, -1, menuKey );

        DOMSource xmlSource = new DOMSource( doc );

        // Stylesheet
        Source xslSource = AdminStore.getStylesheet( session, "log_view.xsl" );
        try
        {
            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException te )
        {
            String message = "Failed to transform xml: %t";
            VerticalAdminLogger.errorAdmin(message, te );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get response writer: %t";
            VerticalAdminLogger.errorAdmin(message, ioe );
        }
    }

    private void handlerFilter( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException
    {
        Document menusDoc = XMLTool.domparse( admin.getMenusForAdmin( user ) );
        XMLTool.mergeDocuments( verticalDoc, menusDoc, true );
        DOMSource xmlSource = new DOMSource( verticalDoc );
        Source xslSource = AdminStore.getStylesheet( session, "log_filter.xsl" );

        // Parameters
        parameters.put( "currentdate", CmsDateAndTimeFormats.printAs_STORE_DATE( ( new Date() ) ) );
        parameters.put( "from", formItems.getInt( "from", 0 ) );
        if ( formItems.containsKey( "filter" ) )
        {
            parameters.put( "filter", formItems.get( "filter" ) );
        }
        addCommonParameters( admin, user, request, parameters, -1, -1 );
        try
        {
            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get writer: %t";
            VerticalAdminLogger.errorAdmin(message, ioe );
        }
        catch ( TransformerException te )
        {
            String message = "Failed to transform filter form: %t";
            VerticalAdminLogger.errorAdmin(message, te );
        }
    }
}
