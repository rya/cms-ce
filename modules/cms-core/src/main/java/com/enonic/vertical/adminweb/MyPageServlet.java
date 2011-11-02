/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentSpecification;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryXmlCreator;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.log.LogEntryEntity;
import com.enonic.cms.core.log.LogEntryResultSet;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;

import com.enonic.cms.core.structure.SectionXmlCreator;
import com.enonic.cms.core.log.ContentLogXMLCreator;

import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;

import com.enonic.cms.core.content.contenttype.ContentTypeXmlCreator;
import com.enonic.cms.core.content.index.ContentIndexQuery.CategoryAccessTypeFilterPolicy;
import com.enonic.cms.core.content.index.ContentIndexQuery.SectionFilterStatus;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.log.ContentLogEntrySpecification;

public class MyPageServlet
    extends AdminHandlerBaseServlet
{
    private static final int ASSIGNED_TO_COUNT = 6;

    public void handlerPage( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                             ExtendedMap parameters, User oldUser, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        Map<Integer, String> contentTypeKeyHandlerMapping = new HashMap<Integer, String>();

        UserEntity user = securityService.getUser( oldUser );
        String maximize = formItems.getString( "maximize", null );

        // ----
        // get last modified
        // ----
        if ( maximize == null || "lastmodified".equals( maximize ) )
        {
            handleRecentItems( verticalDoc, contentTypeKeyHandlerMapping, formItems, user );
        }

        // ----
        // Get content assigned to current user
        // ----
        if ( maximize == null || "assignedto".equals( maximize ) )
        {
            handleAssignedToMe( verticalDoc, contentTypeKeyHandlerMapping, formItems, user );
        }

        // ----
        // Get content waiting for activation
        // ---

        if ( maximize == null || "activation".equals( maximize ) )
        {

            handleActivation( verticalDoc, contentTypeKeyHandlerMapping, formItems, user );

        }

        // Default browse config
        Document defaultBrowseConfig = AdminStore.getXml( session, "defaultbrowseconfig.xml" ).getAsDOMDocument();
        XMLTool.mergeDocuments( verticalDoc, defaultBrowseConfig, true );

        Document contentTypeKeyHandlerMappingDoc = createContentTypeKeyHandlerMappingXml( contentTypeKeyHandlerMapping );
        XMLTool.mergeDocuments( verticalDoc, contentTypeKeyHandlerMappingDoc, true );

        // Feedback
        addFeedback( verticalDoc, formItems );

        addParameters( formItems, parameters, maximize );

        transformXML( request, response, verticalDoc, "dashboard.xsl", parameters );
    }

    private void addParameters( ExtendedMap formItems, ExtendedMap parameters, String maximize )
    {
        int index = formItems.getInt( "index", 0 );
        int count = formItems.getInt( "count", maximize != null ? 20 : 3 );

        parameters.put( "index", index );
        parameters.put( "count", count );
        parameters.put( "maximize", maximize );
    }


    private void handleAssignedToMe( Document verticalDoc, Map<Integer, String> contentTypeKeyHandlerMapping, final ExtendedMap formItems,
                                     UserEntity user )
    {
        String maximize = formItems.getString( "maximize", null );
        int index = formItems.getInt( "index", 0 );
        int count = formItems.getInt( "count", maximize != null ? 20 : ASSIGNED_TO_COUNT );

        ContentSpecification contentSpecification = new ContentSpecification();
        //contentSpecification.setUser( user );
        contentSpecification.setAssignee( user );
        contentSpecification.setAssignedDraftsOnly( false );

        ContentResultSet contentResultSet =
            contentService.getContent( contentSpecification, "c.assignmentDueDate ASC, c.timestamp DESC", count, index );

        for ( ContentEntity content : contentResultSet.getContents() )
        {
            contentTypeKeyHandlerMapping.put( content.getContentType().getKey(),
                                              content.getContentType().getContentHandlerName().getHandlerClassShortName() );
        }

        ContentXMLCreator contentXMLCreator = new ContentXMLCreator();
        contentXMLCreator.setResultIndexing( index, count );
        contentXMLCreator.setIncludeOwnerAndModifierData( true );
        contentXMLCreator.setIncludeContentData( true );
        contentXMLCreator.setIncludeCategoryData( true );
        contentXMLCreator.setIncludeAccessRightsInfo( false );
        contentXMLCreator.setIncludeRelatedContentsInfo( false );
        contentXMLCreator.setIncludeRepositoryPathInfo( true );
        contentXMLCreator.setIncludeVersionsInfoForAdmin( true );
        contentXMLCreator.setIncludeAssignment( true );
        contentXMLCreator.setIncludeDraftInfo( true );
        contentXMLCreator.setIncludeSectionActivationInfo( true );
        XMLDocument xmlDocument =
            contentXMLCreator.createContentVersionsDocument( user, contentResultSet, new RelatedContentResultSetImpl() );

        Document doc = xmlDocument.getAsDOMDocument();
        doc.getDocumentElement().setAttribute( "type", "assignedto" );

        XMLTool.mergeDocuments( verticalDoc, doc, true );
    }

    private void handleActivation( Document verticalDoc, Map<Integer, String> contentTypeKeyHandlerMapping, final ExtendedMap formItems,
                                   UserEntity user )
    {

        String maximize = formItems.getString( "maximize", null );
        int index = formItems.getInt( "index", 0 );
        int count = formItems.getInt( "count", maximize != null ? 20 : 3 );

        ContentBySectionQuery contentBySectionQuery = new ContentBySectionQuery();
        contentBySectionQuery.setSectionFilterStatus( SectionFilterStatus.UNAPPROVED_ONLY );
        contentBySectionQuery.setSearchInAllSections();
        contentBySectionQuery.setCount( count );
        contentBySectionQuery.setUser( user );
        contentBySectionQuery.setOrderBy( "timestamp DESC" );
        contentBySectionQuery.setIndex( index );
        contentBySectionQuery.setFilterIncludeOfflineContent();
        contentBySectionQuery.setLevels( Integer.MAX_VALUE );

        List<CategoryAccessType> categoryAccessTypeFilter = new ArrayList<CategoryAccessType>();
        categoryAccessTypeFilter.add( CategoryAccessType.ADMINISTRATE );
        categoryAccessTypeFilter.add( CategoryAccessType.APPROVE );
        contentBySectionQuery.setCategoryAccessTypeFilter( categoryAccessTypeFilter, CategoryAccessTypeFilterPolicy.OR );

        ContentResultSet contentResultSet = contentService.queryContent( contentBySectionQuery );

        //build contentTypeKey and handlerName mapping
        for ( ContentEntity entity : contentResultSet.getContents() )
        {
            contentTypeKeyHandlerMapping.put( entity.getContentType().getKey(),
                                              entity.getContentType().getContentHandlerName().getHandlerClassShortName() );
        }

        SectionXmlCreator sectionXmlCreator =
            new SectionXmlCreator( siteDao, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
        XMLDocument sectionDocument = sectionXmlCreator.createSectionsDocument( user, contentResultSet );

        Document waitingForActivationDoc = sectionDocument.getAsDOMDocument();
        // waitingForActivationDoc.getDocumentElement().setAttribute("type", "activation");

        XMLTool.mergeDocuments( verticalDoc, waitingForActivationDoc, true );
    }

    private void handleRecentItems( Document verticalDoc, Map<Integer, String> contentTypeKeyHandlerMapping, final ExtendedMap formItems,
                                    UserEntity user )
    {

        String maximize = formItems.getString( "maximize", null );
        int index = formItems.getInt( "index", 0 );
        int count = formItems.getInt( "count", maximize != null ? 20 : 10 );

        ContentLogEntrySpecification logSpecification = new ContentLogEntrySpecification();
        logSpecification.setUser( user );
        logSpecification.setTypes( new LogType[]{LogType.ENTITY_OPENED, LogType.ENTITY_CREATED, LogType.ENTITY_UPDATED} );
        logSpecification.setTableTypes( new Table[]{Table.CONTENT} );
        logSpecification.setAllowDuplicateEntries( false );
        logSpecification.setAllowDeletedContent( false );

        Calendar now = GregorianCalendar.getInstance();
        final int monthsInPast = 3;
        now.add( Calendar.MONTH, -monthsInPast );
        logSpecification.setDateFilter( now.getTime() );

        LogEntryResultSet logResult = logService.getLogEntries( logSpecification, "timestamp DESC", count, index );

        if ( logResult.getLength() > 0 )
        {

            //build contentTypeKey and handler mapping
            List<ContentKey> contentKeys = new ArrayList<ContentKey>();
            for ( LogEntryEntity entity : logResult.getLogEntries() )
            {
                contentKeys.add( new ContentKey( entity.getKeyValue() ) );
            }

            ContentByContentQuery contentByContentQuery = new ContentByContentQuery();
            contentByContentQuery.setContentKeyFilter( contentKeys );
            contentByContentQuery.setUser( user );
            contentByContentQuery.setIndex( 0 );
            contentByContentQuery.setCount( logResult.getLength() );
            ContentResultSet contentResultSet = contentService.queryContent( contentByContentQuery );
            for ( ContentEntity entity : contentResultSet.getContents() )
            {
                contentTypeKeyHandlerMapping.put( entity.getContentType().getKey(),
                                                  entity.getContentType().getContentHandlerName().getHandlerClassShortName() );
            }
        }

        ContentLogXMLCreator logXMLCreator = new ContentLogXMLCreator();
        logXMLCreator.setIncludeContentData( true );
        logXMLCreator.setContentDao( contentDao );
        XMLDocument xmlDocument = logXMLCreator.createLogsDocument( logResult );

        Document lastModifiedDoc = xmlDocument.getAsDOMDocument();

        lastModifiedDoc.getDocumentElement().setAttribute( "type", "lastmodified" );
        XMLTool.mergeDocuments( verticalDoc, lastModifiedDoc, true );
    }

    private Document createContentTypeKeyHandlerMappingXml( Map<Integer, String> contentTypeKeyHandlerMapping )
    {

        org.jdom.Element rootElement = new org.jdom.Element( "contenthandlers" );

        for ( Integer key : contentTypeKeyHandlerMapping.keySet() )
        {
            org.jdom.Element contentHandlerEl = new org.jdom.Element( "contenthandler" );
            contentHandlerEl.setAttribute( "contenttypekey", Integer.toString( key ) );
            contentHandlerEl.setText( contentTypeKeyHandlerMapping.get( key ) );
            rootElement.addContent( contentHandlerEl );
        }

        org.jdom.Document doc = new org.jdom.Document( rootElement );
        return XMLDocumentFactory.create( doc ).getAsDOMDocument();

    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( "page".equals( operation ) )
        {
            handlerPage( request, response, session, formItems, parameters, user, verticalDoc );
        }
        else if ( "createcontentwizard_step1".equals( operation ) )
        {
            handlerCreateContentWizardStep1( request, response, formItems, parameters, user );
        }
        else if ( "createcontentwizard_step2".equals( operation ) )
        {
            handlerCreateContentWizardStep2( request, response, formItems, parameters, user );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    @SuppressWarnings("unchecked")
    private void handlerCreateContentWizardStep1( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems,
                                                  ExtendedMap parameters, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        final UserEntity runningUser = securityService.getUser( user );
        List<ContentTypeEntity> filteredContentTypes = new ArrayList<ContentTypeEntity>();

        for ( ContentTypeEntity contentType : contentTypeDao.getAll() )
        {
            if ( userHasCreateAccessOnCategoriesOfContentType( runningUser, contentType ) )
            {
                filteredContentTypes.add( contentType );
            }
        }

        ContentTypeXmlCreator xmlCreator = new ContentTypeXmlCreator();
        XMLDocument doc = xmlCreator.createContentTypesDocument( filteredContentTypes );

        parameters.put( "step", "1" );
        parameters.put( "source", formItems.getString( "source", "mypage" ) );
        parameters.put( "subop", formItems.getString( "subop", "" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
        transformXML( request, response, doc.getAsJDOMDocument(), "createcontentwizard.xsl", parameters );
    }

    private boolean userHasCreateAccessOnCategoriesOfContentType( UserEntity runningUser, ContentTypeEntity contentType )
    {
        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        for ( CategoryEntity category : contentType.getCategories( false ) )
        {
            if ( categoryAccessResolver.hasAccess( runningUser, category, CategoryAccessType.CREATE ) )
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void handlerCreateContentWizardStep2( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems,
                                                  ExtendedMap parameters, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        final UserEntity runningUser = securityService.getUser( user );

        ContentTypeKey contentTypeKey = new ContentTypeKey( formItems.getInt( "contenttypekey" ) );
        CategoryKey topCategoryKey = CategoryKey.parse( formItems.getInt( "topcategorykey", -1 ) );

        CategoryXmlCreator xmlCreator = new CategoryXmlCreator();
        xmlCreator.setCategoryAccessResolver( new CategoryAccessResolver( groupDao ) );
        xmlCreator.setUser( runningUser );
        xmlCreator.setAnonymousUser( securityService.getUser( securityService.getAnonymousUserKey() ) );
        xmlCreator.setAllowedContentType( contentTypeKey );
        xmlCreator.setIncludeOwnerAndModiferInfo( false );
        xmlCreator.setIncludeCreatedAndTimestampInfo( false );
        xmlCreator.setIncludeAutoApproveInfo( false );
        xmlCreator.setIncludeDescriptionInfo( false );
        xmlCreator.setIncludeSuperCategoryKeyInfo( false );
        xmlCreator.setRootAccess( false );

        List<CategoryEntity> rootCategories;
        if ( runningUser.isEnterpriseAdmin() )
        {
            rootCategories = categoryDao.findRootCategories();
        }
        else
        {
            rootCategories = categoryDao.findRootCategories( runningUser.getAllMembershipsGroupKeys() );
        }
        final XMLDocument doc = xmlCreator.createCategoryBranch( rootCategories, topCategoryKey );

        parameters.put( "step", "2" );
        parameters.put( "contenttypekey", contentTypeKey );
        parameters.put( "source", formItems.getString( "source" ) );
        parameters.put( "subop", formItems.getString( "subop", "" ) );
        parameters.put( "fieldrow", formItems.getString( "fieldrow", "" ) );
        parameters.put( "fieldname", formItems.getString( "fieldname", "" ) );
        transformXML( request, response, doc.getAsJDOMDocument(), "createcontentwizard.xsl", parameters );
    }
}