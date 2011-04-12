/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.security.user.User;

/**
 * Extension of the standard sendmail servlet. <p/> <p> In addition to sending an email (using the functionality in {@link
 * SendMailController}), content is created in the category specified in the form. The content is created as specified in the appropriate
 * modulebuilder XML found in the database. So this servlet will naturally <i>only</i> work for modules created with the modulebuilder. </p>
 * <p> If this functionality is needed for other modules, the buildContentXML method can be overloaded in a child class. </p>
 */
@Controller
@RequestMapping(value = "/*/_services/contentmail")
public class ContentSendMailController
    extends SendMailController
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentSendMailController.class.getName() );

    private final static int ERR_MISSING_CATEGORY_KEY = 150;

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        if ( operation.equals( "send" ) )
        {
            if ( !formItems.containsKey( "categorykey" ) )
            {
                String message = "Category key not specified.";
                LOG.warn( StringUtil.expandString( message, null, null ) );
                redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY, null );
                return;
            }

            int categoryKey = formItems.getInt( "categorykey" );
            User user = securityService.getOldUserObject();

            Document ctDoc = XMLTool.domparse( userServices.getContentTypeByCategory( categoryKey ) );
            Element rootElement = ctDoc.getDocumentElement();
            Element contentTypeElement = XMLTool.getElement( rootElement, "contenttype" );
            Element moduleDataElement = XMLTool.getElement( contentTypeElement, "moduledata" );
            Element moduleElement = XMLTool.getElement( moduleDataElement, "config" );
            formItems.put( "__module_element", moduleElement );

            // we want to keep a reference for later
            Element formElement = XMLTool.getElement( moduleElement, "form" );
            String titleName = XMLTool.getElement( formElement, "title" ).getAttribute( "name" );

            int contentTypeKey = Integer.parseInt( contentTypeElement.getAttribute( "key" ) );

            String contentTitle = formItems.getString( titleName );
            String xmlData = buildXML( userServices, user, formItems, siteKey, contentTypeKey, contentTitle, false );

            BinaryData[] binaries = null;
            if ( formItems.hasFileItems() )
            {
                FileItem[] fileItems = formItems.getFileItems();
                binaries = new BinaryData[fileItems.length];
                for ( int i = 0; i < fileItems.length; i++ )
                {
                    binaries[i] = createBinaryData( fileItems[i] );
                }
            }
            ContentKey newKey = storeNewContent( user, binaries, xmlData );

            formItems.put( "_key", newKey.toString() );
        }

        // call parent method to ensure inherited functionality
        super.handlerCustom( request, response, session, formItems, userServices, siteKey, operation );
    }

}

