/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.portal.PrettyPathNameCreator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.io.FileUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;

import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.portal.cache.PageCacheService;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.ContentAccessEntity;
import com.enonic.cms.core.content.ContentAndVersion;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentLocation;
import com.enonic.cms.core.content.ContentLocationSpecification;
import com.enonic.cms.core.content.ContentLocations;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Base class for userservices servlets related to modifying content from "public" sites.  This class will take care of custom content. All
 * the other content types should extend this class to implement special logic for generating XML and changing the database.
 */
@Controller
public abstract class ContentHandlerBaseController
    extends AbstractUserServicesHandlerController
    implements InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentHandlerBaseController.class.getName() );

    protected final static int ERR_MISSING_CATEGORY_KEY = 100;

    protected final static int SECONDS_IN_WEEK = 60 * 60 * 24 * 7;

    protected int WORD_BREAK_LIMIT;


    public void afterPropertiesSet()
        throws Exception
    {
        WORD_BREAK_LIMIT =
            verticalProperties.getPropertyAsInt( "com.enonic.vertical.userservices.DiscussionHandlerServlet.WORD_BREAK_LIMIT" );
    }

    protected String breakString( String string )
    {
        int cCounter = 0;
        StringBuffer newstring = new StringBuffer( string.length() );
        char c;
        for ( int i = 0; i < string.length(); i++ )
        {
            c = string.charAt( i );

            if ( c == ' ' || c == '.' || c == '!' || c == '?' )
            {
                cCounter = 0;
            }
            else
            {
                ++cCounter;
            }

            if ( cCounter == WORD_BREAK_LIMIT && ( i + 1 ) < string.length() && string.charAt( i + 1 ) != '-' && c != '-' )
            {
                newstring.append( '-' );
                cCounter = 0;
            }
            newstring.append( c );
        }

        return newstring.toString();
    }


    protected void buildContentTypeXML( UserServicesService userServices, Element contentdataElem, ExtendedMap formItems,
                                        boolean skipEmptyElements )
        throws VerticalUserServicesException, RemoteException
    {
        Document doc = contentdataElem.getOwnerDocument();
        Element moduleElement = (Element) formItems.get( "__module_element" );

        contentdataElem.setAttribute( "version", "1.0" );

        // Date conversion objects
        NodeList blockElements;
        try
        {
            blockElements = XMLTool.selectNodes( moduleElement, "form/block" );

            for ( int k = 0; k < blockElements.getLength(); ++k )
            {
                Element blockElement = (Element) blockElements.item( k );
                NodeList inputElements = XMLTool.selectNodes( blockElement, "input" );

                boolean groupBlock = false;
                String groupXPath = blockElement.getAttribute( "group" );
                if ( groupXPath != null && groupXPath.length() > 0 )
                {
                    groupBlock = true;
                }

                if ( !groupBlock )
                {
                    createNormalBlock( formItems, doc, contentdataElem, inputElements, skipEmptyElements );
                }
                else
                {
                    createGroupBlock( formItems, doc, contentdataElem, inputElements, groupXPath, k + 1 );
                }
            }
        }
        catch ( ParseException pe )
        {
            String message = "Failed to parse a date: %t";
            VerticalRuntimeException.error( this.getClass(), VerticalException.class, message, pe );
        }
    }

    protected String buildXML( UserServicesService userServices, User user, ExtendedMap formItems, SiteKey siteKey, int contentTypeKey,
                               String contentTitle, boolean skipEmptyElements )
        throws VerticalUserServicesException, RemoteException
    {

        Document doc;
        Element contentElem;
        Element contentdataElem;
        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            contentKey = formItems.getInt( "contentkey", -1 );
        }

        if ( contentKey == -1 )
        {
            doc = XMLTool.createDocument( "content" );
            contentElem = doc.getDocumentElement();

            int categoryKey = formItems.getInt( "categorykey" );
            CategoryEntity categoryEntity = categoryDao.findByKey( new CategoryKey( categoryKey ) );

            if ( categoryEntity.getAutoMakeAvailableAsBoolean() )
            {
                contentElem.setAttribute( "publishfrom", CalendarUtil.formatCurrentDate() );
                contentElem.setAttribute( "status", "2" );
            }
            else
            {
                // new content is created as draft
                contentElem.setAttribute( "status", "0" );
                contentElem.removeAttribute( "publishfrom" );
                contentElem.removeAttribute( "publishto" );
            }

            contentElem.setAttribute( "contenttypekey", String.valueOf( contentTypeKey ) );
            contentElem.setAttribute( "priority", "0" );

            // category:
            Element category = XMLTool.createElement( doc, contentElem, "categoryname" );
            category.setAttribute( "key", String.valueOf( categoryKey ) );

            // content title
            XMLTool.createElement( doc, contentElem, "title", contentTitle );

            // content data
            contentdataElem = XMLTool.createElement( doc, contentElem, "contentdata" );
        }
        else
        {
            doc = XMLTool.domparse( userServices.getContent( user, contentKey, false, 0, 0, 0 ) );
            Element rootElem = doc.getDocumentElement();
            contentElem = XMLTool.getFirstElement( rootElem );
            rootElem.removeChild( contentElem );
            doc.replaceChild( contentElem, rootElem );

            // modifier/@key
            Element modifierElem = XMLTool.getElement( contentElem, "modifier" );
            XMLTool.removeChildFromParent( contentElem, modifierElem );
            modifierElem = XMLTool.createElement( contentElem, "modifier" );
            modifierElem.setAttribute( "key", String.valueOf( user.getKey() ) );

            // version/@key
            int versionKey = userServices.getCurrentVersionKey( contentKey );
            contentElem.setAttribute( "versionkey", String.valueOf( versionKey ) );

            // content title
            Element title = XMLTool.getElement( contentElem, "title" );
            XMLTool.removeChildNodes( title );
            XMLTool.createTextNode( doc, title, contentTitle );

            // content data
            contentdataElem = XMLTool.getElement( contentElem, "contentdata" );
            XMLTool.removeChildNodes( contentdataElem, true );
        }

        buildContentTypeXML( userServices, contentdataElem, formItems, skipEmptyElements );

        return XMLTool.documentToString( doc );
    }

    protected String cleanBody( String body )
    {

        StringBuffer newBody = new StringBuffer( body.length() );
        String delimiters = " .,:;!?/\\+-*=<>()[]{}";
        StringTokenizer tokenizer = new StringTokenizer( body, delimiters, true );

        while ( tokenizer.hasMoreTokens() )
        {
            String token = tokenizer.nextToken();
            if ( token.length() > WORD_BREAK_LIMIT )
            {
                newBody.append( token.substring( 0, WORD_BREAK_LIMIT - 1 ) );
                newBody.append( '-' );
                newBody.append( token.substring( WORD_BREAK_LIMIT - 1 ) );
            }
            else
            {
                newBody.append( token );
            }
        }

        return newBody.toString();
    }


    /**
     * Handle remove content.
     */
    protected void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalRemoveException, VerticalSecurityException, RemoteException
    {
        User user = securityService.getOldUserObject();
        UserEntity runningUser = securityService.getUser( user );

        ContentKey contentKey = new ContentKey( formItems.getInt( "key" ) );
        ContentEntity content = contentDao.findByKey( contentKey );

        if ( content != null && !content.isDeleted() )
        {
            // just to avoid any effects of the delete function, we find the content's locations before we delete it
            ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
            ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

            contentService.deleteContent( runningUser, content );

            for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
            {
                PageCacheService pageCacheService = siteCachesService.getPageCacheService( contentLocation.getSiteKey() );
                pageCacheService.removeEntriesByMenuItem( contentLocation.getMenuItemKey() );
            }
        }

        redirectToPage( request, response, formItems );
    }

    protected void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalUpdateException, VerticalSecurityException, RemoteException
    {
        User oldTypeUser = securityService.getOldUserObject();

        int contentKey = formItems.getInt( "key", -1 );

        if ( contentKey == -1 )
        {
            String message = "Content key not specified.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY, null );
            return;
        }

        BinaryData[] binaries = fetchUploadedFiles( formItems );

        List<BinaryDataKey> binariesToRemoveAsBinaryDataKey = null;
        List<BinaryDataAndBinary> binariesToAdd = BinaryDataAndBinary.createNewFrom( binaries );

        Document ctDoc = XMLTool.domparse( userServices.getContentTypeByContent( contentKey ) );
        String xmlData = buildContent( userServices, oldTypeUser, formItems, siteKey, ctDoc, false );

        boolean asNewVersion = true;
        updateContent( oldTypeUser, xmlData, binariesToRemoveAsBinaryDataKey, binariesToAdd, asNewVersion );

        redirectToPage( request, response, formItems );
    }

    protected ContentVersionKey updateContent( User oldTypeUser, String xmlData, List<BinaryDataKey> binariesToRemoveAsBinaryDataKey,
                                               List<BinaryDataAndBinary> binariesToAdd, boolean asNewVersion )
    {
        UserEntity runningUser = securityService.getUser( oldTypeUser );

        boolean parseContentData = true;
        ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersion( xmlData, null, parseContentData );
        ContentEntity parsedContent = parsedContentAndVersion.getContent();
        ContentVersionEntity parsedVersion = parsedContentAndVersion.getVersion();
        CategoryEntity parsedCategory = parsedContent.getCategory();

        ContentEntity persistedContent = contentDao.findByKey( parsedContent.getKey() );
        addExistingAccessRights( parsedContent, persistedContent );

        UpdateContentCommand updateContentCommand;

        if ( asNewVersion )
        {
            updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged( parsedContentAndVersion.getVersion().getKey() );
        }
        else
        {
            updateContentCommand = UpdateContentCommand.updateExistingVersion2(
                    parsedContentAndVersion.getVersion().getKey() );
        }

        updateContentCommand.setModifier( runningUser );
        updateContentCommand.setUpdateAsMainVersion( parsedCategory.getAutoMakeAvailableAsBoolean() );

        updateContentCommand.populateContentValuesFromContent( parsedContent );
        updateContentCommand.populateContentVersionValuesFromContentVersion( parsedVersion );

        updateContentCommand.setBinaryDataToAdd( binariesToAdd );
        updateContentCommand.setUseCommandsBinaryDataToAdd( true );

        updateContentCommand.setBinaryDataToRemove( binariesToRemoveAsBinaryDataKey );
        updateContentCommand.setUseCommandsBinaryDataToRemove( true );

        UpdateContentResult updateContentResult = contentService.updateContent( updateContentCommand );

        if ( updateContentResult.isAnyChangesMade() )
        {
            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
        }

        return updateContentResult.getTargetedVersionKey();
    }

    private BinaryData[] fetchUploadedFiles( ExtendedMap formItems )
    {
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

        return binaries;
    }

    private void addExistingAccessRights( ContentEntity parsedContent, ContentEntity persistedContent )
    {
        for ( ContentAccessEntity contentAccess : persistedContent.getContentAccessRights() )
        {
            parsedContent.addContentAccessRight( contentAccess.copy() );
        }
    }

    protected void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey )
        throws VerticalUserServicesException, VerticalSecurityException, RemoteException
    {
        User oldUser = securityService.getOldUserObject();

        int categoryKey = formItems.getInt( "categorykey", -1 );

        if ( categoryKey == -1 )
        {
            String message = "Category key not specified.";
            LOG.warn( StringUtil.expandString( message, null, null ) );
            redirectToErrorPage( request, response, formItems, ERR_MISSING_CATEGORY_KEY, null );
            return;
        }

        BinaryData[] binaries = fetchUploadedFiles( formItems );

        Document ctDoc = XMLTool.domparse( userServices.getContentTypeByCategory( categoryKey ) );
        String xmlData = buildContent( userServices, oldUser, formItems, siteKey, ctDoc, false );

        storeNewContent( oldUser, binaries, xmlData );

        redirectToPage( request, response, formItems );
    }

    protected ContentKey storeNewContent( User oldUser, BinaryData[] binaries, String xmlData )
    {
        UserEntity runningUser = securityService.getUser( oldUser );
        List<BinaryDataAndBinary> binaryDataAndBinaries = BinaryDataAndBinary.createNewFrom( binaries );

        boolean parseContentData = true; // always parse content data when creating content

        ContentAndVersion parsedContentAndVersion = contentParserService.parseContentAndVersion( xmlData, null, parseContentData );
        ContentEntity parsedContent = parsedContentAndVersion.getContent();
        ContentVersionEntity parsedVersion = parsedContentAndVersion.getVersion();

        // forcing owner and modifer to running user
        parsedContent.setOwner( runningUser );
        parsedVersion.setModifiedBy( runningUser );

        CreateContentCommand command = new CreateContentCommand();

        command.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );

        command.populateCommandWithContentValues( parsedContent );
        command.populateCommandWithContentVersionValues( parsedVersion );

        if ( StringUtils.isBlank( command.getContentName() ) )
        {
            command.setContentName( PrettyPathNameCreator.generatePrettyPathName(parsedVersion.getTitle()) );
        }

        command.setCreator( runningUser );
        command.setBinaryDatas( binaryDataAndBinaries );
        command.setUseCommandsBinaryDataToAdd( true );

        ContentKey newContentKey = contentService.createContent( command );
        return newContentKey;
    }

    private String buildContent( UserServicesService services, User user, ExtendedMap formItems, SiteKey siteKey, Document contentType,
                                 boolean skipEmptyElements )
        throws VerticalUserServicesException, RemoteException
    {

        Element rootElement = contentType.getDocumentElement();
        Element contentTypeElement = XMLTool.getElement( rootElement, "contenttype" );
        Element moduleDataElement = XMLTool.getElement( contentTypeElement, "moduledata" );
        Element moduleElement = XMLTool.getElement( moduleDataElement, "config" );
        formItems.put( "__module_element", moduleElement );

        // we want to keep a reference for later
        Element formElement = XMLTool.getElement( moduleElement, "form" );
        String titleName = XMLTool.getElement( formElement, "title" ).getAttribute( "name" );

        int contentTypeKey = Integer.parseInt( contentTypeElement.getAttribute( "key" ) );
        String contentTitle = formItems.getString( titleName );
        return buildXML( services, user, formItems, siteKey, contentTypeKey, contentTitle, skipEmptyElements );
    }

    private BinaryData createBinaryData( String fileName, InputStream inputStream )
        throws VerticalUserServicesException
    {
        BinaryData binaryData = new BinaryData();
        try
        {
            binaryData.fileName = fileName;
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            byte[] buf = new byte[1024 * 16];
            int size;

            while ( ( size = inputStream.read( buf ) ) > 0 )
            {
                bao.write( buf, 0, size );
            }
            binaryData.data = bao.toByteArray();
        }
        catch ( IOException ioe )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( "Failed to read binary data: %t", (Object) null,
                                                                     ioe ), ioe );
        }

        return binaryData;
    }

    protected BinaryData createBinaryData( FileItem fileItem )
        throws VerticalUserServicesException
    {
        String fileName = FileUtil.getFileName( fileItem );
        StringTokenizer nameTokenizer = new StringTokenizer( fileName, "\\/:" );
        while ( nameTokenizer.hasMoreTokens() )
        {
            fileName = nameTokenizer.nextToken();
        }
        try
        {
            return createBinaryData( fileName, fileItem.getInputStream() );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to read file item stream: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, ioe ), ioe );
        }
        return null;
    }

    private void createGroupBlock( ExtendedMap formItems, Document doc, Element contentdata, NodeList inputElements, String groupXPath,
                                   int groupCounter )
        throws ParseException
    {
        // get number of block instances
        int instances = 1;
        if ( isArrayFormItem( formItems, "group" + groupCounter + "_counter" ) )
        {
            instances = ( (String[]) formItems.get( "group" + groupCounter + "_counter" ) ).length;
        }

        Element[] blocks = new Element[instances];

        for ( int k = 0; k < instances; ++k )
        {
            // First, create the elements in the xpath:
            blocks[k] = createXPathElements( contentdata, groupXPath, 1 );
        }

        for ( int i = 0; i < inputElements.getLength(); ++i )
        {
            Element inputElem = (Element) inputElements.item( i );
            String name = inputElem.getAttribute( "name" );
            String xpath = XMLTool.getElementText( inputElem, "xpath" );
            String type = inputElem.getAttribute( "type" );

            if ( xpath != null )
            {
                // Then store the data.
                // Some types may need to be treated separatly.

                // date
                if ( type.equals( "date" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Date tempDate = dateFormatFrom.parse( values[j] );
                                XMLTool.createTextNode( doc, createXPathElements( blocks[j], xpath, 0 ), dateFormatTo.format( tempDate ) );
                            }
                        }
                    }
                    else if ( formItems.containsKey( name ) )
                    {
                        String tmp = formItems.getString( name );
                        if ( tmp.length() > 0 )
                        {
                            Date tempDate = dateFormatFrom.parse( tmp );
                            XMLTool.createTextNode( doc, createXPathElements( blocks[0], xpath, 0 ), dateFormatTo.format( tempDate ) );
                        }
                    }

                }

                // file
                else if ( type.equals( "file" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Element file = XMLTool.createElement( doc, createXPathElements( blocks[j], xpath, 0 ), "file" );
                                file.setAttribute( "key", values[j] );
                            }
                        }
                    }
                    else if ( formItems.containsKey( name ) )
                    {
                        Element file = XMLTool.createElement( doc, createXPathElements( blocks[0], xpath, 0 ), "file" );
                        file.setAttribute( "key", formItems.getString( name ) );
                    }
                }

                // image
                else if ( type.equals( "image" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );
                        String[] textValues = (String[]) formItems.get( name + "text" );

                        for ( int j = 0; j < instances; ++j )
                        {
                            Element tmpElem = createXPathElements( blocks[j], xpath, 0 );

                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                tmpElem.setAttribute( "key", values[j] );
                                if ( "true".equals( inputElem.getAttribute( "imagetext" ) ) )
                                {
                                    XMLTool.createElement( doc, tmpElem, "text", textValues[j] );
                                }
                            }
                        }
                    }
                    else
                    {
                        String value = formItems.getString( name, null );
                        String text = formItems.getString( name + "text", null );
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        if ( value != null && value.length() > 0 )
                        {
                            tmpElem.setAttribute( "key", value );
                            if ( "true".equals( inputElem.getAttribute( "imagetext" ) ) )
                            {
                                XMLTool.createElement( doc, tmpElem, "text", text );
                            }
                        }
                    }

                }

                // checkbox
                else if ( type.equals( "checkbox" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            Element tmpElem = createXPathElements( blocks[j], xpath, 0 );

                            if ( "true".equals( values[j] ) )
                            {
                                XMLTool.createTextNode( doc, tmpElem, "true" );
                            }
                            else
                            {
                                XMLTool.createTextNode( doc, tmpElem, "false" );
                            }
                        }
                    }
                    else
                    {
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        if ( "true".equals( formItems.getString( name, "false" ) ) )
                        {
                            XMLTool.createTextNode( doc, tmpElem, "true" );
                        }
                        else
                        {
                            XMLTool.createTextNode( doc, tmpElem, "false" );
                        }
                    }
                }

                // normal text
                else
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Element tmpElem = createXPathElements( blocks[j], xpath, 0 );

                                if ( type.equals( "htmlarea" ) || type.equals( "simplehtmlarea" ) )
                                {
                                    XMLTool.createXHTMLNodes( doc, tmpElem, values[j], true );
                                }
                                else
                                {
                                    XMLTool.createTextNode( doc, tmpElem, values[j] );
                                }
                            }
                        }
                    }
                    else
                    {
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        String value = formItems.getString( name, "false" );
                        if ( type.equals( "htmlarea" ) || type.equals( "simplehtmlarea" ) )
                        {
                            XMLTool.createXHTMLNodes( doc, tmpElem, value, true );
                        }
                        else
                        {
                            XMLTool.createTextNode( doc, tmpElem, value );
                        }
                    }
                }
            }
        }
    }


    private void createNormalBlock( ExtendedMap formItems, Document doc, Element contentdata, NodeList inputElements,
                                    boolean skipEmptyElements )
        throws ParseException
    {
        for ( int i = 0; i < inputElements.getLength(); ++i )
        {
            Element inputElem = (Element) inputElements.item( i );
            String name = inputElem.getAttribute( "name" );
            String xpath = XMLTool.getElementText( inputElem, "xpath" );
            String type = inputElem.getAttribute( "type" );

            if ( xpath != null )
            {
                // First, create the elements in the xpath:
                Element tmpElem = createXPathElements( contentdata, xpath, 1 );

                // Then store the data.
                // Some types may need to be treated separatly.

                // date
                if ( type.equals( "date" ) )
                {
                    if ( formItems.containsKey( name ) )
                    {
                        String date = formItems.getString( name );
                        if ( date.length() > 0 )
                        {
                            Date tempDate = dateFormatFrom.parse( date );
                            XMLTool.createTextNode( doc, tmpElem, dateFormatTo.format( tempDate ) );
                        }
                    }
                }

                // images
                else if ( type.equals( "images" ) )
                {
                    if ( isArrayFormItem( formItems, name ) )
                    {
                        String[] images = (String[]) formItems.get( name );
                        String[] text = (String[]) formItems.get( name + "text" );
                        for ( int k = 0; k < images.length; k++ )
                        {
                            if ( images[k] == null || images[k].length() == 0 )
                            {
                                continue;
                            }
                            Element image = XMLTool.createElement( doc, tmpElem, "image" );
                            image.setAttribute( "key", images[k] );
                            XMLTool.createElement( doc, image, "text", text[k] );
                        }
                    }
                }

                // image
                else if ( type.equals( "image" ) )
                {
                    String image = formItems.getString( name, "" );
                    String text = formItems.getString( name + "text", null );
                    tmpElem.setAttribute( "key", image );
                    if ( "true".equals( inputElem.getAttribute( "imagetext" ) ) )
                    {
                        XMLTool.createElement( doc, tmpElem, "text", text );
                    }
                }

                // related content
                else if ( type.equals( "relatedcontent" ) )
                {
                    if ( isArrayFormItem( formItems, name ) )
                    {
                        String[] content = (String[]) formItems.get( name );
                        for ( String aContent : content )
                        {
                            if ( aContent == null || aContent.length() == 0 )
                            {
                                continue;
                            }
                            Element contentElem = XMLTool.createElement( doc, tmpElem, "content" );
                            contentElem.setAttribute( "key", aContent );
                        }
                    }
                    else if ( formItems.containsKey( name ) )
                    {
                        if ( !"false".equals( inputElem.getAttribute( "multiple" ) ) )
                        {
                            String content = formItems.getString( name );
                            Element contentElem = XMLTool.createElement( doc, tmpElem, "content" );
                            contentElem.setAttribute( "key", content );
                        }
                        else
                        {
                            String content = formItems.getString( name );
                            tmpElem.setAttribute( "key", content );
                        }
                    }

                }

                // files
                else if ( type.equals( "files" ) )
                {
                    if ( isArrayFormItem( formItems, name ) )
                    {
                        //logCategory.debug("multiple files");
                        String[] files = (String[]) formItems.get( name );
                        for ( String file1 : files )
                        {
                            if ( file1 != null && file1.length() > 0 )
                            {
                                Element file = XMLTool.createElement( doc, tmpElem, "file" );
                                file.setAttribute( "key", file1 );
                            }
                        }
                    }
                    else if ( formItems.containsKey( name ) )
                    {
                        //logCategory.debug("single file");
                        Element file = XMLTool.createElement( doc, tmpElem, "file" );
                        file.setAttribute( "key", formItems.getString( name ) );
                    }
                }

                // file
                else if ( type.equals( "file" ) )
                {
                    //logCategory.debug("single file");
                    if ( formItems.containsKey( name ) )
                    {
                        Element file = XMLTool.createElement( doc, tmpElem, "file" );
                        file.setAttribute( "key", formItems.getString( name ) );
                    }
                }

                // uploaded binary file
                else if ( type.equals( "uploadfile" ) )
                {
                    if ( formItems.containsKey( name ) )
                    {
                        FileItem fileItem = formItems.getFileItem( name );
                        String fileName = FileUtil.getFileName( fileItem );
                        Element binaryElement = XMLTool.createElement( doc, tmpElem, "binarydata" );
                        binaryElement.setAttribute( "key", "%0" );
                        tmpElem.setAttribute( "filename", fileName );
                    }
                    else if ( formItems.containsKey( name + "_key" ) )
                    {
                        String keyStr = formItems.getString( name + "_key" );
                        if ( keyStr != null && keyStr.length() > 0 )
                        {
                            Element binaryElement = XMLTool.createElement( doc, tmpElem, "binarydata" );
                            binaryElement.setAttribute( "key", keyStr );
                        }
                    }
                }

                // checkbox
                else if ( type.equals( "checkbox" ) )
                {
                    String value = formItems.getString( name, skipEmptyElements ? null : "false" );
                    if ( value != null )
                    {
                        if ( "true".equals( value ) )
                        {
                            XMLTool.createTextNode( doc, tmpElem, "true" );
                        }
                        else
                        {
                            XMLTool.createTextNode( doc, tmpElem, "false" );
                        }
                    }
                }

                // normal text
                else
                {
                    String value = formItems.getString( name, skipEmptyElements ? null : "" );

                    if ( value != null )
                    {
                        if ( type.equals( "htmlarea" ) || type.equals( "simplehtmlarea" ) )
                        {
                            XMLTool.createXHTMLNodes( doc, tmpElem, value, true );
                        }
                        else
                        {
                            XMLTool.createTextNode( doc, tmpElem, value );
                        }
                    }
                }
            }
        }
    }

    private Element createXPathElements( Element parentElement, String xpath, int startIdx )
    {
        Document doc = parentElement.getOwnerDocument();

        // First, create the elements in the xpath:
        String[] xpathSplit = StringUtil.splitString( xpath, '/' );
        Element tmpElem = null;

        for ( int j = startIdx; j < xpathSplit.length; ++j )
        {
            if ( tmpElem == null )
            {
                if ( j != ( xpathSplit.length - 1 ) && XMLTool.getElement( parentElement, xpathSplit[j] ) != null )
                {
                    tmpElem = XMLTool.getElement( parentElement, xpathSplit[j] );
                }
                else
                {
                    tmpElem = XMLTool.createElement( doc, parentElement, xpathSplit[j] );
                }
            }
            else
            {
                if ( j != ( xpathSplit.length - 1 ) && XMLTool.getElement( tmpElem, xpathSplit[j] ) != null )
                {
                    tmpElem = XMLTool.getElement( tmpElem, xpathSplit[j] );
                }
                else
                {
                    tmpElem = XMLTool.createElement( doc, tmpElem, xpathSplit[j] );
                }
            }
        }

        return tmpElem;
    }

    protected SimpleDateFormat dateFormatTo = new SimpleDateFormat( "yyyy-MM-dd" );
}