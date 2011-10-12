/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.filters.UnitFilter;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.business.DeploymentPathResolver;

public class ArchiveHandlerServlet
    extends AdminHandlerBaseServlet
{

    public String buildUnitXML( ExtendedMap formItems, boolean createUnit )
        throws VerticalAdminException
    {

        Document doc = XMLTool.createDocument();

        // Create unit element
        Element unit = XMLTool.createRootElement( doc, "unit" );

        if ( !createUnit )
        {
            unit.setAttribute( "key", formItems.getString( "selectedunitkey" ) );
        }

        unit.setAttribute( "languagekey", formItems.getString( "languagekey" ) );

        XMLTool.createElement( doc, unit, "name", formItems.getString( "name" ) );
        if ( formItems.containsKey( "description" ) )
        {
            XMLTool.createElement( doc, unit, "description", formItems.getString( "description" ) );
        }

        // Content types
        Element ctyElem = XMLTool.createElement( doc, unit, "contenttypes" );
        String[] contentTypeKeys = formItems.getStringArray( "contenttypekey" );
        for ( String contentTypeKey : contentTypeKeys )
        {
            XMLTool.createElement( doc, ctyElem, "contenttype" ).setAttribute( "key", contentTypeKey );
        }

        return XMLTool.documentToString( doc );
    }

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
        if ( formItems.containsKey( "categorycontenttypekey" ) )
        {
            categoryElem.setAttribute( "contenttypekey", formItems.getString( "categorycontenttypekey" ) );
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

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        String subop = formItems.getString( "subop", "" );
        String contentTypeString = formItems.getString( "contenttypestring", "" );

        if ( !"browse".equals( subop ) )
        {
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, ContentBaseHandlerServlet.getPopupCookieName( contentTypeString ), "-1",
                                  ContentBaseHandlerServlet.COOKIE_TIMEOUT, deploymentPath );
        }

        UnitFilter uf = new UnitFilter( user );
        Document doc = XMLTool.domparse( admin.getUnitNamesXML( uf ) );

        ExtendedMap xslParams = new ExtendedMap();
        xslParams.put( "contenttypestring", contentTypeString );
        xslParams.put( "page", formItems.getString( "page" ) );
        xslParams.put( "subop", subop );
        xslParams.put( "fieldname", formItems.getString( "fieldname", "" ) );
        xslParams.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        xslParams.put( "minoccurrence", formItems.getString( "minoccurrence", "" ) );
        xslParams.put( "maxoccurrence", formItems.getString( "maxoccurrence", "" ) );
        xslParams.put( "contenthandler", formItems.getString( "contenthandler", "" ) );
        if ( formItems.containsKey( "reload" ) )
        {
            xslParams.put( "reload", formItems.getString( "reload" ) );
        }
        addCommonParameters( admin, user, request, xslParams, -1, -1 );

        addAccessLevelParameters( user, xslParams );

        transformXML( request, response, doc, "repository_browse.xsl", xslParams );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        ExtendedMap xslParams = new ExtendedMap();

        Document doc;
        if ( !formItems.containsKey( "categorykey" ) )
        {
            doc = XMLTool.createDocument( "categories" );

            String xmlDefaultAC = admin.getDefaultAccessRights( user, AccessRight.CATEGORY, -1 );
//            ( xmlDefaultAC );
            XMLTool.mergeDocuments( doc, XMLTool.domparse( xmlDefaultAC ), true );
        }
        else
        {
            int categoryKey = formItems.getInt( "categorykey" );
            doc = XMLTool.domparse( admin.getCategory( user, categoryKey ) );
            int categoryCount = admin.getContentCount( categoryKey, false );
            Element categoryElem = XMLTool.getElement( doc.getDocumentElement(), "category" );
            categoryElem.setAttribute( "contentcount", String.valueOf( categoryCount ) );

            int unitKey = formItems.getInt( "key" );
            String unitXML = admin.getUnit( unitKey );
            XMLTool.mergeDocuments( doc, XMLTool.domparse( unitXML ), false );
        }

        VerticalAdminLogger.debug( this.getClass(), 10, doc );

        String xmlLanguages = admin.getLanguages();
        XMLTool.mergeDocuments( doc, XMLTool.domparse( xmlLanguages ), true );

        // Get content types for this site
        XMLTool.mergeDocuments( doc, admin.getContentTypes( false ).getAsDOMDocument(), true );

        xslParams.put( "page", formItems.getString( "page" ) );

        if ( !formItems.containsKey( "categorykey" ) )
        {
            xslParams.put( "create", "1" );
        }

        if ( formItems.containsKey( "returnpage" ) )
        {
            xslParams.put( "returnpage", formItems.get( "returnpage" ) );
        }

        if ( formItems.containsKey( "minoccurrence" ) )
        {
            xslParams.put( "minoccurrence", formItems.get( "minoccurrence" ) );
        }

        if ( formItems.containsKey( "maxoccurrence" ) )
        {
            xslParams.put( "maxoccurrence", formItems.get( "maxoccurrence" ) );
        }

        addAccessLevelParameters( user, xslParams );
        addCommonParameters( admin, user, request, xslParams, -1, -1 );

        transformXML( request, response, doc, "archive_form.xsl", xslParams );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {
        User user = securityService.getLoggedInAdminConsoleUser();

        String unitXML = buildUnitXML( formItems, true );
        int unitKey = admin.createUnit( unitXML );
        formItems.put( "selectedunitkey", String.valueOf( unitKey ) );

        String categoryXML = buildCategoryXML( user, formItems, true );
        String accessRightsXML = buildAccessRightsXML( null, formItems, AccessRight.CATEGORY );
        Document doc = XMLTool.domparse( categoryXML );
        XMLTool.mergeDocuments( doc, XMLTool.domparse( accessRightsXML ), true );
        admin.createCategory( user, XMLTool.documentToString( doc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        String xmlUnitData = buildUnitXML( formItems, false );
        admin.updateUnit( xmlUnitData );

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
        else
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "op", "browse" );
            if ( formItems.containsKey( "returnpage" ) )
            {
                queryParams.put( "page", formItems.get( "returnpage" ) );
            }
            else
            {
                int cctk = formItems.getInt( "categorycontenttypekey", -1 );
                if ( cctk > -1 )
                {
                    queryParams.put( "page", cctk + 999 );
                }
                else
                {
                    queryParams.put( "page", "991" );
                }
            }

            queryParams.put( "cat", String.valueOf( categoryKey ) );
            queryParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
            queryParams.put( "reload", "true" );
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }

    private void handlerPropagateAccessRights( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                               AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        int categoryKey = formItems.getInt( "cat" );

        // Propagate
        String subop = formItems.getString( "subop", "" );
        if ( "propagate".equals( subop ) )
        {

            String includeContents = formItems.getString( "includecontents", "off" );
            String applyOnlyChanges = formItems.getString( "applyonlychanges", "off" );

            if ( "on".equals( applyOnlyChanges ) )
            {
                //("applying only changes");
                // Prepare for apply only changes..
                Map<String, ExtendedMap> removedCategoryAccessRights = new HashMap<String, ExtendedMap>();
                Map<String, ExtendedMap> addedCategoryAccessRights = new HashMap<String, ExtendedMap>();
                Map<String, ExtendedMap> modifiedCategoryAccessRights = new HashMap<String, ExtendedMap>();

                Map<String, ExtendedMap> removedContentAccessRights = new HashMap<String, ExtendedMap>();
                Map<String, ExtendedMap> addedContentAccessRights = new HashMap<String, ExtendedMap>();
                Map<String, ExtendedMap> modifiedContentAccessRights = new HashMap<String, ExtendedMap>();

                for ( Object o : formItems.keySet() )
                {
                    String paramName = (String) o;
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
                            // Må konvertere category parametere til content parametere
                            ExtendedMap contentAccessRight = buildContentARFromCategoryAR( categoryAccessRight );
                            addedContentAccessRights.put( groupKey, contentAccessRight );
                        }
                        else if ( "modified".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            modifiedCategoryAccessRights.put( groupKey, categoryAccessRight );
                            // Må konvertere category parametere til content parametere
                            ExtendedMap contentAccessRight = buildContentARFromCategoryAR( categoryAccessRight );
                            modifiedContentAccessRights.put( groupKey, contentAccessRight );
                        }
                    }
                }

                // Run through each (selected) category...
                for ( Object o : formItems.keySet() )
                {

                    String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        int curCategoryKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply, only changes
                        if ( "on".equals( applyOnlyChanges ) )
                        {

                            // Henter ut eksisterende accessrights
                            Document docCurrentCategoryAR =
                                XMLTool.domparse( admin.getAccessRights( user, AccessRight.CATEGORY, curCategoryKey, false ) );
                            // getAccessRights() skal enten: ikke returnere userright? eller så utvider jeg den med en parameter hvor jeg kan velge dette.

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
                                    for ( int contentKey : contentKeys )
                                    {
                                        // Henter ut eksisterende accessrights
                                        Document docCurrentContentAR =
                                            XMLTool.domparse( admin.getAccessRights( user, AccessRight.CONTENT, contentKey, false ) );

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
                //("applying as whole");
                // Prepare for overwrite accessrights
                Document docNewCategoryAccessRights = buildAccessRightsXML( null, null, formItems, AccessRight.CATEGORY );

                // Run through each (selected) category...
                for ( Object o : formItems.keySet() )
                {

                    String paramName = (String) o;
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
                                for ( int contentKey : contentKeys )
                                {
                                    // setter content key i accessrights elementet
                                    accessRights.setAttribute( "key", String.valueOf( contentKey ) );

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
            String accessRightsXML = buildAccessRightsXML( String.valueOf( categoryKey ), formItems, AccessRight.CATEGORY );

            // Oppdaterer i db
            admin.updateAccessRights( user, accessRightsXML );
        }

        // Redirect
        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "op", "browse" );
        if ( formItems.containsKey( "returnpage" ) )
        {
            queryParams.put( "page", formItems.get( "returnpage" ) );
            queryParams.put( "cat", String.valueOf( categoryKey ) );
            queryParams.put( "selectedunitkey", formItems.get( "selectedunitkey" ) );
        }
        else
        {
            queryParams.put( "page", formItems.get( "page" ) );
        }
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void handlerPropagateAccessRightsPage( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                                   AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        int unitKey = formItems.getInt( "selectedunitkey", -1 );
        int categoryKey = formItems.getInt( "key", -1 );

        Document doc = XMLTool.createDocument( "data" );

        Document categories = XMLTool.domparse( admin.getCategoryMenu( user, categoryKey, null, true ) );
        //Don't seam to be in use (JAM 27.10.2008)
        //Document categoryNames = XMLTool.domparse(admin.getSuperCategoryNames(user, categoryKey, false, true));
        Document changedAccessRights = buildChangedAccessRightsXML( formItems );
        Document currentAccessRights = XMLTool.domparse( buildAccessRightsXML( formItems ) );
        XMLTool.mergeDocuments( doc, categories, true );
        //XMLTool.mergeDocuments(doc, categoryNames, true);
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

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        CategoryKey categoryKey = new CategoryKey( formItems.getInt( "cat" ) );

        DeleteCategoryCommand command = new DeleteCategoryCommand();
        command.setDeleter( user.getKey() );
        command.setCategoryKey( categoryKey );
        command.setIncludeContent( false );
        command.setRecursive( false );
        categoryService.deleteCategory( command );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "reload", "true" );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
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

        Element[] categoryAccessRightElems = XMLTool.getElements( categoryAccessRights.getDocumentElement() );
        for ( Element categoryAccessRightElem : categoryAccessRightElems )
        {
            Element contentAccessRight = buildContentARFromCategoryAR( categoryAccessRightElem );

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

        if ( "propagateaccessrights".equals( operation ) )
        {
            handlerPropagateAccessRights( request, response, session, admin, formItems );
        }
        else if ( "popup".equals( operation ) )
        {
            handlerPopup( request, response, admin, formItems );
        }
        else if ( "emptycategory".equals( operation ) )
        {
            handlerEmptyCategory( request, response, session, formItems );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    public boolean handlerPopup( HttpServletRequest request, HttpServletResponse response, AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Display the frameset for selecting content:
        Document docDummy = XMLTool.createDocument( "foo" );

        String contentTypeString = null;
        if ( formItems.containsKey( "handler" ) )
        {
            String handler = formItems.getString( "handler" );
            int[] contentTypes = admin.getContentTypesByHandlerClass( handler );
            if ( contentTypes == null || contentTypes.length == 0 )
            {
                contentTypeString = "";
            }
            else
            {
                contentTypeString = StringUtil.mergeInts( contentTypes, "," );
            }

        }
        else if ( formItems.containsKey( "contenttypekey" ) || formItems.containsKey( "contenttypename" ) )
        {
            TIntArrayList contentTypes = new TIntArrayList();

            String[] contentTypeKeys = getArrayFormItem( formItems, "contenttypekey" );
            if ( contentTypeKeys != null )
            {
                for ( String contentTypeKey : contentTypeKeys )
                {
                    contentTypes.add( Integer.parseInt( contentTypeKey ) );
                }
            }

            String[] contentTypeNames = getArrayFormItem( formItems, "contenttypename" );
            if ( contentTypeNames != null )
            {
                for ( String contentTypeName : contentTypeNames )
                {
                    int contentTypeKey = admin.getContentTypeKeyByName( contentTypeName );
                    if ( contentTypeKey >= 0 )
                    {
                        contentTypes.add( contentTypeKey );
                    }
                }
            }

            contentTypeString = StringUtil.mergeInts( contentTypes.toArray(), "," );
        }

        ExtendedMap xslParams = new ExtendedMap();
        xslParams.put( "page", formItems.getString( "page" ) );
        xslParams.put( "contenttypestring", contentTypeString );
        xslParams.put( "fieldname", formItems.getString( "fieldname", "" ) );
        xslParams.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        xslParams.put( "selectedunitkey", formItems.getString( "selectedunitkey", "" ) );
        xslParams.put( "cat", formItems.getString( "cat", null ) );
        xslParams.put( "subop", formItems.getString( "subop", "" ) );

        xslParams.put( "unitfiltercontenttype", formItems.getString( "unitfiltercontenttype", null ) );
        xslParams.put( "requirecategoryadmin", formItems.getString( "requirecategoryadmin", null ) );
        xslParams.put( "excludecategorykey", formItems.getString( "excludecategorykey", null ) );
        xslParams.put( "excludecategorykey_withchildren", formItems.getString( "excludecategorykey_withchildren", null ) );
        xslParams.put( "contenthandler", formItems.getString( "contenthandler", null ) );
        xslParams.put( "user-agent", request.getHeader( "user-agent" ) );
        xslParams.put( "minoccurrence", formItems.getString( "minoccurrence", null ) );
        xslParams.put( "maxoccurrence", formItems.getString( "maxoccurrence", null ) );

        transformXML( request, response, docDummy, "content_selector_frameset.xsl", xslParams );
        return true;
    }

    public void handlerEmptyCategory( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
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
}