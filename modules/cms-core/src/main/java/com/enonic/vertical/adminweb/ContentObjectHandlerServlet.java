/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.plugin.PluginManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.framework.xml.XMLException;

import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.internal.service.DataSourceFailedXmlCreator;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.service.DataSourceService;

import com.enonic.cms.business.AdminConsoleTranslationService;
import com.enonic.cms.business.portal.InvocationCache;
import com.enonic.cms.business.portal.datasource.DatasourceExecutor;
import com.enonic.cms.business.portal.datasource.DatasourceExecutorContext;
import com.enonic.cms.business.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.business.portal.datasource.processor.DataSourceProcessor;
import com.enonic.cms.business.portal.datasource.processor.NonDoingDataSourceProcessor;
import com.enonic.cms.business.preview.PreviewContext;

import com.enonic.cms.domain.portal.PageRequestType;
import com.enonic.cms.domain.portal.datasource.DataSourceResult;
import com.enonic.cms.domain.portal.datasource.Datasources;
import com.enonic.cms.domain.portal.datasource.DatasourcesType;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.SiteEntity;

import com.enonic.cms.domain.stylesheet.InvalidStylesheetException;
import com.enonic.cms.domain.stylesheet.StylesheetNotFoundException;

/**
 * This class handles all content object related functionality in administration console.
 */
public final class ContentObjectHandlerServlet
    extends AdminHandlerBaseServlet
{
    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DatasourceExecutorFactory datasourceExecutorFactory;

    @Autowired
    private PluginManager pluginManager;

    private Document buildContentObjectXML( AdminService admin, ExtendedMap formItems, boolean createContentObject,
                                            boolean updateStyleSheets )
        throws VerticalAdminException
    {
        Document doc;
        Element contentObject;
        if ( updateStyleSheets )
        {
            doc = XMLTool.createDocument( "contentobjects" );
            Element root = doc.getDocumentElement();
            contentObject = XMLTool.createElement( doc, root, "contentobject" );
        }
        else
        {
            doc = XMLTool.createDocument( "contentobject" );
            contentObject = doc.getDocumentElement();
        }

        contentObject.setAttribute( "menukey", formItems.getString( "menukey" ) );

        if ( !createContentObject )
        {
            contentObject.setAttribute( "key", formItems.getString( "key" ) );

            String docContentKeyStr = formItems.getString( "contentkey", null );
            if ( docContentKeyStr != null )
            {
                contentObject.setAttribute( "contentkey", docContentKeyStr );
            }
        }

        String runAsKey = formItems.getString( "runAs", null );
        if ( runAsKey != null )
        {
            contentObject.setAttribute( "runAs", runAsKey );
        }

        Element tempElement;
        if ( updateStyleSheets )
        {
            String name = formItems.getString( "name", null );
            if ( name != null )
            {
                tempElement = XMLTool.createElement( doc, contentObject, "name", name );
            }
            else
            {
                tempElement = XMLTool.createElement( doc, contentObject, "name" );
            }
        }
        else
        {
            String name = formItems.getString( "name" );
            tempElement = XMLTool.createElement( doc, contentObject, "name", name );
        }

        /*
        if (formItems.containsKey("viewborderstylesheet")) {
            tempElement = XMLTool.createElement(doc, contentObject, "borderstylesheet");
            tempElement.setAttribute("key", formItems.getString("viewborderstylesheet"));
        }


        if (formItems.containsKey("viewstylesheet")) {
            tempElement = XMLTool.createElement(doc, contentObject, "objectstylesheet");
            tempElement.setAttribute("key", formItems.getString("viewstylesheet"));
        }
        */

        ResourceKey stylesheetKey = ResourceKey.parse( formItems.getString( "stylesheet", null ) );
        if ( stylesheetKey != null )
        {
            tempElement = XMLTool.createElement( doc, contentObject, "objectstylesheet" );
            tempElement.setAttribute( "key", String.valueOf( stylesheetKey ) );
        }

        ResourceKey borderStylesheetKey = ResourceKey.parse( formItems.getString( "borderstylesheet", null ) );
        if ( borderStylesheetKey != null )
        {
            tempElement = XMLTool.createElement( doc, contentObject, "borderstylesheet" );
            tempElement.setAttribute( "key", String.valueOf( borderStylesheetKey ) );
        }

        /*
        String ssName = formItems.getString("viewborderstylesheet", null);
        if (ssName != null) {
            tempElement = XMLTool.createElement(doc, contentObject, "borderstylesheet", ssName);
            ResourceKey stylesheetKey = new ResourceKey(formItems.getInt("borderstylesheet"));
            tempElement.setAttribute("key", String.valueOf(stylesheetKey));
            boolean shared = admin.isResourceShared(stylesheetKey);
            if (shared)
                tempElement.setAttribute("shared", "true");
        }

        ssName = formItems.getString("viewstylesheet", null);
        if (ssName != null) {
            tempElement = XMLTool.createElement(doc, contentObject, "objectstylesheet", ssName);
            tempElement.setAttribute("key", formItems.getString("stylesheet"));
            ResourceKey stylesheetKey = new ResourceKey(formItems.getInt("stylesheet"));
            tempElement.setAttribute("key", String.valueOf(stylesheetKey));
            boolean shared = admin.isResourceShared(stylesheetKey);
            if (shared)
                tempElement.setAttribute("shared", "true");
        }
        */

        Element contentObjectData = XMLTool.createElement( doc, contentObject, "contentobjectdata" );

        // caching
        String cacheType = formItems.getString( "cachetype" );
        if ( !"off".equals( cacheType ) )
        {
            contentObjectData.setAttribute( "cachedisabled", "false" );

            // cache type
            contentObjectData.setAttribute( "cachetype", cacheType );

            if ( cacheType.equals( "specified" ) )
            {
                contentObjectData.setAttribute( "mincachetime", formItems.getString( "mincachetime" ) );
            }
        }
        else
        {
            contentObjectData.setAttribute( "cachedisabled", "true" );
        }

        // document
        Element document = XMLTool.createElement( doc, contentObjectData, "document" );

        if ( verticalProperties.isStoreXHTMLOn() )
        {
            XMLTool.createXHTMLNodes( doc, document, formItems.getString( "contentdata_body", "" ), true );
        }
        else
        {
            XMLTool.createCDATASection( doc, document, formItems.getString( "contentdata_body", "" ) );
        }

        Document datasourcesDoc = null;
        try
        {
            String datasources = formItems.getString( "datasources", null );
            // Do NOT replace this with formItems.getString("datasources", "</datasources">) since
            // the editor could send blankspaces
            if ( StringUtils.isBlank( datasources ) )
            {
                datasources = "<datasources/>";
            }

            datasourcesDoc = XMLTool.domparse( datasources, "datasources" );
        }
        catch ( Exception e )
        {
            String message = "Failed to parse datasource document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
        contentObjectData.appendChild( doc.importNode( datasourcesDoc.getDocumentElement(), true ) );

        // Add script
        Element scriptElem = doc.createElement( "script" );
        scriptElem.appendChild( doc.createCDATASection( formItems.getString( "script", "" ) ) );
        contentObjectData.appendChild( scriptElem );

        // Stylesheet params
        Element styleSheetParams = XMLTool.createElement( doc, contentObjectData, "stylesheetparams" );
        if ( isArrayFormItem( formItems, "xslparam_name" ) )
        {
            String[] xslParameters = (String[]) formItems.get( "xslparam_name" );
            String[] xslParameterValues = (String[]) formItems.get( "xslparam_value" );
            String[] xslParameterTypes = (String[]) formItems.get( "xslparam_type" );

            for ( int i = 0; i < xslParameters.length; i++ )
            {
                String valueStr = xslParameterValues[i];
                if ( valueStr != null && valueStr.length() > 0 )
                {
                    tempElement = XMLTool.createElement( doc, styleSheetParams, "stylesheetparam", valueStr );
                    tempElement.setAttribute( "name", xslParameters[i] );
                    if ( xslParameterTypes[i] != null && xslParameterTypes[i].length() > 0 )
                    {
                        String type = xslParameterTypes[i];
                        tempElement.setAttribute( "type", type );

                        if ( "page".equals( type ) )
                        {
                            int menuItemKey = -1;
                            try
                            {
                                menuItemKey = Integer.parseInt( valueStr );
                            }
                            catch ( NumberFormatException nfe )
                            {
                                String message = "Failed to parse menu item key: %t";
                                VerticalAdminLogger.errorAdmin(message, nfe );
                            }
                            String menuItemName = admin.getMenuItemName( menuItemKey );
                            tempElement.setAttribute( "valuename", menuItemName );
                        }
                        else if ( "category".equals( type ) )
                        {
                            int categoryKey = -1;
                            try
                            {
                                categoryKey = Integer.parseInt( valueStr );
                            }
                            catch ( NumberFormatException nfe )
                            {
                                String message = "Failed to parse category key: %t";
                                VerticalAdminLogger.errorAdmin(message, nfe );
                            }
                            String categoryName = admin.getCategoryName( categoryKey );
                            tempElement.setAttribute( "valuename", categoryName );
                        }
                    }
                }
            }
        }
        else if ( formItems.containsKey( "xslparam_name" ) )
        {
            //String valueStr = (String) formItems.get("xslparam_value", null);
            if ( formItems.containsKey( "xslparam_value" ) )
            {
                String valueStr = formItems.getString( "xslparam_value" );
                tempElement = XMLTool.createElement( doc, styleSheetParams, "stylesheetparam", valueStr );
                tempElement.setAttribute( "name", formItems.getString( "xslparam_name" ) );
                String xslParameterType = formItems.getString( "xslparam_type", null );
                if ( xslParameterType != null )
                {
                    String type = xslParameterType;
                    tempElement.setAttribute( "type", type );

                    if ( "page".equals( type ) )
                    {
                        int menuItemKey = -1;
                        try
                        {
                            menuItemKey = formItems.getInt( "xslparam_value" );
                        }
                        catch ( NumberFormatException nfe )
                        {
                            String message = "Failed to parse menu item key: %t";
                            VerticalAdminLogger.errorAdmin(message, nfe );
                        }
                        String menuItemName = admin.getMenuItemName( menuItemKey );
                        tempElement.setAttribute( "valuename", menuItemName );
                    }
                    else if ( "category".equals( type ) )
                    {
                        int categoryKey = -1;
                        try
                        {
                            categoryKey = formItems.getInt( "xslparam_value" );
                        }
                        catch ( NumberFormatException nfe )
                        {
                            String message = "Failed to parse menu item key: %t";
                            VerticalAdminLogger.errorAdmin(message, nfe );
                        }
                        String categoryName = admin.getCategoryName( categoryKey );
                        tempElement.setAttribute( "valuename", categoryName );
                    }
                }
            }
        }

        // border params
        Element borderParams = XMLTool.createElement( doc, contentObjectData, "borderparams" );
        if ( isArrayFormItem( formItems, "borderparam_name" ) )
        {
            String[] borderParameters = (String[]) formItems.get( "borderparam_name" );
            String[] borderParameterValues = (String[]) formItems.get( "borderparam_value" );
            String[] borderParameterTypes = (String[]) formItems.get( "borderparam_type" );

            for ( int i = 0; i < borderParameters.length; i++ )
            {
                String valueStr = borderParameterValues[i];
                if ( valueStr != null && valueStr.length() > 0 )
                {
                    tempElement = XMLTool.createElement( doc, borderParams, "borderparam", valueStr );
                    tempElement.setAttribute( "name", borderParameters[i] );
                    if ( borderParameterTypes[i] != null && borderParameterTypes[i].length() > 0 )
                    {
                        String type = borderParameterTypes[i];
                        tempElement.setAttribute( "type", type );

                        if ( "page".equals( type ) )
                        {
                            int menuItemKey = -1;
                            try
                            {
                                menuItemKey = Integer.parseInt( valueStr );
                            }
                            catch ( NumberFormatException nfe )
                            {
                                String message = "Failed to parse menu item key: %t";
                                VerticalAdminLogger.errorAdmin(message, nfe );
                            }
                            String menuItemName = admin.getMenuItemName( menuItemKey );
                            tempElement.setAttribute( "valuename", menuItemName );
                        }
                        else if ( "category".equals( type ) )
                        {
                            int categoryKey = -1;
                            try
                            {
                                categoryKey = Integer.parseInt( valueStr );
                            }
                            catch ( NumberFormatException nfe )
                            {
                                String message = "Failed to parse menu item key: %t";
                                VerticalAdminLogger.errorAdmin(message, nfe );
                            }
                            String categoryName = admin.getCategoryName( categoryKey );
                            tempElement.setAttribute( "valuename", categoryName );
                        }
                    }
                }
            }
        }
        else if ( formItems.containsKey( "borderparam_name" ) )
        {
            String valueStr = formItems.getString( "borderparam_value", null );
            if ( valueStr != null && valueStr.length() > 0 )
            {
                tempElement = XMLTool.createElement( doc, borderParams, "borderparam", valueStr );
                tempElement.setAttribute( "name", formItems.getString( "borderparam_name" ) );
                String borderParameterType = formItems.getString( "borderparam_type", null );
                if ( borderParameterType != null )
                {
                    String type = borderParameterType;
                    tempElement.setAttribute( "type", type );

                    if ( "page".equals( type ) )
                    {
                        int menuItemKey = -1;
                        try
                        {
                            menuItemKey = Integer.parseInt( valueStr );
                        }
                        catch ( NumberFormatException nfe )
                        {
                            String message = "Failed to parse menu item key: %t";
                            VerticalAdminLogger.errorAdmin(message, nfe );
                        }
                        String menuItemName = "null";
                        menuItemName = admin.getMenuItemName( menuItemKey );
                        tempElement.setAttribute( "valuename", menuItemName );
                    }
                    else if ( "category".equals( type ) )
                    {
                        int categoryKey = -1;
                        try
                        {
                            categoryKey = Integer.parseInt( valueStr );
                        }
                        catch ( NumberFormatException nfe )
                        {
                            String message = "Failed to parse menu item key: %t";
                            VerticalAdminLogger.errorAdmin(message, nfe );
                        }
                        String categoryName = admin.getCategoryName( categoryKey );
                        tempElement.setAttribute( "valuename", categoryName );
                    }
                }
            }
        }

        return doc;
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        int menuKey = formItems.getInt( "menukey" );
        User user = securityService.getLoggedInAdminConsoleUser();
        String subop = formItems.getString( "subop", null );

        Document doc = admin.getContentObjectsByMenu( menuKey ).getAsDOMDocument();

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        addCommonParameters( admin, user, request, parameters, -1, menuKey );
        parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
        parameters.put( "menukey", String.valueOf( menuKey ) );
        parameters.put( "datetoday", DateUtil.formatISODateTime( new Date() ) );
        parameters.put( "subop", subop );
        parameters.put( "fieldname", formItems.getString( "fieldname", null ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", null ) );

        addSortParamteres( "name", "ascending", formItems, session, parameters );

        transformXML( request, response, doc, "contentobject_browse.xsl", parameters );
    }

    public void handlerCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        Document doc = admin.getContentObject( Integer.parseInt( request.getParameter( "key" ) ) ).getAsDOMDocument();

        Element nameElem = (Element) XMLTool.selectNode( doc, "/contentobjects/contentobject/name" );
        Node[] nameArray = XMLTool.filterNodes( nameElem.getChildNodes(), Node.TEXT_NODE );

        Text name = (Text) nameArray[0];
        AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
        Map translationMap = languageMap.getTranslationMap( user.getSelectedLanguageCode() );
        name.setData( name.getData() + " (" + translationMap.get( "%txtCopy%" ) + ")" );

        Element coElem = (Element) XMLTool.selectNode( doc, "/contentobjects/contentobject" );
        coElem.removeAttribute( "key" );

        Element docRoot = doc.getDocumentElement();
        doc.replaceChild( coElem, docRoot );

        key = admin.createContentObject( user, XMLTool.documentToString( doc ) );

        redirectClientToReferer( request, response );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document coDoc = buildContentObjectXML( admin, formItems, true, false );
        admin.createContentObject( user, XMLTool.documentToString( coDoc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "subop", formItems.getString( "subop", null ) );
        queryParams.put( "fieldname", formItems.getString( "fieldname", null ) );
        queryParams.put( "fieldrow", formItems.getString( "fieldrow", null ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        int menuKey = formItems.getInt( "menukey", -1 );

        if ( operation.equals( "datasourcepreview" ) )
        {
            if ( formItems.containsKey( "datasources" ) )
            {
                User user = securityService.getLoggedInAdminConsoleUser();

                Document doc;
                Document documentAsW3cDocument = XMLTool.createDocument( "document" );
                Element rootElem = documentAsW3cDocument.getDocumentElement();
                if ( verticalProperties.isStoreXHTMLOn() )
                {
                    XMLTool.createXHTMLNodes( documentAsW3cDocument, rootElem, formItems.getString( "document", "" ), true );
                }
                else
                {
                    XMLTool.createCDATASection( documentAsW3cDocument, rootElem, formItems.getString( "document", "" ) );
                }
                final SiteKey siteKey = new SiteKey( menuKey );
                String xmlData;
                XMLDocument dataSourcesXml = XMLDocumentFactory.create( formItems.getString( "datasources" ) );
                String docType = formItems.getString( "documenttype" );
                boolean pageTemplateDataource = "pagetemplate".equals( docType );
                try
                {
                    if ( pageTemplateDataource )
                    {
                        xmlData = doGetPageTemplateDatasourceResult( user, siteKey, dataSourcesXml );
                    }
                    else
                    {
                        XMLDocument portletDocumentAsXmlDocument = XMLDocumentFactory.create( documentAsW3cDocument );
                        xmlData = doGetPortletDatasourceResult( user, siteKey, dataSourcesXml, portletDocumentAsXmlDocument );
                    }
                }
                catch ( final Exception e )
                {
                    final String resultRootName = verticalProperties.getDatasourceDefaultResultRootElement();
                    final XMLDocument failedDatasourceDoc =
                        XMLDocumentFactory.create( DataSourceFailedXmlCreator.buildExceptionDocument( resultRootName, e ) );
                    xmlData = failedDatasourceDoc.getAsString();
                }

                if ( xmlData != null )
                {
                    doc = XMLTool.domparse( xmlData );
                }
                else
                {
                    doc = XMLTool.createDocument( "datasources" );
                }

                spoolDocument( response, doc );
            }
        }
        else if ( operation.equals( "updatestylesheet" ) )
        {

            User user = securityService.getLoggedInAdminConsoleUser();
            String key = formItems.getString( "key", null );
            boolean createContentObject = ( key == null || key.length() == 0 );
            Document coDoc = buildContentObjectXML( admin, formItems, createContentObject, true );

            NodeList coNodes = coDoc.getElementsByTagName( "contentobject" );
            Element contentobject = (Element) coNodes.item( 0 );

            ResourceKey borderStyleSheetKey = ResourceKey.parse( formItems.getString( "borderstylesheet", null ) );
            if ( borderStyleSheetKey != null )
            {
                addStyleSheet( contentobject, "borderstylesheet_xsl", borderStyleSheetKey );
            }

            ResourceKey objectStyleSheetKey = ResourceKey.parse( formItems.getString( "stylesheet", null ) );
            if ( objectStyleSheetKey != null )
            {
                addStyleSheet( contentobject, "objectstylesheet_xsl", objectStyleSheetKey );
            }

            String queryParam = "";
            NodeList nodeList = contentobject.getElementsByTagName( "datasources" );
            if ( nodeList.getLength() > 0 )
            {
                Document doc = XMLTool.createDocument();
                doc.appendChild( doc.importNode( nodeList.item( 0 ), true ) );
                queryParam = XMLTool.documentToString( doc );
                queryParam = StringUtil.formatXML( queryParam, 2 );
            }

            if ( key != null )
            {
                int cobKey = Integer.parseInt( key );

                // Add pages that uses this content object
                Document menuItemsDoc = admin.getMenuItemsByContentObject( user, cobKey ).getAsDOMDocument();
                contentobject.appendChild( coDoc.importNode( menuItemsDoc.getDocumentElement(), true ) );

                // Add frameworks that uses this content object
                Document pageTemplatesDoc = admin.getPageTemplatesByContentObject( cobKey ).getAsDOMDocument();
                contentobject.appendChild( coDoc.importNode( pageTemplatesDoc.getDocumentElement(), true ) );
            }

            ExtendedMap parameters = new ExtendedMap();
            addCommonParameters( admin, user, request, parameters, -1, menuKey );

            if ( createContentObject )
            {
                parameters.put( "create", "1" );
            }
            else
            {
                parameters.put( "create", "0" );
            }

            if ( queryParam != null && queryParam.length() > 0 )
            {
                parameters.put( "queryparam", queryParam );
            }
            else
            {
                parameters.put( "queryparam", "<?xml version=\"1.0\" ?><datasources/>" );
            }

            parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            parameters.put( "referer", formItems.get( "referer" ) );

            parameters.put( "subop", formItems.getString( "subop", "" ) );
            parameters.put( "menukey", formItems.getString( "menukey" ) );
            parameters.put( "rememberselectedtab", formItems.getString( "rememberselectedtab", "" ) );
            parameters.put( "script", formItems.getString( "script", "" ) );

            parameters.put( "fieldname", formItems.getString( "fieldname", null ) );
            parameters.put( "fieldrow", formItems.getString( "fieldrow", null ) );

            UserEntity defaultRunAsUser = siteDao.findByKey( menuKey ).resolveDefaultRunAsUser();
            String defaultRunAsUserName = "NA";
            if ( defaultRunAsUser != null )
            {
                defaultRunAsUserName = defaultRunAsUser.getDisplayName();
            }
            parameters.put( "defaultRunAsUser", defaultRunAsUserName );

            transformXML( request, response, coDoc, "contentobject_form.xsl", parameters );
        }
    }

    private String doGetPageTemplateDatasourceResult( final User user, final SiteKey siteKey, final XMLDocument dataSourcesXML )
    {

        ResourceKey[] cssKeys = null;

        DataSourceProcessor[] dsProcessors = new DataSourceProcessor[]{new NonDoingDataSourceProcessor()};
        SitePath sitePath = new SitePath( siteKey, "" );
        RequestParameters requestParameters = new RequestParameters();
        SiteEntity site = siteDao.findByKey( siteKey );
        UserEntity userEntity = userDao.findByKey( user.getKey() );

        DatasourceExecutorContext datasourceExecutorContext = new DatasourceExecutorContext();
        datasourceExecutorContext.setCssKeys( cssKeys );
        datasourceExecutorContext.setDatasourcesType( DatasourcesType.PAGETEMPLATE );
        datasourceExecutorContext.setInvocationCache( new InvocationCache() );
        datasourceExecutorContext.setHttpRequest( null );
        datasourceExecutorContext.setLanguage( site.getLanguage() );
        datasourceExecutorContext.setOriginalSitePath( sitePath );
        datasourceExecutorContext.setPageRequestType( PageRequestType.MENUITEM );
        datasourceExecutorContext.setPreviewContext( PreviewContext.NO_PREVIEW );
        datasourceExecutorContext.setProcessors( dsProcessors );
        datasourceExecutorContext.setSite( site );
        datasourceExecutorContext.setRequestParameters( requestParameters );
        datasourceExecutorContext.setPortalInstanceKey( null );
        datasourceExecutorContext.setUser( userEntity );
        datasourceExecutorContext.setDefaultResultRootElementName( verticalProperties.getDatasourceDefaultResultRootElement() );
        datasourceExecutorContext.setDataSourceService( this.dataSourceService );
        datasourceExecutorContext.setPluginManager( this.pluginManager );

        DatasourceExecutor datasourceExecutor = datasourceExecutorFactory.createDatasourceExecutor( datasourceExecutorContext );

        Datasources datasources = new Datasources( DatasourcesType.PAGETEMPLATE, dataSourcesXML.getAsJDOMDocument().getRootElement() );

        DataSourceResult dsr2 = datasourceExecutor.getDataSourceResult( datasources );

        return dsr2.getData().getAsString();
    }

    private String doGetPortletDatasourceResult( final User oldUser, final SiteKey siteKey, final XMLDocument dataSourcesXML,
                                                 final XMLDocument portletDocumentXmlDocument )
    {

        DataSourceProcessor[] dsProcessors = new DataSourceProcessor[]{new NonDoingDataSourceProcessor()};
        SitePath sitePath = new SitePath( siteKey, "" );
        RequestParameters requestParameters = new RequestParameters();

        ResourceKey[] cssKeys = null;

        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        UserEntity userEntity = userDao.findByKey( oldUser.getKey() );

        DatasourceExecutorContext datasourceExecutorContext = new DatasourceExecutorContext();
        if ( portletDocumentXmlDocument != null )
        {
            datasourceExecutorContext.setPortletDocument( portletDocumentXmlDocument.getAsJDOMDocument() );
        }
        datasourceExecutorContext.setCssKeys( cssKeys );
        datasourceExecutorContext.setDatasourcesType( DatasourcesType.PORTLET );
        datasourceExecutorContext.setInvocationCache( new InvocationCache() );
        datasourceExecutorContext.setHttpRequest( null );
        datasourceExecutorContext.setLanguage( site.getLanguage() );
        datasourceExecutorContext.setOriginalSitePath( sitePath );
        datasourceExecutorContext.setPageRequestType( PageRequestType.MENUITEM );
        datasourceExecutorContext.setProcessors( dsProcessors );
        datasourceExecutorContext.setPreviewContext( PreviewContext.NO_PREVIEW );
        datasourceExecutorContext.setSite( site );
        datasourceExecutorContext.setRequestParameters( requestParameters );
        datasourceExecutorContext.setPortalInstanceKey( null );
        datasourceExecutorContext.setUser( userEntity );
        datasourceExecutorContext.setDefaultResultRootElementName( verticalProperties.getDatasourceDefaultResultRootElement() );
        datasourceExecutorContext.setDataSourceService( this.dataSourceService );
        datasourceExecutorContext.setPluginManager( this.pluginManager );

        DatasourceExecutor datasourceExecutor = datasourceExecutorFactory.createDatasourceExecutor( datasourceExecutorContext );

        DatasourcesType datasourcesType = DatasourcesType.PORTLET;

        Datasources datasources = new Datasources( datasourcesType, dataSourcesXML.getAsJDOMDocument().getRootElement() );

        DataSourceResult dsr2 = datasourceExecutor.getDataSourceResult( datasources );

        return dsr2.getData().getAsString();
    }

    private void addStyleSheet( Element contentobjectElem, String elemName, ResourceKey styleSheetKey )
        throws VerticalAdminException
    {

        ResourceFile resource = resourceService.getResourceFile( styleSheetKey );
        if ( resource == null )
        {
            throw new StylesheetNotFoundException( styleSheetKey );
        }
        Document styleSheetDoc;
        try
        {
            styleSheetDoc = resource.getDataAsXml().getAsDOMDocument();
        }
        catch ( XMLException e )
        {
            throw new InvalidStylesheetException( styleSheetKey, e );
        }

        Element styleSheetRoot = styleSheetDoc.getDocumentElement();
        String attr = styleSheetRoot.getAttribute( "xmlns:xsl" );
        styleSheetRoot.removeAttribute( "xmlns:xsl" );
        styleSheetRoot.setAttributeNS( "http://www.w3.org/2000/xmlns/", "xmlns:xsl", attr );
        Document doc = contentobjectElem.getOwnerDocument();
        Element elem = XMLTool.createElement( doc, contentobjectElem, elemName );
        elem.appendChild( doc.importNode( styleSheetRoot, true ) );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        boolean createContentObject = false;
        Document xmlData = null;
        Document doc;
        String queryParam = "";
        String script = "";

        int menuKey = formItems.getInt( "menukey" );

        if ( request.getParameter( "key" ) == null || request.getParameter( "key" ).equals( "" ) )
        {
            // Blank form, make dummy document
            doc = XMLTool.createDocument( "contentobjects" );
            createContentObject = true;
        }
        else
        {
            int cobKey = Integer.parseInt( request.getParameter( "key" ) );

            xmlData = admin.getContentObject( cobKey ).getAsDOMDocument();
            doc = xmlData;

            NodeList coNodes = doc.getElementsByTagName( "contentobject" );
            Element contentobject = (Element) coNodes.item( 0 );
            String contentKeyStr = contentobject.getAttribute( "contentkey" );
            if ( contentKeyStr != null && contentKeyStr.length() > 0 )
            {
                int contentKey = Integer.parseInt( contentKeyStr );

                Document contentDoc = admin.getContent( user, contentKey, 0, 0, 0 ).getAsDOMDocument();
                NodeList contentNodes = contentDoc.getElementsByTagName( "content" );
                Element content = (Element) contentNodes.item( 0 );
                content = (Element) doc.importNode( content, true );
                contentobject.appendChild( content );
            }

            Document menuItemsDoc = admin.getMenuItemsByContentObject( user, cobKey ).getAsDOMDocument();
            contentobject.appendChild( doc.importNode( menuItemsDoc.getDocumentElement(), true ) );

            // Henter ut pagetemplates/frameworks som bruker dette contentobject
            Document pageTemplatesDoc = admin.getPageTemplatesByContentObject( cobKey ).getAsDOMDocument();
            contentobject.appendChild( doc.importNode( pageTemplatesDoc.getDocumentElement(), true ) );

            Element objectstylesheetElem = XMLTool.getElement( contentobject, "objectstylesheet" );
            ResourceKey objectStyleSheetKey = new ResourceKey( objectstylesheetElem.getAttribute( "key" ) );

            ResourceFile res = resourceService.getResourceFile( objectStyleSheetKey );
            objectstylesheetElem.setAttribute( "exist", res == null ? "false" : "true" );

            if ( res != null )
            {
                try
                {
                    Document styleSheetDoc = res.getDataAsXml().getAsDOMDocument();
                    objectstylesheetElem.setAttribute( "valid", "true" );

                    Element styleSheetRoot = styleSheetDoc.getDocumentElement();
                    String attr = styleSheetRoot.getAttribute( "xmlns:xsl" );
                    styleSheetRoot.removeAttribute( "xmlns:xsl" );
                    styleSheetRoot.setAttributeNS( "http://www.w3.org/2000/xmlns/", "xmlns:xsl", attr );
                    Element elem = XMLTool.createElement( doc, contentobject, "objectstylesheet_xsl" );
                    elem.appendChild( doc.importNode( styleSheetRoot, true ) );
                }
                catch ( XMLException e )
                {
                    objectstylesheetElem.setAttribute( "valid", "false" );
                }
            }

            Element borderstylesheetElem = XMLTool.getElement( contentobject, "borderstylesheet" );
            if ( borderstylesheetElem != null )
            {
                ResourceKey borderStyleSheetKey = ResourceKey.parse( borderstylesheetElem.getAttribute( "key" ) );
                if ( borderStyleSheetKey != null )
                {
                    res = resourceService.getResourceFile( borderStyleSheetKey );
                    borderstylesheetElem.setAttribute( "exist", res == null ? "false" : "true" );
                    if ( res != null )
                    {
                        try
                        {
                            Document borderStyleSheetDoc = res.getDataAsXml().getAsDOMDocument();
                            borderstylesheetElem.setAttribute( "valid", "true" );

                            Element styleSheetRoot = borderStyleSheetDoc.getDocumentElement();
                            String attr = styleSheetRoot.getAttribute( "xmlns:xsl" );
                            styleSheetRoot.removeAttribute( "xmlns:xsl" );
                            styleSheetRoot.setAttributeNS( "http://www.w3.org/2000/xmlns/", "xmlns:xsl", attr );
                            Element elem = XMLTool.createElement( doc, contentobject, "borderstylesheet_xsl" );
                            elem.appendChild( doc.importNode( styleSheetRoot, true ) );
                        }
                        catch ( XMLException e )
                        {
                            borderstylesheetElem.setAttribute( "valid", "false" );
                        }
                    }
                }
            }
        }

        if ( xmlData != null )
        {
            Map subelems = XMLTool.filterElements( doc.getDocumentElement().getChildNodes() );
            if ( subelems.get( "contentobject" ) != null )
            {
                Element tempElem = (Element) subelems.get( "contentobject" );
                Map contentobjectmap = XMLTool.filterElements( tempElem.getChildNodes() );

                Element contentobjectdata = (Element) contentobjectmap.get( "contentobjectdata" );
                Map queryparammap = XMLTool.filterElements( contentobjectdata.getChildNodes() );
                tempElem = (Element) queryparammap.get( "datasources" );

                Document queryParamDoc = XMLTool.createDocument();
                tempElem = (Element) queryParamDoc.importNode( tempElem, true );
                queryParamDoc.appendChild( tempElem );

                StringWriter sw = new StringWriter();
                queryParamDoc.normalize();
                XMLTool.printDocument( sw, queryParamDoc, 0 );
                queryParam = sw.toString();

                // Find the script data
                Element scriptElem = (Element) queryparammap.get( "script" );
                if ( scriptElem != null )
                {
                    script = XMLTool.getElementText( scriptElem );
                }

                Element[] paramElems = XMLTool.getElements( contentobjectdata, "stylesheetparam" );
                for ( Element paramElem1 : paramElems )
                {
                    String type = paramElem1.getAttribute( "type" );
                    if ( "page".equals( type ) )
                    {
                        String menuItemKeyStr = XMLTool.getElementText( paramElem1 );
                        int menuItemKey = Integer.parseInt( menuItemKeyStr );
                        String name = admin.getMenuItemName( menuItemKey );
                        paramElem1.setAttribute( "valuename", name );
                    }
                    else if ( "category".equals( type ) )
                    {
                        String categoryKeyStr = XMLTool.getElementText( paramElem1 );
                        int categoryKey = Integer.parseInt( categoryKeyStr );
                        String name = admin.getMenuItemName( categoryKey );
                        paramElem1.setAttribute( "valuename", name );
                    }
                }

                paramElems = XMLTool.getElements( contentobjectdata, "borderparam" );
                for ( Element paramElem : paramElems )
                {
                    String type = paramElem.getAttribute( "type" );
                    if ( "page".equals( type ) )
                    {
                        String menuItemKeyStr = XMLTool.getElementText( paramElem );
                        int menuItemKey = Integer.parseInt( menuItemKeyStr );
                        String name = admin.getMenuItemName( menuItemKey );
                        paramElem.setAttribute( "valuename", name );
                    }
                    else if ( "category".equals( type ) )
                    {
                        String categoryKeyStr = XMLTool.getElementText( paramElem );
                        int categoryKey = Integer.parseInt( categoryKeyStr );
                        String name = admin.getMenuItemName( categoryKey );
                        paramElem.setAttribute( "valuename", name );
                    }
                }
            }
        }

        ExtendedMap parameters = new ExtendedMap();

        // Get default css if present
        ResourceKey defaultCSS = admin.getDefaultCSSByMenu( menuKey );
        if ( defaultCSS != null )
        {
            parameters.put( "defaultcsskey", defaultCSS.toString() );
            parameters.put( "defaultcsskeyExist", resourceService.getResourceFile( defaultCSS ) == null ? "false" : "true" );
        }

        addCommonParameters( admin, user, request, parameters, -1, menuKey );

        if ( createContentObject )
        {
            parameters.put( "create", "1" );
            parameters.put( "queryparam", "<?xml version=\"1.0\" ?>\n<datasources/>" );
        }
        else
        {
            parameters.put( "create", "0" );
            queryParam = StringUtil.formatXML( queryParam, 2 );
            parameters.put( "queryparam", queryParam );
        }

        parameters.put( "menukey", String.valueOf( menuKey ) );

        parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );

        String subop = formItems.getString( "subop", "" );
        if ( "popup".equals( subop ) )
        {
            URL redirectURL = new URL( "adminpage" );
            redirectURL.setParameter( "op", "callback" );
            redirectURL.setParameter( "callback", formItems.getString( "callback" ) );
            redirectURL.setParameter( "page", 991 );
            redirectURL.setParameter( "key", Integer.parseInt( request.getParameter( "key" ) ) );
            redirectURL.setParameter( "fieldname", formItems.getString( "fieldname" ) );
            redirectURL.setParameter( "fieldrow", formItems.getString( "fieldrow" ) );
            parameters.put( "referer", redirectURL.toString() );
            parameters.put( "subop", "popup" );
        }
        else
        {
            parameters.put( "referer", request.getHeader( "referer" ) );
        }

        parameters.put( "subop", subop );
        parameters.put( "fieldname", formItems.getString( "fieldname", null ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", null ) );
        parameters.put( "script", script );

        addAccessLevelParameters( user, parameters );

        UserEntity defaultRunAsUser = siteDao.findByKey( menuKey ).resolveDefaultRunAsUser();
        String defaultRunAsUserName = "NA";
        if ( defaultRunAsUser != null )
        {
            defaultRunAsUserName = defaultRunAsUser.getDisplayName();
        }
        parameters.put( "defaultRunAsUser", defaultRunAsUserName );

        transformXML( request, response, doc, "contentobject_form.xsl", parameters );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalRemoveException, VerticalSecurityException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        admin.removeContentObject( key );
        redirectClientToReferer( request, response );
    }

    public boolean handlerSelect( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        int menuKey = formItems.getInt( "menukey" );
        User user = securityService.getLoggedInAdminConsoleUser();

        Document doc = admin.getContentObjectsByMenu( menuKey ).getAsDOMDocument();

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        addCommonParameters( admin, user, request, parameters, -1, menuKey );
        parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
        parameters.put( "menukey", String.valueOf( menuKey ) );
        parameters.put( "datetoday", DateUtil.formatISODateTime( new Date() ) );

        //addSortParamteres("name", "ascending", formItems, session, parameters);

        String returnKey = request.getParameter( "returnkey" );
        String returnView = request.getParameter( "returnview" );
        int returnRow;
        String tmp = request.getParameter( "returnrow" );
        if ( tmp != null && tmp.length() > 0 )
        {
            returnRow = Integer.parseInt( tmp );
        }
        else
        {
            returnRow = -1;
        }

        parameters.put( "returnview", returnView );
        parameters.put( "returnkey", returnKey );
        if ( returnRow != -1 )
        {
            parameters.put( "returnrow", String.valueOf( returnRow ) );
        }

        if ( request.getParameter( "objectdoc" ) != null && request.getParameter( "objectdoc" ).length() > 0 )
        {
            parameters.put( "objectdoc", request.getParameter( "objectdoc" ) );
        }

        transformXML( request, response, doc, "contentobject_selector.xsl", parameters );

        return true;
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document coDoc = buildContentObjectXML( admin, formItems, false, false );
        admin.updateContentObject( XMLTool.documentToString( coDoc ) );

        String subop = formItems.getString( "subop", "" );
        if ( "popup".equals( subop ) )
        {
            String referer = formItems.getString( "referer" ) + "&title=" + formItems.getString( "name" );
            redirectClientToAdminPath( referer, request, response );
        }
        else
        {
            int menuKey = formItems.getInt( "menukey", -1 );
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "subop", subop );
            queryParams.put( "op", "browse" );
            queryParams.put( "menukey", menuKey );
            queryParams.put( "fieldname", formItems.getString( "fieldname", null ) );
            queryParams.put( "fieldrow", formItems.getString( "fieldrow", null ) );
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }


    private void spoolDocument( HttpServletResponse res, Document doc )
        throws VerticalAdminException
    {
        res.setContentType( "text/xml; charset=UTF-8" );
        OutputStream out = null;

        try
        {
            out = res.getOutputStream();
            XMLTool.printDocument( out, doc, 4 );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
        finally
        {
            if ( out != null )
            {
                try
                {
                    out.close();
                }
                catch ( IOException e )
                {
                    VerticalAdminLogger.errorAdmin("I/O error: %t", e );
                }
            }
        }
    }
}
