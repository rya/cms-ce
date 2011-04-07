/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.filters.Filter;
import com.enonic.vertical.engine.handlers.BinaryDataHandler;
import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.ContentHandler;
import com.enonic.vertical.engine.handlers.ContentObjectHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;
import com.enonic.vertical.engine.handlers.LanguageHandler;
import com.enonic.vertical.engine.handlers.LogHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.PageHandler;
import com.enonic.vertical.engine.handlers.PageTemplateHandler;
import com.enonic.vertical.engine.handlers.SectionHandler;
import com.enonic.vertical.engine.handlers.SecurityHandler;
import com.enonic.vertical.engine.handlers.SystemHandler;
import com.enonic.vertical.engine.handlers.UnitHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.core.content.RegenerateIndexBatcher;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.ContentService;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.query.ContentByCategoryQuery;
import com.enonic.cms.domain.content.query.RelatedContentQuery;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;

public final class AdminEngine
    extends BaseEngine
    implements InitializingBean
{

    private SiteDao siteDao;

    @Autowired
    private UserDao userDao;

    private BinaryDataHandler binaryDataHandler;

    private CategoryHandler categoryHandler;

    private CommonHandler commonHandler;

    private ContentHandler contentHandler;

    private ContentService contentService;

    private ContentObjectHandler contentObjectHandler;

    private GroupHandler groupHandler;

    @Autowired
    private MemberOfResolver memberOfResolver;

    private IndexService indexService;

    private LanguageHandler languageHandler;


    private LogHandler logHandler;

    private MenuHandler menuHandler;

    private PageHandler pageHandler;

    private PageTemplateHandler pageTemplateHandler;

    private SectionHandler sectionHandler;

    private SecurityHandler securityHandler;

    private SecurityService securityService;

    private SystemHandler systemHandler;

    private UnitHandler unitHandler;

    private UserHandler userHandler;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private GroupDao groupDao;

    public void afterPropertiesSet()
        throws Exception
    {
        init();
    }

    private void init()
    {
        // event listeners
        contentHandler.addListener( logHandler );
        contentHandler.addListener( sectionHandler );
        menuHandler.addListener( logHandler );
    }

    public CategoryHandler getCategoryHandler()
    {
        return categoryHandler;
    }

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public ContentObjectHandler getContentObjectHandler()
    {
        return contentObjectHandler;
    }

    public GroupHandler getGroupHandler()
    {
        return groupHandler;
    }

    public LanguageHandler getLanguageHandler()
    {
        return languageHandler;
    }

    public LogHandler getLogHandler()
    {
        return logHandler;
    }

    public MenuHandler getMenuHandler()
    {
        return menuHandler;
    }

    public PageHandler getPageHandler()
    {
        return pageHandler;
    }

    public PageTemplateHandler getPageTemplateHandler()
    {
        return pageTemplateHandler;
    }

    public SectionHandler getSectionHandler()
    {
        return sectionHandler;
    }

    public SecurityHandler getSecurityHandler()
    {
        return securityHandler;
    }

    public UserHandler getUserHandler()
    {
        return userHandler;
    }

    public String getPageTemplates( PageTemplateType type )
    {
        Document doc = pageTemplateHandler.getPageTemplates( type );
        return XMLTool.documentToString( doc );
    }

    public void copyMenu( User user, int menuKey, boolean includeContent )
        throws VerticalCopyException, VerticalSecurityException
    {

        if ( !user.isEnterpriseAdmin() )
        {
            String enterpriseGroupKey = groupHandler.getEnterpriseAdministratorGroupKey();
            String[] groupKeys = groupHandler.getAllGroupMembershipsForUser( user );
            Arrays.sort( groupKeys );
            if ( Arrays.binarySearch( groupKeys, enterpriseGroupKey ) < 0 )
            {
                String message = "User does not have rights to copy menu.";
                VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
            }
        }

        menuHandler.copyMenu( user, menuKey, includeContent );
    }


    public boolean contentExists( int categoryKey, String contentTitle )
    {
        return contentHandler.contentExists( CategoryKey.parse( categoryKey ), contentTitle );
    }

    public int getContentKey( int categoryKey, String contentTitle )
    {
        return contentHandler.getContentKey( CategoryKey.parse( categoryKey ), contentTitle );
    }

    public String getContentCreatedTimestamp( int contentKey )
    {
        return contentHandler.getCreatedTimestamp( contentKey );
    }

    public Date getContentPublishFromTimestamp( int contentKey )
    {
        return contentHandler.getPublishFromTimestamp( contentKey );
    }

    public Date getContentPublishToTimestamp( int contentKey )
    {
        return contentHandler.getPublishToTimestamp( contentKey );
    }

    public int getCategoryKey( int superCategoryKey, String name )
    {
        return categoryHandler.getCategoryKey( superCategoryKey, name );
    }

    public int createCategory( User user, int superCategoryKey, String name )
        throws VerticalSecurityException
    {
        return categoryHandler.createCategory( user, CategoryKey.parse( superCategoryKey ), name );
    }


    public int createCategory( User user, String xmlData )
        throws VerticalSecurityException
    {

        Document doc = XMLTool.domparse( xmlData, "category" );
        Element categoryElem = doc.getDocumentElement();
        if ( !isEnterpriseAdmin( user ) )
        {
            CategoryKey superCategoryKey = CategoryKey.parse( categoryElem.getAttribute( "supercategorykey" ) );

            if ( !securityHandler.validateCategoryCreate( user, superCategoryKey ) )
            {
                String message = "User does not have access rights to create a new category";
                VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
            }
        }

        return categoryHandler.createCategory( user, doc );
    }

    public int createContentObject( String xmlData )
        throws VerticalSecurityException
    {
        return contentObjectHandler.createContentObject( xmlData );
    }

    public int createContentType( User user, String xmlData )
        throws VerticalSecurityException
    {

        Document doc = XMLTool.domparse( xmlData, "contenttype" );

        if ( !( securityHandler.isSiteAdmin( user ) || isDeveloper( user ) ) )
        {
            String message = "User is not administrator or developer";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        return contentHandler.createContentType( user, doc );
    }

    public void createLanguage( User user, String languageCode, String description )
        throws VerticalSecurityException
    {

        if ( !isEnterpriseAdmin( user ) )
        {
            String message = "User is not enterprise administrator";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        languageHandler.createLanguage( languageCode, description );
    }

    public String[] createLogEntries( User user, String xmlData )
        throws VerticalSecurityException
    {
        String[] rootNames = {"logentry", "logentries"};
        Document doc = XMLTool.domparse( xmlData, rootNames );
        return logHandler.createLogEntries( user, doc );
    }

    public int createMenu( User user, String xmlData )
        throws VerticalSecurityException
    {
        return menuHandler.createMenu( user, xmlData );
    }

    public void updateMenuItem( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {
        menuHandler.updateMenuItem( user, xmlData );
    }

    public void removeMenuItem( User user, int mikey )
        throws VerticalRemoveException, VerticalSecurityException
    {
        menuHandler.removeMenuItem( user, mikey );
    }

    public int createMenuItem( User user, String xmlData )
        throws VerticalSecurityException
    {
        return menuHandler.createMenuItem( user, xmlData );
    }

    public int createPageTemplate( String xmlData )
        throws VerticalSecurityException
    {
        return pageTemplateHandler.createPageTemplate( xmlData );
    }

    public int copyPageTemplate( User user, int pageTemplateKey )
        throws VerticalCopyException
    {
        return pageTemplateHandler.copyPageTemplate( user, new PageTemplateKey( pageTemplateKey ) );
    }

    public int createUnit( String xmlData )
        throws VerticalSecurityException
    {
        return unitHandler.createUnit( xmlData );
    }

    public String generateUID( String fName, String sName, UserStoreKey userStoreKey )
    {
        return userHandler.generateUID( fName, sName, userStoreKey );
    }

    public BinaryData getBinaryData( int binaryDataKey )
    {
        return binaryDataHandler.getBinaryData( binaryDataKey );
    }

    public String getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel )
    {
        Document doc =
            contentHandler.getContent( user, contentKey, false, parentLevel, childrenLevel, parentChildrenLevel, false, true, null );
        securityHandler.appendAccessRights( user, doc, true, true );
        return XMLTool.documentToString( doc );
    }

    public String getCategoryName( int categoryKey )
    {

        return categoryHandler.getCategoryName( CategoryKey.parse( categoryKey ) );
    }

    public String getCategoryNameXML( int categoryKey )
    {

        return XMLTool.documentToString( categoryHandler.getCategoryNameDoc( CategoryKey.parse( categoryKey ) ) );
    }

    public String getCategory( User user, int categoryKey )
    {
        Document doc = categoryHandler.getCategory( user, CategoryKey.parse( categoryKey ) );
        boolean hasSubs = categoryHandler.hasSubCategories( CategoryKey.parse( categoryKey ) );
        Element categoryElem = XMLTool.getElement( doc.getDocumentElement(), "category" );
        if ( categoryElem != null )
        {
            categoryElem.setAttribute( "subcategories", String.valueOf( hasSubs ) );
        }
        securityHandler.appendAccessRights( user, doc, true, true );

        return XMLTool.documentToString( doc );
    }

    public MenuItemAccessRight getMenuItemAccessRight( User user, MenuItemKey key )
    {
        return securityHandler.getMenuItemAccessRight( user, key );
    }

    public MenuAccessRight getMenuAccessRight( User user, int key )
    {
        return securityHandler.getMenuAccessRight( user, key );
    }

    public CategoryAccessRight getCategoryAccessRight( User user, int key )
    {
        return securityHandler.getCategoryAccessRight( user, CategoryKey.parse( key ) );
    }

    public ContentAccessRight getContentAccessRight( User user, int key )
    {
        return securityHandler.getContentAccessRight( user, key );
    }

    public String getMenuItem( User user, int key, boolean withParents )
    {
        Document doc = menuHandler.getMenuItem( user, key, withParents );
        securityHandler.appendAccessRights( user, doc, true, true );
        return XMLTool.documentToString( doc );
    }

    public String getMenuItem( User user, int key, boolean withParents, boolean complete )
    {
        Document doc = menuHandler.getMenuItem( user, key, withParents, complete, true );
        securityHandler.appendAccessRights( user, doc, true, true );
        return XMLTool.documentToString( doc );
    }

    public int getCategoryKey( int contentKey )
    {
        CategoryKey categoryKey = contentHandler.getCategoryKey( contentKey );
        if ( categoryKey == null )
        {
            return -1;
        }
        return categoryKey.toInt();
    }

    public int getSuperCategoryKey( int categoryKey )
    {
        CategoryKey parentCategoryKey = categoryHandler.getParentCategoryKey( CategoryKey.parse( categoryKey ) );
        if ( parentCategoryKey == null )
        {
            return -1;
        }
        return parentCategoryKey.toInt();
    }

    public String getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory )
    {

        Document doc = categoryHandler.getSuperCategoryNames( CategoryKey.parse( categoryKey ), withContentCount, includeCategory );
        return XMLTool.documentToString( doc );
    }

    public int getContentCount( int categoryKey, boolean recursive )
    {

        return categoryHandler.getContentCount( null, CategoryKey.parse( categoryKey ), recursive );
    }

    public String getContentObject( int contentObjectKey )
    {

        return contentObjectHandler.getContentObject( contentObjectKey ).getAsString();
    }

    public User getContentObjectRunAs( int contentObjectKey )
    {
        return this.contentObjectHandler.getContentObjectRunAs( contentObjectKey );
    }

    public String getContentObjectsByMenu( int menuKey )
    {
        return XMLTool.documentToString( contentObjectHandler.getContentObjectsByMenu( menuKey ) );
    }

    public String getContentTitle( int versionKey )
    {
        return contentHandler.getContentTitle( versionKey );
    }

    public String getContentType( int contentTypeKey )
    {
        Document doc = contentHandler.getContentType( contentTypeKey, false );
        return XMLTool.documentToString( doc );
    }

    public String getContentType( int contentTypeKey, boolean includeContentCount )
    {
        Document doc = contentHandler.getContentType( contentTypeKey, includeContentCount );
        return XMLTool.documentToString( doc );
    }

    public int getContentTypeKey( int contentKey )
    {
        return contentHandler.getContentTypeKey( contentKey );
    }

    public int[] getContentTypeKeysByHandler( String handlerClass )
    {
        return contentHandler.getContentTypeKeysByHandler( handlerClass );
    }

    public int getContentTypeKeyByCategory( int categoryKey )
    {
        return categoryHandler.getContentTypeKey( CategoryKey.parse( categoryKey ) );
    }

    public String getContentTypeName( int contentTypeKey )
    {
        return contentHandler.getContentTypeName( contentTypeKey );
    }

    public String getContentTypeModuleData( int contentTypeKey )
    {
        return XMLTool.documentToString( contentHandler.getContentTypeModuleData( contentTypeKey ) );
    }

    public XMLDocument getLanguage( LanguageKey languageKey )
    {
        return languageHandler.getLanguage( languageKey );
    }

    public XMLDocument getLanguages()
    {
        return languageHandler.getLanguages();
    }

    public String getMenu( User user, int menuKey, boolean complete )
    {
        Document doc = menuHandler.getMenu( user, menuKey, complete, true );
        securityHandler.appendAccessRights( user, doc, true, true );
        return XMLTool.documentToString( doc );
    }

    public String getMenuName( int menuKey )
    {
        return menuHandler.getMenuName( menuKey );
    }

    public String getMenuItemName( int menuItemKey )
    {
        return menuHandler.getMenuItemName( menuItemKey );
    }

    public String getPageTemplate( PageTemplateKey pageTemplateKey )
    {
        return pageTemplateHandler.getPageTemplate( pageTemplateKey ).getAsString();
    }

    public String getPageTemplatesByMenu( int menuKey, int[] excludeTypeKeys )
    {
        try
        {
            openSharedConnection();
            return doGetPageTemplatesByMenu( menuKey, excludeTypeKeys );
        }
        finally
        {
            closeSharedConnection();
        }
    }

    private String doGetPageTemplatesByMenu( int menuKey, int[] excludeTypeKeys )
    {
        Document doc = pageTemplateHandler.getPageTemplatesByMenu( menuKey, excludeTypeKeys );
        return XMLTool.documentToString( doc );
    }

    public String getPageTemplatesByContentObject( int contentObjectKey )
    {
        Document doc = pageTemplateHandler.getPageTemplatesByContentObject( contentObjectKey );
        return XMLTool.documentToString( doc );
    }

    public String getPageTemplParams( int pageTemplateKey )
    {
        return pageTemplateHandler.getPageTemplParams( pageTemplateKey );
    }

    public int getUnitLanguageKey( int unitKey )
    {
        return unitHandler.getUnitLanguageKey( unitKey );
    }

    public String getUnit( int unitKey )
    {
        return unitHandler.getUnit( unitKey );
    }

    public String getUnitName( int unitKey )
    {
        return unitHandler.getUnitName( unitKey );
    }

    public int getUnitKey( int categoryKey )
    {
        return categoryHandler.getUnitKey( CategoryKey.parse( categoryKey ) );
    }

    public String getUnitNamesXML( Filter filter )
    {
        return unitHandler.getUnitNamesXML( filter );
    }

    public String getUnits()
    {
        return unitHandler.getUnits();
    }

    public boolean hasContent( int categoryKey )
    {
        return categoryHandler.hasContent( null, CategoryKey.parse( categoryKey ) );
    }

    public boolean hasSubCategories( int categoryKey )
    {
        return categoryHandler.hasSubCategories( CategoryKey.parse( categoryKey ) );
    }

    public void regenerateIndexForContentType( int contentTypeKey )
    {

        ContentTypeEntity contentType = contentTypeDao.findByKey( contentTypeKey );

//        final int batchSize = 100;
        final int batchSize = 10;
        RegenerateIndexBatcher batcher = new RegenerateIndexBatcher( indexService, contentService );
        batcher.regenerateIndex( contentType, batchSize, null );
    }

    public void regenerateIndexForContentHandler( int contentHandlerKey )
    {
        ContentHandler handler = getContentHandler();
        int[] contentTypes = handler.getContentTypeKeysByHandler( contentHandlerKey );

        for ( int contentType : contentTypes )
        {
            regenerateIndexForContentType( contentType );
        }
    }

    public void removeCategory( User user, int categoryKey )
        throws VerticalSecurityException, VerticalRemoveException
    {

        if ( !isEnterpriseAdmin( user ) )
        {
            CategoryKey superCategoryKey = categoryHandler.getParentCategoryKey( CategoryKey.parse( categoryKey ) );
            if ( !securityHandler.validateCategoryRemove( user, superCategoryKey ) )
            {
                String message = "User does not have access rights to remove the category.";
                VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
            }
        }
        categoryHandler.removeCategory( null, CategoryKey.parse( categoryKey ) );
    }


    public void removeContentObject( int contentObjectKey )
        throws VerticalSecurityException, VerticalRemoveException
    {
        contentObjectHandler.removeContentObject( contentObjectKey );
    }

    public void removeContentType( User user, int contentTypeKey )
        throws VerticalSecurityException, VerticalRemoveException
    {

        if ( !( securityHandler.isSiteAdmin( user ) || isDeveloper( user ) ) )
        {
            String message = "User is not administrator or developer";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        contentHandler.removeContentType( contentTypeKey );
    }

    public void removeLanguage( LanguageKey languageKey )
        throws VerticalSecurityException, VerticalRemoveException
    {
        languageHandler.removeLanguage( languageKey );
    }

    public void removeMenu( User user, int menuKey )
        throws VerticalRemoveException, VerticalSecurityException
    {
        menuHandler.removeMenu( user, menuKey );
    }

    public void removePageTemplate( int pageTemplateKey )
        throws VerticalSecurityException, VerticalRemoveException
    {
        pageTemplateHandler.removePageTemplate( pageTemplateKey );
    }

    public void removeUnit( User user, int unitKey )
        throws VerticalRemoveException, VerticalSecurityException
    {

        try
        {
            String unitXml = getUnit( unitKey );
            unitHandler.removeUnit( unitKey );
            String categoryKey = JDOMUtil.evaluateSingleXPathValueAsString( "/units/unit/@categorykey", JDOMUtil.parseDocument( unitXml ) );
            removeCategory( user, Integer.valueOf( categoryKey ) );
        }
        catch ( IOException e )
        {
            throw new VerticalAdminException( e );
        }
        catch ( JDOMException e )
        {
            throw new VerticalAdminException( e );
        }

    }

    public void updateCategory( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {

        Document doc = XMLTool.domparse( xmlData, "category" );
        Element categoryElem = doc.getDocumentElement();
        if ( !isEnterpriseAdmin( user ) )
        {
            CategoryKey categoryKey = CategoryKey.parse( categoryElem.getAttribute( "key" ) );

            if ( !securityHandler.validateCategoryUpdate( user, categoryKey ) )
            {
                String message = "User does not have access rights to update the category.";
                VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
            }
        }
        categoryHandler.updateCategory( null, user, doc );
    }

    public void updateContentObject( String xmlData )
        throws VerticalSecurityException, VerticalUpdateException
    {
        contentObjectHandler.updateContentObject( xmlData );
    }

    public void updateContentType( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData, "contenttype" );

        if ( !( securityHandler.isSiteAdmin( user ) || isDeveloper( user ) ) )
        {
            String message = "User is not administrator or developer";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        contentHandler.updateContentType( user, doc );
    }

    public void updateLanguage( LanguageKey languageKey, String languageCode, String description )
        throws VerticalSecurityException, VerticalUpdateException
    {
        languageHandler.updateLanguage( languageKey, languageCode, description );
    }

    public void updateMenuData( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData, "menu" );
        if ( !isAdmin( user ) )
        {
            String message = "User does not have rights to update menu data.";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        menuHandler.updateMenuData( doc );
    }

    public void updatePageTemplate( String xmlData )
        throws VerticalSecurityException, VerticalUpdateException
    {
        pageTemplateHandler.updatePageTemplate( xmlData );
    }

    public void updateUnit( String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {
        unitHandler.updateUnit( xmlData );
    }

    public String getMenuItemsByContentObject( User user, int cobKey )
    {
        Document doc = menuHandler.getMenuItemsByContentObject( user, cobKey );
        return XMLTool.documentToString( doc );
    }

    public String getMenuItemsByPageTemplates( User user, int[] pageTemplateKeys )
    {
        Document doc = menuHandler.getMenuItemsByPageTemplates( user, pageTemplateKeys );
        return XMLTool.documentToString( doc );
    }

    public String getAccessRights( User user, int type, int key, boolean includeUserright )
    {
        Document doc = securityHandler.getAccessRights( user, type, key, includeUserright );

        return XMLTool.documentToString( doc );
    }

    public String getDefaultAccessRights( User user, int type, int key )
    {
        Document doc = securityHandler.getDefaultAccessRights( user, type, key );

        return XMLTool.documentToString( doc );
    }

    public String getGroup( String gKey )
    {
        return XMLTool.documentToString( groupHandler.getGroup( gKey ) );
    }

    public void updateAccessRights( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData, "accessrights" );
        securityHandler.updateAccessRights( user, doc );
    }

    public XMLDocument getContent( User oldTypeUser, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index,
                                   int count, int childrenLevel, int parentLevel, int parentChildrenLevel )
    {
        UserEntity user = securityService.getUser( oldTypeUser.getKey() );
        List<CategoryKey> categories = CategoryKey.convertToList( categoryKey );

        ContentByCategoryQuery contentByCategoryQuery = new ContentByCategoryQuery();
        contentByCategoryQuery.setUser( user );
        contentByCategoryQuery.setCategoryKeyFilter( categories, includeSubCategories ? Integer.MAX_VALUE : 1 );
        contentByCategoryQuery.setOrderBy( orderBy );
        contentByCategoryQuery.setIndex( index );
        contentByCategoryQuery.setCount( count );
        contentByCategoryQuery.setFilterIncludeOfflineContent();
        contentByCategoryQuery.setFilterAdminBrowseOnly( false );

        ContentResultSet contents = contentService.queryContent( contentByCategoryQuery );

        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( new Date() );
        relatedContentQuery.setUser( user );
        relatedContentQuery.setContentResultSet( contents );
        relatedContentQuery.setParentLevel( parentLevel );
        relatedContentQuery.setChildrenLevel( childrenLevel );
        relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
        relatedContentQuery.setIncludeOnlyMainVersions( true );

        RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );

        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        xmlCreator.setResultIndexing( index, count );
        xmlCreator.setIncludeOwnerAndModifierData( true );
        xmlCreator.setIncludeContentData( true );
        xmlCreator.setIncludeCategoryData( true );
        xmlCreator.setIncludeRelatedContentData( true );
        xmlCreator.setIncludeUserRightsInfo( true, new CategoryAccessResolver( groupDao ), new ContentAccessResolver( groupDao ) );
        xmlCreator.setIncludeVersionsInfoForAdmin( true );
        xmlCreator.setIncludeAssignment( true );
        xmlCreator.setIncludeDraftInfo( true );
        return xmlCreator.createContentsDocument( user, contents, relatedContents );
    }

    public boolean isEnterpriseAdmin( User user )
    {
        return memberOfResolver.hasEnterpriseAdminPowers( user.getKey() );
    }

    public boolean isSiteAdmin( User user, SiteKey siteKey )
    {
        return getSecurityHandler().getMenuAccessRight( user, siteKey.toInt() ).getAdministrate();
    }

    public boolean isAdmin( User user )
    {
        return memberOfResolver.hasAdministratorPowers( user.getKey() );
    }

    public boolean isUserStoreAdmin( User user, UserStoreKey userStoreKey )
    {
        return memberOfResolver.hasUserStoreAdministratorPowers( user.getKey(), userStoreKey );
    }

    public boolean isDeveloper( User user )
    {
        return memberOfResolver.hasDeveloperPowers( user.getKey() );
    }

    public void moveCategory( User user, int catKey, int newSuperCategoryKey )
        throws VerticalUpdateException, VerticalSecurityException
    {

        CategoryKey oldSuperCategoryKey = categoryHandler.getParentCategoryKey( CategoryKey.parse( catKey ) );
        if ( !securityHandler.validateCategoryRemove( user, oldSuperCategoryKey ) ||
            !securityHandler.validateCategoryCreate( user, CategoryKey.parse( newSuperCategoryKey ) ) )
        {
            String message = "User does not have access rights to move the category.";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        if ( categoryHandler.isSubCategory( CategoryKey.parse( catKey ), CategoryKey.parse( newSuperCategoryKey ) ) )
        {
            String message = "Cannot move a category to a subcategory";
            VerticalEngineLogger.errorUpdate( this.getClass(), 0, message, null );
        }

        categoryHandler.moveCategory( user, CategoryKey.parse( catKey ), CategoryKey.parse( newSuperCategoryKey ) );
    }

    public int[] getContentKeysByCategory( User user, int categoryKey )
    {
        return contentHandler.getContentKeysByCategory( user, CategoryKey.parse( categoryKey ) );
    }

    public XMLDocument getMenu( User user, int type, Criteria criteria, boolean includeSubtrees )
    {
        Document doc = XMLTool.createDocument( "data" );
        Element root = doc.getDocumentElement();

        if ( type == Types.CATEGORY )
        {
            categoryHandler.getMenu( user, root, criteria, includeSubtrees );
        }

        return XMLDocumentFactory.create( doc );
    }

    public String getPath( User user, int type, int key )
    {
        Document doc = XMLTool.createDocument( "data" );

        if ( type == Types.CATEGORY )
        {
            // Get unit
            int unitKey = categoryHandler.getUnitKey( CategoryKey.parse( key ) );
            Document unitDoc = commonHandler.getSingleData( Types.UNIT, unitKey, null );
            Element unitElem = (Element) unitDoc.getDocumentElement().getFirstChild();

            // Get categories
            CategoryCriteria criteria = new CategoryCriteria();
            criteria.setCategoryKey( key );
            criteria.setUseDisableAttribute( false );
            categoryHandler.getMenu( user, unitElem, criteria, false );

            // Append unit (and categories) to site doc
            //siteElem.appendChild(siteDoc.importNode(unitElem, true));

            // Append site doc to global doc
            doc.getDocumentElement().appendChild( doc.importNode( unitElem, true ) );
        }
        return XMLTool.documentToString( doc );
    }

    public String getPathString( int type, int key )
    {
        if ( type == Types.MENUITEM )
        {
            return menuHandler.getPathString( key, true, true ).toString();
        }
        return null;
    }

    public String getContentTitleXML( int versionKey )
    {
        Document doc = contentHandler.getContentTitleDoc( versionKey );
        return XMLTool.documentToString( doc );
    }

    public String getMenusForAdmin( User user, MenuGetterSettings getterSettings )
    {
        Document doc = menuHandler.getMenusForAdmin( user, getterSettings );
        return XMLTool.documentToString( doc );
    }

    public void moveMenuItem( User user, Element[] menuItemElems, int menuItemKey, int fromMenuKey, int fromParentKey, int toMenuKey,
                              int toParentKey )
    {
        menuHandler.moveMenuItem( user, menuItemElems, menuItemKey, fromMenuKey, fromParentKey, toMenuKey, toParentKey );
    }

    public void shiftMenuItems( User user, Element[] menuItemElems, int menuKey, int parentMenuItemKey )
    {
        menuHandler.shiftMenuItems( user, menuItemElems, menuKey, parentMenuItemKey );
    }

    public String getUserNames( String[] groupKeys )
    {
        Document doc = groupHandler.getUserNames( groupKeys );
        return XMLTool.documentToString( doc );
    }

    public String getContentHandler( int contentHandlerKey )
    {
        return XMLTool.documentToString( contentHandler.getContentHandler( contentHandlerKey ) );
    }

    public String getContentHandlerClassForContentType( int contentTypeKey )
    {
        return contentHandler.getContentHandlerClassForContentType( contentTypeKey );
    }

    public String getContentHandlers()
    {
        return XMLTool.documentToString( contentHandler.getContentHandlers() );
    }

    public int createContentHandler( User user, String xmlData )
        throws VerticalSecurityException
    {

        if ( !securityHandler.isEnterpriseAdmin( user ) )
        {
            String message = "User does not have access rights to create content handlers.";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        Document doc = XMLTool.domparse( xmlData );
        return contentHandler.createContentHandler( user, doc );
    }

    public void updateContentHandler( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {

        if ( !securityHandler.isEnterpriseAdmin( user ) )
        {
            String message = "User does not have access rights to update content handlers.";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        Document doc = XMLTool.domparse( xmlData );
        contentHandler.updateContentHandler( user, doc );
    }

    public void removeContentHandler( User user, int contentHandlerKey )
        throws VerticalSecurityException, VerticalRemoveException
    {

        if ( !isEnterpriseAdmin( user ) )
        {
            String message = "User does not have access rights to delete content handlers.";
            VerticalEngineLogger.errorSecurity( this.getClass(), 0, message, null );
        }

        contentHandler.removeContentHandler( contentHandlerKey );
    }

    public String getContentTypes()
    {
        Document doc = contentHandler.getContentTypes( null, false );
        return XMLTool.documentToString( doc );
    }

    public XMLDocument getContentTypes( boolean includeContentCount )
    {
        Document doc = contentHandler.getContentTypes( includeContentCount );
        return XMLDocumentFactory.create( doc );
    }

    public XMLDocument getContentTypes( int[] contentTypeKeys, boolean includeContentCount )
    {
        Document doc = contentHandler.getContentTypes( contentTypeKeys, includeContentCount );
        return XMLDocumentFactory.create( doc );
    }

    public String getIndexingParametersXML( int contentTypeKey )
    {

        ContentTypeEntity contentType = contentTypeDao.findByKey( contentTypeKey );

        XMLOutputter printer = new XMLOutputter( Format.getPrettyFormat() );
        org.jdom.Element element = new org.jdom.Element( "indexparameters" );
        if ( contentType != null )
        {
            element = contentType.getIndexingParametersXML();

        }
        return printer.outputString( element );
    }

    public Document getContentHandlerByContentType( int contentTypeKey )
    {
        return contentHandler.getContentHandlerByContentType( contentTypeKey );
    }

    public int createSection( Document doc )
        throws VerticalSecurityException
    {
        return sectionHandler.createSection( doc );
    }

    public void updateSection( User user, Document doc )
        throws VerticalUpdateException, VerticalSecurityException
    {
        sectionHandler.updateSection( user, doc );
    }

    public long getSectionContentTimestamp( MenuItemKey sectionKey )
    {
        return sectionHandler.getSectionContentTimestamp( sectionKey.toInt() );
    }

    public String getSections( User user, SectionCriteria criteria )
    {
        Document doc = sectionHandler.getSections( user, criteria );
        return XMLTool.documentToString( doc );
    }

    public void removeSection( int sectionKey, boolean recursive )
        throws VerticalRemoveException, VerticalSecurityException
    {
        sectionHandler.removeSection( sectionKey, recursive );
    }

    public void copySection( int sectionKey )
        throws VerticalCopyException, VerticalSecurityException
    {
        sectionHandler.copySection( sectionKey );
    }

    public void addContentToSections( User user, String xmlData )
        throws VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData, "sections" );
        sectionHandler.addContentToSections( user, doc );
    }

    public String getSuperSectionNames( MenuItemKey sectionKey, boolean includeSection )
    {
        Document doc = sectionHandler.getSuperSectionNames( sectionKey.toInt(), includeSection );
        return XMLTool.documentToString( doc );
    }

    public void setSectionContentsApproved( User user, int sectionKey, int[] contentKeys, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException
    {
        sectionHandler.setSectionContentsApproved( user, sectionKey, contentKeys, approved );
    }

    public boolean isSectionOrdered( int sectionKey )
    {
        return sectionHandler.isSectionOrdered( sectionKey );
    }

    public void updateSectionContent( User user, MenuItemKey sectionKey, int contentKey, int order, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException
    {
        sectionHandler.updateSectionContent( user, sectionKey.toInt(), contentKey, order, approved, null );
    }

    public int getMenuKeyBySection( MenuItemKey sectionKey )
    {
        return sectionHandler.getMenuKeyBySection( sectionKey.toInt() );
    }

    public MenuItemKey getMenuItemKeyBySection( MenuItemKey sectionKey )
    {
        return sectionHandler.getMenuItemKeyBySection( sectionKey.toInt() );
    }

    public int getMenuKeyByMenuItem( MenuItemKey menuItemKey )
    {
        return menuHandler.getMenuKeyByMenuItem( menuItemKey.toInt() );
    }

    public int getParentMenuItemKey( int menuItemKey )
    {
        return menuHandler.getParentMenuItemKey( menuItemKey );
    }

    public XMLDocument getContentTitlesBySection( MenuItemKey sectionKey, String orderBy, int fromIndex, int count,
                                                  boolean includeTotalCount, boolean approveOnly )
    {
        return sectionHandler.getContentTitlesBySection( sectionKey.toInt(), orderBy, fromIndex, count, includeTotalCount, approveOnly );
    }

    public XMLDocument getContentTitles( int[] contentKeys )
    {
        return contentHandler.getContentTitles( contentKeys, false, null );
    }

    public String getUsersWithPublishRight( int categoryKey )
    {
        Document doc = securityHandler.getUsersWithPublishRight( CategoryKey.parse( categoryKey ) );
        return XMLTool.documentToString( doc );
    }

    public String getContentOwner( int contentKey )
    {
        Document doc = contentHandler.getContentOwner( contentKey );
        return XMLTool.documentToString( doc );
    }

    public String getLogEntries( User user, MultiValueMap adminParams, int fromIdx, int count, boolean complete )
    {
        Document doc = logHandler.getLogEntries( user, adminParams, fromIdx, count, complete, true );
        return XMLTool.documentToString( doc );
    }

    public String getLogEntry( String key )
    {
        Document doc = logHandler.getLogEntry( key );
        return XMLTool.documentToString( doc );
    }

    public int getContentCountByContentType( int contentTypeKey )
    {
        return contentHandler.getContentCountByContentType( contentTypeKey );
    }

    public String getCategoryPathXML( CategoryKey categoryKey, int[] contentTypes )
    {
        Document doc = XMLTool.createDocument( "path" );
        categoryHandler.getPathXML( doc, null, categoryKey, contentTypes );
        return XMLTool.documentToString( doc );
    }

    public ResourceKey getContentTypeCSSKey( int contentTypeKey )
    {
        return contentHandler.getContentTypeCSSKey( contentTypeKey );
    }

    public String getData( User user, int type, int[] keys )
    {
        Document doc = commonHandler.getData( user, type, keys );
        return XMLTool.documentToString( doc );
    }

    public ResourceKey getDefaultCSSByMenu( int menuKey )
    {
        return menuHandler.getDefaultCSSByMenu( menuKey );
    }

    public int getCurrentVersionKey( int contentKey )
    {
        return contentHandler.getCurrentVersionKey( contentKey );
    }

    public int getContentKeyByVersionKey( int versionKey )
    {
        return contentHandler.getContentKeyByVersionKey( versionKey );
    }

    public int[] getBinaryDataKeysByVersion( int versionKey )
    {
        return binaryDataHandler.getBinaryDataKeysByVersion( versionKey );
    }

    public String getContentVersion( User user, int versionKey )
    {
        Document doc = contentHandler.getContentVersion( user, versionKey );
        return XMLTool.documentToString( doc );
    }

    public String getContentXMLField( int versionKey )
    {
        Document doc = contentHandler.getContentXMLField( versionKey );
        return XMLTool.documentToString( doc );
    }

    public int[] getContentTypesByHandlerClass( String className )
    {
        return contentHandler.getContentTypesByHandlerClass( className );
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return binaryDataHandler.getBinaryDataKey( contentKey, label );
    }

    public String getCategoryMenu( User user, int categoryKey, int[] contentTypes, boolean includeRootCategories )
    {
        Document doc = categoryHandler.getCategoryMenu( user, CategoryKey.parse( categoryKey ), contentTypes, includeRootCategories );
        return XMLTool.documentToString( doc );
    }

    public int getContentVersionState( int versionKey )
    {
        return contentHandler.getState( versionKey );
    }

    public MenuItemKey getSectionKeyByMenuItemKey( MenuItemKey menuItemKey )
    {
        return sectionHandler.getSectionKeyByMenuItem( menuItemKey );
    }

    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseSchema();
    }

    public boolean initializeDatabaseValues()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseValues();
    }

    public PageTemplateType getPageTemplateType( PageTemplateKey pageTemplateKey )
    {
        return pageTemplateHandler.getPageTemplateType( pageTemplateKey );
    }

    public boolean isContentVersionApproved( int versionKey )
    {
        return contentHandler.isContentVersionApproved( versionKey );
    }

    public void updateContentPublishing( User user, int contentKey, int versionKey, int status, Date publishFrom, Date publishTo )
        throws VerticalUpdateException
    {
        contentHandler.updateContentPublishing( user, contentKey, versionKey, status, publishFrom, publishTo );
    }

    public void setContentHome( User user, int contentKey, int menuKey, int menuItemKey, int pageTemplateKey )
        throws VerticalUpdateException
    {
        contentHandler.setContentHome( user, contentKey, menuKey, menuItemKey, pageTemplateKey );
    }

    public String getContentHomes( int contentKey )
    {
        Document doc = contentHandler.getContentHomes( contentKey );
        return XMLTool.documentToString( doc );
    }

    public boolean hasContentPageTemplates( int menuKey, int contentTypeKey )
    {
        return pageTemplateHandler.hasContentPageTemplates( menuKey, contentTypeKey );
    }

    public int getContentStatus( int versionKey )
    {
        return contentHandler.getContentStatus( versionKey );
    }

    public Document getAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {

        try
        {
            openSharedConnection();
            return doGetAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );
        }
        finally
        {
            closeSharedConnection();
        }
    }

    private Document doGetAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {

        Document menuDoc = menuHandler.getAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );

        // Sort menuitems based on order
        Element[] siteElems = XMLTool.getElements( menuDoc.getDocumentElement() );
        for ( Element siteElem : siteElems )
        {
            XMLTool.sortChildElements( siteElem, "order", false, true );
        }

        return menuDoc;
    }

    public void updateMenuDetails( int menuKey, int frontPageKey, int loginPageKey, int errorPageKey, int defaultPageTemplateKey )
    {
        menuHandler.updateMenuDetails( menuKey, frontPageKey, loginPageKey, errorPageKey, defaultPageTemplateKey );
    }

    public int getContentTypeKeyByName( String name )
    {
        return this.contentHandler.getContentTypeKeyByName( name );
    }

    /**
     * Return a map of top level menus with name.
     *
     * @return A map with the keys of the top level menus as keys, and their names as the corresponding value.
     * @throws SQLException If a database error occurs.
     */
    public Map<Integer, String> getMenuMap()
        throws SQLException
    {
        return this.menuHandler.getMenuMap();
    }

    public long getArchiveSizeByCategory( int categoryKey )
    {
        return this.categoryHandler.getArchiveSizeByCategory( CategoryKey.parse( categoryKey ) );
    }

    public long getArchiveSizeByUnit( int unitKey )
    {
        return this.categoryHandler.getArchiveSizeByUnit( unitKey );
    }

    public void cleanReadLogs( User user )
    {
        if ( ( user != null ) && isEnterpriseAdmin( user ) )
        {
            this.systemHandler.cleanReadLogs();
        }
    }

    public void cleanUnusedContent( User user )
    {
        if ( ( user != null ) && isEnterpriseAdmin( user ) )
        {
            this.systemHandler.cleanUnusedContent();
        }
    }

    public void setBinaryDataHandler( BinaryDataHandler binaryDataHandler )
    {
        this.binaryDataHandler = binaryDataHandler;
    }

    public void setCategoryHandler( CategoryHandler categoryHandler )
    {
        this.categoryHandler = categoryHandler;
    }

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void setContentHandler( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void setContentService( ContentService service )
    {
        contentService = service;
    }

    public void setContentObjectHandler( ContentObjectHandler contentObjectHandler )
    {
        this.contentObjectHandler = contentObjectHandler;
    }

    public void setGroupHandler( GroupHandler groupHandler )
    {
        this.groupHandler = groupHandler;
    }

    public void setLanguageHandler( LanguageHandler languageHandler )
    {
        this.languageHandler = languageHandler;
    }

    public void setLogHandler( LogHandler logHandler )
    {
        this.logHandler = logHandler;
    }

    public void setMenuHandler( MenuHandler menuHandler )
    {
        this.menuHandler = menuHandler;
    }

    public void setPageHandler( PageHandler pageHandler )
    {
        this.pageHandler = pageHandler;
    }

    public void setPageTemplateHandler( PageTemplateHandler pageTemplateHandler )
    {
        this.pageTemplateHandler = pageTemplateHandler;
    }

    public void setSectionHandler( SectionHandler sectionHandler )
    {
        this.sectionHandler = sectionHandler;
    }

    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }

    public void setUnitHandler( UnitHandler unitHandler )
    {
        this.unitHandler = unitHandler;
    }

    public void setSystemHandler( SystemHandler systemHandler )
    {
        this.systemHandler = systemHandler;
    }

    public void setSecurityHandler( SecurityHandler securityHandler )
    {
        this.securityHandler = securityHandler;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }

    public void setSiteDao( SiteDao value )
    {
        this.siteDao = value;
    }

    public void setIndexService( IndexService service )
    {
        indexService = service;
    }
}
