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
import com.enonic.esl.net.Mail;
import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.security.user.User;

public class LeadsHandlerController
    extends ContentHandlerBaseController
{

    protected int contentTypeKey = 44;

    private String[] keys =
        {"name", "mail", "phone", "fax", "address", "postalcode", "location", "state", "country", "description", "title", "organization",
            "subject", "recipientmail"};

    public LeadsHandlerController()
    {
        super();
    }

    @Override
    protected void buildContentTypeXML( UserServicesService userServices, Element contentdataElem, ExtendedMap formItems,
                                        boolean skipElements )
        throws VerticalUserServicesException
    {

        Document doc = contentdataElem.getOwnerDocument();
        XMLTool.createElement( doc, contentdataElem, "name", formItems.getString( "name" ) );
        XMLTool.createElement( doc, contentdataElem, "mail", formItems.getString( "mail" ) );
        XMLTool.createElement( doc, contentdataElem, "phone", formItems.getString( "phone", "" ) );
        XMLTool.createElement( doc, contentdataElem, "fax", formItems.getString( "fax", "" ) );
        XMLTool.createElement( doc, contentdataElem, "address", formItems.getString( "address", "" ) );
        XMLTool.createElement( doc, contentdataElem, "postalcode", formItems.getString( "postalcode", "" ) );
        XMLTool.createElement( doc, contentdataElem, "location", formItems.getString( "location", "" ) );
        XMLTool.createElement( doc, contentdataElem, "state", formItems.getString( "state", "" ) );
        XMLTool.createElement( doc, contentdataElem, "country", formItems.getString( "country", "" ) );
        XMLTool.createElement( doc, contentdataElem, "description", formItems.getString( "description", "" ) );
        XMLTool.createElement( doc, contentdataElem, "title", formItems.getString( "title" ) );
        XMLTool.createElement( doc, contentdataElem, "organization", formItems.getString( "organization", "" ) );
        XMLTool.createElement( doc, contentdataElem, "subject", formItems.getString( "subject", "" ) );
        XMLTool.createElement( doc, contentdataElem, "recipientmail", formItems.getString( "recipientemail" ) );
    }

    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalCreateException, VerticalSecurityException, RemoteException
    {

        User user = securityService.getOldUserObject();

        String contentTitle = formItems.getString( "name" );
        String xmlData = buildXML( userServices, user, formItems, siteKey, contentTypeKey, contentTitle, false );

        storeNewContent( user, null, xmlData );

        // send mail:
        Mail mail = new Mail();
        mail.setSMTPHost( verticalProperties.getSMTPHost() );

        mail.addRecipient( null, formItems.getString( "recipientemail" ), Mail.TO_RECIPIENT );
        mail.setFrom( formItems.getString( "name" ), formItems.getString( "mail" ) );
        mail.setSubject( formItems.getString( "mail_subject" ) );

        String mailMessage = formItems.getString( "mail_message" );
        mailMessage = replaceVars( mailMessage, formItems );

        if ( formItems.containsKey( "categorykey" ) )
        {
            int categoryKey = formItems.getInt( "categorykey" );
            String categoryNameXML = userServices.getCategoryName( categoryKey );
            String categoryName = XMLTool.getElementText( categoryNameXML, "//categoryname" );
            mailMessage = RegexpUtil.substituteAll( "\\%category\\%", categoryName, mailMessage );
        }

        mail.setMessage( mailMessage );
        if ( "true".equals( formItems.get( "attachxml", null ) ) )
        {
            mail.addAttachment( xmlData );
        }
        mail.send();
        redirectToPage( request, response, formItems );
    }

    private String replaceVars( String mailMessage, ExtendedMap formItems )
    {
        for ( String key : keys )
        {
            mailMessage = RegexpUtil.substituteAll( "\\%" + key + "\\%", formItems.getString( key, "" ), mailMessage );
        }

        return mailMessage;
    }

}
