/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.server.service.tools.DataSourceInfoResolver;

import com.enonic.cms.core.portal.cache.SiteCachesService;

import com.enonic.cms.core.security.user.User;

/**
 *
 */
public class SystemHandlerServlet
    extends AdminHandlerBaseServlet
{
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SiteCachesService siteCachesService;

    @Autowired
    private DataSourceInfoResolver datasourceInfoResolver;

    private Properties configurationProperties;

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalEngineException, VerticalAdminException
    {

        if ( "page".equals( operation ) )
        {
            handlerPage( request, response, session, admin, formItems, operation );
        }
        else if ( "cleanReadLogs".equals( operation ) )
        {
            handlerCleanReadLogs( admin, request, response );
        }
        else if ( "cleanUnusedContent".equals( operation ) )
        {
            handlerCleanUnusedContent( admin, request, response );
        }
        else if ( "clearcache".equals( operation ) )
        {
            clearCache( request, response, formItems );
        }
        else if ( "clearstatistics".equals( operation ) )
        {
            clearStatistics( request, response, formItems );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    public void handlerPage( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, String operation )
        throws VerticalEngineException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Source xslSource = AdminStore.getStylesheet( session, "system_page.xsl" );

        String mode = formItems.getString( "mode" );

        Document doc = XMLTool.createDocument( "vertical" );
        Element root = doc.getDocumentElement();

        try
        {
            if ( mode.equals( "system" ) )
            {
                root.appendChild( buildJavaInfo( doc ) );
                root.setAttribute( "version", Version.getVersion() );
                root.setAttribute( "modelVersion", String.valueOf( this.upgradeService.getCurrentModelNumber() ) );
                root.appendChild( buildComponentsInfo( doc ) );
            }
            else if ( mode.equals( "java_properties" ) )
            {
                XMLTool.mergeDocuments( doc, createPropertiesInfoDocument(), true );

            }
            else if ( mode.equals( "system_cache" ) )
            {
                doc = XMLTool.createDocument( "vertical" );

                XMLTool.mergeDocuments( doc, cacheManager.getInfoAsXml().getAsDOMDocument(), true );
            }

            Source xmlSource = new DOMSource( doc );

            // parameters
            ExtendedMap xslParams = new ExtendedMap();

            xslParams.put( "page", request.getParameter( "page" ) );
            xslParams.put( "selectedtabpage", request.getParameter( "selectedtabpage" ) );
            xslParams.put( "mode", mode );

            xslParams.put( "selectedoperation", request.getParameter( "selectedoperation" ) );
            xslParams.put( "selectedcachename", request.getParameter( "selectedcachename" ) );

            addAccessLevelParameters( user, xslParams );

            transformXML( session, response.getWriter(), xmlSource, xslSource, xslParams );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
    }

    private Document createPropertiesInfoDocument()
    {
        PropertiesInfoModelFactory propertiesInfoModelFactory =
            new PropertiesInfoModelFactory( datasourceInfoResolver, configurationProperties );
        PropertiesInfoModel infoModel = propertiesInfoModelFactory.createSystemPropertiesModel();

        final Document doc;
        try
        {
            doc = JDOMUtil.toW3CDocument( infoModel.toXML() );
        }
        catch ( JDOMException e )
        {
            throw new VerticalAdminException( "Failed to create system-properties document" );
        }

        return doc;
    }

    /**
     * Clean read logs.
     */
    private void handlerCleanReadLogs( AdminService admin, HttpServletRequest request, HttpServletResponse response )
    {
        admin.cleanReadLogs( securityService.getLoggedInAdminConsoleUser() );
        redirectClientToReferer( request, response );
    }

    /**
     * Clean unused content.
     */
    private void handlerCleanUnusedContent( AdminService admin, HttpServletRequest request, HttpServletResponse response )
    {
        admin.cleanUnusedContent( securityService.getLoggedInAdminConsoleUser() );
        redirectClientToReferer( request, response );
    }

    private Element buildJavaInfo( Document doc )
    {

        Element javaEl = doc.createElement( "java" );
        javaEl.setAttribute( "version", findJavaVersion() );

        Element memoryEl = XMLTool.createElement( javaEl, "memory" );
        Element heapEl = XMLTool.createElement( memoryEl, "heap" );

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        heapEl.setAttribute( "max", String.valueOf( heap.getMax() ) );
        heapEl.setAttribute( "committed", String.valueOf( heap.getCommitted() ) );
        heapEl.setAttribute( "used", String.valueOf( heap.getUsed() ) );

        MemoryUsage nonheap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        Element nonheapEl = XMLTool.createElement( memoryEl, "nonheap" );
        nonheapEl.setAttribute( "max", String.valueOf( nonheap.getMax() ) );
        nonheapEl.setAttribute( "committed", String.valueOf( nonheap.getCommitted() ) );
        nonheapEl.setAttribute( "used", String.valueOf( nonheap.getUsed() ) );

        return javaEl;
    }

    /**
     * Append component version informations.
     */
    private Element buildComponentsInfo( Document doc )
    {
        Element root = doc.createElement( "components" );
        root.appendChild( buildComponentInfo( doc, "Saxon", findSaxonVersion() ) );
        return root;
    }

    /**
     * Append component version information.
     */
    private Element buildComponentInfo( Document doc, String name, String version )
    {
        Element root = doc.createElement( "component" );
        root.setAttribute( "name", name );
        root.setAttribute( "version", version );
        return root;
    }

    /**
     * Find java version.
     */
    private String findJavaVersion()
    {
        return System.getProperty( "java.vm.version" );
    }

    /**
     * Find saxon version.
     */
    private String findSaxonVersion()
    {
        return net.sf.saxon.Version.getProductVersion();
    }

    private void clearCache( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        String cacheName = formItems.getString( "cacheName" );
        siteCachesService.clearCache( cacheName );

        URL referer = new URL( request.getHeader( "referer" ) );
        referer.setParameter( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        referer.setParameter( "selectedoperation", formItems.getString( "selectedoperation", formItems.getString( "op", "" ) ) );
        referer.setParameter( "selectedcachename", formItems.getString( "selectedcachename", formItems.getString( "cacheName", "" ) ) );
        referer.setParameter( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );

        redirectClientToURL( referer, response );
    }

    private void clearStatistics( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {
        String cacheName = formItems.getString( "cacheName" );
        siteCachesService.clearCacheStatistics( cacheName );

        URL referer = new URL( request.getHeader( "referer" ) );
        referer.setParameter( "selectedtabpage", formItems.getString( "selectedtabpage", "" ) );
        referer.setParameter( "selectedoperation", formItems.getString( "selectedoperation", formItems.getString( "op", "" ) ) );
        referer.setParameter( "selectedcachename", formItems.getString( "selectedcachename", formItems.getString( "cacheName", "" ) ) );
        redirectClientToURL( referer, response );
    }

    public void setCacheFacadeManager( CacheManager value )
    {
        this.cacheManager = value;
    }

    public void setSiteCachesService( SiteCachesService value )
    {
        this.siteCachesService = value;
    }

    public void setDatasourceInfoResolver( DataSourceInfoResolver datasourceInfoResolver )
    {
        this.datasourceInfoResolver = datasourceInfoResolver;
    }

    public void setConfigurationProperties( Properties configurationProperties )
    {
        this.configurationProperties = configurationProperties;
    }

}
