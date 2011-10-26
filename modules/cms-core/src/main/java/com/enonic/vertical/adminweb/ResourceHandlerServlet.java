/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.Base64Util;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.util.UrlPathDecoder;
import com.enonic.cms.framework.util.UrlPathEncoder;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.resource.ResourceBase;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceFolder;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceXmlCreator;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.business.DeploymentPathResolver;

public class ResourceHandlerServlet
        extends AdminHandlerBaseServlet
{

    private final static Logger LOG = LoggerFactory.getLogger( ResourceHandlerServlet.class );

    private Document filterResources( Document resourceDoc, String searchText )
    {

        if ( searchText == null || searchText.length() == 0 )
        {
            return resourceDoc;
        }

        Element root = resourceDoc.getDocumentElement();
        Element[] children = XMLTool.selectElements( root, "//resource" );

        for ( Element child : children )
        {
            String name = child.getAttribute( "name" );
            if ( !name.toLowerCase().contains( searchText.toLowerCase() ) )
            {
                child.getParentNode().removeChild( child );
            }
        }

        return resourceDoc;
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                               AdminService admin, ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
            throws VerticalAdminException, TransformerException, IOException
    {

        String path = formItems.getString( "path", "/" );
        String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
        String resourcePickerPathBase64Encoded = Base64Util.encode( path.getBytes( "UTF-8" ) );
        String resourcePickerPathBase64EncodedAndUrlEncoded = UrlPathEncoder.encode( resourcePickerPathBase64Encoded );
        CookieUtil.setCookie( response, "resourcePickerPath", resourcePickerPathBase64EncodedAndUrlEncoded,
                              ContentBaseHandlerServlet.COOKIE_TIMEOUT, deploymentPath );

        Document doc;
        if ( request.getParameter( "searchtext" ) != null )
        {
            String searchText = formItems.getString( "searchtext", "" );
            doc = admin.getResourceTreeXml( path, true, true, -1, true, true ).getAsDOMDocument();
            doc = filterResources( doc, searchText );
            parameters.put( "searchtext", searchText );
            parameters.put( "search", "true" );
        }
        else
        {
            doc = admin.getResourceTreeXml( path, true, true, 1, false, true ).getAsDOMDocument();
        }

        addAccessLevelParameters( user, parameters );

        parameters.put( "mimetype", formItems.get( "mimetype", "" ) );
        parameters.put( "extension", formItems.get( "extension", "" ) );
        parameters.put( "fieldname", formItems.get( "fieldname" ) );
        parameters.put( "path", doc.getDocumentElement().getAttribute( "root" ) );
        parameters.put( "sortby", formItems.get( "sortby", "@name" ) );
        parameters.put( "sortby-direction", formItems.get( "sortby-direction", "ascending" ) );
        parameters.put( "reload", formItems.get( "reload", false ) );
        parameters.put( "move", formItems.get( "move", false ) );

        transformXML( request, response, doc, "resource_browse.xsl", parameters );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                               AdminService admin, ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
            throws VerticalAdminException, VerticalEngineException
    {

        if ( "popup".equals( operation ) )
        {
            handlerPopup( request, response, session, admin, formItems, parameters, user, verticalDoc );
        }
        else if ( "inuseby".equals( operation ) )
        {
            handlerInUseBy( request, response, session, admin, formItems, parameters, user, verticalDoc );
        }
        else if ( "moveFile".equals( operation ) )
        {
            handlerMove( request, response, session, admin, formItems, parameters, user, verticalDoc, false );
        }
        else if ( "moveFolder".equals( operation ) )
        {
            handlerMove( request, response, session, admin, formItems, parameters, user, verticalDoc, true );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
        }
    }

    private void handlerPopup( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                               AdminService admin, ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
            throws VerticalAdminException
    {

        parameters.put( "mimetype", formItems.get( "mimetype", "" ) );
        parameters.put( "extension", formItems.get( "extension", "" ) );
        parameters.put( "fieldname", formItems.get( "fieldname" ) );
        parameters.put( "user-agent", request.getHeader( "user-agent" ) );

        transformXML( request, response, verticalDoc, "resource_selector_frameset.xsl", parameters );
    }

    public void handlerMenu( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
            throws VerticalAdminException, TransformerException, IOException
    {

        //get resource tree xml
        ResourceFolder root = resourceService.getResourceRoot();
        ResourceXmlCreator xmlCreator = new ResourceXmlCreator();
        xmlCreator.setIncludeFullPath( true );
        xmlCreator.setListFolders( true );
        xmlCreator.setListResources( false );
        XMLDocument resourcesDoc = xmlCreator.createResourceTreeXml( root );

        Cookie cookie = CookieUtil.getCookie( request, "resourcePickerPath" );
        if ( cookie != null )
        {
            try
            {
                String resourcePickerPathBase64AndUrlEncoded = cookie.getValue();
                String resourcePickerPathBase64Encoded = UrlPathDecoder.decode( resourcePickerPathBase64AndUrlEncoded );
                String resourcePickerPath = new String( Base64Util.decode( resourcePickerPathBase64Encoded ), "UTF-8" );
                if ( resourcePickerPath != null )
                {
                    parameters.put( "path", resourcePickerPath );
                }
            }
            catch ( IllegalArgumentException e )
            {
                LOG.warn( "Value in Cookie 'resourcePickerPath' is not base64 encoded " );
            }

        }

        // add popup parameters
        parameters.put( "mimetype", formItems.get( "mimetype", "" ) );
        parameters.put( "extension", formItems.get( "extension", "" ) );
        parameters.put( "fieldname", formItems.get( "fieldname" ) );
        parameters.put( "subop", formItems.get( "subop", "" ) );
        parameters.put( "sourceKey", formItems.get( "sourceKey", "" ) );
        parameters.put( "destinationKey", formItems.get( "destinationKey", "" ) );

        transformXML( request, response, resourcesDoc.getAsDOMDocument(), "resource_selector_frame1.xsl", parameters );
    }

    public void handlerInUseBy( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                AdminService admin, ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
            throws VerticalAdminException
    {

        ResourceKey resourceKey = new ResourceKey( formItems.getString( "resourcekey" ) );
        ResourceXmlCreator xmlCreator = new ResourceXmlCreator();
        ResourceFile resourceFile = resourceService.getResourceFile( resourceKey );
        Document doc = null;
        if ( resourceFile != null )
        {
            xmlCreator.setUsedByMap( resourceService.getUsedBy( resourceFile.getResourceKey() ) );
            doc = xmlCreator.createResourceXml( resourceFile ).getAsDOMDocument();
        }
        transformXML( request, response, doc, "resource_inuseby.xsl", parameters );
    }

    public void handlerMove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc, boolean moveFolder )
    {

        ResourceKey sourceKey = new ResourceKey( formItems.getString( "sourceKey" ) );
        ResourceBase source = resourceService.getResource( sourceKey );
        if ( source == null )
        {
            throw new IllegalArgumentException( "Source (" + sourceKey + ") not found" );
        }

        ResourceKey destinationKey = new ResourceKey( formItems.getString( "destinationKey" ) );
        ResourceFolder destination = resourceService.getResourceFolder( destinationKey );
        if ( destination == null )
        {
            throw new IllegalArgumentException( "Destination (" + destinationKey + ") not found" );
        }

        ResourceKey newSourceKey = resourceService.moveResource( source, destination );

        if ( moveFolder )
        {
            URL url = new URL( request.getHeader( "referer" ) );
            MultiValueMap params = new MultiValueMap();
            params.putAll( url.getParameterMap() );
            if ( params.containsKey( "reload" ) )
            {
                params.remove( "reload" );
            }
            params.put( "reload", "true" );
            if ( params.containsKey( "path" ) )
            {
                params.remove( "path" );
            }
            params.put( "path", resolvePathForNewFolder( sourceKey, newSourceKey ) );
            redirectClientToAdminPath( "adminpage", params, request, response );
        }
        else
        {
            redirectClientToReferer( request, response );
        }
    }

    /*
     * Extract the moving folder name from source path and compute the full destination path.
     * Example:
     *      source:      "/libraries/resolvers"
     *      destination: "/sites/stuff"
     *  we move folder "resolvers"
     *      result: "/sites/stuff/resolvers"
     * @param sourceFolderPath source folder path (like "/libraries/resolvers")
     * @param destinationFolderPath path to destination folder (like "/sites/stuff")
     */
    protected String resolvePathForNewFolder( ResourceKey sourceFolderPath, ResourceKey destinationFolderPath )
    {
        Pattern pattern = Pattern.compile(".*/([^/]+)$");
        Matcher matcher = pattern.matcher( sourceFolderPath.toString() );

        String currentDir = "";

        // extract the moving folder name
        if ( matcher.matches() )
        {
            currentDir = matcher.group(1);
        }

        if ( StringUtils.isEmpty( currentDir ) )
        {
            return destinationFolderPath.toString();
        }

        // moving into root folder: / + moving folder
        if ( "/".equals( destinationFolderPath.toString() ) )
        {
            return "/" + currentDir;
        }

        // computing destination path: destination + / + moving folder
        return destinationFolderPath.toString() + "/" + currentDir;
    }
}

