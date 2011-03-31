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
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.security.user.User;

public class DiscussionHandlerController
    extends ContentHandlerBaseController
{

    private static final int contentTypeKey = 4;

    public DiscussionHandlerController()
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

        if ( formItems.containsKey( "parentkey" ) )
        {
            contentdataElem.setAttribute( "parentkey", formItems.getString( "parentkey" ) );
            contentdataElem.setAttribute( "top", "false" );
        }
        else
        {
            contentdataElem.setAttribute( "top", "true" );
        }

        if ( formItems.containsKey( "topkey" ) )
        {
            contentdataElem.setAttribute( "topkey", formItems.getString( "topkey" ) );
        }

        // Author
        XMLTool.createElement( doc, contentdataElem, "author", formItems.getString( "uid", "" ) );

        // E-mail
        XMLTool.createElement( doc, contentdataElem, "email", formItems.getString( "email", "" ) );

        // Title
        XMLTool.createElement( doc, contentdataElem, "title", breakString( formItems.getString( "title" ) ) );

        // Body
        Element elem = XMLTool.createElement( doc, contentdataElem, "body" );
        XMLTool.createCDATASection( doc, elem, cleanBody( formItems.getString( "body" ) ) );

    }

    protected String cleanBody( String body )
    {
        StringBuffer newBody = new StringBuffer( body );

        char c;
        for ( int i = 0; i < newBody.length(); i++ )
        {
            c = newBody.charAt( i );

            if ( c == '<' )
            {
                newBody.replace( i, i + 1, "&lt;" );
            }
            else if ( c == '>' )
            {
                newBody.replace( i, i + 1, "&gt;" );
            }
            else if ( c == (char) 10 )
            {
                newBody.replace( i, i + 1, "<br />" );
                i = i + 5;
            }
        }

        return super.cleanBody( newBody.toString() );
    }

    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalCreateException, VerticalSecurityException, RemoteException
    {

        User user = securityService.getOldUserObject();
        String contentTitle = (String) formItems.get( "title" );
        String xmlData = buildXML( userServices, user, formItems, siteKey, contentTypeKey, contentTitle, false );

        ContentKey newKey = storeNewContent( user, null, xmlData );

        MultiValueMap queryParams = new MultiValueMap();
        if ( formItems.containsKey( "pageid" ) )
        {
            queryParams.put( "id", formItems.get( "pageid" ) );
            queryParams.put( "category", formItems.get( "categorykey" ) );
            formItems.remove( "redirect" );
            formItems.remove( "_redirect" );
        }

        if ( !formItems.containsKey( "parentkey" ) )
        {
            queryParams.put( "thread", newKey.toString() );
        }

        queryParams.put( "current", newKey.toString() );

        String topKey = formItems.getString( "topkey", newKey.toString() );
        queryParams.put( "criteria", "(/content/contentdata/@topkey like \"" + topKey + "\")" );

        redirectToPage( request, response, formItems, queryParams );
    }
}
