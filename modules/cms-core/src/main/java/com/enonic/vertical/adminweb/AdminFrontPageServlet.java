/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.service.AdminService;

public class AdminFrontPageServlet
    extends AdminHandlerBaseServlet
{

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        String keyStr;
        int contentKey = -1;

        keyStr = request.getParameter( "contentkey" );
        if ( keyStr != null && keyStr.length() > 0 )
        {
            contentKey = Integer.parseInt( keyStr );
        }

        ExtendedMap parameters = new ExtendedMap();
        if ( contentKey >= 0 )
        {
            parameters.put( "selectedcontentkey", String.valueOf( contentKey ) );
        }

        Document doc = XMLTool.createDocument( "contents" );
        transformXML( request, response, doc, "frontpage.xsl", parameters );
    }
}