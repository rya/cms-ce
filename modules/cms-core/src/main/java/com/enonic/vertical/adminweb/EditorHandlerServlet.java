/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.service.AdminService;

public class EditorHandlerServlet
    extends AdminHandlerBaseServlet
{

    public EditorHandlerServlet()
    {
        super();
    }

    public boolean handlerSelect( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        try
        {
            ExtendedMap parameters = new ExtendedMap();

            Document emptyDoc = XMLTool.createDocument( "empty" );

            Source xmlSource = new DOMSource( emptyDoc );

            Source xslSource = AdminStore.getStylesheet( session, "editor/" + "insert-edit-link-window.xsl" );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException te )
        {
            String message = "Failed to transform menu xml: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, message, te );
        }
        catch ( IOException ioe )
        {
            String message = "I/O error: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 2, message, ioe );
        }
        return true;
    }

}
