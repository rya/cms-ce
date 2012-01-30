/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLException;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.StoreNewCategoryCommand;
import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.stylesheet.StylesheetNotFoundException;
import com.enonic.cms.core.xslt.XsltProcessorHelper;

final public class CategoryHandlerServlet
    extends AdminHandlerBaseServlet
{

    private String buildCategoryXML( User user, ExtendedMap formItems, boolean createCategory )
    {
        Document doc = XMLTool.createDocument( "category" );
        Element categoryElem = doc.getDocumentElement();

        if ( !createCategory )
        {
            categoryElem.setAttribute( "key", formItems.getString( "key" ) );
        }

        if ( formItems.containsKey( "selectedunitkey" ) )
        {
            categoryElem.setAttribute( "unitkey", formItems.getString( "selectedunitkey" ) );
        }
        if ( formItems.containsKey( "contenttypekey" ) )
        {
            categoryElem.setAttribute( "contenttypekey", formItems.getString( "contenttypekey" ) );
        }
        if ( formItems.containsKey( "supercategorykey" ) )
        {
            categoryElem.setAttribute( "supercategorykey", formItems.getString( "supercategorykey" ) );
        }
        categoryElem.setAttribute( "name", formItems.getString( "name" ) );
        categoryElem.setAttribute( "autoApprove", formItems.getString( "autoApprove" ) );

        if ( !createCategory )
        {
            categoryElem.setAttribute( "created", formItems.getString( "created" ) );
            Element ownerElem = XMLTool.createElement( doc, categoryElem, "owner" );
            ownerElem.setAttribute( "key", formItems.getString( "ownerkey" ) );
        }
        else
        {
            Element ownerElem = XMLTool.createElement( doc, categoryElem, "owner" );
            ownerElem.setAttribute( "key", String.valueOf( user.getKey() ) );
        }
        if ( formItems.containsKey( "description" ) )
        {
            Element descriptionElem = XMLTool.createElement( doc, categoryElem, "description" );
            XMLTool.createCDATASection( doc, descriptionElem, formItems.getString( "description" ) );
        }
        Element modifierElem = XMLTool.createElement( doc, categoryElem, "modifier" );
        modifierElem.setAttribute( "key", String.valueOf( user.getKey() ) );

        return XMLTool.documentToString( doc );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        if ( formItems.containsKey( "updateaccessrights" ) )
        {
            StoreNewCategoryCommand command = createStoreNewCategoryCommand( user, formItems );
            categoryService.storeNewCategory( command );
        }
        else
        {
            StoreNewCategoryCommand command = createStoreNewCategoryCommand( user, formItems );
            categoryService.storeNewCategory( command );
        }

        if ( formItems.containsKey( "redirecturl" ) )
        {
            redirectClientToAbsoluteUrl( formItems.getString( "redirecturl" ), response );
        }
        else
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.getString( "modulepage" ) );
            if ( "600".equals( formItems.getString( "modulepage" ) ) )
            {
                queryParams.put( "op", "page" );
            }
            else
            {
                queryParams.put( "op", "browse" );
            }
            if ( formItems.containsKey( "cat" ) )
            {
                queryParams.put( "cat", formItems.getString( "cat" ) );
            }
            if ( formItems.containsKey( "selectedunitkey" ) )
            {
                queryParams.put( "selectedunitkey", formItems.getString( "selectedunitkey" ) );
            }
            queryParams.put( "reload", "true" );
            appendSelectorParameters( queryParams, formItems );

            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        int categoryKey;
        Document doc;

        if ( formItems.containsKey( "key" ) )
        {
            categoryKey = formItems.getInt( "key" );
            doc = admin.getCategory( user, categoryKey ).getAsDOMDocument();
            Element categoryElem = XMLTool.getElement( doc.getDocumentElement(), "category" );
            String tmpStr = categoryElem.getAttribute( "supercategorykey" );
            if ( tmpStr != null && tmpStr.length() > 0 )
            {
                int superCategoryKey = Integer.parseInt( tmpStr );
                Document tmpXMLData = admin.getCategory( user, superCategoryKey ).getAsDOMDocument();
                String superCategoryName = XMLTool.getElementText( tmpXMLData, "/categories/category/@name" );
                formItems.put( "supercategoryname", superCategoryName );
            }
            formItems.putBoolean( "create", false );
            Document categorynamesDoc = admin.getSuperCategoryNames( categoryKey, false, true ).getAsDOMDocument();
            XMLTool.mergeDocuments( doc, categorynamesDoc, true );
        }
        else
        {
            categoryKey = -1;
            doc = XMLTool.createDocument( "categories" );
            int superCategoryKey = formItems.getInt( "cat", -1 );
            if ( superCategoryKey != -1 )
            {
                formItems.putInt( "contenttypekey", admin.getContentTypeKeyByCategory( superCategoryKey ) );
            }
            Document docAccessRights = admin.getDefaultAccessRights( user, AccessRight.CATEGORY, superCategoryKey ).getAsDOMDocument();
            XMLTool.mergeDocuments( doc, docAccessRights, true );
            formItems.putBoolean( "create", true );
            Document categorynamesDoc = admin.getSuperCategoryNames( superCategoryKey, false, true ).getAsDOMDocument();
            XMLTool.mergeDocuments( doc, categorynamesDoc, true );
        }

        if ( formItems.containsKey( "selectedunitkey" ) )
        {
            int unitKey = formItems.getInt( "selectedunitkey" );
            final XMLDocument contentTypeXmlDoc = admin.getContentTypes( false );
            Document contentTypeDoc = contentTypeXmlDoc.getAsDOMDocument();
            XMLTool.mergeDocuments( doc, contentTypeDoc, true );
            XMLTool.mergeDocuments( doc, admin.getUnit( unitKey ).getAsDOMDocument(), false );
            addCommonParameters( admin, user, request, formItems, unitKey, -1 );
        }
        else
        {
            addCommonParameters( admin, user, request, formItems, -1, -1 );
        }
        int contentCount = admin.getContentCount( categoryKey, false );
        formItems.putBoolean( "nocontent", contentCount == 0 );

        // Parameters
        addAccessLevelParameters( user, formItems );
        addSelectorParameters( formItems, admin, formItems );

        transformXML( request, response, doc, "category_form.xsl", formItems );
    }

    public void handlerEmptyCategory( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                      ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User oldUser = securityService.getLoggedInAdminConsoleUser();
        UserEntity user = securityService.getUser( oldUser );
        CategoryKey categoryKey = new CategoryKey( formItems.getInt( "cat" ) );
        CategoryEntity category = categoryDao.findByKey( categoryKey );
        contentService.deleteByCategory( user, category );
        String referer = request.getHeader( "referer" );
        URL url = new URL( referer );
        url.setParameter( "feedback", 8 );
        url.setParameter( "index", 0 );
        redirectClientToURL( url, response );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        boolean hasSubCategories, hasContent;
        int superCategoryKey = -1;
        if ( !( hasSubCategories = admin.hasSubCategories( key ) ) & !( hasContent = admin.hasContent( key ) ) )
        {
            superCategoryKey = admin.getSuperCategoryKey( key );
            User user = securityService.getLoggedInAdminConsoleUser();

            DeleteCategoryCommand command = new DeleteCategoryCommand();
            command.setDeleter( user.getKey() );
            command.setCategoryKey( new CategoryKey( key ) );
            command.setIncludeContent( false );
            command.setRecursive( false );

            categoryService.deleteCategory( command );
        }

        if ( hasSubCategories )
        {
            String message = "Failed to delete category because it has sub-categories";
            ErrorPageServlet.Error error = new ErrorPageServlet.MessageError( message );
            session.setAttribute( "com.enonic.vertical.error", error );
            redirectClientToAdminPath( "errorpage", request, response );
        }
        else if ( hasContent )
        {
            String message = "Failed to delete category because it contains contents";
            ErrorPageServlet.Error error = new ErrorPageServlet.MessageError( message );
            session.setAttribute( "com.enonic.vertical.error", error );
            redirectClientToAdminPath( "errorpage", request, response );
        }
        else
        {
            if ( superCategoryKey >= 0 )
            {
                String referer = request.getHeader( "referer" );

                URL url = new URL( referer );
                url.setParameter( "categorykey", Integer.toString( superCategoryKey ) );
                int page;

                // Find the correct page/content type for parent category
                int contentTypeKey = admin.getContentTypeKeyByCategory( superCategoryKey );
                if ( contentTypeKey > 0 )
                {
                    page = contentTypeKey + 999;
                }
                else
                {
                    page = 991;
                }

                url.setParameter( "page", Integer.toString( page ) );

                if ( formItems.containsKey( "selectedunitkey" ) )
                {
                    url.setParameter( "selectedunitkey", formItems.getString( "selectedunitkey" ) );
                }

                if ( !url.hasParameter( "reload" ) )
                {
                    url.setParameter( "reload", "true" );
                }

                redirectClientToURL( url, response );
            }
            else
            {
                MultiValueMap queryParams = new MultiValueMap();
                queryParams.put( "page", "600" );
                queryParams.put( "op", "page" );
                if ( formItems.containsKey( "selectedunitkey" ) )
                {
                    queryParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
                }
                queryParams.put( "reload", true );

                redirectClientToAdminPath( "adminpage", queryParams, request, response );
            }
        }
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        String xmlData = buildCategoryXML( user, formItems, false );
        int categoryKey = formItems.getInt( "key" );

        // Oppdaterer kategorien med rettigheter bare hvis brukeren ikke har valgt å propagere
        if ( formItems.containsKey( "updateaccessrights" ) && !formItems.getString( "propagate", "" ).equals( "true" ) )
        {
            String accessRightsXML = buildAccessRightsXML( String.valueOf( categoryKey ), formItems, AccessRight.CATEGORY );
            Document doc = XMLTool.domparse( xmlData );
            XMLTool.mergeDocuments( doc, XMLTool.domparse( accessRightsXML ), true );
            admin.updateCategory( user, XMLTool.documentToString( doc ) );
        }
        else
        {
            admin.updateCategory( user, xmlData );
        }

        // Redirect to propagate page
        if ( "true".equals( formItems.getString( "propagate" ) ) )
        {
            handlerPropagateAccessRightsPage( request, response, session, admin, formItems );
        }
        // Redirect to category browsing
        else
        {

            redirectToBrowse( request, response, formItems );

        }
    }

    private void redirectToBrowse( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        MultiValueMap queryParams = new MultiValueMap();
        if ( formItems.containsKey( "contenttypekey" ) )
        {
            int contentTypekey = formItems.getInt( "contenttypekey" );
            queryParams.put( "page", String.valueOf( 999 + contentTypekey ) );
        }
        else
        {
            queryParams.put( "page", "991" );
        }
        queryParams.put( "op", "browse" );
        queryParams.put( "cat", formItems.getString( "cat" ) );
        if ( formItems.containsKey( "selectedunitkey" ) )
        {
            queryParams.put( "selectedunitkey", formItems.getString( "selectedunitkey" ) );
        }
        if ( formItems.containsKey( "contentarchive" ) )
        {
            queryParams.put( "contentarchive", formItems.getString( "contentarchive" ) );
        }
        queryParams.put( "reload", "true" );
        appendSelectorParameters( queryParams, formItems );

        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void appendSelectorParameters( MultiValueMap queryParams, ExtendedMap parameters )
    {

        queryParams.put( "subop", parameters.getString( "fieldname", "" ) );
        queryParams.put( "fieldname", parameters.getString( "fieldname", "" ) );
        queryParams.put( "fieldrow", parameters.getString( "fieldrow", "" ) );
        queryParams.put( "contenttypestring", parameters.getString( "contenttypestring", "" ) );

        if ( parameters.getString( "selector", "false" ).equals( "true" ) )
        {
            queryParams.put( "selector", "true" );
            queryParams.put( "selector_mode", parameters.getString( "selector_mode", "" ) );
            queryParams.put( "selector_row", parameters.getString( "selector_row", "" ) );
            queryParams.put( "selector_reloadcategories", parameters.getString( "selector_reloadcategories", "" ) );
            queryParams.put( "selector_contenttypekey", parameters.getString( "selector_contenttypekey", "" ) );
            queryParams.put( "selector_returnkey", parameters.getString( "selector_returnkey", "" ) );
            queryParams.put( "selector_returnview", parameters.getString( "selector_returnview", "" ) );
        }
        else
        {
            queryParams.put( "selector", "false" );
        }
    }

    public void handlerPropagateAccessRightsPage( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                                  AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        int unitKey = formItems.getInt( "selectedunitkey", -1 );
        int categoryKey = formItems.getInt( "key", -1 );

        Document doc = XMLTool.createDocument( "data" );

        Document categories = admin.getCategoryMenu( user, categoryKey, null, false ).getAsDOMDocument();
        // Don't seam to be in use (JAM 27.10.2008)
        // Document categoryNames = XMLTool.domparse(admin.getSuperCategoryNames(user, categoryKey, false, true));
        Document changedAccessRights = buildChangedAccessRightsXML( formItems );
        Document currentAccessRights = XMLTool.domparse( buildAccessRightsXML( formItems ) );
        XMLTool.mergeDocuments( doc, categories, true );
        // XMLTool.mergeDocuments(doc, categoryNames, true);
        XMLTool.mergeDocuments( doc, changedAccessRights, true );
        XMLTool.mergeDocuments( doc, currentAccessRights, true );

        // Parameters
        ExtendedMap parameters = new ExtendedMap();
        addCommonParameters( admin, user, request, parameters, unitKey, -1 );
        addAccessLevelParameters( user, parameters );
        parameters.putInt( "cat", categoryKey );
        parameters.put( "page", formItems.get( "page" ) );
        parameters.put( "contenttypekey", formItems.get( "contenttypekey", "" ) );
        parameters.putString( "categoryname", formItems.getString( "name", "" ) );

        transformXML( request, response, doc, "category_propagateaccessrights.xsl", parameters );
    }

    public void handlerPropagateAccessRights( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                              AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // Propagate
        String subop = formItems.getString( "subop", "" );
        if ( "propagate".equals( subop ) )
        {

            String includeContents = formItems.getString( "includecontents", "off" );
            String applyOnlyChanges = formItems.getString( "applyonlychanges", "off" );

            if ( "on".equals( applyOnlyChanges ) )
            {
                // ("applying only changes");
                // Prepare for apply only changes..
                Hashtable<String, ExtendedMap> removedCategoryAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> addedCategoryAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> modifiedCategoryAccessRights = new Hashtable<String, ExtendedMap>();

                Hashtable<String, ExtendedMap> removedContentAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> addedContentAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> modifiedContentAccessRights = new Hashtable<String, ExtendedMap>();

                for ( Iterator i = formItems.keySet().iterator(); i.hasNext(); )
                {
                    String paramName = (String) i.next();
                    if ( paramName.startsWith( "arc[key=" ) )
                    {
                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        String paramValue = formItems.getString( paramName );
                        ExtendedMap categoryAccessRight = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );
                        String diffinfo = categoryAccessRight.getString( "diffinfo" );
                        if ( "removed".equals( diffinfo ) )
                        {
                            removedCategoryAccessRights.put( paramsInName.getString( "key" ), categoryAccessRight );
                            removedContentAccessRights.put( paramsInName.getString( "key" ), categoryAccessRight );
                        }
                        else if ( "added".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            addedCategoryAccessRights.put( groupKey, categoryAccessRight );
                            // M� konvertere category parametere til content parametere
                            ExtendedMap contentAccessRight = buildContentARFromCategoryAR( categoryAccessRight );
                            addedContentAccessRights.put( groupKey, contentAccessRight );
                        }
                        else if ( "modified".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            modifiedCategoryAccessRights.put( groupKey, categoryAccessRight );
                            // M� konvertere category parametere til content parametere
                            ExtendedMap contentAccessRight = buildContentARFromCategoryAR( categoryAccessRight );
                            modifiedContentAccessRights.put( groupKey, contentAccessRight );
                        }
                    }
                }

                // Run through each (selected) category...
                for ( Iterator it = formItems.keySet().iterator(); it.hasNext(); )
                {

                    String paramName = (String) it.next();
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        int curCategoryKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply, only changes
                        if ( "on".equals( applyOnlyChanges ) )
                        {

                            // Henter ut eksisterende accessrights
                            Document docCurrentCategoryAR =
                                admin.getAccessRights( user, AccessRight.CATEGORY, curCategoryKey, false ).getAsDOMDocument();
                            // getAccessRights() skal enten: ikke returnere userright? eller s� utvider jeg den med en
                            // parameter hvor jeg kan velge dette.

                            // Påfører endringer
                            Document docChangedCategoryAR =
                                applyChangesInAccessRights( docCurrentCategoryAR, removedCategoryAccessRights, modifiedCategoryAccessRights,
                                                            addedCategoryAccessRights );
                            // Lagrer
                            admin.updateAccessRights( user, XMLTool.documentToString( docChangedCategoryAR ) );

                            // Apply on contents in current category too...
                            if ( "on".equals( includeContents ) )
                            {

                                // Loop thru each content in category
                                int[] contentKeys = admin.getContentKeysByCategory( user, curCategoryKey );
                                if ( contentKeys != null )
                                {
                                    for ( int i = 0; i < contentKeys.length; i++ )
                                    {
                                        // Henter ut eksisterende accessrights
                                        Document docCurrentContentAR =
                                            admin.getAccessRights( user, AccessRight.CONTENT, contentKeys[i], false ).getAsDOMDocument();

                                        // Påfører endringer
                                        Document docChangedContentAR =
                                            applyChangesInAccessRights( docCurrentContentAR, removedContentAccessRights,
                                                                        modifiedContentAccessRights, addedContentAccessRights );

                                        // Lagrer
                                        admin.updateAccessRights( user, XMLTool.documentToString( docChangedContentAR ) );
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Apply accessright as whole
            else
            {
                // ("applying as whole");
                // Prepare for overwrite accessrights
                Document docNewCategoryAccessRights = buildAccessRightsXML( null, null, formItems, AccessRight.CATEGORY );

                // Run through each (selected) category...
                for ( Iterator it = formItems.keySet().iterator(); it.hasNext(); )
                {

                    String paramName = (String) it.next();
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );

                        int curCategoryKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply on current category
                        Element categoryAccessrighs = docNewCategoryAccessRights.getDocumentElement();
                        categoryAccessrighs.setAttribute( "key", String.valueOf( curCategoryKey ) );

                        admin.updateAccessRights( user, XMLTool.documentToString( docNewCategoryAccessRights ) );

                        // Apply on contents in current category too...
                        if ( "on".equals( includeContents ) )
                        {
                            Document docNewContentAccessRights = buildContentARsFromCategoryARs( docNewCategoryAccessRights );
                            Element accessRights = docNewContentAccessRights.getDocumentElement();

                            int[] contentKeys = admin.getContentKeysByCategory( user, curCategoryKey );
                            if ( contentKeys != null )
                            {
                                for ( int i = 0; i < contentKeys.length; i++ )
                                {
                                    // setter content key i accessrights elementet
                                    accessRights.setAttribute( "key", String.valueOf( contentKeys[i] ) );

                                    admin.updateAccessRights( user, XMLTool.documentToString( docNewContentAccessRights ) );
                                }
                            }
                        }
                    }
                }
            }
        }
        // Ikke propager, bare lagre accessrights p� valgte categori
        else
        {

            int categoryKey = formItems.getInt( "cat" );

            String accessRightsXML = buildAccessRightsXML( String.valueOf( categoryKey ), formItems, AccessRight.CATEGORY );

            // Oppdaterer i db
            admin.updateAccessRights( user, accessRightsXML );
        }

        // Redirect...
        redirectToBrowse( request, response, formItems );

    }

    private ExtendedMap buildContentARFromCategoryAR( ExtendedMap categoryAccessRight )
    {

        ExtendedMap contentAccessRights = new ExtendedMap();

        String category_read = categoryAccessRight.getString( "read" );
        String category_create = categoryAccessRight.getString( "create" );
        String category_publish = categoryAccessRight.getString( "publish" );
        String category_administrate = categoryAccessRight.getString( "administrate" );

        String content_read = "false";
        String content_update = "false";
        String content_delete = "false";

        if ( "true".equals( category_administrate ) )
        {
            content_read = "true";
            content_update = "true";
            content_delete = "true";
        }
        else if ( "true".equals( category_publish ) )
        {
            content_read = "true";
            content_update = "true";
            content_delete = "true";
        }
        else if ( "true".equals( category_create ) )
        {
            content_read = "true";
            content_update = "false";
            content_delete = "false";
        }
        else if ( "true".equals( category_read ) )
        {
            content_read = "true";
            content_update = "false";
            content_delete = "false";
        }

        contentAccessRights.putString( "read", content_read );
        contentAccessRights.putString( "update", content_update );
        contentAccessRights.putString( "delete", content_delete );

        return contentAccessRights;
    }

    private Document buildContentARsFromCategoryARs( Document categoryAccessRights )
    {

        Document doc = XMLTool.createDocument( "accessrights" );
        Element accessRights = doc.getDocumentElement();
        accessRights.setAttribute( "type", String.valueOf( AccessRight.CONTENT ) );

        NodeList categoryAccessRightList = categoryAccessRights.getDocumentElement().getChildNodes();
        for ( int i = 0; i < categoryAccessRightList.getLength(); i++ )
        {

            Element categoryAccessRight = (Element) categoryAccessRightList.item( i );
            Element contentAccessRight = buildContentARFromCategoryAR( categoryAccessRight );

            accessRights.appendChild( doc.importNode( contentAccessRight, true ) );
        }

        return doc;
    }

    private Element buildContentARFromCategoryAR( Element categoryAccessRight )
    {

        Document doc = XMLTool.createDocument( "accessright" );
        Element accessRight = doc.getDocumentElement();
        accessRight.setAttribute( "groupkey", categoryAccessRight.getAttribute( "groupkey" ) );

        String category_read = categoryAccessRight.getAttribute( "read" );
        String category_create = categoryAccessRight.getAttribute( "create" );
        String category_publish = categoryAccessRight.getAttribute( "publish" );
        String category_administrate = categoryAccessRight.getAttribute( "administrate" );

        String content_read = "false";
        String content_update = "false";
        String content_delete = "false";

        if ( "true".equals( category_administrate ) )
        {
            content_read = "true";
            content_update = "true";
            content_delete = "true";
        }
        else if ( "true".equals( category_publish ) )
        {
            content_read = "true";
            content_update = "true";
            content_delete = "true";
        }
        else if ( "true".equals( category_create ) )
        {
            content_read = "true";
            content_update = "false";
            content_delete = "false";
        }
        else if ( "true".equals( category_read ) )
        {
            content_read = "true";
            content_update = "false";
            content_delete = "false";
        }

        accessRight.setAttribute( "read", content_read );
        accessRight.setAttribute( "update", content_update );
        accessRight.setAttribute( "delete", content_delete );

        return accessRight;
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        int unitKey = formItems.getInt( "selectedunitkey", -1 );

        User user = securityService.getLoggedInAdminConsoleUser();

        if ( "move".equals( operation ) )
        {
            admin.moveCategory( user, formItems.getInt( "cat" ), formItems.getInt( "newparent" ) );

            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.getString( "oldpage" ) );
            queryParams.put( "op", "browse" );
            queryParams.put( "cat", formItems.getString( "cat" ) );
            queryParams.put( "selectedunitkey", unitKey );
            queryParams.put( "reload", "true" );

            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
        else if ( "propagateaccessrights".equals( operation ) )
        {
            handlerPropagateAccessRights( request, response, session, admin, formItems );
        }
        else if ( "emptycategory".equals( operation ) )
        {
            handlerEmptyCategory( request, response, session, admin, formItems );
        }
    }

    private void addSelectorParameters( ExtendedMap formItems, AdminService admin, ExtendedMap parameters )
    {
        if ( formItems.getString( "selector", "false" ).equals( "true" ) )
        {
            parameters.putString( "selector", "true" );
            String mode = formItems.getString( "mode", formItems.getString( "selector_mode", null ) );
            parameters.putString( "selector_mode", mode );
            parameters.putString( "selector_row", formItems.getString( "row", formItems.getString( "selector_row", null ) ) );
            String reloadcategories = formItems.getString( "reloadcategories", formItems.getString( "selector_reloadcategories", null ) );
            parameters.putString( "selector_reloadcategories", reloadcategories != null ? "true" : null );

            if ( "module".equals( mode ) )
            {
                parameters.putString( "selector_returnkey",
                                      formItems.getString( "returnkey", formItems.getString( "selector_returnkey", null ) ) );
                parameters.putString( "selector_returnview",
                                      formItems.getString( "returnview", formItems.getString( "selector_returnview", null ) ) );
            }
            else
            {
                parameters.putString( "selector_returnkey", "" );
                parameters.putString( "selector_returnview", "" );
            }
        }
        else
        {
            parameters.putString( "selector", "false" );
        }
    }

    private int[] resolveContentTypes( String[] contentTypeStringArray )
    {

        if ( contentTypeStringArray != null && contentTypeStringArray.length > 0 && contentTypeStringArray[0] != null )
        {
            if ( contentTypeStringArray[0].indexOf( "," ) > -1 )
            {
                // contenttypene ligger kommaseparert i første element (skjer ved linker)
                contentTypeStringArray = contentTypeStringArray[0].split( "," );
            }
            int[] types = ArrayUtil.toIntArray( contentTypeStringArray );
            if ( ArrayUtil.contains( types, -1 ) )
            {
                return null;
            }
            return types;
        }
        return null;
    }

    public void handlerReport( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String subOp )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        ExtendedMap parameters = formItems;

        if ( "form".equals( subOp ) )
        {
            int unitKey = formItems.getInt( "selectedunitkey", -1 );
            int categoryKey = formItems.getInt( "cat" );

            Document doc = admin.getSuperCategoryNames( categoryKey, false, true ).getAsDOMDocument();

            addCommonParameters( admin, user, request, parameters, unitKey, -1 );

            transformXML( request, response, doc, "report_form.xsl", parameters );
        }
        else if ( "create".equals( subOp ) )
        {
            ResourceKey stylesheetKey = new ResourceKey( formItems.getString( "stylesheetkey" ) );
            ResourceFile res = resourceService.getResourceFile( stylesheetKey );
            if ( res == null )
            {
                throw new StylesheetNotFoundException( stylesheetKey );
            }
            Document xslDoc;
            try
            {
                xslDoc = res.getDataAsXml().getAsDOMDocument();
            }
            catch ( XMLException e )
            {
                throw new InvalidStylesheetException( stylesheetKey, e );
            }
            DOMSource xslSource = new DOMSource( xslDoc );

            int cat = formItems.getInt( "cat" );
            String reportXML;
            String searchType = formItems.getString( "searchtype" );
            if ( "simple".equals( searchType ) )
            {
                reportXML = new SearchUtility( userDao, groupDao, securityService, contentService ).simpleReport( user, formItems, cat );
            }
            else
            {
                String[] contentTypeStringArray = formItems.getStringArray( "contenttypestring" );
                int[] contentTypes = resolveContentTypes( contentTypeStringArray );
                reportXML =
                    new SearchUtility( userDao, groupDao, securityService, contentService ).advancedReport( user, formItems, contentTypes );
            }
            Document reportDoc = XMLTool.domparse( reportXML );
            Element contentsElem = reportDoc.getDocumentElement();
            String datasourcesDefaultResultElementName = verticalProperties.getDatasourceDefaultResultRootElement();
            Element verticaldataElem = XMLTool.createElement( reportDoc, datasourcesDefaultResultElementName );
            reportDoc.replaceChild( verticaldataElem, contentsElem );
            verticaldataElem.appendChild( contentsElem );
            DOMSource reportSource = new DOMSource( reportDoc );

            new XsltProcessorHelper().stylesheet( xslSource, getStylesheetURIResolver( admin ) ).params( parameters ).input(
                reportSource ).process( response );
        }
        else
        {
            String message = "Unknown sub-operation for operation report: {0}";
            VerticalAdminLogger.errorAdmin( message, subOp, null );
        }
    }

    public void handlerMenu( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, TransformerException, IOException
    {

        String contentTypeString = formItems.getString( "contenttypestring", "" );

        String subop = formItems.getString( "subop", "" );

        final boolean rememberTreeMenuState =
            subop.equals( "insert" ) || subop.equals( "contentfield" ) || subop.equals( "addcontenttosection" ) ||
                subop.equals( "relatedcontent" ) || subop.indexOf( "relatedimage" ) > -1 || subop.indexOf( "relatedfile" ) > -1;

        Cookie cookie = CookieUtil.getCookie( request, ContentBaseHandlerServlet.getPopupCookieName( contentTypeString ) );
        int cookieCategoryKey = -1;
        if ( cookie != null )
        {
            cookieCategoryKey = Integer.parseInt( cookie.getValue() );
        }

        int topCategoryKey = formItems.getInt( "topcategorykey", -1 );

        int selectedParentCategoryKey = -1;
        int selectedCategoryKey;
        boolean redirect;

        if ( topCategoryKey != -1 || !rememberTreeMenuState )
        {
            selectedCategoryKey = topCategoryKey;
            redirect = false;
        }
        else
        {
            selectedCategoryKey = cookieCategoryKey;
            redirect = true;
            selectedParentCategoryKey = admin.getSuperCategoryKey( selectedCategoryKey );
        }

        int unitFilterContentType = formItems.getInt( "unitfiltercontenttype", -1 );
        boolean requireCategoryAdmin = "true".equals( formItems.getString( "requirecategoryadmin", "" ) );
        int excludeCategoryKey = formItems.getInt( "excludecategorykey", -1 );
        int excludeCategoryKeyWithChildren = formItems.getInt( "excludecategorykey_withchildren", -1 );

        int[] contentTypes = null;
        if ( contentTypeString != null )
        {
            String[] contentTypeStrings = StringUtil.splitString( contentTypeString, "," );
            contentTypes = ArrayUtil.toIntArray( contentTypeStrings );
        }

        Document doc = admin.getCategoryMenu( user, selectedCategoryKey, contentTypes, true ).getAsDOMDocument();
        Element selectedCategoryElem = XMLTool.selectElement( doc.getDocumentElement(), "//category[@key = " + selectedCategoryKey + "]" );
        if ( selectedCategoryElem == null )
        {
            selectedCategoryKey = -1;
        }
        else if ( contentTypes != null && !selectedCategoryHasValidContentType( contentTypes, selectedCategoryElem ) )
        {
            selectedCategoryKey = -1;
        }

        if ( contentTypes != null || requireCategoryAdmin ) // Dirty hack here, requireCategoryAdmin is set for move
        // category
        {
            doc.getDocumentElement().setAttribute( "disabled", "true" );
        }

        if ( unitFilterContentType != -1 )
        {
            filterUnitsOnContentTypes( admin, doc, unitFilterContentType );
        }

        if ( excludeCategoryKeyWithChildren != -1 )
        {
            Element categoryElem =
                XMLTool.selectElement( doc.getDocumentElement(), "//category[@key = '" + excludeCategoryKeyWithChildren + "']" );
            disableCategory( categoryElem, true );
        }

        if ( excludeCategoryKey != -1 )
        {
            Element categoryElem = XMLTool.selectElement( doc.getDocumentElement(), "//category[@key = '" + excludeCategoryKey + "']" );
            disableCategory( categoryElem, false );
        }

        if ( requireCategoryAdmin )
        {
            disableCategoriesWithoutAdminRight( doc );
        }

        ExtendedMap xslParams = new ExtendedMap();
        xslParams.put( "selectedunitkey", formItems.getString( "selectedunitkey", "" ) );
        xslParams.put( "topcategorykey", topCategoryKey );
        xslParams.put( "fieldname", formItems.getString( "fieldname", "" ) );
        xslParams.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        if ( redirect )
        {
            xslParams.put( "redirect", String.valueOf( redirect ) );
            xslParams.put( "selectedcategorykey", selectedCategoryKey );
            xslParams.put( "selectedparentcategorykey", selectedParentCategoryKey );
        }
        if ( contentTypeString != null )
        {
            xslParams.put( "contenttypestring", contentTypeString );
        }
        xslParams.put( "cat", formItems.getString( "cat", null ) );
        xslParams.put( "subop", formItems.getString( "subop" ) );
        xslParams.put( "contenthandler", formItems.getString( "contenthandler", null ) );
        xslParams.put( "minoccurrence", formItems.getString( "minoccurrence", null ) );
        xslParams.put( "maxoccurrence", formItems.getString( "maxoccurrence", null ) );

        transformXML( request, response, doc, "content_selector_frame1.xsl", xslParams );
    }

    private boolean selectedCategoryHasValidContentType( int[] contentTypes, Element selectedCategoryElem )
    {
        String allowedContentType = selectedCategoryElem.getAttribute( "contenttypekey" );

        if ( StringUtils.isNotBlank( allowedContentType ) )
        {
            for ( int i = 0; i < contentTypes.length; i++ )
            {
                if ( new Integer( allowedContentType ).equals( contentTypes[i] ) )
                {
                    return true;
                }
            }

            return false;
        }

        return true;
    }


    private void disableCategoriesWithoutAdminRight( Document categoriesDoc )
    {
        Element[] categoryElems = XMLTool.selectElements( categoriesDoc.getDocumentElement(), "//category" );
        for ( int i = 0; i < categoryElems.length; i++ )
        {
            if ( !"true".equals( categoryElems[i].getAttribute( "useradministrate" ) ) )
            {
                categoryElems[i].setAttribute( "disabled", "true" );
            }
        }
    }

    private void disableCategory( Element categoryElem, boolean withChildren )
    {
        if ( categoryElem != null )
        {
            categoryElem.setAttribute( "disabled", "true" );

            if ( withChildren )
            {
                Element[] subCategories = XMLTool.getElements( categoryElem );
                for ( int i = 0; i < subCategories.length; i++ )
                {
                    disableCategory( subCategories[i], withChildren );
                }
            }
        }
    }

    /*
     * This method removes units that does not allow a specified contenttype. Used in move category popup
     */

    private void filterUnitsOnContentTypes( AdminService admin, Document categoriesDoc, int contentTypeKey )
    {
        Document unitsDoc = admin.getUnits().getAsDOMDocument();

        Element[] topCategories = XMLTool.getElements( categoriesDoc.getDocumentElement() );
        for ( int i = 0; i < topCategories.length; i++ )
        {
            int categoryKey = Integer.parseInt( topCategories[i].getAttribute( "key" ) );

            Element unitElem = XMLTool.selectElement( unitsDoc.getDocumentElement(), "unit[@categorykey = '" + categoryKey + "']" );
            if ( XMLTool.selectElement( unitElem, "contenttypes/contenttype" ) != null )
            {
                if ( XMLTool.selectElement( unitElem, "contenttypes/contenttype[@key = '" + contentTypeKey + "']" ) == null )
                {
                    XMLTool.removeChildFromParent( categoriesDoc.getDocumentElement(), topCategories[i] );
                }
            }
        }
    }

}