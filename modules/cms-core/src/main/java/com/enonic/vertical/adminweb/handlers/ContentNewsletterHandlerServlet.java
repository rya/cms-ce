/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.ESLException;
import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.Mail;
import com.enonic.esl.servlet.http.HttpServletRequestWrapper;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminStore;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.util.URLUtils;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.resolver.ResolverContext;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;

import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.portal.rendering.PageRenderer;
import com.enonic.cms.business.portal.rendering.PageRendererContext;
import com.enonic.cms.business.portal.rendering.RegionsResolver;
import com.enonic.cms.business.preview.PreviewContext;

import com.enonic.cms.core.LanguageEntity;
import com.enonic.cms.core.LanguageResolver;
import com.enonic.cms.core.RequestParametersMerger;

import com.enonic.cms.domain.portal.PageRequestType;
import com.enonic.cms.domain.portal.rendering.RenderedPageResult;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

public class ContentNewsletterHandlerServlet
    extends ContentBaseHandlerServlet
{

    protected String SEND_XSL = null;

    protected String SENT_XSL = null;

    protected String VIEWRECIPIENTS_XSL = null;

    private String HTML_EXTRACT_XSL = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">" + "<xsl:output method=\"html\"/>" +
        "<xsl:template match=\"/\">" + "<xsl:choose>" + "<xsl:when test=\"/contents\">" +
        "<xsl:copy-of select=\"/contents/content/contentdata/newsletter/*\"/>" + "</xsl:when>" + "<xsl:otherwise>" +
        "<xsl:copy-of select=\"/content/contentdata/newsletter/*\"/>" + "</xsl:otherwise>" + "</xsl:choose>" + "</xsl:template>" +
        "</xsl:stylesheet>";

    protected static final String FORM_ITEM_KEY_OTHER_RECIPIENTS = "other_recipients";

    public ContentNewsletterHandlerServlet()
    {
        super();

        // Set filenames:
        FORM_XSL = "newsletter_form.xsl";
        SEND_XSL = "newsletter_send.xsl";
        SENT_XSL = "newsletter_sent.xsl";
        VIEWRECIPIENTS_XSL = "newsletter_viewrecipients.xsl";
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getContentNewsletterXMLBuilder() );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( "send".equals( operation ) )
        {
            handlerSend( request, response, session, admin, formItems );
        }
        if ( "confirmsend".equals( operation ) )
        {
            handlerConfirmSend( request, response, session, admin, formItems );
        }
        else if ( "viewrecipients".equals( operation ) )
        {
            handlerViewRecipients( request, response, session, formItems );
        }
    }

    public void handlerPreview( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String xmlData = null;
        if ( formItems.containsKey( "previewbtn" ) )
        {
            try
            {
                boolean formDisabled = formItems.getBoolean( "formdisabled", false ) || alwaysDisabled;

                // remove conflicting fields before generating xml
                if ( !formDisabled )
                {
                    formItems.remove( "contentdata_newsletter_XML_xhtmleditor" );
                    formItems.remove( "contentdata_newsletter_XML_data" );
                    String newsletterXML = formItems.getString( "contentdata_newsletter_XML" );
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    InputStream in = new ByteArrayInputStream( newsletterXML.getBytes( "UTF-8" ) );

                    // Run Tidy on the input
                    XMLTool.HTMLtoXML( in, out );
                    newsletterXML = out.toString( "UTF-8" );
                    formItems.put( "contentdata_newsletter_XML", newsletterXML );
                }

                User user = securityService.getLoggedInAdminConsoleUser();
                xmlData = contentXMLBuilder.buildXML( formItems, user, true, false, formDisabled );
            }
            catch ( UnsupportedEncodingException uee )
            {
                VerticalAdminLogger.fatalAdmin( this.getClass(), 0, "%t", uee );
            }
        }
        else
        {
            xmlData = (String) session.getAttribute( "_xml" );
        }

        if ( xmlData != null )
        {
            Document doc = XMLTool.domparse( xmlData );
            StringWriter sw = new StringWriter();
            StringReader sr = new StringReader( HTML_EXTRACT_XSL );
            try
            {
                transformXML( session, sw, new DOMSource( doc ), new StreamSource( sr ), new HashMap<String, Object>() );
            }
            catch ( TransformerException te )
            {
                throw new VerticalAdminException( te );
            }
            String htmlDoc = sw.toString();

            try
            {
                PrintWriter writer = response.getWriter();
                writer.print( htmlDoc );
                writer.flush();
            }
            catch ( IOException ioe )
            {
                String message = "I/O error: %t";
                VerticalAdminLogger.errorAdmin( this.getClass(), 0, message, ioe );
            }
        }
    }

    private void handlerViewRecipients( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                        ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String xmlData = (String) session.getAttribute( "_xml" );
        if ( xmlData != null )
        {
            Document doc = XMLTool.domparse( xmlData );
            transformXML( request, response, doc, VIEWRECIPIENTS_XSL, formItems );
        }
    }

    private void handlerConfirmSend( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                     ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String xmlData = (String) session.getAttribute( "_xml" );

        if ( xmlData == null )
        {
            return;
        }

        int unitKey = formItems.getInt( "selectedunitkey" );
        int contentTypeKey = getContentTypeKey( formItems );
        User user = securityService.getLoggedInAdminConsoleUser();

        // mail properties
        Document doc = XMLTool.domparse( xmlData );
        String senderName = formItems.getString( "sender_name" );
        String senderEmail = formItems.getString( "sender_email" );
        Element contentsElem = doc.getDocumentElement();
        Element contentElem = XMLTool.getElement( contentsElem, "content" );
        Element contentdataElem = XMLTool.getElement( contentElem, "contentdata" );
        String subject = XMLTool.getElementText( XMLTool.getElement( contentdataElem, "subject" ) );

        StringWriter sw = new StringWriter();
        StringReader sr = new StringReader( HTML_EXTRACT_XSL );
        try
        {
            transformXML( session, sw, new DOMSource( doc ), new StreamSource( sr ), new HashMap<String, Object>() );
        }
        catch ( TransformerException te )
        {
            throw new VerticalAdminException( te );
        }

        String htmlDoc = sw.toString();

        Map<String, Map<String, String>> emailMap = getAllNewsetterRecipients( admin, formItems );

        Mail mail = setupMail( senderName, senderEmail, subject );

        Element sendhistoryElem = XMLTool.createElementIfNotPresent( doc, contentdataElem, "sendhistory" );

        for ( Object recipient : emailMap.keySet() )
        {
            String email = (String) recipient;

            Map<String, String> paramMap = emailMap.get( email );

            String mailBody = htmlDoc;
            try
            {
                for ( String paramName : paramMap.keySet() )
                {
                    String paramValue = paramMap.get( paramName );
                    mailBody = RegexpUtil.substituteAll( "\\%" + paramName + "\\%", paramValue, mailBody );
                }
                String name = paramMap.get( "recipientName" );
                mail.clearRecipients();
                mail.addRecipient( name, email, Mail.TO_RECIPIENT );
                mail.setMessage( mailBody, true );
                mail.send();

                Element sentElem = XMLTool.createElement( doc, sendhistoryElem, "sent" );
                sentElem.setAttribute( "timestamp", DateUtil.formatISODateTime( new Date() ) );
                sentElem.setAttribute( "uid", user.getName() );
                sentElem.setAttribute( "userkey", String.valueOf( user.getKey() ) );
                Element senderElem = XMLTool.createElement( doc, sentElem, "sender" );
                senderElem.setAttribute( "name", senderName );
                senderElem.setAttribute( "email", senderEmail );
                Element recipientsElem = XMLTool.createElement( doc, sentElem, "recipients" );
                Element recipientElem = XMLTool.createElement( doc, recipientsElem, "recipient" );
                recipientElem.setAttribute( "name", name );
                recipientElem.setAttribute( "email", email );
                for ( String paramName : paramMap.keySet() )
                {
                    String paramValue = paramMap.get( paramName );
                    Element parameterElem = XMLTool.createElement( doc, recipientElem, "parameter", paramValue );
                    parameterElem.setAttribute( "name", paramName );
                }
            }
            catch ( ESLException esle )
            {
                String msg = "Failed to send email: %0";
                VerticalAdminLogger.warn( this.getClass(), 0, msg, esle.getMessage(), null );
                Element sentElem = XMLTool.createElement( doc, sendhistoryElem, "sent" );
                sentElem.setAttribute( "timestamp", DateUtil.formatISODateTime( new Date() ) );
                sentElem.setAttribute( "uid", user.getName() );
                sentElem.setAttribute( "userkey", String.valueOf( user.getKey() ) );
                sentElem.setAttribute( "error", "true" );
                Element senderElem = XMLTool.createElement( doc, sentElem, "sender" );
                senderElem.setAttribute( "name", senderName );
                senderElem.setAttribute( "email", senderEmail );
                Element recipientsElem = XMLTool.createElement( doc, sentElem, "recipients" );
                Element recipientElem = XMLTool.createElement( doc, recipientsElem, "recipient" );
                String name = paramMap.get( "recipientName" );
                recipientElem.setAttribute( "name", name );
                recipientElem.setAttribute( "email", email );
                for ( String paramName : paramMap.keySet() )
                {
                    String paramValue = paramMap.get( paramName );
                    Element parameterElem = XMLTool.createElement( doc, recipientElem, "parameter", paramValue );
                    parameterElem.setAttribute( "name", paramName );
                }
                Element errorElem = XMLTool.createElement( doc, sentElem, "error" );
                XMLTool.createCDATASection( doc, errorElem, esle.getMessage() );
            }
        }

        try
        {
            // add updated xml to session
            session.setAttribute( "_xml", XMLTool.documentToString( doc ) );

            String xmlCat;
            int categoryKey = formItems.getInt( "cat" );
            xmlCat = admin.getSuperCategoryNames( categoryKey, false, true );
            XMLTool.mergeDocuments( doc, XMLTool.domparse( xmlCat ), true );

            DOMSource xmlSource = new DOMSource( doc );

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, SENT_XSL );

            // Stylesheet parameters
            Map<String, Object> parameters = new HashMap<String, Object>();
            addCommonParameters( admin, user, request, parameters, unitKey, -1 );
            parameters.put( "unitkey", String.valueOf( unitKey ) );
            parameters.put( "selectedunitkey", String.valueOf( unitKey ) );

            parameters.put( "page", formItems.getInt( "page" ) );
            parameters.put( "cat", formItems.getString( "cat" ) );
            parameters.put( "modulename", admin.getContentTypeName( contentTypeKey ) );
            if ( "referer".equals( formItems.getString( "useredirect", "" ) ) )
            {
                parameters.put( "useredirect", getReferer( request ) );
            }

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException te )
        {
            String msg = "XSLT transformation error: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, msg, te );
        }
        catch ( IOException ioe )
        {
            String msg = "I/O error: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, msg, ioe );
        }

    }

    private Mail setupMail( String senderName, String senderEmail, String subject )
    {
        Mail mail = new Mail();
        mail.setSMTPHost( verticalProperties.getSMTPHost() );
        mail.setFrom( senderName, senderEmail );
        mail.setSubject( subject );
        return mail;
    }

    private Map<String, Map<String, String>> getAllNewsetterRecipients( AdminService admin, ExtendedMap formItems )
    {
        Map<String, Map<String, String>> emailMap = new HashMap<String, Map<String, String>>();

        emailMap.putAll( parseOtherRecipients( formItems ) );
        emailMap.putAll( parseInternalReciptients( admin, formItems ) );
        emailMap.putAll( parseConfigRecipients( formItems ) );

        return emailMap;
    }


    protected Map<String, Map<String, String>> parseOtherRecipients( ExtendedMap formItems )
    {

        Map<String, Map<String, String>> emailMap = new HashMap<String, Map<String, String>>();

        if ( formItems.containsKey( FORM_ITEM_KEY_OTHER_RECIPIENTS ) )
        {
            String otherRecipients = formItems.getString( FORM_ITEM_KEY_OTHER_RECIPIENTS );

            Pattern p = Pattern.compile( RegexpUtil.REG_EXP_VALID_EMAIL, Pattern.CASE_INSENSITIVE );

            Matcher m = p.matcher( otherRecipients );

            while ( m.find() )
            {
                Map<String, String> paramMap = new HashMap<String, String>();

                String email = m.group( 0 );
                paramMap.put( "recipientName", email.substring( 0, email.indexOf( '@' ) ) );
                paramMap.put( "recipientEmail", email );
                emailMap.put( email, paramMap );
            }
        }

        return emailMap;

    }

    private Map<String, Map<String, String>> parseConfigRecipients( ExtendedMap formItems )
    {
        Map<String, Map<String, String>> emailMap = new HashMap<String, Map<String, String>>();

        if ( formItems.containsKey( "config" ) )
        {
            Document configDoc = XMLTool.domparse( formItems.getString( "config" ) );
            Element[] recipientElems = XMLTool.getElements( XMLTool.getFirstElement( configDoc.getDocumentElement() ) );
            for ( Element recipientElem : recipientElems )
            {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put( "recipientName", recipientElem.getAttribute( "name" ) );
                paramMap.put( "recipientEmail", recipientElem.getAttribute( "email" ) );
                Element[] paramElems = XMLTool.getElements( recipientElem, "parameter" );
                for ( Element paramElem : paramElems )
                {
                    paramMap.put( paramElem.getAttribute( "name" ), XMLTool.getElementText( paramElem ) );
                }
                emailMap.put( recipientElem.getAttribute( "email" ), paramMap );
            }
        }

        return emailMap;
    }

    private Map<String, Map<String, String>> parseInternalReciptients( AdminService admin, ExtendedMap formItems )
    {
        Document usernamesDoc = createUsernamesDoc( admin, formItems );

        Map<String, Map<String, String>> emailMap = new HashMap<String, Map<String, String>>();

        Element[] usernameElems = XMLTool.getElements( usernamesDoc.getDocumentElement() );
        for ( Element usernameElem : usernameElems )
        {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put( "recipientName", XMLTool.getElementText( usernameElem ) );
            paramMap.put( "recipientEmail", usernameElem.getAttribute( "email" ) );
            emailMap.put( usernameElem.getAttribute( "email" ), paramMap );
        }

        return emailMap;
    }

    private Document createUsernamesDoc( AdminService admin, ExtendedMap formItems )
    {
        String[] groupKeys;
        Document usernamesDoc;
        if ( isArrayFormItem( formItems, "member" ) )
        {
            groupKeys = (String[]) formItems.get( "member" );
            usernamesDoc = XMLTool.domparse( admin.getUserNames( groupKeys ) );
        }
        else if ( formItems.containsKey( "member" ) )
        {
            groupKeys = new String[]{formItems.getString( "member" )};
            usernamesDoc = XMLTool.domparse( admin.getUserNames( groupKeys ) );
        }
        else
        {
            usernamesDoc = XMLTool.createDocument( "usernames" );
        }
        return usernamesDoc;
    }

    private void handlerSend( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                              ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {
            int unitKey = formItems.getInt( "selectedunitkey" );
            int contentTypeKey = getContentTypeKey( formItems );

            String languageKey = formItems.getString( "languagekey", null );
            if ( languageKey == null || languageKey.length() == 0 )
            {
                languageKey = String.valueOf( admin.getUnitLanguageKey( unitKey ) );
                formItems.put( "languagekey", languageKey );
            }

            User user = securityService.getLoggedInAdminConsoleUser();
            int key = formItems.getInt( "key" );
            String xmlData = admin.getContent( user, key, 0, 0, 0 );
            Document doc = XMLTool.domparse( xmlData );

            session.setAttribute( "_xml", XMLTool.documentToString( doc ) );

            String xmlCat;
            int categoryKey = formItems.getInt( "cat" );
            xmlCat = admin.getSuperCategoryNames( categoryKey, false, true );
            XMLTool.mergeDocuments( doc, XMLTool.domparse( xmlCat ), true );

            DOMSource xmlSource = new DOMSource( doc );

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, SEND_XSL );

            // Stylesheet parameters
            Map<String, Object> parameters = new HashMap<String, Object>();
            addCommonParameters( admin, user, request, parameters, unitKey, -1 );

            if ( unitKey != -1 )
            {
                parameters.put( "unitkey", String.valueOf( unitKey ) );
                parameters.put( "selectedunitkey", String.valueOf( unitKey ) );
            }

            parameters.put( "create", "0" );
            parameters.put( "page", formItems.getString( "page" ) );
            parameters.put( "cat", formItems.getString( "cat" ) );
            parameters.put( "modulename", admin.getContentTypeName( contentTypeKey ) );
            if ( "referer".equals( formItems.getString( "useredirect", "" ) ) )
            {
                parameters.put( "useredirect", getReferer( request ) );
            }

            parameters.put( "user_fullname", user.getDisplayName() );
            parameters.put( "user_email", user.getEmail() );

            int userStoreKey = user.getUserStoreKey() != null ? user.getUserStoreKey().toInt() : -1;
            parameters.put( "user_userstorekey", String.valueOf( userStoreKey ) );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException te )
        {
            String msg = "XSLT transformation error: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, msg, te );
        }
        catch ( IOException ioe )
        {
            String msg = "I/O error: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, msg, ioe );
        }
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, boolean createContent, int unitKey, int categoryKey, int contentTypeKey,
                             int contentKey, int versionKey )
        throws VerticalAdminException
    {

        formItems.put( "_session", session );
        formItems.put( "_presentation", presentation );

        if ( "session".equals( formItems.getString( "content", null ) ) )
        {
            formItems.put( "previouspage", session.getAttribute( "_previouspage" ) );
        }
        else if ( formItems.containsKey( "contentdata_newsletter_@menuitemkey" ) )
        {
            formItems.put( "previouspage", session.getAttribute( "_previouspage" ) );
        }
        else
        {
            String referer = getReferer( request ).toString();
            session.setAttribute( "_previouspage", referer );
            formItems.put( "previouspage", referer );
        }

        super.handlerForm( request, response, session, admin, formItems, user, createContent, unitKey, categoryKey, contentTypeKey,
                           contentKey, versionKey );
    }

    protected void addCustomData( HttpSession session, User user, AdminService admin, Document doc, int contentKey, int contentTypeKey,
                                  ExtendedMap formItems, ExtendedMap parameters )
        throws VerticalAdminException
    {

        XMLTool.mergeDocuments( doc, admin.getPageTemplates( PageTemplateType.NEWSLETTER ), true );

        String selectedMenuItemKey = null;
        String selectedMenuItemPath = null;

        if ( formItems.containsKey( "contentdata_newsletter_@menuitemkey" ) )
        {
            selectedMenuItemKey = formItems.getString( "contentdata_newsletter_@menuitemkey" );
        }
        if ( formItems.containsKey( "viewcontentdata_newsletter_@menuitemkey" ) )
        {
            selectedMenuItemPath = formItems.getString( "viewcontentdata_newsletter_@menuitemkey" );
        }
        // Need to resolve selected menuitem from contentdata when form is newly opened params above is not submittet
        if ( selectedMenuItemKey == null )
        {
            Element contentsEl = doc.getDocumentElement();
            Element contentEl = XMLTool.getElement( contentsEl, "content" );
            if ( contentEl != null )
            {
                Element contentdataEl = XMLTool.getElement( contentEl, "contentdata" );
                Element newsletterEl = XMLTool.getElement( contentdataEl, "newsletter" );
                String menuItemKeyStr = newsletterEl.getAttribute( "menuitemkey" );

                if ( StringUtils.isEmpty( menuItemKeyStr ) )
                {
                    throw new UnsupportedOperationException(
                        "This newsletter content seems to be created before version 4.4 and is no longer supported. " );
                }

                MenuItemKey menuItemKey = new MenuItemKey( menuItemKeyStr );
                MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );

                selectedMenuItemKey = menuItem.getMenuItemKey().toString();
                selectedMenuItemPath = menuItem.getSite().getName() + ": " + menuItem.getPathAsString();
            }
        }

        parameters.put( "selected-menuitem-key", selectedMenuItemKey );
        parameters.put( "selected-menuitem-path", selectedMenuItemPath );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {
            // remove conflicting fields before generating xml
            formItems.remove( "contentdata_newsletter_XML_xhtmleditor" );
            formItems.remove( "contentdata_newsletter_XML_data" );
            String newsletterXML = formItems.getString( "contentdata_newsletter_XML" );
            InputStream in = new ByteArrayInputStream( newsletterXML.getBytes( "UTF-8" ) );

            // Run Tidy on the input
            Document doc = XMLTool.HTMLtoXML( in, new ByteArrayOutputStream() );
            newsletterXML = XMLTool.documentToString( doc );
            formItems.put( "contentdata_newsletter_XML", newsletterXML );


        }
        catch ( UnsupportedEncodingException uee )
        {
            VerticalAdminLogger.fatalAdmin( this.getClass(), 0, "%t", uee );
        }
        super.handlerCreate( request, response, session, admin, formItems, user );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {
            boolean formDisabled = formItems.getBoolean( "formdisabled", false );

            if ( !formDisabled )
            {
                // remove conflicting fields before generating xml
                formItems.remove( "contentdata_newsletter_XML_xhtmleditor" );
                formItems.remove( "contentdata_newsletter_XML_data" );
                String newsletterXML = formItems.getString( "contentdata_newsletter_XML" );
                InputStream newsletterXMLInputStream = new ByteArrayInputStream( newsletterXML.getBytes( "UTF-8" ) );

                // Run Tidy on the input
                Document doc = XMLTool.HTMLtoXML( newsletterXMLInputStream, new ByteArrayOutputStream() );
                newsletterXML = XMLTool.documentToString( doc );
                formItems.put( "contentdata_newsletter_XML", newsletterXML );
            }
        }
        catch ( UnsupportedEncodingException uee )
        {
            VerticalAdminLogger.fatalAdmin( this.getClass(), 0, "%t", uee );
        }
        super.handlerUpdate( request, response, formItems, user );
    }

    protected void preProcessContentDocument( User user, AdminService admin, Document doc, ExtendedMap formItems,
                                              HttpServletRequest request )
        throws VerticalAdminException
    {

        Element contentElem = XMLTool.getElement( doc.getDocumentElement(), "content" );
        if ( formItems.containsKey( "contentdata_newsletter_@menuitemkey" ) )
        {
            preProcessContentDocumentWithNewsletterPageTemplate( user, doc, formItems, request, contentElem );
        }
        else if ( contentElem != null )
        {
            preProcesscontentDocumentWithoutNewsletterPageTemplate( doc, formItems, contentElem );
        }
    }

    private void preProcessContentDocumentWithNewsletterPageTemplate( User oldUser, Document doc, ExtendedMap formItems,
                                                                      HttpServletRequest request, Element contentElem )
    {
        String menuItemKeyStr = formItems.getString( "contentdata_newsletter_@menuitemkey" );

        MenuItemKey menuItemKey = new MenuItemKey( menuItemKeyStr );
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        SiteEntity site = menuItem.getSite();
        PageTemplateEntity pageTemplate = menuItem.getPage().getTemplate();

        final Regions regionsInPage = RegionsResolver.resolveRegionsForPageRequest( menuItem, pageTemplate, PageRequestType.MENUITEM );

        RequestParameters requestParameters = new RequestParameters();
        requestParameters.addParameterValue( "id", menuItem.getMenuItemKey().toString() );

        RequestParameters mergedRequestParameters =
            RequestParametersMerger.mergeWithMenuItemRequestParameters( requestParameters, menuItem.getRequestParameters() );

        SitePath sitePath = new SitePath( site.getKey(), menuItem.getPath(), mergedRequestParameters );

        UserEntity renderer = securityService.getUser( oldUser );

        // Resolve run as user
        UserEntity runAsUser = pageTemplate.resolveRunAsUser( renderer );
        if ( runAsUser == null )
        {
            runAsUser = renderer;
        }

        final LanguageEntity language = LanguageResolver.resolve( site, menuItem );

        final ResolverContext resolverContext = new ResolverContext( request, site, menuItem, language );
        final Locale locale = localeResolverService.getLocale( resolverContext );
        final String deviceClass = "newsletter";

        request.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );

        String siteUrl = sitePropertiesService.getProperty( SitePropertyNames.SITE_URL, site.getKey() );

        if ( !URLUtils.verifyValidURL( siteUrl ) )
        {
            throw new VerticalAdminException(
                "No valid cms.site.url defined in site-" + site.getKey().toInt() + ".properties: " + siteUrl );
        }

        // ensure that the rendering uses the site's base path (and not the admin consoles base path)

        if ( !siteUrl.endsWith( "/" ) )
        {
            siteUrl += "/";
        }
        request.setAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, siteUrl );

        HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request );
        wrappedRequest.setServletPath( "/site/" + site.getKey().toString() );
        ServletRequestAccessor.setRequest( wrappedRequest );

        PageRendererContext pageRendererContext = new PageRendererContext();
        pageRendererContext.setForceNoCacheUsage( true );
        pageRendererContext.setDeviceClass( deviceClass );
        pageRendererContext.setOverridingSitePropertyCreateUrlAsPath( false );
        pageRendererContext.setHttpRequest( wrappedRequest );
        pageRendererContext.setLanguage( language );
        pageRendererContext.setLocale( locale );
        pageRendererContext.setMenuItem( menuItem );
        pageRendererContext.setOriginalSitePath( sitePath );
        pageRendererContext.setPageRequestType( PageRequestType.MENUITEM );
        pageRendererContext.setPreviewContext( PreviewContext.NO_PREVIEW );
        pageRendererContext.setRegionsInPage( regionsInPage );
        pageRendererContext.setRenderer( renderer );
        pageRendererContext.setRequestTime( new DateTime() );
        pageRendererContext.setRunAsUser( runAsUser );
        pageRendererContext.setTicketId( request.getSession().getId() );
        pageRendererContext.setSite( site );
        pageRendererContext.setSitePath( sitePath );

        PageRenderer pageRenderer = pageRendererFactory.createPageRenderer( pageRendererContext );

        RenderedPageResult result = pageRenderer.renderPage( pageTemplate );
        String htmlDoc = result.getContent();

        // create newsletter xml
        if ( contentElem == null )
        {
            contentElem = XMLTool.createElement( doc, doc.getDocumentElement(), "content" );
        }
        Element contentdataElem = XMLTool.getElement( contentElem, "contentdata" );
        if ( contentdataElem == null )
        {
            contentdataElem = XMLTool.createElement( doc, contentElem, "contentdata" );
        }

        String subject = formItems.getString( "contentdata_subject", "" );

        Element titleElem = XMLTool.getElement( contentElem, "title" );
        if ( titleElem == null )
        {
            XMLTool.createElement( doc, contentElem, "title", subject );
        }
        else
        {
            XMLTool.removeChildNodes( titleElem, false );
            XMLTool.createTextNode( doc, titleElem, subject );
        }

        Element subjectElem = XMLTool.getElement( contentdataElem, "subject" );
        if ( subjectElem == null )
        {
            XMLTool.createElement( doc, contentdataElem, "subject", subject );
        }
        else
        {
            XMLTool.removeChildNodes( subjectElem, false );
            XMLTool.createTextNode( doc, subjectElem, subject );
        }

        Element summaryElem = XMLTool.getElement( contentdataElem, "summary" );
        String summary = formItems.getString( "contentdata_summary", "" );
        if ( summaryElem == null )
        {
            XMLTool.createElement( doc, contentdataElem, "summary", summary );
        }
        else
        {
            XMLTool.removeChildNodes( summaryElem, false );
            XMLTool.createTextNode( doc, summaryElem, summary );
        }

        Element newsletterElem = XMLTool.getElement( contentdataElem, "newsletter" );
        if ( newsletterElem == null )
        {
            newsletterElem = XMLTool.createElement( doc, contentdataElem, "newsletter" );
        }
        else
        {
            XMLTool.removeChildNodes( newsletterElem, false );
        }
        newsletterElem.setAttribute( "menuitemkey", menuItemKey.toString() );
        XMLTool.createCDATASection( doc, newsletterElem, htmlDoc );
    }

    private void preProcesscontentDocumentWithoutNewsletterPageTemplate( Document doc, ExtendedMap formItems, Element contentElem )
    {
        Element contentdataElem = XMLTool.getElement( contentElem, "contentdata" );
        Element newsletterElem = XMLTool.getElement( contentdataElem, "newsletter" );
        HttpSession session = (HttpSession) formItems.get( "_session" );

        StringWriter sw = new StringWriter();
        StringReader sr = new StringReader( HTML_EXTRACT_XSL );
        try
        {
            transformXML( session, sw, new DOMSource( doc ), new StreamSource( sr ), new HashMap<String, Object>() );
        }
        catch ( TransformerException te )
        {
            throw new VerticalAdminException( te );
        }
        String htmlDoc = sw.toString();

        // create newsletter xml
        XMLTool.removeChildNodes( newsletterElem, true );
        XMLTool.createCDATASection( doc, newsletterElem, htmlDoc );
    }
}
