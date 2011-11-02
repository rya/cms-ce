/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.util.Set;

import org.w3c.dom.Element;

import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.ContentAccessRight;
import com.enonic.vertical.engine.MenuAccessRight;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.LanguageKey;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

public interface AdminService
{

    public XMLDocument getPageTemplates( PageTemplateType type );

    public void copyMenu( User user, int menuKey, boolean includeContent );

    public boolean contentExists( int categoryKey, String contentTitle );

    public int getContentKey( int categoryKey, String contentTitle );

    public String getContentCreatedTimestamp( int contentKey );

    public java.util.Date getContentPublishFromTimestamp( int contentKey );

    public java.util.Date getContentPublishToTimestamp( int contentKey );

    public int getCategoryKey( int superCategoryKey, String name );

    public int createCategory( User user, int superCategoryKey, String name );

    public MenuItemKey getSectionKeyByMenuItemKey( MenuItemKey menuItemKey );

    public int createCategory( User user, String xmlData );

    public XMLDocument getCategory( User user, int categoryKey );

    public int getCategoryKey( int contentKey );

    public XMLDocument getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory );

    public void updateCategory( User user, String xmlData );

    public int createContentObject( User user, String xmlData );

    public int createContentType( User user, String xmlData );

    public void createLanguage( User user, String languageCode, String description );

    public int createMenu( User user, String xmlData );

    public void updateAccessRights( User user, String xmlData );

    public void updateMenuItem( User user, String xmlData );

    public void removeMenuItem( User user, int mikey );

    public int createMenuItem( User user, String xmlData );

    public int createPageTemplate( User user, String xmlData );

    public int copyPageTemplate( User user, int pageTemplateKey );

    public int createUnit( String xmlData );

    public String generateUID( String fName, String sName, UserStoreKey userStoreKey );

    public BinaryData getBinaryData( User user, int binaryDataKey );

    public XMLDocument getAccessRights( User user, int type, int key, boolean includeUserright );

    public XMLDocument getDefaultAccessRights( User user, int type, int key );

    public XMLDocument getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel );

    public String getCategoryName( int categoryKey );

    public XMLDocument getCategoryNameXML( int categoryKey );

    public CategoryAccessRight getCategoryAccessRight( User user, int categoryKey );

    public ContentAccessRight getContentAccessRight( User user, int categoryKey );

    public MenuAccessRight getMenuAccessRight( User user, int menuKey );

    public int[] getContentKeysByCategory( User user, int categoryKey );

    public XMLDocument getContentObject( int coc_lKey );

    public XMLDocument getContentObjectsByMenu( int menuKey );

    public String getContentTitle( int versionKey );

    public XMLDocument getContentType( int contentTypeKey );

    public XMLDocument getContentType( int contentTypeKey, boolean includeContentCount );

    public int getContentCountByContentType( int contentTypeKey );

    public int getContentTypeKey( int contentKey );

    public int[] getContentTypeKeysByHandler( String handlerClass );

    public int getContentTypeKeyByCategory( int categoryKey );

    public String getContentTypeName( int contentTypeKey );

    public XMLDocument getContentTypes();

    public XMLDocument getLanguage( LanguageKey languageKey );

    public XMLDocument getLanguages();

    public XMLDocument getMenu( User user, int menuKey, boolean complete );

    public MenuItemAccessRight getMenuItemAccessRight( User user, MenuItemKey key );

    public XMLDocument getMenuItem( User user, int key, boolean withParents );

    public XMLDocument getMenuItem( User user, int key, boolean withParents, boolean complete );

    public String getMenuItemName( int menuItemKey );

    public XMLDocument getMenuItemsByContentObject( User user, int cobKey );

    public XMLDocument getMenuItemsByPageTemplates( User user, int[] pageTemplateKeys );

    public String getPageTemplate( int pageTemplateKey );

    public XMLDocument getPageTemplatesByMenu( int menuKey, int[] excludeTypeKeys );

    public XMLDocument getPageTemplatesByContentObject( int contentObjectKey );

    public String getPageTemplParams( int pageTemplateKey );

    public int getSuperCategoryKey( int categoryKey );

    public XMLDocument getUnit( int unitKey );

    public String getUnitName( int unitKey );

    public int getUnitKey( int categoryKey );

    public int getUnitLanguageKey( int unitKey );

    public XMLDocument getUnitNamesXML( Filter filter );

    public boolean hasContent( int contentCategoryKey );

    public boolean hasSubCategories( int contentCategoryKey );

    public void regenerateIndexForContentType( int contentTypeKey );

    public void regenerateIndexForContentHandler( int contentHandlerKey );

    public void removeContentObject( int coc_lKey );

    public void removeContentType( User user, int contentTypeKey );

    public void removeLanguage( LanguageKey languageKey );

    public void removeMenu( User user, int key );

    public void removePageTemplate( int pageTemplateKey );

    public void updateContentObject( String xmlData );

    public void updateContentType( User user, String xmlData );

    public void updateLanguage( LanguageKey languageKey, String languageCode, String description );

    public void updateMenuData( User user, String xmlData );

    public void updatePageTemplate( User user, String xmlData );

    public void updateUnit( String xmlData );

    public XMLDocument getGroup( String gKey );

    public XMLDocument getContentTypeModuleData( int ctKey );

    public int getContentCount( int categoryKey, boolean recursive );

    public XMLDocument getContent( User user, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index, int count,
                                   int childrenLevel, int parentLevel, int parentChildrenLevel );

    public boolean isEnterpriseAdmin( User user );

    public boolean isAdmin( User user );

    public boolean isUserStoreAdmin( User user, UserStoreKey userStoreKey );

    public void moveCategory( User user, int catKey, int superCatKey );

    public XMLDocument getMenu( User user, CategoryCriteria criteria );

    public XMLDocument getPath( User user, int type, int key );

    public XMLDocument getMenusForAdmin( User user );

    public void moveMenuItem( User user, Element[] menuItemElems, int menuItemKey, int fromMenuKey, int fromParentKey, int toMenuKey,
                              int toParentKey );

    public void shiftMenuItems( User user, Element[] menuItemElems, int menuKey, int parentMenuItemKey );

    public Set<UserEntity> getUserNames( String[] groupKeys );

    public XMLDocument getContentHandler( int contentHandlerKey );

    public String getContentHandlerClassForContentType( int contentTypeKey );

    public XMLDocument getContentHandlers();

    public int createContentHandler( User user, String xmlData );

    public void updateContentHandler( User user, String xmlData );

    public void removeContentHandler( User user, int contentHandlerKey );

    public XMLDocument getContentTypes( boolean includeContentCount );

    public String getIndexingParametersXML( int contentTypeKey );

    public long getSectionContentTimestamp( MenuItemKey sectionKey );

    public XMLDocument getSections( User user, SectionCriteria criteria );

    public void removeSection( int sectionKey, boolean recursive );

    public void copySection( int sectionKey );

    public boolean isSectionOrdered( int sectionKey );

    public MenuItemKey getMenuItemKeyBySection( MenuItemKey sectionKey );

    public int getMenuKeyBySection( MenuItemKey sectionKey );

    public XMLDocument getContentTitlesBySection( MenuItemKey sectionKey, String orderBy, int fromIndex, int count,
                                                  boolean includeTotalCount, boolean approveOnly );

    public XMLDocument getContentTitles( int[] contentKeys );

    public XMLDocument getUsersWithPublishRight( int categoryKey );

    public XMLDocument getContentOwner( int contentKey );

    public XMLDocument getLogEntries( User user, com.enonic.esl.containers.MultiValueMap adminParams, int fromIdx, int count, boolean complete );

    public XMLDocument getLogEntry( String key );

    public int getMenuKeyByMenuItem( MenuItemKey menuItemKey );

    public int getParentMenuItemKey( int menuItemKey );

    public XMLDocument getCategoryPathXML( CategoryKey categoryKey, int[] contentTypes );

    public ResourceKey getContentTypeCSSKey( int contentTypeKey );

    public XMLDocument getContentTypes( int[] contentTypeKeys, boolean includeContentCount );

    public XMLDocument getData( User user, int type, int[] keys );

    public ResourceKey getDefaultCSSByMenu( int menuKey );

    public int getCurrentVersionKey( int contentKey );

    public int[] getBinaryDataKeysByVersion( int versionKey );

    public int getContentKeyByVersionKey( int versionKey );

    public XMLDocument getContentVersion( User user, int versionKey );

    public XMLDocument getContentXMLField( User user, int versionKey );

    public int[] getContentTypesByHandlerClass( String className );

    public int getBinaryDataKey( int contentKey, String label );

    public XMLDocument getCategoryMenu( User user, int categoryKey, int[] contentTypes, boolean includeRootCategories );

    public int getContentVersionState( int versionKey );

    public boolean initializeDatabaseSchema()
        throws Exception;

    public boolean initializeDatabaseValues()
        throws Exception;

    public boolean isContentVersionApproved( int versionKey );

    public XMLDocument getContentHomes( int contentKey );

    public String getPathString( int type, int key );

    public XMLDocument getContentTitleXML( int versionKey );

    public boolean hasContentPageTemplates( int menuKey, int contentTypeKey );

    public int getContentStatus( int versionKey );

    public XMLDocument getAdminMenu( User user, int menuKey );

    public XMLDocument getAdminMenuIncludeReadOnlyAccessRights( User user, int menuKey );

    public void updateMenuDetails( int menuKey, int frontPageKey, int loginPageKey, int errorPageKey, int defaultPageTemplateKey );

    public int getContentTypeKeyByName( String name );

    public XMLDocument getUnits();

    public long getArchiveSizeByCategory( int categoryKey );

    public long getArchiveSizeByUnit( int unitKey );

    public boolean isSiteAdmin( User user, SiteKey siteKey );

    public void cleanReadLogs( User user );

    public void cleanUnusedContent( User user );

    public XMLDocument getResourceTreeXml( String rootPath, boolean includeFullPath, boolean includeUsageCount, int maxLevels,
                                           boolean listFolders, boolean listResources );

    public boolean isDeveloper( User user );

}
