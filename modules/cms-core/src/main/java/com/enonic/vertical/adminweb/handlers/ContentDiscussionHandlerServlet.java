/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.security.user.User;

public class ContentDiscussionHandlerServlet
    extends ContentBaseHandlerServlet
{

    public ContentDiscussionHandlerServlet()
    {
        super();

        FORM_XSL = "discussion_form.xsl";
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getContentDiscussionXMLBuilder() );
    }

    protected void addCustomData( HttpSession session, User user, AdminService admin, Document doc, int contentKey, int contentTypeKey,
                                  ExtendedMap formItems, ExtendedMap parameters )
    {

        boolean replyPosting = false;
        String parentKeyStr = formItems.getString( "parentkey", "" );
        if ( contentKey != -1 && parentKeyStr != "" )
        {
            // Get posting to reply to
            int parentKey = formItems.getInt( "parentkey" );
            String xmlData = admin.getContent( user, parentKey, 0, 0, 0 );
            XMLTool.mergeDocuments( doc, xmlData, false );

            replyPosting = true;
        }

        // Stylesheet parameters
        parameters.put( "replyposting", replyPosting == true ? "true" : "false" );
    }

}
