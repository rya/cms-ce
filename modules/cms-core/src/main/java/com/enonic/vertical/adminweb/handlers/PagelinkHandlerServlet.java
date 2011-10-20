/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;

public class PagelinkHandlerServlet
    extends ContentBaseHandlerServlet
{


    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getContentBaseXMLBuilder() );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {
        VerticalAdminLogger.errorAdmin("OperationWrapper CREATE not implemented", null );
    }

    public boolean handlerSelect( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        ExtendedMap parameters = new ExtendedMap();

        int contentTypeKey = getContentTypeKey( formItems );
        String tmp = (String) formItems.getString( "selectedunitkey", null );
        int unitKey = -1;
        if ( tmp != null && tmp.length() > 0 )
        {
            unitKey = Integer.parseInt( tmp );
        }

        if ( unitKey != -1 )
        {
            parameters.put( "unitkey", Integer.toString( unitKey ) );
            parameters.put( "selectedunitkey", Integer.toString( unitKey ) );
        }

        parameters.put( "unitkey", Integer.toString( unitKey ) );
        parameters.put( "contenttypekey", Integer.toString( contentTypeKey ) );

        Document menuDoc = null;
        int menuKey = Integer.parseInt( tmp );
        if ( formItems.containsKey( "menukey" ) )
        {
            menuKey = formItems.getInt( "menukey" );
            menuDoc = admin.getMenu( user, menuKey, false ).getAsDOMDocument();
            parameters.put( "internallink", Boolean.TRUE );
        }
        else
        {
            menuDoc = XMLTool.createDocument( "menus" );
            parameters.put( "internallink", Boolean.FALSE );
        }

        transformXML( request, response, menuDoc, "pagelink_insert.xsl", parameters );

        return true;
    }
}
