/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.util.Date;
import java.util.Map;

import org.w3c.dom.Element;

import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.ContentAccessRight;
import com.enonic.vertical.engine.MenuAccessRight;
import com.enonic.vertical.engine.MenuGetterSettings;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.VerticalCopyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;

public interface AdminService
{

    public String getPageTemplates( PageTemplateType type );

    public void copyMenu( User user, int menuKey, boolean includeContent )
        throws VerticalCopyException, VerticalSecurityException;

    public boolean contentExists( int categoryKey, String contentTitle );

    public int getContentKey( int categoryKey, String contentTitle );

    public String getContentCreatedTimestamp( int contentKey );

    public java.util.Date getContentPublishFromTimestamp( int contentKey );

    public java.util.Date getContentPublishToTimestamp( int contentKey );

    public int getCategoryKey( int superCategoryKey, String name );

    public int createCategory( User user, int superCategoryKey, String name )
        throws VerticalSecurityException;

    public MenuItemKey getSectionKeyByMenuItemKey( MenuItemKey menuItemKey );

    public int createCategory( User user, String xmlData )
        throws VerticalSecurityException;

    public String getCategory( User user, int categoryKey );

    public int getCategoryKey( int contentKey );

    public String getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory );

    public void removeCategory( User user, int categoryKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public void updateCategory( User user, String xmlData )
        throws VerticalSecurityException, VerticalUpdateException;

    public int createContentObject( User user, String xmlData )
        throws VerticalSecurityException;

    public int createContentType( User user, String xmlData )
        throws VerticalSecurityException;

    public void createLanguage( User user, String languageCode, String description )
        throws VerticalSecurityException;

    public String[] createLogEntries( User user, String xmlData )
        throws VerticalSecurityException;

    public int createMenu( User user, String xmlData )
        throws VerticalSecurityException;

    public void updateAccessRights( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException;

    public void updateMenuItem( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException;

    public void removeMenuItem( User user, int mikey )
        throws VerticalRemoveException, VerticalSecurityException;

    public int createMenuItem( User user, String xmlData )
        throws VerticalSecurityException;

    public int createPageTemplate( User user, String xmlData )
        throws VerticalSecurityException;

    public int copyPageTemplate( User user, int pageTemplateKey )
        throws VerticalCopyException;

    public int createUnit( String xmlData )
        throws VerticalSecurityException;

    public String generateUID( String fName, String sName, UserStoreKey userStoreKey );

    public BinaryData getBinaryData( User user, int binaryDataKey );

    public String getAccessRights( User user, int type, int key, boolean includeUserright );

    public String getDefaultAccessRights( User user, int type, int key );


    public String getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel );

    public String getCategoryName( int categoryKey );

    public String getCategoryNameXML( int categoryKey );

    public CategoryAccessRight getCategoryAccessRight( User user, int categoryKey );

    public ContentAccessRight getContentAccessRight( User user, int categoryKey );

    public MenuAccessRight getMenuAccessRight( User user, int menuKey );

    public int[] getContentKeysByCategory( User user, int categoryKey );

    public String getContentObject( int coc_lKey );

    public User getContentObjectRunAs( int contentObjectKey );

    public String getContentObjectsByMenu( int menuKey );

    public String getContentTitle( int versionKey );

    public String getContentType( int contentTypeKey );

    public String getContentType( int contentTypeKey, boolean includeContentCount );

    public int getContentCountByContentType( int contentTypeKey );

    public int getContentTypeKey( int contentKey );

    public int[] getContentTypeKeysByHandler( String handlerClass );

    public int getContentTypeKeyByCategory( int categoryKey );

    public String getContentTypeName( int contentTypeKey );

    public String getContentTypes();

    public String getLanguage( LanguageKey languageKey );

    public String getLanguages();

    //public String getMenuData(User user);

    public String getMenu( User user, int menuKey, boolean complete );

    public String getMenuName( int menuKey );

    public MenuItemAccessRight getMenuItemAccessRight( User user, MenuItemKey key );

    public String getMenuItem( User user, int key, boolean withParents );

    public String getMenuItem( User user, int key, boolean withParents, boolean complete );

    public String getMenuItemName( int menuItemKey );

    public String getMenuItemsByContentObject( User user, int cobKey );

    public String getMenuItemsByPageTemplates( User user, int[] pageTemplateKeys );

    public String getPageTemplate( int pageTemplateKey );

    public String getPageTemplatesByMenu( int menuKey, int[] excludeTypeKeys );

    public String getPageTemplatesByContentObject( int contentObjectKey );

    public String getPageTemplParams( int pageTemplateKey );

    public int getSuperCategoryKey( int categoryKey );

    public String getUnit( int unitKey );

    public String getUnitName( int unitKey );

    public int getUnitKey( int categoryKey );

    public int getUnitLanguageKey( int unitKey );

    public String getUnitNamesXML( Filter filter );

    public boolean hasContent( int contentCategoryKey );

    public boolean hasSubCategories( int contentCategoryKey );

    public void regenerateIndexForContentType( int contentTypeKey );

    public void regenerateIndexForContentHandler( int contentHandlerKey );

    public void removeContentObject( int coc_lKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public void removeContentType( User user, int contentTypeKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public void removeLanguage( LanguageKey languageKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public void removeMenu( User user, int key )
        throws VerticalRemoveException, VerticalSecurityException;

    public void removePageTemplate( int pageTemplateKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public void removeUnit( User user, int unitKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public void updateContentObject( String xmlData )
        throws VerticalSecurityException, VerticalUpdateException;

    public void updateContentType( User user, String xmlData )
        throws VerticalSecurityException, VerticalUpdateException;

    public void updateLanguage( LanguageKey languageKey, String languageCode, String description )
        throws VerticalSecurityException, VerticalUpdateException;

    public void updateMenuData( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException;

    public void updatePageTemplate( User user, String xmlData )
        throws VerticalSecurityException, VerticalUpdateException;

    public void updateUnit( String xmlData )
        throws VerticalSecurityException, VerticalUpdateException;

    public String getGroup( String gKey );

    public String getContentTypeModuleData( int ctKey );

    public int getContentCount( int categoryKey, boolean recursive );

    public XMLDocument getContent( User user, CategoryKey categoryKey, boolean includeSubCategories, String orderBy, int index, int count,
                                   int childrenLevel, int parentLevel, int parentChildrenLevel );

    public boolean isEnterpriseAdmin( User user );

    public boolean isAdmin( User user );

    public boolean isUserStoreAdmin( User user, UserStoreKey userStoreKey );

    public void moveCategory( User user, int catKey, int superCatKey )
        throws VerticalUpdateException, VerticalSecurityException;

    public XMLDocument getMenu( User user, int type, Criteria criteria, boolean includeSubtrees );

    public String getPath( User user, int type, int key );

    public String getMenusForAdmin( User user, MenuGetterSettings getterSettings );

    public void moveMenuItem( User user, Element[] menuItemElems, int menuItemKey, int fromMenuKey, int fromParentKey, int toMenuKey,
                              int toParentKey );

    public void shiftMenuItems( User user, Element[] menuItemElems, int menuKey, int parentMenuItemKey );

    public String getUserNames( String[] groupKeys );

    public String getContentHandler( int contentHandlerKey );

    public String getContentHandlerClassForContentType( int contentTypeKey );

    public String getContentHandlers();

    public int createContentHandler( User user, String xmlData )
        throws VerticalSecurityException;

    public void updateContentHandler( User user, String xmlData )
        throws VerticalSecurityException, VerticalUpdateException;

    public void removeContentHandler( User user, int contentHandlerKey )
        throws VerticalSecurityException, VerticalRemoveException;

    public XMLDocument getContentTypes( boolean includeContentCount );

    public String getIndexingParametersXML( int contentTypeKey );

    public String getContentHandlerByContentType( int contentTypeKey );

    public int createSection( String xmlData )
        throws VerticalSecurityException;

    public void updateSection( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException;

    public long getSectionContentTimestamp( MenuItemKey sectionKey );

    public String getSections( User user, SectionCriteria criteria );

    public void removeSection( int sectionKey, boolean recursive )
        throws VerticalRemoveException, VerticalSecurityException;

    public void copySection( int sectionKey )
        throws VerticalCopyException, VerticalSecurityException;

    public void addContentToSections( User user, String xmlData )
        throws VerticalSecurityException;

    public String getSuperSectionNames( MenuItemKey sectionKey, boolean includeSection );

    public void setSectionContentsApproved( User user, int sectionKey, int[] contentKeys, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException;

    public boolean isSectionOrdered( int sectionKey );

    public void updateSectionContent( User user, MenuItemKey sectionKey, int contentKey, int order, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException;

    public MenuItemKey getMenuItemKeyBySection( MenuItemKey sectionKey );

    public int getMenuKeyBySection( MenuItemKey sectionKey );

    public XMLDocument getContentTitlesBySection( MenuItemKey sectionKey, String orderBy, int fromIndex, int count,
                                                  boolean includeTotalCount, boolean approveOnly );

    public XMLDocument getContentTitles( int[] contentKeys );

    public String getUsersWithPublishRight( int categoryKey );

    public String getContentOwner( int contentKey );

    public String getLogEntries( User user, com.enonic.esl.containers.MultiValueMap adminParams, int fromIdx, int count, boolean complete );

    public String getLogEntry( String key );

    public int getMenuKeyByMenuItem( MenuItemKey menuItemKey );

    public int getParentMenuItemKey( int menuItemKey );

    public String getCategoryPathXML( CategoryKey categoryKey, int[] contentTypes );

    public ResourceKey getContentTypeCSSKey( int contentTypeKey );

    public XMLDocument getContentTypes( int[] contentTypeKeys, boolean includeContentCount );

    public String getData( User user, int type, int[] keys );

    public ResourceKey getDefaultCSSByMenu( int menuKey );

    public int getCurrentVersionKey( int contentKey );

    public int[] getBinaryDataKeysByVersion( int versionKey );

    public int getContentKeyByVersionKey( int versionKey );

    public String getContentVersion( User user, int versionKey );

    public String getContentXMLField( User user, int versionKey );

    public int[] getContentTypesByHandlerClass( String className );

    public int getBinaryDataKey( int contentKey, String label );

    public String getCategoryMenu( User user, int categoryKey, int[] contentTypes, boolean includeRootCategories );

    public int getContentVersionState( int versionKey );

    public boolean initializeDatabaseSchema()
        throws Exception;

    public boolean initializeDatabaseValues()
        throws Exception;


    public PageTemplateType getPageTemplateType( int pageTemplateKey );

    public boolean isContentVersionApproved( int versionKey );

    public void updateContentPublishing( User user, int contentKey, int versionKey, int status, Date publishFrom, Date publishTo )
        throws VerticalUpdateException;

    public void setContentHome( User user, int contentKey, int menuKey, int menuItemKey, int pageTemplateKey )
        throws VerticalUpdateException;

    public String getContentHomes( int contentKey );

    public String getPathString( int type, int key );

    public String getContentTitleXML( int versionKey );

    public boolean hasContentPageTemplates( int menuKey, int contentTypeKey );

    public int getContentStatus( int versionKey );

    public String getAdminMenu( User user, int menuKey );

    public String getAdminMenuIncludeReadOnlyAccessRights( User user, int menuKey );

    public void updateMenuDetails( int menuKey, int frontPageKey, int loginPageKey, int errorPageKey, int defaultPageTemplateKey )
        throws VerticalSecurityException;

    public int getContentTypeKeyByName( String name );

    public Map getMenuMap()
        throws Exception;

    public String getUnits();

    public long getArchiveSizeByCategory( int categoryKey );

    public long getArchiveSizeByUnit( int unitKey );

    public boolean isSiteAdmin( User user, SiteKey siteKey );

    public void cleanReadLogs( User user );

    public void cleanUnusedContent( User user );

    public XMLDocument getResourceTreeXml( String rootPath, boolean includeFullPath, boolean includeUsageCount, int maxLevels,
                                           boolean listFolders, boolean listResources );

    public boolean isDeveloper( User user );

}
