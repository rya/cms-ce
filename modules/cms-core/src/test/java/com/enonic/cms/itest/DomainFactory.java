/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest;

import java.util.Date;

import org.jdom.Document;
import org.joda.time.DateTime;

import com.enonic.cms.framework.xml.XMLBytes;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.content.ContentAccessEntity;
import com.enonic.cms.domain.content.ContentAccessType;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerEntity;
import com.enonic.cms.domain.content.ContentHandlerKey;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.UnitEntity;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.category.CategoryAccessEntity;
import com.enonic.cms.domain.content.category.CategoryAccessKey;
import com.enonic.cms.domain.content.category.CategoryAccessType;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;
import com.enonic.cms.domain.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemType;
import com.enonic.cms.domain.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.domain.structure.menuitem.section.SectionContentKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;
import com.enonic.cms.domain.user.field.UserFieldType;

/**
 * Nov 26, 2009
 */
public class DomainFactory
{
    private int lastUsedId2 = -1;

    private DomainFixture fixture;

    public DomainFactory( DomainFixture fixture )
    {
        this.fixture = fixture;
        fixture.setFactory( this );
    }

    public LanguageEntity createLanguage( String code )
    {
        LanguageEntity language = new LanguageEntity();
        language.setKey( new LanguageKey( nextKey2() ) );
        language.setCode( code );
        language.setTimestamp( new Date() );
        return language;
    }

    public UserStoreEntity createUserStore( String name )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( nextKey2() ) );
        userStore.setName( name );
        userStore.setDeleted( false );
        return userStore;
    }

    public UserStoreEntity createUserStore( String name, String connectorName, boolean isDefault )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( nextKey2() ) );
        userStore.setName( name );
        userStore.setDeleted( false );
        userStore.setConnectorName( connectorName );
        userStore.setDefaultStore( isDefault );
        return userStore;
    }

    public UserEntity createUser( String uid, String displayName, UserType type, String userStoreName )
    {
        return createUser( uid, displayName, type, userStoreName, null );
    }

    public UserEntity createNormalUserInUserstore( String uid, String displayName, String userstoreName )
    {
        return createUser( uid, displayName, UserType.NORMAL, userstoreName, null );
    }

    public UserEntity createUser( String uid, String displayName, UserType type, String userStoreName, GroupEntity group )
    {
        UserEntity user = new UserEntity();
        user.setName( uid );
        user.setDisplayName( displayName );
        user.setSyncValue( uid );
        user.setTimestamp( new DateTime() );
        user.setType( type );
        user.setDeleted( 0 );
        if ( userStoreName != null )
        {
            user.setUserStore( fixture.findUserStoreByName( userStoreName ) );
        }
        if ( group != null )
        {
            user.setUserGroup( group );
        }

        return user;
    }

    public UserStoreUserFieldConfig createUserStoreUserFieldConfig( UserFieldType type, String properties )
    {
        UserStoreUserFieldConfig config = new UserStoreUserFieldConfig( type );
        config.setRemote( properties.contains( "remote" ) );
        config.setRequired( properties.contains( "required" ) );
        config.setReadOnly( properties.contains( "read-only" ) );
        return config;
    }

    public GroupEntity createGlobalGroup( String name )
    {
        GroupEntity group = createGroup( name, GroupType.GLOBAL_GROUP );
        return group;
    }

    public GroupEntity createGroup( String name, GroupType groupType )
    {
        GroupEntity group = new GroupEntity();
        group.setName( name );
        group.setSyncValue( "sync_" + name );
        group.setDeleted( 0 );
        group.setRestricted( 1 );
        group.setType( groupType );
        return group;
    }

    public GroupEntity createGroupInUserstore( String name, GroupType groupType, String userstoreName )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( Integer.toString( nextKey2() ) ) );
        group.setName( name );
        group.setSyncValue( "sync_" + name );
        group.setDeleted( 0 );
        group.setRestricted( 1 );
        group.setType( groupType );
        group.setUserStore( fixture.findUserStoreByName( userstoreName ) );
        return group;
    }


    public ContentHandlerEntity createContentHandler( String name, String handlerClassName )
    {
        ContentHandlerName contentHandlerName = ContentHandlerName.parse( handlerClassName );
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setKey( new ContentHandlerKey( nextKey2() ) );
        contentHandler.setName( name );
        contentHandler.setClassName( contentHandlerName.getHandlerClassShortName() );
        contentHandler.setTimestamp( new Date() );
        return contentHandler;
    }

    public ContentTypeEntity createContentType( String name, String contentHandlerClassName )
    {
        return createContentType( name, contentHandlerClassName, null );
    }

    public ContentTypeEntity createContentType( String name, String contentHandlerClassName, XMLBytes data )
    {
        ContentTypeEntity contenType = new ContentTypeEntity();
        contenType.setKey( nextKey2() );
        contenType.setName( name );
        contenType.setHandler( fixture.findContentHandlerByClassName( contentHandlerClassName ) );
        contenType.setTimestamp( new Date() );
        contenType.setData( data );
        return contenType;
    }

    public UnitEntity createUnit( String name )
    {
        return createUnit( name, null );
    }

    public UnitEntity createUnit( String name, String languageCode )
    {
        UnitEntity unit = new UnitEntity();
        unit.setKey( nextKey2() );
        unit.setName( name );
        unit.setLanguage( fixture.findLanguageByCode( languageCode ) );
        return unit;
    }

    public CategoryEntity createCategory( String name, String contentTypeName, String unitName, String ownerUid, String modifierUid )
    {
        return createCategory( name, contentTypeName, unitName, ownerUid, modifierUid, false );
    }

    public CategoryEntity createCategory( String name, String contentTypeName, String unitName, String ownerUid, String modifierUid,
                                          boolean autoApprove )
    {
        CategoryEntity category = new CategoryEntity();
        category.setKey( new CategoryKey( nextKey2() ) );
        category.setName( name );
        if ( contentTypeName != null )
        {
            category.setContentType( fixture.findContentTypeByName( contentTypeName ) );
        }
        category.setUnit( fixture.findUnitByName( unitName ) );
        category.setCreated( new Date() );
        category.setTimestamp( new Date() );
        category.setOwner( fixture.findUserByName( ownerUid ) );
        category.setModifier( fixture.findUserByName( modifierUid ) );
        category.setAutoMakeAvailable( autoApprove );
        category.setDeleted( false );
        return category;
    }

    public ContentEntity createContent( String categoryName, String languageCode, String ownerQualifiedName, String priority, Date created )
    {
        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( languageCode ) );
        content.setCategory( fixture.findCategoryByName( categoryName ) );
        content.setOwner( fixture.findUserByName( ownerQualifiedName ) );
        content.setPriority( Integer.valueOf( priority ) );
        content.setCreatedAt( created );  // Not-null field in database.
        content.setDeleted( false );      // Not-null field in database.
        content.setName( "testcontent_" + categoryName );
        return content;
    }

    public ContentVersionEntity createContentVersion( String status, String modiferQualifiedName )
    {
        ContentVersionEntity version = new ContentVersionEntity();
        version.setStatus( ContentStatus.get( Integer.valueOf( status ) ) );
        version.setModifiedBy( fixture.findUserByName( modiferQualifiedName ) );
        return version;
    }

    public BinaryDataAndBinary createBinaryDataAndBinary( String name, byte[] data )
    {
        BinaryDataEntity binaryData = createBinaryData( name, data.length );
        return new BinaryDataAndBinary( binaryData, data );
    }

    public BinaryDataEntity createBinaryData( String name, int size )
    {
        BinaryDataEntity binaryData = new BinaryDataEntity();
        binaryData.setName( name );
        binaryData.setCreatedAt( new Date() );
        binaryData.setSize( size );
        return binaryData;
    }

    public CategoryAccessEntity createCategoryAccess( String categoryName, GroupEntity group, String accesses )
    {
        CategoryEntity category = fixture.findCategoryByName( categoryName );

        CategoryAccessEntity access = new CategoryAccessEntity();
        access.setKey( new CategoryAccessKey( category.getKey(), group.getGroupKey() ) );
        access.setReadAccess( accesses.contains( CategoryAccessType.READ.toString().toLowerCase() ) );
        access.setAdminAccess( accesses.contains( CategoryAccessType.ADMINISTRATE.toString().toLowerCase() ) );
        access.setCreateAccess( accesses.contains( CategoryAccessType.CREATE.toString().toLowerCase() ) );
        access.setPublishAccess( accesses.contains( CategoryAccessType.APPROVE.toString().toLowerCase() ) );
        access.setAdminBrowseAccess( accesses.contains( CategoryAccessType.ADMIN_BROWSE.toString().toLowerCase() ) );
        return access;
    }

    public MenuItemAccessEntity createMenuItemAccess( String menuItemName, int menuItemOrder, GroupEntity group, String accesses )
    {
        MenuItemEntity menuItem = fixture.findMenuItemByName( menuItemName, menuItemOrder );

        MenuItemAccessEntity access = new MenuItemAccessEntity();
        access.setKey( new MenuItemAccessKey( menuItem.getKey(), group.getGroupKey() ) );
        access.setAddAccess( accesses.contains( MenuItemAccessType.ADD.toString().toLowerCase() ) );
        access.setAdminAccess( accesses.contains( MenuItemAccessType.ADMINISTRATE.toString().toLowerCase() ) );
        access.setCreateAccess( accesses.contains( MenuItemAccessType.CREATE.toString().toLowerCase() ) );
        access.setDeleteAccess( accesses.contains( MenuItemAccessType.DELETE.toString().toLowerCase() ) );
        access.setPublishAccess( accesses.contains( MenuItemAccessType.PUBLISH.toString().toLowerCase() ) );
        access.setReadAccess( accesses.contains( MenuItemAccessType.READ.toString().toLowerCase() ) );
        access.setUpdateAccess( accesses.contains( MenuItemAccessType.UPDATE.toString().toLowerCase() ) );
        return access;
    }

    public CategoryAccessEntity createCategoryAccess( String categoryName, UserEntity user, String accesses )
    {
        return createCategoryAccess( categoryName, user.getUserGroup(), accesses );
    }

    public CategoryAccessEntity createCategoryAccessForUser( String categoryName, String userName, String accesses )
    {
        UserEntity user = fixture.findUserByName( userName );
        return createCategoryAccess( categoryName, user.getUserGroup(), accesses );
    }

    public CategoryAccessEntity createCategoryAccessForGroup( String categoryName, String groupName, String accesses )
    {
        GroupEntity group = fixture.findGroupByName( groupName );
        return createCategoryAccess( categoryName, group, accesses );
    }

    public ContentAccessEntity createContentAccess( String accesses, GroupEntity group, ContentEntity content )
    {
        ContentAccessEntity access = new ContentAccessEntity();
        access.setContent( content );
        access.setGroup( group );
        access.setReadAccess( accesses.contains( ContentAccessType.READ.toString().toLowerCase() ) );
        access.setUpdateAccess( accesses.contains( ContentAccessType.UPDATE.toString().toLowerCase() ) );
        access.setDeleteAccess( accesses.contains( ContentAccessType.DELETE.toString().toLowerCase() ) );
        return access;
    }

    public ContentAccessEntity createContentAccess( ContentKey contentKey, GroupEntity group, String accesses )
    {
        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentAccessEntity access = new ContentAccessEntity();
        access.setContent( content );
        access.setGroup( group );
        access.setReadAccess( accesses.contains( ContentAccessType.READ.toString().toLowerCase() ) );
        access.setUpdateAccess( accesses.contains( ContentAccessType.UPDATE.toString().toLowerCase() ) );
        access.setDeleteAccess( accesses.contains( ContentAccessType.DELETE.toString().toLowerCase() ) );
        return access;
    }

    public ContentAccessEntity createContentAccess( ContentKey contentKey, UserEntity user, String accesses )
    {
        return createContentAccess( contentKey, user.getUserGroup(), accesses );
    }

    public SiteEntity createSite( String name, Date timestamp, Document xmlData, String language )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( nextKey2() );
        site.setName( name );
        site.setTimestamp( timestamp );
        site.setXmlData( xmlData );
        site.setLanguage( fixture.findLanguageByCode( language ) );
        return site;
    }

    public MenuItemEntity createSectionMenuItem( String name, int order, String menuName, String displayName, String site, String owner,
                                                 String modifier, String language, String parentName, Integer parentOrder,
                                                 boolean isOrderedSection, Date timestamp, boolean isHidden, Document xmlData )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( nextKey2() );
        menuItem.setName( name );
        menuItem.setMenuName( menuName );
        menuItem.setDisplayName( displayName );
        menuItem.setSite( fixture.findSiteByName( site ) );
        menuItem.setOrder( order );
        menuItem.setOwner( fixture.findUserByName( owner ) );
        menuItem.setModifier( fixture.findUserByName( modifier ) );
        menuItem.setType( MenuItemType.SECTION );
        menuItem.setSection( true );
        menuItem.setOrderedSection( isOrderedSection );
        menuItem.setLanguage( fixture.findLanguageByCode( language ) );
        if ( parentName != null && parentOrder != null )
        {
            menuItem.setParent( fixture.findMenuItemByName( parentName, parentOrder ) );
        }
        menuItem.setTimestamp( timestamp == null ? new Date() : timestamp );
        menuItem.setHidden( isHidden );
        menuItem.setXmlData( xmlData );
        return menuItem;
    }

    public MenuItemEntity createPageMenuItem( String name, int order, String menuName, String displayName, String site, String owner,
                                              String modifier, boolean hasSection, Boolean isOrderedSection, String language,
                                              String parentName, Integer parentOrder, Date timestamp, boolean isHidden, Document xmlData )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( nextKey2() );
        menuItem.setName( name );
        menuItem.setOrder( order );
        menuItem.setMenuName( menuName );
        menuItem.setDisplayName( displayName );
        menuItem.setSite( fixture.findSiteByName( site ) );
        menuItem.setOwner( fixture.findUserByName( owner ) );
        menuItem.setModifier( fixture.findUserByName( modifier ) );
        menuItem.setType( MenuItemType.PAGE );
        menuItem.setSection( hasSection );
        menuItem.setOrderedSection( isOrderedSection );
        menuItem.setLanguage( fixture.findLanguageByCode( language ) );
        if ( parentName != null && parentOrder != null )
        {
            menuItem.setParent( fixture.findMenuItemByName( parentName, parentOrder ) );
        }
        menuItem.setTimestamp( timestamp == null ? new Date() : timestamp );
        menuItem.setHidden( isHidden );
        menuItem.setXmlData( xmlData );
        return menuItem;
    }

    public PageTemplateEntity createSectionPagePageTemplate( String name, String site, Date timestamp )
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( nextKey2() );
        pageTemplate.setName( name );
        pageTemplate.setSite( fixture.findSiteByName( site ) );
        pageTemplate.setType( PageTemplateType.SECTIONPAGE );
        pageTemplate.setTimestamp( timestamp == null ? new Date() : timestamp );
        return pageTemplate;
    }

    public SectionContentEntity createContentSection( String menuItemName, int menuItemOrder, ContentKey contentKey, Boolean approved,
                                                      Integer order )
    {
        ContentEntity content = fixture.findContentByKey( contentKey );
        MenuItemEntity menuItem = fixture.findMenuItemByName( menuItemName, menuItemOrder );

        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( nextKey2() ) );
        sectionContent.setTimestamp( new Date() );
        sectionContent.setOrder( order );
        sectionContent.setApproved( approved );
        sectionContent.setContent( content );
        sectionContent.setMenuItem( menuItem );
        return sectionContent;

    }

    private synchronized int nextKey2()
    {
        return ++lastUsedId2;
    }
}