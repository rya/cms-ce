/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.user.User;

public class CatalogHandlerController
    extends ContentHandlerBaseController
{

    private static final int contentTypeKey = 2;

    public CatalogHandlerController()
    {
        super();
    }

    @Override
    protected void buildContentTypeXML( UserServicesService userServices, Element contentdataElem, ExtendedMap formItems,
                                        boolean skipElements )
        throws VerticalUserServicesException
    {

        Document doc = contentdataElem.getOwnerDocument();
        contentdataElem.setAttribute( "version", "1.0" );

        // Title
        if ( formItems.containsKey( "title" ) )
        {
            XMLTool.createElement( doc, contentdataElem, "title", formItems.getString( "title" ) );
        }

        // URL
        if ( formItems.containsKey( "url" ) )
        {
            XMLTool.createElement( doc, contentdataElem, "url", formItems.getString( "url" ) );
        }

        // Description
        if ( formItems.containsKey( "description" ) )
        {
            Element elem = XMLTool.createElement( doc, contentdataElem, "description" );
            XMLTool.createCDATASection( doc, elem, cleanBody( formItems.getString( "description" ) ) );
        }

        // Country
        if ( formItems.containsKey( "country" ) )
        {
            XMLTool.createElement( doc, contentdataElem, "country", formItems.getString( "country" ) );
        }

        // Author
        Element author = XMLTool.createElement( doc, contentdataElem, "author" );

        if ( formItems.containsKey( "author_name" ) )
        {
            XMLTool.createElement( doc, author, "name", formItems.getString( "author_name" ) );
        }
        if ( formItems.containsKey( "author_phone" ) )
        {
            XMLTool.createElement( doc, author, "phone", formItems.getString( "author_phone" ) );
        }
        if ( formItems.containsKey( "author_email" ) )
        {
            XMLTool.createElement( doc, author, "email", formItems.getString( "author_email" ) );
        }
        if ( formItems.containsKey( "author_org" ) )
        {
            XMLTool.createElement( doc, author, "org", formItems.getString( "author_org" ) );
        }
    }

    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalSecurityException, RemoteException
    {

        User user = securityService.getOldUserObject();
        String contentTitle = formItems.getString( "title" );
        String xmlData = buildXML( userServices, user, formItems, siteKey, contentTypeKey, contentTitle, false );

        storeNewContent( user, null, xmlData );
        redirectToPage( request, response, formItems );
    }
}