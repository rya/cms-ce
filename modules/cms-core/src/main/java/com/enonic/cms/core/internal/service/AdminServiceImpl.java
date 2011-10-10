/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import java.util.Date;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AdminEngine;
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

import com.enonic.cms.core.LanguageKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.resource.ResourceXmlCreator;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.resource.ResourceFolder;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
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

    public String getPageTemplates( PageTemplateType type )
    {
        return adminEngine.getPageTemplates( type );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 86400)
    public void copyMenu( User user, int menuKey, boolean includeContent )
        throws VerticalCopyException, VerticalSecurityException
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createCategory( User user, int superCategoryKey, String name )
        throws VerticalSecurityException
    {
        return adminEngine.createCategory( user, superCategoryKey, name );
    }

    public MenuItemKey getSectionKeyByMenuItemKey( MenuItemKey menuItemKey )
    {
        return adminEngine.getSectionKeyByMenuItemKey( menuItemKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createCategory( User user, String xmlData )
        throws VerticalSecurityException
    {
        return adminEngine.createCategory( user, xmlData );
    }

    public String getCategory( User user, int categoryKey )
    {
        return adminEngine.getCategory( user, categoryKey );
    }

    public int getCategoryKey( int contentKey )
    {
        return adminEngine.getCategoryKey( contentKey );
    }

    public String getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory )
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
    public String[] createLogEntries( User user, String xmlData )
    {
        return adminEngine.createLogEntries( user, xmlData );
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
        throws VerticalRemoveException, VerticalSecurityException
    {
        adminEngine.removeMenuItem( user, mikey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createMenuItem( User user, String xmlData )
        throws VerticalSecurityException
    {
        return adminEngine.createMenuItem( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createPageTemplate( User user, String xmlData )
        throws VerticalSecurityException
    {
        return adminEngine.createPageTemplate( xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int copyPageTemplate( User user, int pageTemplateKey )
        throws VerticalCopyException
    {
        return adminEngine.copyPageTemplate( user, pageTemplateKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createUnit( String xmlData )
        throws VerticalSecurityException
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

    public String getAccessRights( User user, int type, int key, boolean includeUserright )
    {
        return adminEngine.getAccessRights( user, type, key, includeUserright );
    }

    public String getDefaultAccessRights( User user, int type, int key )
    {
        return adminEngine.getDefaultAccessRights( user, type, key );
    }


    public String getContent( User user, int contentKey, int parentLevel, int childrenLevel, int parentChildrenLevel )
    {
        return adminEngine.getContent( user, contentKey, parentLevel, childrenLevel, parentChildrenLevel );
    }

    public String getCategoryName( int categoryKey )
    {

        return adminEngine.getCategoryName( categoryKey );
    }

    public String getCategoryNameXML( int categoryKey )
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

    public String getContentObject( int coc_lKey )
    {
        return adminEngine.getContentObject( coc_lKey );
    }

    /**
     * Return the content object run as user.
     */
    public User getContentObjectRunAs( int contentObjectKey )
    {
        return adminEngine.getContentObjectRunAs( contentObjectKey );
    }

    public String getContentObjectsByMenu( int menuKey )
    {
        return adminEngine.getContentObjectsByMenu( menuKey );
    }

    public String getContentTitle( int versionKey )
    {
        return adminEngine.getContentTitle( versionKey );
    }

    public String getContentType( int contentTypeKey )
    {
        return adminEngine.getContentType( contentTypeKey );
    }

    public String getContentType( int contentTypeKey, boolean includeContentCount )
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

    public String getContentTypes()
    {
        return adminEngine.getContentTypes();
    }

    public String getLanguage( LanguageKey languageKey )
    {
        return adminEngine.getLanguage( languageKey ).getAsString();
    }

    public String getLanguages()
    {
        return adminEngine.getLanguages().getAsString();
    }

    public String getMenu( User user, int menuKey, boolean complete )
    {
        return adminEngine.getMenu( user, menuKey, complete );
    }

    public String getMenuName( int menuKey )
    {
        return adminEngine.getMenuName( menuKey );
    }

    public MenuItemAccessRight getMenuItemAccessRight( User user, MenuItemKey key )
    {
        return adminEngine.getMenuItemAccessRight( user, key );
    }

    public String getMenuItem( User user, int key, boolean withParents )
    {
        return adminEngine.getMenuItem( user, key, withParents );
    }

    public String getMenuItem( User user, int key, boolean withParents, boolean complete )
    {
        return adminEngine.getMenuItem( user, key, withParents, complete );
    }

    public String getMenuItemName( int menuItemKey )
    {
        return adminEngine.getMenuItemName( menuItemKey );
    }

    public String getMenuItemsByContentObject( User user, int cobKey )
    {
        return adminEngine.getMenuItemsByContentObject( user, cobKey );
    }

    public String getMenuItemsByPageTemplates( User user, int[] pageTemplateKeys )
    {
        return adminEngine.getMenuItemsByPageTemplates( user, pageTemplateKeys );
    }

    public String getPageTemplate( int pageTemplateKey )
    {
        return adminEngine.getPageTemplate( new PageTemplateKey( pageTemplateKey ) );
    }

    public String getPageTemplatesByMenu( int menuKey, int[] excludeTypeKeys )
    {
        return adminEngine.getPageTemplatesByMenu( menuKey, excludeTypeKeys );
    }

    public String getPageTemplatesByContentObject( int contentObjectKey )
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

    public String getUnit( int unitKey )
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

    public String getUnitNamesXML( Filter filter )
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
        throws VerticalSecurityException, VerticalRemoveException
    {
        adminEngine.removeContentObject( coc_lKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeContentType( User user, int contentTypeKey )
        throws VerticalSecurityException, VerticalRemoveException
    {
        adminEngine.removeContentType( user, contentTypeKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeLanguage( LanguageKey languageKey )
        throws VerticalSecurityException, VerticalRemoveException
    {
        adminEngine.removeLanguage( languageKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = TIMEOUT_24HOURS)
    public void removeMenu( User user, int key )
        throws VerticalRemoveException, VerticalSecurityException
    {
        adminEngine.removeMenu( user, key );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removePageTemplate( int pageTemplateKey )
        throws VerticalSecurityException, VerticalRemoveException
    {
        adminEngine.removePageTemplate( pageTemplateKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContentObject( String xmlData )
        throws VerticalSecurityException, VerticalUpdateException
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
        throws VerticalSecurityException, VerticalUpdateException
    {
        adminEngine.updateUnit( xmlData );
    }

    public String getGroup( String gKey )
    {
        return adminEngine.getGroup( gKey );
    }

    public String getContentTypeModuleData( int ctKey )
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
        throws VerticalUpdateException, VerticalSecurityException
    {
        adminEngine.moveCategory( user, catKey, superCatKey );
    }

    public XMLDocument getMenu( User user, int type, Criteria criteria, boolean includeSubtrees )
    {
        return adminEngine.getMenu( user, type, criteria, includeSubtrees );
    }

    public String getPath( User user, int type, int key )
    {
        return adminEngine.getPath( user, type, key );
    }

    public String getMenusForAdmin( User user, MenuGetterSettings getterSettings )
    {
        return adminEngine.getMenusForAdmin( user, getterSettings );
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

    public String getUserNames( String[] groupKeys )
    {
        return adminEngine.getUserNames( groupKeys );
    }

    public String getContentHandler( int contentHandlerKey )
    {
        return adminEngine.getContentHandler( contentHandlerKey );
    }

    public String getContentHandlerClassForContentType( int contentTypeKey )
    {
        return adminEngine.getContentHandlerClassForContentType( contentTypeKey );
    }

    public String getContentHandlers()
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
        throws VerticalSecurityException, VerticalUpdateException
    {
        adminEngine.updateContentHandler( user, xmlData );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeContentHandler( User user, int contentHandlerKey )
        throws VerticalSecurityException, VerticalRemoveException
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

    public String getContentHandlerByContentType( int contentTypeKey )
    {
        return XMLTool.documentToString( adminEngine.getContentHandlerByContentType( contentTypeKey ) );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createSection( String xmlData )
        throws VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData );
        return adminEngine.createSection( doc );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSection( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData );
        adminEngine.updateSection( user, doc );
    }

    public long getSectionContentTimestamp( MenuItemKey sectionKey )
    {
        return adminEngine.getSectionContentTimestamp( sectionKey );
    }

    public String getSections( User user, SectionCriteria criteria )
    {
        return adminEngine.getSections( user, criteria );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void removeSection( int sectionKey, boolean recursive )
        throws VerticalRemoveException, VerticalSecurityException
    {
        adminEngine.removeSection( sectionKey, recursive );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void copySection( int sectionKey )
        throws VerticalCopyException, VerticalSecurityException
    {
        adminEngine.copySection( sectionKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void addContentToSections( User user, String xmlData )
        throws VerticalSecurityException
    {
        adminEngine.addContentToSections( user, xmlData );
    }

    public String getSuperSectionNames( MenuItemKey sectionKey, boolean includeSection )
    {
        return adminEngine.getSuperSectionNames( sectionKey, includeSection );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void setSectionContentsApproved( User user, int sectionKey, int[] contentKeys, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException
    {
        adminEngine.setSectionContentsApproved( user, sectionKey, contentKeys, approved );
    }

    public boolean isSectionOrdered( int sectionKey )
    {
        return adminEngine.isSectionOrdered( sectionKey );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSectionContent( User user, MenuItemKey sectionKey, int contentKey, int order, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException
    {
        adminEngine.updateSectionContent( user, sectionKey, contentKey, order, approved );
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

    public String getUsersWithPublishRight( int categoryKey )
    {
        return adminEngine.getUsersWithPublishRight( categoryKey );
    }

    public String getContentOwner( int contentKey )
    {
        return adminEngine.getContentOwner( contentKey );
    }

    public String getLogEntries( User user, MultiValueMap adminParams, int fromIdx, int count, boolean complete )
    {
        return adminEngine.getLogEntries( user, adminParams, fromIdx, count, complete );
    }

    public String getLogEntry( String key )
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

    public String getCategoryPathXML( CategoryKey categoryKey, int[] contentTypes )
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

    public String getData( User user, int type, int[] keys )
    {
        return adminEngine.getData( user, type, keys );
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

    public String getContentVersion( User user, int versionKey )
    {
        return adminEngine.getContentVersion( user, versionKey );
    }

    public String getContentXMLField( User user, int versionKey )
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

    public String getCategoryMenu( User user, int categoryKey, int[] contentTypes, boolean includeRootCategories )
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateContentPublishing( User user, int contentKey, int versionKey, int status, Date publishFrom, Date publishTo )
        throws VerticalUpdateException
    {
        adminEngine.updateContentPublishing( user, contentKey, versionKey, status, publishFrom, publishTo );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void setContentHome( User user, int contentKey, int menuKey, int menuItemKey, int pageTemplateKey )
        throws VerticalUpdateException
    {
        adminEngine.setContentHome( user, contentKey, menuKey, menuItemKey, pageTemplateKey );
    }

    public String getContentHomes( int contentKey )
    {
        return adminEngine.getContentHomes( contentKey );
    }

    public String getPathString( int type, int key )
    {
        return adminEngine.getPathString( type, key );
    }

    public String getContentTitleXML( int versionKey )
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
    public String getAdminMenu( User user, int menuKey )
    {
        return doGetAdminMenu( user, menuKey < 0 ? new int[0] : new int[]{menuKey}, null, false );
    }

    public String getAdminMenuIncludeReadOnlyAccessRights( User user, int menuKey )
    {
        return doGetAdminMenu( user, menuKey < 0 ? new int[0] : new int[]{menuKey}, null, true );
    }

    private String doGetAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {
        Document doc = this.adminEngine.getAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );
        return XMLTool.documentToString( doc );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateMenuDetails( int menuKey, int frontPageKey, int loginPageKey, int errorPageKey, int defaultPageTemplateKey )
        throws VerticalSecurityException
    {
        adminEngine.updateMenuDetails( menuKey, frontPageKey, loginPageKey, errorPageKey, defaultPageTemplateKey );
    }

    public int getContentTypeKeyByName( String name )
    {
        return adminEngine.getContentTypeKeyByName( name );
    }

    /**
     * Return a map of top level menus with name.
     */
    public Map<Integer, String> getMenuMap()
        throws Exception
    {
        return this.adminEngine.getMenuMap();
    }

    public String getUnits()
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
