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

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;

public class FramesetServlet
    extends AdminHandlerBaseServlet
{

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException
    {

        try
        {
            DOMSource xmlSource = new DOMSource( XMLTool.createDocument( "foo" ) );
            Source xslSource = AdminStore.getStylesheet( session, "frameset.xsl" );

            // Parameters
            ExtendedMap parameters = new ExtendedMap();
            String mainframe = request.getParameter( "mainframe" );
            if ( mainframe != null && mainframe.length() > 0 )
            {
                parameters.put( "mainframe", mainframe );
            }

            if ( formItems.containsKey( "selectedmenukey" ) )
            {
                String selectedMenuKey = formItems.getString( "selectedmenukey" );
                session.setAttribute( "selectedmenukey", selectedMenuKey );
                parameters.put( "selectedmenukey", selectedMenuKey );
            }

            parameters.put( "rightframe", formItems.getString( "rightframe", "" ) );
            parameters.put( "referer", formItems.getString( "referer", "" ) );
            parameters.put( "user-agent", request.getHeader( "user-agent" ) );

            //ren: Lagt til for VS-1970
            //legge editContent p� sesjon
            //fjerne editContent fra url
            //redirect
            Integer editContent = formItems.getInt( "editContent", -1 );
            if ( editContent > -1 )
            {
                session.setAttribute( "editContent", editContent );
                formItems.remove( "editContent" );
                redirectClientToAdminPath( "adminpage", formItems, request, response );
                return;
            }

            //neste request
            //sjekke om editContent finnes p� sesjon
            //hente contentKey
            //hente categoryKey og selectedunitkey via api
            //fjerne fra sesjon
            //sende editContent inn i transformasjon som parameter
            editContent = (Integer) session.getAttribute( "editContent" );
            if ( editContent != null && editContent > -1 )
            {
                assert ( contentDao != null );
                ContentEntity entity = contentDao.findByKey( new ContentKey( editContent ) );
                int categoryKey = entity.getCategory().getKey().toInt();
                int selectedUnitKey = entity.getCategory().getUnitExcludeDeleted().getKey();
                int contentTypeKey = entity.getContentType().getKey();

                parameters.put( "rightframe",
                                "adminpage?page=" + ( contentTypeKey + 999 ) + "&op=form&key=" + editContent + "&cat=" + categoryKey +
                                    "&selectedunitkey=" + selectedUnitKey );
                parameters.put( "referer", formItems.getString( "referer",
                                                                "adminpage?mainmenu=true&op=browse&page=" + ( contentTypeKey + 999 ) +
                                                                    "&categorykey=" + categoryKey ) );

                session.removeAttribute( "editContent" );
            }
            //end: VS-1970

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error.", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error.", e );
        }
    }
}
