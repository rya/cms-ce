/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.util.Date;
import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.vertical.engine.AdminEngine;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.ContentAccessRight;
import com.enonic.vertical.engine.MenuAccessRight;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.resource.ResourceFolder;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.resource.ResourceXmlCreator;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

public class AdminServiceImpl
    implements AdminService
{

    private static final int TIMEOUT_24HOURS = 86400;

    public void setAdminEngine( AdminEngine value )
    {
        adminEngine = value;
    }

    protected AdminEngine adminEngine;

    private ResourceService resourceService;

    public void setResourceService( ResourceService value )
    {
        this.resourceService = value;
    }

    public XMLDocument getPageTemplates( PageTemplateType type )
    {
        return adminEngine.getPageTemplates( type );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 86400)
    public void copyMenu( User user, int menuKey, boolean includeContent )
    {
        adminEngine.copyMenu( user, menuKey, includeContent );
    }

    public boolean contentExists( int categoryKey, String contentTitle )
    {
        return adminEngine.contentExists( categoryKey, contentTitle );
    }

    public int getContentKey( int categoryKey, String contentTitle )
    {
        return adminEngine.getContentKey( categoryKey, contentTitle );
    }

    public String getContentCreatedTimestamp( int contentKey )
    {
        return adminEngine.getContentCreatedTimestamp( contentKey );
    }

    public Date getContentPublishFromTimestamp( int contentKey )
    {
        return adminEngine.getContentPublishFromTimestamp( contentKey );
    }

    public Date getContentPublishToTimestamp( int contentKey )
    {
        return adminEngine.getContentPublishToTimestamp( contentKey );
    }

    public int getCategoryKey( int superCategoryKey, String name )
    {
        return adminEngine.getCategoryKey( superCategoryKey, name );
    }

    public MenuItemKey getSectionKeyByMenuItemKey( MenuItemKey menuItemKey )
    {
        return adminEngine.getSectionKeyByMenuItemKey( menuItemKey );
    }

    public XMLDocument getCategory( User user, int categoryKey )
    {
        return adminEngine.getCategory( user, categoryKey );
    }

    public int getCategoryKey( int contentKey )
    {
        return adminEngine.getCategoryKey( contentKey );
    }

    public XMLDocument getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory )
    {
        return adminEngine.getSuperCategoryNames( categoryKey, withContentCount, includeCategory );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateCategory( User user, String xmlData )
    {
        adminEngine.updateCategory( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createContentObject( User user, String xmlData )
    {
        return adminEngine.createContentObject( xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createContentType( User user, String xmlData )
    {
        return adminEngine.createContentType( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void createLanguage( User user, String languageCode, String description )
    {
        adminEngine.createLanguage( user, languageCode, description );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createMenu( User user, String xmlData )
    {
        return adminEngine.createMenu( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateAccessRights( User user, String xmlData )
    {
        adminEngine.updateAccessRights( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateMenuItem( User user, String xmlData )
    {
        adminEngine.updateMenuItem( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeMenuItem( User user, int mikey )
    {
        adminEngine.removeMenuItem( user, mikey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createMenuItem( User user, String xmlData )
    {
        return adminEngine.createMenuItem( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createPageTemplate( User user, String xmlData )
    {
        return adminEngine.createPageTemplate( xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int copyPageTemplate( User user, int pageTemplateKey )
    {
        return adminEngine.copyPageTemplate( user, pageTemplateKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createUnit( String xmlData )
    {
        return adminEngine.createUnit( xmlData );
    }

    public String generateUID( String fName, String sName, UserStoreKey userStoreKey )
    {
        return adminEngine.generateUID( fName, sName, userStoreKey );
    }

    public BinaryData getBinaryData( User user, int binaryDataKey )
    {
        return adminEngine.getBinaryData( binaryDataKey );
    }

    public XMLDocument getAccessRights( User user, int type, int key, boolean includeUserright )
    {
        return adminEngine.getAccessRights( user, type, key, includeUserright );
    }

    public XMLDocument getDefaultAccessRights( User user, int type, int key )
    {
        return adminEngine.getDefaultAccessRights( user, type, key );
    }

    public XMLDocument getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel )
    {
        return adminEngine.getContent( user, contentKey, parentLevel, childrenLevel, parentChildrenLevel );
    }

    public String getCategoryName( int categoryKey )
    {
        return adminEngine.getCategoryName( categoryKey );
    }

    public XMLDocument getCategoryNameXML( int categoryKey )
    {
        return adminEngine.getCategoryNameXML( categoryKey );
    }

    public CategoryAccessRight getCategoryAccessRight( User user, int categoryKey )
    {
        return adminEngine.getCategoryAccessRight( user, categoryKey );
    }

    public ContentAccessRight getContentAccessRight( User user, int categoryKey )
    {
        return adminEngine.getContentAccessRight( user, categoryKey );
    }

    public MenuAccessRight getMenuAccessRight( User user, int menuKey )
    {
        return adminEngine.getMenuAccessRight( user, menuKey );
    }

    public int[] getContentKeysByCategory( User user, int categoryKey )
    {
        return adminEngine.getContentKeysByCategory( user, categoryKey );
    }

    public XMLDocument getContentObject( int coc_lKey )
    {
        return adminEngine.getContentObject( coc_lKey );
    }

    public XMLDocument getContentObjectsByMenu( int menuKey )
    {
        return adminEngine.getContentObjectsByMenu( menuKey );
    }

    public String getContentTitle( int versionKey )
    {
        return adminEngine.getContentTitle( versionKey );
    }

    public XMLDocument getContentType( int contentTypeKey )
    {
        return adminEngine.getContentType( contentTypeKey );
    }

    public XMLDocument getContentType( int contentTypeKey, boolean includeContentCount )
    {
        return adminEngine.getContentType( contentTypeKey, includeContentCount );
    }

    public int getContentCountByContentType( int contentTypeKey )
    {
        return adminEngine.getContentCountByContentType( contentTypeKey );
    }

    public int getContentTypeKey( int contentKey )
    {
        return adminEngine.getContentTypeKey( contentKey );
    }

    public int[] getContentTypeKeysByHandler( String handlerClass )
    {
        return adminEngine.getContentTypeKeysByHandler( handlerClass );
    }

    public int getContentTypeKeyByCategory( int categoryKey )
    {
        return adminEngine.getContentTypeKeyByCategory( categoryKey );
    }

    public String getContentTypeName( int contentTypeKey )
    {
        return adminEngine.getContentTypeName( contentTypeKey );
    }

    public XMLDocument getContentTypes()
    {
        return adminEngine.getContentTypes();
    }

    public XMLDocument getLanguage( LanguageKey languageKey )
    {
        return adminEngine.getLanguage( languageKey );
    }

    public XMLDocument getLanguages()
    {
        return adminEngine.getLanguages();
    }

    public XMLDocument getMenu( User user, int menuKey, boolean complete )
    {
        return adminEngine.getMenu( user, menuKey, complete );
    }

    public MenuItemAccessRight getMenuItemAccessRight( User user, MenuItemKey key )
    {
        return adminEngine.getMenuItemAccessRight( user, key );
    }

    public XMLDocument getMenuItem( User user, int key, boolean withParents )
    {
        return adminEngine.getMenuItem( user, key, withParents );
    }

    public XMLDocument getMenuItem( User user, int key, boolean withParents, boolean complete )
    {
        return adminEngine.getMenuItem( user, key, withParents, complete );
    }

    public String getMenuItemName( int menuItemKey )
    {
        return adminEngine.getMenuItemName( menuItemKey );
    }

    public XMLDocument getMenuItemsByContentObject( User user, int cobKey )
    {
        return adminEngine.getMenuItemsByContentObject( user, cobKey );
    }

    public XMLDocument getMenuItemsByPageTemplates( User user, int[] pageTemplateKeys )
    {
        return adminEngine.getMenuItemsByPageTemplates( user, pageTemplateKeys );
    }

    public String getPageTemplate( int pageTemplateKey )
    {
        return adminEngine.getPageTemplate( new PageTemplateKey( pageTemplateKey ) );
    }

    public XMLDocument getPageTemplatesByMenu( int menuKey, int[] excludeTypeKeys )
    {
        return adminEngine.getPageTemplatesByMenu( menuKey, excludeTypeKeys );
    }

    public XMLDocument getPageTemplatesByContentObject( int contentObjectKey )
    {
        return adminEngine.getPageTemplatesByContentObject( contentObjectKey );
    }

    public String getPageTemplParams( int pageTemplateKey )
    {
        return adminEngine.getPageTemplParams( pageTemplateKey );
    }

    public int getSuperCategoryKey( int categoryKey )
    {
        return adminEngine.getSuperCategoryKey( categoryKey );
    }

    public XMLDocument getUnit( int unitKey )
    {
        return adminEngine.getUnit( unitKey );
    }

    public String getUnitName( int unitKey )
    {
        return adminEngine.getUnitName( unitKey );
    }

    public int getUnitKey( int categoryKey )
    {
        return adminEngine.getUnitKey( categoryKey );
    }

    public int getUnitLanguageKey( int unitKey )
    {
        return adminEngine.getUnitLanguageKey( unitKey );
    }

    public XMLDocument getUnitNamesXML( Filter filter )
    {
        return adminEngine.getUnitNamesXML( filter );
    }

    public boolean hasContent( int contentCategoryKey )
    {
        return adminEngine.hasContent( contentCategoryKey );
    }

    public boolean hasSubCategories( int contentCategoryKey )
    {
        return adminEngine.hasSubCategories( contentCategoryKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void regenerateIndexForContentType( int contentTypeKey )
    {
        adminEngine.regenerateIndexForContentType( contentTypeKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void regenerateIndexForContentHandler( int contentHandlerKey )
    {
        adminEngine.regenerateIndexForContentHandler( contentHandlerKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeContentObject( int coc_lKey )
    {
        adminEngine.removeContentObject( coc_lKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeContentType( User user, int contentTypeKey )
    {
        adminEngine.removeContentType( user, contentTypeKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeLanguage( LanguageKey languageKey )
    {
        adminEngine.removeLanguage( languageKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = TIMEOUT_24HOURS)
    public void removeMenu( User user, int key )
    {
        adminEngine.removeMenu( user, key );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removePageTemplate( int pageTemplateKey )
    {
        adminEngine.removePageTemplate( pageTemplateKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContentObject( String xmlData )
    {
        adminEngine.updateContentObject( xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContentType( User user, String xmlData )
    {
        adminEngine.updateContentType( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateLanguage( LanguageKey languageKey, String languageCode, String description )
    {
        adminEngine.updateLanguage( languageKey, languageCode, description );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateMenuData( User user, String xmlData )
    {
        adminEngine.updateMenuData( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePageTemplate( User user, String xmlData )
    {
        adminEngine.updatePageTemplate( xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUnit( String xmlData )
    {
        adminEngine.updateUnit( xmlData );
    }

    public XMLDocument getGroup( String gKey )
    {
        return adminEngine.getGroup( gKey );
    }

    public XMLDocument getContentTypeModuleData( int ctKey )
    {
        return adminEngine.getContentTypeModuleData( ctKey );
    }

    public int getContentCount( int categoryKey, boolean recursive )
    {
        return adminEngine.getContentCount( categoryKey, recursive );
    }

    public XMLDocument getContent( User user, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index, int count,
                                   int childrenLevel, int parentLevel, int parentChildrenLevel )
    {
        return adminEngine.getContent( user, categoryKey, includeSubCategories, orderBy, index, count, childrenLevel, parentLevel,
                                       parentChildrenLevel );
    }

    public boolean isEnterpriseAdmin( User user )
    {
        return adminEngine.isEnterpriseAdmin( user );
    }

    public boolean isAdmin( User user )
    {
        return adminEngine.isAdmin( user );
    }

    public boolean isUserStoreAdmin( User user, UserStoreKey userStoreKey )
    {
        return adminEngine.isUserStoreAdmin( user, userStoreKey );
    }

    public void moveCategory( User user, int catKey, int superCatKey )
    {
        adminEngine.moveCategory( user, catKey, superCatKey );
    }

    public XMLDocument getMenu( User user, CategoryCriteria criteria )
    {
        return adminEngine.getMenu( user, criteria );
    }

    public XMLDocument getPath( User user, int type, int key )
    {
        return adminEngine.getPath( user, type, key );
    }

    public XMLDocument getMenusForAdmin( User user )
    {
        return adminEngine.getMenusForAdmin( user );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void moveMenuItem( User user, Element[] menuItemElems, int menuItemKey, int fromMenuKey, int fromParentKey, int toMenuKey,
                              int toParentKey )
    {
        adminEngine.moveMenuItem( user, menuItemElems, menuItemKey, fromMenuKey, fromParentKey, toMenuKey, toParentKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void shiftMenuItems( User user, Element[] menuItemElems, int menuKey, int parentMenuItemKey )
    {
        adminEngine.shiftMenuItems( user, menuItemElems, menuKey, parentMenuItemKey );
    }

    public Set<UserEntity> getUserNames( String[] groupKeys )
    {
        return adminEngine.getUserNames( groupKeys );
    }

    public XMLDocument getContentHandler( int contentHandlerKey )
    {
        return adminEngine.getContentHandler( contentHandlerKey );
    }

    public String getContentHandlerClassForContentType( int contentTypeKey )
    {
        return adminEngine.getContentHandlerClassForContentType( contentTypeKey );
    }

    public XMLDocument getContentHandlers()
    {
        return adminEngine.getContentHandlers();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createContentHandler( User user, String xmlData )
    {
        return adminEngine.createContentHandler( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContentHandler( User user, String xmlData )
    {
        adminEngine.updateContentHandler( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeContentHandler( User user, int contentHandlerKey )
    {
        adminEngine.removeContentHandler( user, contentHandlerKey );
    }

    public XMLDocument getContentTypes( boolean includeContentCount )
    {
        return adminEngine.getContentTypes( includeContentCount );
    }

    public String getIndexingParametersXML( int contentTypeKey )
    {
        return adminEngine.getIndexingParametersXML( contentTypeKey );
    }

    public long getSectionContentTimestamp( MenuItemKey sectionKey )
    {
        return adminEngine.getSectionContentTimestamp( sectionKey );
    }

    public XMLDocument getSections( User user, SectionCriteria criteria )
    {
        return adminEngine.getSections( user, criteria );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeSection( int sectionKey, boolean recursive )
    {
        adminEngine.removeSection( sectionKey, recursive );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void copySection( int sectionKey )
    {
        adminEngine.copySection( sectionKey );
    }

    public boolean isSectionOrdered( int sectionKey )
    {
        return adminEngine.isSectionOrdered( sectionKey );
    }

    public MenuItemKey getMenuItemKeyBySection( MenuItemKey sectionKey )
    {
        return adminEngine.getMenuItemKeyBySection( sectionKey );
    }

    public int getMenuKeyBySection( MenuItemKey sectionKey )
    {
        return adminEngine.getMenuKeyBySection( sectionKey );
    }

    public XMLDocument getContentTitlesBySection( MenuItemKey sectionKey, String orderBy, int fromIndex, int count,
                                                  boolean includeTotalCount, boolean approveOnly )
    {

        return adminEngine.getContentTitlesBySection( sectionKey, orderBy, fromIndex, count, includeTotalCount, approveOnly );
    }

    public XMLDocument getContentTitles( int[] contentKeys )
    {
        return adminEngine.getContentTitles( contentKeys );
    }

    public XMLDocument getUsersWithPublishRight( int categoryKey )
    {
        return adminEngine.getUsersWithPublishRight( categoryKey );
    }

    public XMLDocument getContentOwner( int contentKey )
    {
        return adminEngine.getContentOwner( contentKey );
    }

    public XMLDocument getLogEntries( User user, MultiValueMap adminParams, int fromIdx, int count, boolean complete )
    {
        return adminEngine.getLogEntries( user, adminParams, fromIdx, count, complete );
    }

    public XMLDocument getLogEntry( String key )
    {
        return adminEngine.getLogEntry( key );
    }

    public int getMenuKeyByMenuItem( MenuItemKey menuItemKey )
    {
        return adminEngine.getMenuKeyByMenuItem( menuItemKey );
    }

    public int getParentMenuItemKey( int menuItemKey )
    {
        return adminEngine.getParentMenuItemKey( menuItemKey );
    }

    public XMLDocument getCategoryPathXML( CategoryKey categoryKey, int[] contentTypes )
    {
        return adminEngine.getCategoryPathXML( categoryKey, contentTypes );
    }

    public ResourceKey getContentTypeCSSKey( int contentTypeKey )
    {
        return adminEngine.getContentTypeCSSKey( contentTypeKey );
    }

    public XMLDocument getContentTypes( int[] contentTypeKeys, boolean includeContentCount )
    {
        return adminEngine.getContentTypes( contentTypeKeys, includeContentCount );
    }

    public XMLDocument getData( User user, int type, int[] keys )
    {
        return adminEngine.getData(type, keys );
    }

    public ResourceKey getDefaultCSSByMenu( int menuKey )
    {
        return adminEngine.getDefaultCSSByMenu( menuKey );
    }

    public int getCurrentVersionKey( int contentKey )
    {
        return adminEngine.getCurrentVersionKey( contentKey );
    }

    public int[] getBinaryDataKeysByVersion( int versionKey )
    {
        return adminEngine.getBinaryDataKeysByVersion( versionKey );
    }

    public int getContentKeyByVersionKey( int versionKey )
    {
        return adminEngine.getContentKeyByVersionKey( versionKey );
    }

    public XMLDocument getContentVersion( User user, int versionKey )
    {
        return adminEngine.getContentVersion( user, versionKey );
    }

    public XMLDocument getContentXMLField( User user, int versionKey )
    {
        return adminEngine.getContentXMLField( versionKey );
    }

    public int[] getContentTypesByHandlerClass( String className )
    {
        return adminEngine.getContentTypesByHandlerClass( className );
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return adminEngine.getBinaryDataKey( contentKey, label );
    }

    public XMLDocument getCategoryMenu( User user, int categoryKey, int[] contentTypes, boolean includeRootCategories )
    {
        return adminEngine.getCategoryMenu( user, categoryKey, contentTypes, includeRootCategories );
    }

    public int getContentVersionState( int versionKey )
    {
        return adminEngine.getContentVersionState( versionKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return adminEngine.initializeDatabaseSchema();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initializeDatabaseValues()
        throws Exception
    {
        return adminEngine.initializeDatabaseValues();
    }

    public boolean isContentVersionApproved( int versionKey )
    {
        return adminEngine.isContentVersionApproved( versionKey );
    }

    public XMLDocument getContentHomes( int contentKey )
    {
        return adminEngine.getContentHomes( contentKey );
    }

    public String getPathString( int type, int key )
    {
        return adminEngine.getPathString( type, key );
    }

    public XMLDocument getContentTitleXML( int versionKey )
    {
        return adminEngine.getContentTitleXML( versionKey );
    }

    public boolean hasContentPageTemplates( int menuKey, int contentTypeKey )
    {
        return adminEngine.hasContentPageTemplates( menuKey, contentTypeKey );
    }

    public int getContentStatus( int versionKey )
    {
        return adminEngine.getContentStatus( versionKey );
    }

    /**
     * Collect all menus and menu items for use in administration interface.
     */
    public XMLDocument getAdminMenu( User user, int menuKey )
    {
        return doGetAdminMenu( user, menuKey < 0 ? new int[0] : new int[]{menuKey}, null, false );
    }

    public XMLDocument getAdminMenuIncludeReadOnlyAccessRights( User user, int menuKey )
    {
        return doGetAdminMenu( user, menuKey < 0 ? new int[0] : new int[]{menuKey}, null, true );
    }

    private XMLDocument doGetAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {
        Document doc = this.adminEngine.getAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );
        return XMLDocumentFactory.create(doc);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateMenuDetails( int menuKey, int frontPageKey, int loginPageKey, int errorPageKey, int defaultPageTemplateKey )
    {
        adminEngine.updateMenuDetails( menuKey, frontPageKey, loginPageKey, errorPageKey, defaultPageTemplateKey );
    }

    public int getContentTypeKeyByName( String name )
    {
        return adminEngine.getContentTypeKeyByName( name );
    }

    public XMLDocument getUnits()
    {
        return adminEngine.getUnits();
    }

    public long getArchiveSizeByCategory( int categoryKey )
    {
        return adminEngine.getArchiveSizeByCategory( categoryKey );
    }

    public long getArchiveSizeByUnit( int unitKey )
    {
        return adminEngine.getArchiveSizeByUnit( unitKey );
    }

    public boolean isSiteAdmin( User user, SiteKey siteKey )
    {
        return adminEngine.isSiteAdmin( user, siteKey );
    }

    /**
     * Clean the read logs.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cleanReadLogs( User user )
    {
        this.adminEngine.cleanReadLogs( user );
    }

    /**
     * Clean unused content.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void cleanUnusedContent( User user )
    {
        this.adminEngine.cleanUnusedContent( user );
    }

    public boolean isDeveloper( User user )
    {
        return adminEngine.isDeveloper( user );
    }

    public XMLDocument getResourceTreeXml( String rootPath, boolean includeFullPath, boolean includeUsageCount, int maxLevels,
                                           boolean listFolders, boolean listResources )
    {
        ResourceFolder root;
        if ( rootPath != null )
        {
            root = this.resourceService.getResourceRoot().getFolder( rootPath );

            // if rootPath is not found, return absolute root
            if ( root == null )
            {
                root = this.resourceService.getResourceRoot();
            }
        }
        else
        {
            root = this.resourceService.getResourceRoot();
        }

        ResourceXmlCreator xmlCreator = new ResourceXmlCreator();
        xmlCreator.setIncludeFullPath( includeFullPath );
        xmlCreator.setMaxLevels( maxLevels );
        xmlCreator.setListFolders( listFolders );
        xmlCreator.setListResources( listResources );
        if ( includeUsageCount )
        {
            xmlCreator.setUsageCountMap( this.resourceService.getUsageCountMap() );
        }

        return xmlCreator.createResourceTreeXml( root );
    }

}
