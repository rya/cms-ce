/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.framework.xml.XMLException;

import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemSpecification;
import com.enonic.cms.core.structure.page.PageSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;
import com.enonic.cms.core.structure.page.template.PageTemplateSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

import com.enonic.cms.business.portal.cache.PageCacheService;

public class PageTemplateHandlerServlet
    extends AdminHandlerBaseServlet
{

    private Document buildPageTemplateXML( ExtendedMap formItems, boolean createPageTemplate )
        throws VerticalAdminException
    {

        String key = null;
        Element tempElement;

        Document doc = XMLTool.createDocument( "pagetemplate" );
        Element pageTemplate = doc.getDocumentElement();

        String type = formItems.getString( "type", "page" );
        pageTemplate.setAttribute( "type", type );

        String runAs = formItems.getString( "runAs", RunAsType.INHERIT.toString() );
        pageTemplate.setAttribute( "runAs", runAs );

        String menuKey = formItems.getString( "menukey" );
        pageTemplate.setAttribute( "menukey", menuKey );

        if ( !createPageTemplate )
        {
            key = formItems.getString( "key" );
            pageTemplate.setAttribute( "key", key );
        }

        // CSS:
        String tmp = formItems.getString( "csskey", null );
        if ( tmp != null && tmp.length() > 0 )
        {
            tempElement = XMLTool.createElement( doc, pageTemplate, "css" );
            tempElement.setAttribute( "stylesheetkey", tmp );
        }

        XMLTool.createElement( doc, pageTemplate, "name", formItems.getString( "name", "" ) );

        XMLTool.createElement( doc, pageTemplate, "description", formItems.getString( "description", "" ) );

        tempElement = XMLTool.createElement( doc, pageTemplate, "stylesheet" );
        if ( formItems.containsKey( "stylesheetkey" ) )
        {
            tempElement.setAttribute( "stylesheetkey", formItems.getString( "stylesheetkey" ) );
        }

        Element templateParams = XMLTool.createElement( doc, pageTemplate, "pagetemplateparameters" );

        if ( isArrayFormItem( formItems, "paramname" ) == false )
        {
            if ( formItems.containsKey( "paramname" ) )
            {
                if ( createPageTemplate )
                {
                    createTemplateParamXML( createPageTemplate, doc, templateParams, "", null,
                                            formItems.getString( "multiple1", null ) != null && formItems.getString( "multiple1", "" ).length() > 0 ? formItems.getString( "multiple1" )
                                                : "0", "0", formItems.getString( "paramname", "" ),
                                            formItems.getString( "separator", "" ) );
                }
                else
                {
                    createTemplateParamXML( createPageTemplate, doc, templateParams, formItems.getString( "paramkey", "" ), key,
                                            formItems.getString( "multiple1", null ) != null && ( formItems.getString( "multiple1" ) ).length() > 0 ? formItems.getString( "multiple1" )
                                                : "0", "0", formItems.getString( "paramname" ), formItems.getString( "separator", "" ) );
                }
            }
        }
        else
        {
            String[] paramNameArray = (String[]) formItems.get( "paramname" );
            String[] paramKeyArray = (String[]) formItems.get( "paramkey", null );
            String[] separatorArray = (String[]) formItems.get( "separator" );

            for ( int j = 0; j < paramNameArray.length; j++ )
            {
                if ( paramNameArray[j] != null && paramNameArray[j].trim().length() != 0 )
                {
                    String multiple = formItems.getString( "multiple" + ( j + 1 ), "0" );
                    if ( createPageTemplate )
                    {
                        createTemplateParamXML( createPageTemplate, doc, templateParams, "", null, multiple, "0", paramNameArray[j],
                                                separatorArray[j] );
                    }
                    else
                    {
                        createTemplateParamXML( createPageTemplate, doc, templateParams, paramKeyArray[j], key, multiple, "0",
                                                paramNameArray[j], separatorArray[j] );
                    }
                }
            }
        }

        // Default contentobjects for the page template:
        if ( isArrayFormItem( formItems, "paramname" ) )
        {
            String[] paramNameArray = (String[]) formItems.get( "paramname" );
            String[] paramKeyArray = (String[]) formItems.get( "paramkey", null );
            Element contentObjectsElem = XMLTool.createElement( doc, pageTemplate, "contentobjects" );

            int newObjectCounter = 0;
            for ( int i = 0; i < paramNameArray.length; ++i )
            {
                if ( isArrayFormItem( formItems, paramNameArray[i] + "co" ) )
                {
                    String[] coArray = (String[]) formItems.get( paramNameArray[i] + "co" );
                    String[] coNameArray = (String[]) formItems.get( "view" + paramNameArray[i] + "co" );
                    boolean used = false;
                    for ( int j = 0; j < coArray.length; j++ )
                    {
                        Element contentObjectElem = XMLTool.createElement( doc, contentObjectsElem, "contentobject" );
                        contentObjectElem.setAttribute( "conobjkey", coArray[j] );
                        if ( paramKeyArray != null && paramKeyArray[i].length() > 0 )
                        {
                            contentObjectElem.setAttribute( "parameterkey", paramKeyArray[i] );
                        }
                        else
                        {
                            contentObjectElem.setAttribute( "parameterkey", "_" + newObjectCounter );
                            used = true;
                        }
                        XMLTool.createElement( doc, contentObjectElem, "order", String.valueOf( j ) );
                        XMLTool.createElement( doc, contentObjectElem, "name", coNameArray[j] );

                        if ( pageTemplate.getAttribute( "key" ) != null )
                        {
                            contentObjectElem.setAttribute( "pagetemplatekey", pageTemplate.getAttribute( "key" ) );
                        }
                    }
                    if ( used )
                    {
                        newObjectCounter++;
                    }
                }
                else if ( formItems.containsKey( paramNameArray[i] + "co" ) )
                {
                    String coName = formItems.getString( "view" + paramNameArray[i] + "co" );
                    Element contentObjectElem = XMLTool.createElement( doc, contentObjectsElem, "contentobject" );
                    contentObjectElem.setAttribute( "conobjkey", formItems.getString( paramNameArray[i] + "co" ) );
                    if ( paramKeyArray != null && paramKeyArray[i].length() > 0 )
                    {
                        contentObjectElem.setAttribute( "parameterkey", paramKeyArray[i] );
                    }
                    else
                    {
                        contentObjectElem.setAttribute( "parameterkey", "_" + newObjectCounter++ );
                    }
                    XMLTool.createElement( doc, contentObjectElem, "order", "0" );
                    XMLTool.createElement( doc, contentObjectElem, "name", coName );

                    if ( pageTemplate.getAttribute( "key" ) != null )
                    {
                        contentObjectElem.setAttribute( "pagetemplatekey", pageTemplate.getAttribute( "key" ) );
                    }
                }
                else
                {
                    // This fixes a bug when inserting objects in a new page template. If the object was inserted at pos 3 with no
                    // object at pos 1, it would be placed in pos 1.
                    if ( createPageTemplate )
                    {
                        newObjectCounter++;
                    }
                }
            }
        }
        else if ( formItems.containsKey( "paramname" ) )
        {
            String paramName = formItems.getString( "paramname" );
            String paramKey = formItems.getString( "paramkey", "_0" );

            Element contentObjectsElem = XMLTool.createElement( doc, pageTemplate, "contentobjects" );
            if ( isArrayFormItem( formItems, paramName + "co" ) )
            {
                String[] coArray = (String[]) formItems.get( paramName + "co" );
                for ( int j = 0; j < coArray.length; j++ )
                {
                    Element contentObjectElem = XMLTool.createElement( doc, contentObjectsElem, "contentobject" );
                    contentObjectElem.setAttribute( "conobjkey", coArray[j] );
                    if ( paramKey != null && paramKey.length() > 0 )
                    {
                        contentObjectElem.setAttribute( "parameterkey", paramKey );
                    }
                    XMLTool.createElement( doc, contentObjectElem, "order", String.valueOf( j ) );

                    if ( pageTemplate.getAttribute( "key" ) != null )
                    {
                        contentObjectElem.setAttribute( "pagetemplatekey", pageTemplate.getAttribute( "key" ) );
                    }
                }
            }
            else if ( formItems.containsKey( paramName + "co" ) )
            {
                Element contentObjectElem = XMLTool.createElement( doc, contentObjectsElem, "contentobject" );
                contentObjectElem.setAttribute( "conobjkey", formItems.getString( paramName + "co" ) );
                if ( paramKey != null && paramKey.length() > 0 )
                {
                    contentObjectElem.setAttribute( "parameterkey", paramKey );
                }
                XMLTool.createElement( doc, contentObjectElem, "order", "0" );

                if ( pageTemplate.getAttribute( "key" ) != null )
                {
                    contentObjectElem.setAttribute( "pagetemplatekey", pageTemplate.getAttribute( "key" ) );
                }
            }

        }

        // page template parameters other than parameters of type "object"
        Element ptdElem = XMLTool.createElement( doc, pageTemplate, "pagetemplatedata" );
        if ( isArrayFormItem( formItems, "parameter_name" ) )
        {

            String[] paramNames = (String[]) formItems.get( "parameter_name" );
            String[] paramValues = (String[]) formItems.get( "parameter_value" );
            String[] paramValueNames = (String[]) formItems.get( "viewparameter_value" );
            String[] paramTypes = (String[]) formItems.get( "parameter_type" );

            for ( int i = 0; i < paramNames.length; ++i )
            {
                Element ptpElem = XMLTool.createElement( doc, ptdElem, "pagetemplateparameter" );
                ptpElem.setAttribute( "name", paramNames[i] );
                ptpElem.setAttribute( "value", paramValues[i] );
                ptpElem.setAttribute( "type", paramTypes[i] );
                if ( paramValueNames[i] != null && paramValueNames[i].length() > 0 )
                {
                    ptpElem.setAttribute( "valuename", paramValueNames[i] );
                }
            }
        }
        else if ( formItems.containsKey( "parameter_name" ) )
        {
            String paramName = formItems.getString( "parameter_name", null );
            if ( paramName != null && paramName.length() > 0 )
            {
                String paramValue = formItems.getString( "parameter_value", "" );
                String paramValueName = formItems.getString( "viewparameter_value", null );
                String paramType = formItems.getString( "parameter_type", "" );

                Element ptpElem = XMLTool.createElement( doc, ptdElem, "pagetemplateparameter" );
                ptpElem.setAttribute( "name", paramName );
                ptpElem.setAttribute( "value", paramValue );
                ptpElem.setAttribute( "type", paramType );
                if ( paramValueName != null )
                {
                    ptpElem.setAttribute( "valuename", paramValueName );
                }
            }
        }

        // Datasources
        String datasources = formItems.getString( "datasources", null );
        if ( StringUtils.isNotBlank( datasources ) )
        {
            Document dsDoc = XMLTool.domparse( datasources );
            ptdElem.appendChild( doc.importNode( dsDoc.getDocumentElement(), true ) );
        }

        // default document
        Element documentElem = XMLTool.createElement( doc, ptdElem, "document" );
        if ( verticalProperties.isStoreXHTMLOn() )
        {
            documentElem.setAttribute( "mode", "xhtml" );
            XMLTool.createXHTMLNodes( doc, documentElem, formItems.getString( "contentdata_body", "" ), true );
        }
        else
        {
            XMLTool.createCDATASection( doc, documentElem, formItems.getString( "contentdata_body", "" ) );
        }

        // Content types
        Element ctyElem = XMLTool.createElement( doc, pageTemplate, "contenttypes" );
        String[] contentTypeKeys = formItems.getStringArray( "contenttypekey" );
        for ( String contentTypeKey : contentTypeKeys )
        {
            XMLTool.createElement( doc, ctyElem, "contenttype" ).setAttribute( "key", contentTypeKey );
        }

        return doc;
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        try
        {
            User user = securityService.getLoggedInAdminConsoleUser();

            int menuKey = formItems.getInt( "menukey" );

            Source xmlSource = admin.getPageTemplatesByMenu( menuKey, null ).getAsDOMSource();

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, "pagetemplate_browse.xsl" );

            // Parameters
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            parameters.put( "menukey", String.valueOf( menuKey ) );
            addSortParamteres( "name", "ascending", formItems, session, parameters );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException te )
        {
            String message = "XSLT error: %t";
            VerticalAdminLogger.errorAdmin( message, te );
        }
        catch ( IOException ioe )
        {
            String message = "I/O error: %t";
            VerticalAdminLogger.errorAdmin( message, ioe );
        }
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalEngineException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // Build site XML and create the protal
        String xmlData = XMLTool.documentToString( buildPageTemplateXML( formItems, true ) );
        admin.createPageTemplate( user, xmlData );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );

    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        try
        {
            User user = securityService.getLoggedInAdminConsoleUser();
            boolean createPageTemplate;
            boolean updateStyleSheet = formItems.getBoolean( "updatestylesheet", true );
            boolean updateCSS = formItems.getBoolean( "updatecss", false );
            String xmlData;
            Document doc;
            String cssStylesheetKey = null;

            String datasourcesXML;

            int menuKey = formItems.getInt( "menukey" );

            ResourceKey stylesheetKey = null;
            ResourceFile stylesheet = null;
            ResourceKey cssKey = null;
            boolean stylesheetValid = false;
            String cssKeyParam = request.getParameter( "selectedcsskey" );
            if ( cssKeyParam != null && !"".equals( cssKeyParam ) )
            {
                cssKey = new ResourceKey( cssKeyParam );
            }
            if ( request.getParameter( "selstylesheetkey" ) != null && request.getParameter( "selstylesheetkey" ).length() > 0 )
            {
                stylesheetKey = new ResourceKey( request.getParameter( "selstylesheetkey" ) );
                formItems.putString( "stylesheetkey", stylesheetKey.toString() );
            }

            int key = formItems.getInt( "key", -1 );

            // If we have not selected a stylesheet yet
            if ( stylesheetKey == null && cssKey == null && key == -1 )
            {

                createPageTemplate = true;

                doc = XMLTool.createDocument( "pagetemplates" );
                Document dsDoc = XMLTool.createDocument( "datasources" );
                datasourcesXML = XMLTool.documentToString( dsDoc );
            }
            else
            {
                createPageTemplate = ( key == -1 );
                int pageTemplateKey;

                if ( stylesheetKey == null && cssKey == null )  // createPageTemplate = false
                {
                    //	If we are editing an existing template
                    pageTemplateKey = Integer.parseInt( request.getParameter( "key" ) );
                    xmlData = admin.getPageTemplate( pageTemplateKey );
                    doc = XMLTool.domparse( xmlData );
                    Element pagetemplateElem = XMLTool.getElement( doc.getDocumentElement(), "pagetemplate" );
                    Element stylesheetElem = XMLTool.getElement( pagetemplateElem, "stylesheet" );
                    stylesheetKey = new ResourceKey( stylesheetElem.getAttribute( "stylesheetkey" ) );
                }
                else
                {
                    // If we are making a new template
                    doc = buildPageTemplateXML( formItems, key == -1 );
                    Element oldRoot = doc.getDocumentElement();
                    String keyStr = oldRoot.getAttribute( "key" );
                    if ( keyStr != null && keyStr.length() > 0 )
                    {
                        pageTemplateKey = Integer.parseInt( keyStr );
                    }
                    else
                    {
                        pageTemplateKey = -1;
                    }
                    Element newRoot = XMLTool.createElement( doc, "pagetemplates" );
                    doc.replaceChild( newRoot, oldRoot );
                    newRoot.appendChild( oldRoot );
                    updateStyleSheet = stylesheetKey != null;
                    if ( !updateStyleSheet )
                    {
                        Element elem = XMLTool.getElement( oldRoot, "stylesheet" );
                        String styleSheetKeyStr = elem.getAttribute( "stylesheetkey" );
                        if ( styleSheetKeyStr.length() > 0 )
                        {
                            stylesheetKey = new ResourceKey( styleSheetKeyStr );
                        }
                    }
                }

                Element cssElem = (Element) XMLTool.selectNode( doc, "/pagetemplates/pagetemplate/css" );
                if ( cssElem != null )
                {
                    if ( updateCSS && cssKey == null )
                    {
                        cssElem.getParentNode().removeChild( cssElem );
                    }
                    else
                    {
                        cssStylesheetKey = cssElem.getAttribute( "stylesheetkey" );
                    }
                }

                Element ptdElem = (Element) XMLTool.selectNode( doc, "/pagetemplates/pagetemplate/pagetemplatedata" );
                Element dselem = XMLTool.getElement( ptdElem, "datasources" );
                if ( dselem != null )
                {
                    Document dsDoc = XMLTool.createDocument();
                    dsDoc.appendChild( dsDoc.importNode( dselem, true ) );
                    datasourcesXML = XMLTool.documentToString( dsDoc );
                }
                else
                {
                    Document dsDoc = XMLTool.createDocument( "datasources" );
                    datasourcesXML = XMLTool.documentToString( dsDoc );
                }

                if ( !updateStyleSheet )
                {
                    // Insert valuename attributes all parameters
                    Element[] pagetemplateparamElems = XMLTool.getElements( ptdElem, "pagetemplateparameter" );
                    for ( Element pagetemplateparamElem : pagetemplateparamElems )
                    {
                        String keyStr = pagetemplateparamElem.getAttribute( "value" );
                        if ( keyStr != null && keyStr.length() > 0 )
                        {
                            String type = pagetemplateparamElem.getAttribute( "type" );
                            if ( "category".equals( type ) )
                            {
                                int categoryKey = Integer.parseInt( keyStr );
                                String name = admin.getCategoryName( categoryKey );
                                pagetemplateparamElem.setAttribute( "valuename", name );
                            }
                            else if ( "page".equals( type ) )
                            {
                                int menuItemKey = Integer.parseInt( keyStr );
                                String name = admin.getMenuItemName( menuItemKey );
                                pagetemplateparamElem.setAttribute( "valuename", name );
                            }
                            else if ( "resource".equals( type ) )
                            {
                                pagetemplateparamElem.setAttribute( "valuename", keyStr );
                            }
                        }
                    }
                }

                Document menuItemsDoc = admin.getMenuItemsByPageTemplates( user, new int[]{pageTemplateKey} ).getAsDOMDocument();
                XMLTool.mergeDocuments( doc, menuItemsDoc, true );
            }

            if ( stylesheetKey != null && ( ( createPageTemplate && cssKey == null ) || updateStyleSheet ) )
            {

                Map<String, Element> elemMap = new HashMap<String, Element>();
                Element root = doc.getDocumentElement();
                if ( updateStyleSheet )
                {
                    // Remove all parameters from xml
                    root = XMLTool.getElement( root, "pagetemplate" );
                    Element pageTemplateParamterRootElement = XMLTool.getElement( root, "pagetemplateparameters" );
                    Element[] pageTemplateParameterElems = XMLTool.getElements( pageTemplateParamterRootElement, "pagetemplateparameter" );
                    for ( Element elem : pageTemplateParameterElems )
                    {
                        String name = XMLTool.getElementText( XMLTool.getElement( elem, "name" ) );
                        elemMap.put( name, elem );
                    }
                    root.removeChild( pageTemplateParamterRootElement );
                    pageTemplateParamterRootElement = XMLTool.getElement( root, "pagetemplatedata" );
                    pageTemplateParameterElems = XMLTool.getElements( pageTemplateParamterRootElement, "pagetemplateparameter" );
                    for ( Element elem1 : pageTemplateParameterElems )
                    {
                        String name = elem1.getAttribute( "name" );
                        elemMap.put( name, elem1 );
                        pageTemplateParamterRootElement.removeChild( elem1 );
                    }
                }
                Element stylesheetParams = XMLTool.createElement( doc, root, "pagetemplateparameters" );

                Element pagetemplatedataElem = XMLTool.getElement( root, "pagetemplatedata" );
                if ( pagetemplatedataElem == null )
                {
                    pagetemplatedataElem = XMLTool.createElement( doc, root, "pagetemplatedata" );
                }

                stylesheet = resourceService.getResourceFile( stylesheetKey );
                if ( stylesheet != null )
                {
                    Document stylesheetDoc = null;
                    try
                    {
                        stylesheetDoc = stylesheet.getDataAsXml().getAsDOMDocument();
                        stylesheetValid = true;
                    }
                    catch ( XMLException e )
                    {
                    }
                    if ( stylesheetDoc != null )
                    {
                        Element[] paramElems = XMLTool.getElements( stylesheetDoc.getDocumentElement(), "xsl:param" );
                        for ( Element paramElem : paramElems )
                        {
                            Element typeElem = XMLTool.getElement( paramElem, "type" );
                            Element tempElem;
                            String name = paramElem.getAttribute( "name" );
                            if ( typeElem != null )
                            {
                                String type = XMLTool.getElementText( typeElem );
                                if ( "object".equals( type ) || "region".equals( type ) )
                                {
                                    Element elem = elemMap.get( name );
                                    if ( elem != null && elem.getAttribute( "type" ).length() == 0 )
                                    {
                                        stylesheetParams.appendChild( elem );
                                    }
                                    else
                                    {
                                        tempElem = XMLTool.createElement( doc, stylesheetParams, "pagetemplateparameter" );
                                        XMLTool.createElement( doc, tempElem, "name", name );
                                    }
                                }
                                else
                                {
                                    if ( elemMap.containsKey( name ) )
                                    {
                                        Element elem = elemMap.get( name );
                                        String elemType = elem.getAttribute( "type" );
                                        if ( elemType.length() == 0 )
                                        {
                                            elem.setAttribute( "name", name );
                                            XMLTool.removeChildNodes( elem, true );
                                        }
                                        elem.setAttribute( "type", type );
                                        pagetemplatedataElem.appendChild( elem );
                                    }
                                    else
                                    {
                                        tempElem = XMLTool.createElement( doc, pagetemplatedataElem, "pagetemplateparameter" );
                                        tempElem.setAttribute( "name", name );
                                        tempElem.setAttribute( "type", type );
                                    }
                                }
                            }
                            else
                            {
                                // Alle vanlige parametere, spesifisert som as="xs:string", e.l. i XSL'en.
                                if ( elemMap.containsKey( name ) )
                                {
                                    Element elem = elemMap.get( name );
                                    String type = elem.getAttribute( "type" );
                                    if ( type.length() == 0 )
                                    {
                                        elem.setAttribute( "name", name );
                                        XMLTool.removeChildNodes( elem, true );
                                    }
                                    else
                                    {
                                        elem.removeAttribute( "type" );
                                        elem.removeAttribute( "valuename" );
                                    }
                                    pagetemplatedataElem.appendChild( elem );
                                }
                                else
                                {
                                    tempElem = XMLTool.createElement( doc, pagetemplatedataElem, "pagetemplateparameter" );
                                    tempElem.setAttribute( "name", name );
                                }
                            }
                        }
                    }
                }
            }

            if ( stylesheet == null && stylesheetKey != null )
            {
                stylesheet = resourceService.getResourceFile( stylesheetKey );
            }

            if ( stylesheet != null )
            {
                Document stylesheetDoc = null;
                try
                {
                    stylesheetDoc = stylesheet.getDataAsXml().getAsDOMDocument();
                    stylesheetValid = true;
                }
                catch ( XMLException e )
                {
                }
                if ( stylesheetDoc != null )
                {
                    Element tmpElem = XMLTool.createElement( doc.getDocumentElement(), "resource" );
                    tmpElem.appendChild( doc.importNode( stylesheetDoc.getDocumentElement(), true ) );
                }
            }

            // Get content types for this site
            XMLTool.mergeDocuments( doc, admin.getContentTypes( false ).getAsDOMDocument(), true );

            DOMSource xmlSource = new DOMSource( doc );

            Source xslSource = AdminStore.getStylesheet( session, "pagetemplate_form.xsl" );

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            addCommonParameters( admin, user, request, parameters, -1, menuKey );

            if ( cssStylesheetKey != null )
            {
                parameters.put( "cssStylesheetKey", cssStylesheetKey );
                parameters.put( "cssStylesheetExist",
                                resourceService.getResourceFile( new ResourceKey( cssStylesheetKey ) ) == null ? "false" : "true" );
            }

            ResourceKey defaultCSSKey = admin.getDefaultCSSByMenu( menuKey );
            if ( defaultCSSKey != null )
            {
                parameters.put( "defaultcsskey", defaultCSSKey.toString() );
                parameters.put( "defaultcssExist", resourceService.getResourceFile( defaultCSSKey ) == null ? "false" : "true" );
            }

            if ( createPageTemplate )
            {
                parameters.put( "create", "1" );
            }
            else
            {
                parameters.put( "create", "0" );
            }

            parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            datasourcesXML = StringUtil.formatXML( datasourcesXML, 2 );
            parameters.put( "datasources", datasourcesXML );

            parameters.put( "menukey", formItems.getString( "menukey" ) );
            parameters.put( "selectedtabpageid", formItems.getString( "selectedtabpageid", "none" ) );

            if ( stylesheetKey != null )
            {
                parameters.put( "selstylesheetkey", stylesheetKey.toString() );
                parameters.put( "selstylesheetExist", stylesheet == null ? "false" : "true" );
                parameters.put( "selstylesheetValid", stylesheetValid ? "true" : "false" );

            }

            addAccessLevelParameters( user, parameters );

            UserEntity defaultRunAsUser = siteDao.findByKey( formItems.getInt( "menukey" ) ).resolveDefaultRunAsUser();
            String defaultRunAsUserName = "NA";
            if ( defaultRunAsUser != null )
            {
                defaultRunAsUserName = defaultRunAsUser.getDisplayName();
            }
            parameters.put( "defaultRunAsUser", defaultRunAsUserName );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin( "XSLT error: %t", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin( "I/O error: %t", e );
        }
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalRemoveException, VerticalSecurityException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        admin.removePageTemplate( key );

        redirectClientToReferer( request, response );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalEngineException, VerticalAdminException
    {

        String xmlData = XMLTool.documentToString( buildPageTemplateXML( formItems, false ) );
        User user = securityService.getLoggedInAdminConsoleUser();

        admin.updatePageTemplate( user, xmlData );

        invalidateCacheForPageTemplateChange( formItems.getInt( "key", -1 ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void invalidateCacheForPageTemplateChange( int pageTemplateKey )
    {
        if ( pageTemplateKey == -1 )
        {
            return;
        }

        PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey );

        if ( pageTemplate == null )
        {
            return;
        }

        PageCacheService pageCacheService = siteCachesService.getPageCacheService( pageTemplate.getSite().getKey() );

        boolean clearAllPageEntriesOnSite = pageTemplate.getType().equals( PageTemplateType.CONTENT );

        if ( clearAllPageEntriesOnSite )
        {
            pageCacheService.removePageEntriesBySite();
        }
        else
        {
            List<MenuItemEntity> menuItemsUsingPageTemplate = getMenuItemsUsingPageTemplate( pageTemplate.getKey() );

            for ( MenuItemEntity menuItem : menuItemsUsingPageTemplate )
            {
                pageCacheService.removeEntriesByMenuItem( menuItem.getMenuItemKey() );
            }
        }
    }

    private List<MenuItemEntity> getMenuItemsUsingPageTemplate( int pageTemplateKey )
    {
        MenuItemSpecification menuItemSpec = new MenuItemSpecification();
        PageSpecification pageSpecification = new PageSpecification();

        PageTemplateSpecification pageTemplateSpecification = new PageTemplateSpecification();
        pageTemplateSpecification.setKey( new PageTemplateKey( pageTemplateKey ) );
        pageSpecification.setTemplateSpecification( pageTemplateSpecification );

        menuItemSpec.setPageSpecification( pageSpecification );

        return menuItemDao.findBySpecification( menuItemSpec );
    }

    private void createTemplateParamXML( boolean create, Document doc, Element templateParams, String key, String pageTemplateKey,
                                         String multiple, String override, String name, String separator )
    {

        Element templateParam = XMLTool.createElement( doc, templateParams, "pagetemplateparameter" );

        templateParam.setAttribute( "key", key );
        if ( !create )
        {
            templateParam.setAttribute( "pagetemplatekey", pageTemplateKey );
        }

        templateParam.setAttribute( "multiple", multiple );
        templateParam.setAttribute( "override", override );

        XMLTool.createElement( doc, templateParam, "name", name );
        XMLTool.createElement( doc, templateParam, "separator", separator );

    }

    public void handlerCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        admin.copyPageTemplate( user, key );
        redirectClientToReferer( request, response );
    }

}
