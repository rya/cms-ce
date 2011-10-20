/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.JDOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerKey;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeXmlCreator;
import com.enonic.cms.core.content.contenttype.InvalidContentTypeConfigException;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;


public class ContentTypeHandlerServlet
    extends AdminHandlerBaseServlet
{

    public String buildContentTypeXML( ExtendedMap formItems, boolean reload )
    {
        StringWriter sw = new StringWriter();

        try
        {
            Document doc = XMLTool.createDocument();

            // Create unit element
            Element contentType = XMLTool.createRootElement( doc, "contenttype" );

            if ( formItems.containsKey( "key" ) )
            {
                contentType.setAttribute( "key", formItems.getString( "key" ) );
            }
            if ( formItems.containsKey( "sitekey" ) )
            {
                contentType.setAttribute( "sitekey", formItems.getString( "sitekey" ) );
            }
            if ( formItems.containsKey( "contenthandlerkey" ) )
            {
                contentType.setAttribute( "contenthandlerkey", formItems.getString( "contenthandlerkey" ) );
            }
            if ( formItems.containsKey( "csskey" ) )
            {
                contentType.setAttribute( "csskey", formItems.getString( "csskey" ) );
            }

            XMLTool.createElement( doc, contentType, "name", formItems.getString( "name", "" ) );
            XMLTool.createElement( doc, contentType, "description", formItems.getString( "description", "" ) );

            // Module XML
            String moduleXML = formItems.getString( "module", "" );
            if ( moduleXML == null || moduleXML.length() == 0 )
            {
                XMLTool.createElement( doc, contentType, "moduledata" );
            }
            else
            {
                if ( !reload )
                {
                    try
                    {
                        Document modDocTemp = XMLTool.domparse( moduleXML, "contenttype" );

                        Document modDoc = XMLTool.createDocument( "moduledata" );
                        XMLTool.mergeDocuments( modDoc, modDocTemp, false );

                        contentType.appendChild( doc.importNode( modDoc.getDocumentElement(), true ) );
                    }
                    catch ( Exception e )
                    {
                        addError( 2, "module", moduleXML );
                    }
                }
            }
            XMLTool.printDocument( sw, doc );
        }
        catch ( Exception e )
        {
            System.err.println( "[Error] Something failed:\n" + e.toString() );
        }
        return sw.toString();
    }

    /**
     * Insert the method's description here.
     */
    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        List<ContentTypeEntity> allContentTypes = contentTypeDao.getAll();
        ContentTypeXmlCreator xmlCreator = new ContentTypeXmlCreator();

        XMLDocument contentTypesDoc = xmlCreator.createContentTypesDocument( allContentTypes );

        Document doc = contentTypesDoc.getAsDOMDocument();

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        parameters.put( "page", String.valueOf( request.getParameter( "page" ).toString() ) );
        addSortParamteres( "name", "ascending", formItems, session, parameters );

        transformXML( request, response, doc, "contenttype_browse.xsl", parameters );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // Enforce unique names
        String name = formItems.getString( "name" );
        if ( admin.getContentTypeKeyByName( name ) > -1 )
        {
            addError( 7, "name", name );
        }

        String xmlData = buildContentTypeXML( formItems, false );

        ContentHandlerKey contentHandlerKey = new ContentHandlerKey( formItems.getString( "contenthandlerkey" ) );
        ContentHandlerEntity contentHandler = contentHandlerDao.findByKey( contentHandlerKey );
        ContentHandlerName contentHandlerName = ContentHandlerName.parse( contentHandler.getClassName() );
        String errorInConfig = validateConfig( contentHandlerName, xmlData );

        if ( this.hasErrors() || errorInConfig != null )
        {
            if ( errorInConfig != null )
            {
                String moduleXML = formItems.getString( "module", "" );
                addError( 2, "module", moduleXML );
                formItems.put( "errorInConfig", errorInConfig );
            }
            handlerForm( request, response, session, admin, formItems );
            return;
        }

        admin.createContentType( user, xmlData );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        boolean createContent = formItems.containsKey( "create" );
        boolean reloading = formItems.getString( "reload", "" ).equals( "true" );
        boolean usehandlerindexing = false;
        HashMap<String, String> parameters = new HashMap<String, String>();
        Element contentTypeElem = null;
        String xmlData = null;
        Document doc = null;
        String referer = request.getHeader( "referer" );

        int contentTypeKey = -1;
        String generateKey = formItems.getString( "generatectykey", "" );
        if ( generateKey.equals( "true" ) )
        {
            parameters.put( "generatectykey", "true" );
        }
        else
        {
            String keyStr = (String) request.getParameter( "key" );
            if ( keyStr != null && keyStr.length() > 0 )
            {
                contentTypeKey = Integer.parseInt( keyStr );
            }
        }

        int contentHandlerKey = formItems.getInt( "contenthandlerkey", 0 );

        if ( reloading || hasErrors() )
        {
            if ( reloading )
            {
                usehandlerindexing = true;
            }

            doc = XMLTool.createDocument( "contenttypes" );
            Document contentTypeDoc = XMLTool.domparse( buildContentTypeXML( formItems, reloading ) );
            contentTypeElem = contentTypeDoc.getDocumentElement();
            if ( formItems.containsKey( "key" ) )
            {
                int key = formItems.getInt( "key" );
                int contentCount = admin.getContentCountByContentType( key );
                contentTypeElem.setAttribute( "contentcount", String.valueOf( contentCount ) );
            }
            doc.getDocumentElement().appendChild( doc.importNode( contentTypeElem, true ) );

            referer = formItems.getString( "referer" );
        }
        else if ( contentTypeKey == -1 )
        {
            // Blank form, make dummy document
            doc = XMLTool.createDocument( "contenttypes" );

            usehandlerindexing = true;

            // Create content type element
            contentTypeElem = XMLTool.createElement( doc, doc.getDocumentElement(), "contenttype" );
            createContent = true;
        }
        else
        {
            // Edit content type
            xmlData = admin.getContentType( contentTypeKey, true );

            doc = XMLTool.domparse( xmlData );

            contentTypeElem = XMLTool.getElement( doc.getDocumentElement(), "contenttype" );
            String contentHandlerString = contentTypeElem.getAttribute( "contenthandlerkey" );
            if ( contentHandlerString.length() > 0 )
            {
                contentHandlerKey = Integer.parseInt( contentHandlerString );
            }
            String cssString = contentTypeElem.getAttribute( "csskey" );
            if ( cssString.length() > 0 )
            {
                ResourceKey cssKey = new ResourceKey( cssString );
                parameters.put( "cssname", cssKey.toString() );

                ResourceFile contentTypeStylesheet = this.resourceService.getResourceFile( cssKey );
                if ( contentTypeStylesheet == null )
                {
                    parameters.put( "cssexist", "false" );
                }
                else
                {
                    parameters.put( "cssexist", "true" );
                }
            }
        }
        String contentHandlersXML = admin.getContentHandlers();
        Document contentHandlersDoc = XMLTool.domparse( contentHandlersXML );
        doc.getDocumentElement().appendChild(doc.importNode(contentHandlersDoc.getDocumentElement(), true));

        addErrorsXML(doc);

        if ( createContent )
        {
            parameters.put( "create", "1" );
        }
        else
        {
            parameters.put( "create", "0" );
        }
        parameters.put( "referer", referer );

        if ( contentHandlerKey > -1 )
        {
            parameters.put( "contenthandlerkey", Integer.toString( contentHandlerKey ) );
        }
        parameters.put( "usehandlerindexing", String.valueOf( usehandlerindexing ) );
        parameters.put( "page", String.valueOf( request.getParameter( "page" ).toString() ) );
        parameters.put( "errorInConfig", formItems.getString( "errorInConfig", "" ) );

        transformXML( request, response, doc, "contenttype_form.xsl", parameters );
    }

    /**
     * Uses the form input to remove one selected content type from the system.
     */
    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        admin.removeContentType( user, key );
        redirectClientToReferer( request, response );
    }

    /**
     * Provides a list of all the content types in the system.
     */
    public boolean handlerSelect( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        String returnKey = null;
        String returnView = null;
        int returnRow = -1;

        if ( !( request.getParameter( "returnkey" ) == null || request.getParameter( "returnkey" ).toString().equals( "" ) ) )
        {
            returnKey = request.getParameter( "returnkey" ).toString();
        }
        else
        {
            String message = "Parameter not found: returnkey";
            VerticalAdminLogger.errorAdmin(message, null );
        }

        if ( !( request.getParameter( "returnview" ) == null || request.getParameter( "returnview" ).toString().equals( "" ) ) )
        {
            returnView = request.getParameter( "returnview" ).toString();
        }
        else
        {
            String message = "Parameter not found: returnview";
            VerticalAdminLogger.errorAdmin(message, null );
        }

        if ( !( request.getParameter( "returnrow" ) == null || request.getParameter( "returnrow" ).toString().equals( "" ) ) )
        {
            returnRow = Integer.parseInt( request.getParameter( "returnrow" ).toString() );
        }

        Document doc = XMLTool.domparse( admin.getContentTypes() ); // siteKey

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put( "returnview", returnView );
        parameters.put( "returnkey", returnKey );

        if ( returnRow != -1 )
        {
            parameters.put( "returnrow", String.valueOf( returnRow ) );
        }

        transformXML( request, response, doc, "contenttype_selector.xsl", parameters );

        return true;
    }

    /**
     * Uses the form input to update a content type.
     */
    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // Build site XML and create the protal
        String xmlData = buildContentTypeXML( formItems, false );

        ContentHandlerKey contentHandlerKey = new ContentHandlerKey( formItems.getString( "contenthandlerkey" ) );
        ContentHandlerEntity contentHandler = contentHandlerDao.findByKey( contentHandlerKey );
        ContentHandlerName contentHandlerName = ContentHandlerName.parse( contentHandler.getClassName() );
        String errorInConfig = validateConfig( contentHandlerName, xmlData );

        if ( this.hasErrors() || errorInConfig != null )
        {
            if ( errorInConfig != null )
            {
                String moduleXML = formItems.getString( "module", "" );
                addError( 2, "module", moduleXML );
                formItems.put( "errorInConfig", errorInConfig );
            }
            handlerForm( request, response, session, admin, formItems );
            return;
        }

        admin.updateContentType( user, xmlData );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( operation.equals( "selectsite" ) )
        {
            String returnKey = null;
            String returnView = null;
            int returnRow = -1;

            if ( !( request.getParameter( "returnkey" ) == null || request.getParameter( "returnkey" ).toString().equals( "" ) ) )
            {
                returnKey = request.getParameter( "returnkey" ).toString();
            }
            else
            {
                String message = "Parameter not found: returnkey";
                VerticalAdminLogger.errorAdmin(message, null );
            }

            if ( !( request.getParameter( "returnview" ) == null || request.getParameter( "returnview" ).toString().equals( "" ) ) )
            {
                returnView = request.getParameter( "returnview" ).toString();
            }
            else
            {
                String message = "Parameter not found: returnview";
                VerticalAdminLogger.errorAdmin(message, null );
            }

            if ( !( request.getParameter( "returnrow" ) == null || request.getParameter( "returnrow" ).toString().equals( "" ) ) )
            {
                returnRow = Integer.parseInt( request.getParameter( "returnrow" ).toString() );
            }

            Document doc = admin.getContentTypes( false ).getAsDOMDocument();

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put( "returnview", returnView );
            parameters.put( "returnkey", returnKey );

            if ( returnRow != -1 )
            {
                parameters.put( "returnrow", String.valueOf( returnRow ) );
            }

            transformXML( request, response, doc, "contenttype_selector.xsl", parameters );
        }
        else if ( "regenerateindex".equals( operation ) )
        {
            int contentTypeKey = formItems.getInt( "contenttypekey" );
//            ( "regenerate index for content type key: " + contentTypeKey );

            admin.regenerateIndexForContentType( contentTypeKey );

            redirectClientToReferer( request, response );
        }
    }

    private String validateConfig( ContentHandlerName contentHandlerName, String xmlData )
    {
        org.jdom.Element configEl;

        try
        {
            org.jdom.Element contentTypeEl = JDOMUtil.parseDocument( xmlData ).getRootElement();
            org.jdom.Element moduleDataEl = contentTypeEl.getChild( "moduledata" );
            if ( moduleDataEl == null )
            {
                configEl = contentTypeEl.getChild( "config" );
            }
            else
            {
                configEl = moduleDataEl.getChild( "config" );
            }

        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to validate content type config", e );
        }
        catch ( JDOMException e )
        {
            throw new RuntimeException( "Failed to validate content type config", e );
        }

        if ( configEl != null )
        {
            // Parse the content type config... the parser will throw exceptions if anything is not correctly written
            try
            {
                if ( contentHandlerName.equals( ContentHandlerName.CUSTOM ) )
                {
                    final ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( contentHandlerName, configEl );
                    contentTypeConfig.validate();
                }
            }
            catch ( InvalidContentTypeConfigException e )
            {
                return e.getMessage();
            }
        }

        return null;
    }

}
