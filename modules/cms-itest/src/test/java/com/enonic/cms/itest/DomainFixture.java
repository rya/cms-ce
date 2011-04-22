/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest;

import java.util.List;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.structure.SiteEntity;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerKey;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.RelatedContentEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Nov 26, 2009
 */
public class DomainFixture
{
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    public DomainFixture( HibernateTemplate hibernateTemplate )
    {
        this.hibernateTemplate = hibernateTemplate;
    }

    void setFactory( DomainFactory factory )
    {
        this.factory = factory;
    }

    private void deleteAllEntities( Class entityClass )
    {
        List entities = hibernateTemplate.find( "from " + entityClass.getName() );
        hibernateTemplate.deleteAll( entities );
        hibernateTemplate.flush();
    }

    public void initSystemData()
    {
        hibernateTemplate.clear();

        /*deleteAllEntities( ContentAccessEntity.class );
        deleteAllEntities( CategoryAccessEntity.class );
        deleteAllEntities( BinaryDataEntity.class );
        deleteAllEntities( ContentBinaryDataEntity.class );
        deleteAllEntities( ContentVersionEntity.class );
        deleteAllEntities( ContentEntity.class );
        deleteAllEntities( CategoryEntity.class );
        deleteAllEntities( UnitEntity.class );
        deleteAllEntities( LanguageEntity.class );
        deleteAllEntities( GroupEntity.class );
        deleteAllEntities( UserEntity.class );
        deleteAllEntities( UserStoreEntity.class );*/

        save( factory.createLanguage( "en" ) );

        save( factory.createGroup( GroupType.ENTERPRISE_ADMINS.getName(), GroupType.ENTERPRISE_ADMINS ) );
        save( factory.createGroup( GroupType.ADMINS.getName(), GroupType.ADMINS ) );
        save( factory.createGroup( GroupType.DEVELOPERS.getName(), GroupType.DEVELOPERS ) );
        save( factory.createGroup( GroupType.EXPERT_CONTRIBUTORS.getName(), GroupType.EXPERT_CONTRIBUTORS ) );
        save( factory.createGroup( GroupType.CONTRIBUTORS.getName(), GroupType.CONTRIBUTORS ) );

        save( factory.createUserStore( "testuserstore" ) );
        save( factory.createGroupInUserstore( GroupType.AUTHENTICATED_USERS.getName(), GroupType.AUTHENTICATED_USERS, "testuserstore" ) );

        createAndStoreUserAndUserGroup( "anonymous", UserType.ANONYMOUS.getName(), UserType.ANONYMOUS, null );

        save( factory.createUser( User.ROOT_UID, "Enterprise Admin", UserType.ADMINISTRATOR, null ) );

        flushAndClearHibernateSesssion();
    }

    public UserEntity createAndStoreNormalUserWithUserGroup( String uid, String displayName, String userStoreName )
    {
        return createAndStoreUserAndUserGroup( uid, displayName, UserType.NORMAL, userStoreName );
    }

    public UserEntity createAndStoreUserAndUserGroup( String uid, String displayName, UserType type, String userStoreName )
    {
        GroupEntity userGroup = new GroupEntity();
        userGroup.setName( uid );
        userGroup.setSyncValue( uid );
        userGroup.setDeleted( 0 );
        userGroup.setType( GroupType.resolveAssociate( type ) );
        userGroup.setRestricted( 1 );
        hibernateTemplate.save( userGroup );

        UserEntity user = factory.createUser( uid, displayName, type, userStoreName );
        user.setUserGroup( userGroup );

        hibernateTemplate.save( user );
        hibernateTemplate.flush();

        return user;
    }

    public List<LanguageEntity> findAllLanguage()
    {
        return typecastList( LanguageEntity.class, hibernateTemplate.find( "from LanguageEntity" ) );
    }

    public LanguageEntity findLanguageByCode( String value )
    {
        LanguageEntity example = new LanguageEntity();
        example.setCode( value );
        return (LanguageEntity) findFirstByExample( example );
    }

    public UserStoreEntity findUserStoreByName( String userStoreName )
    {
        UserStoreEntity example = new UserStoreEntity();
        example.setName( userStoreName );
        return (UserStoreEntity) findFirstByExample( example );
    }

    public int countUsersByType( UserType type )
    {
        UserEntity example = new UserEntity();
        example.setType( type );
        return findByExample( example ).size();
    }

    public UserEntity findUserByKey( String value )
    {
        return findUserByKey( value );
    }

    public UserEntity findUserByKey( UserKey value )
    {
        UserEntity example = new UserEntity();
        example.setKey( value );
        return (UserEntity) findFirstByExample( example );
    }

    public UserEntity findUserByName( String value )
    {
        UserEntity example = new UserEntity();
        example.setName( value );
        return (UserEntity) findFirstByExample( example );
    }

    public UserEntity findUserByType( UserType userType )
    {
        UserEntity example = new UserEntity();
        example.setType( userType );
        return (UserEntity) findFirstByExample( example );
    }

    public GroupEntity findGroupByKey( String groupKey )
    {
        GroupEntity example = new GroupEntity();
        example.setKey( groupKey );
        return (GroupEntity) findFirstByExample( example );
    }

    public GroupEntity findGroupByName( String groupName )
    {
        GroupEntity example = new GroupEntity();
        example.setName( groupName );
        return (GroupEntity) findFirstByExample( example );
    }

    public GroupEntity findGroupByType( GroupType groupType )
    {
        GroupEntity example = new GroupEntity();
        example.setType( groupType );
        return (GroupEntity) findFirstByExample( example );
    }

    public GroupEntity findGroupByTypeAndUserstore( GroupType groupType, String userstoreName )
    {
        GroupEntity example = new GroupEntity();
        example.setType( groupType );
        example.setUserStore( findUserStoreByName( userstoreName ) );
        return (GroupEntity) findFirstByExample( example );
    }

    public ContentHandlerEntity findContentHandlerByKey( String value )
    {
        ContentHandlerEntity example = new ContentHandlerEntity();
        example.setKey( new ContentHandlerKey( value ) );
        return (ContentHandlerEntity) findFirstByExample( example );
    }

    public ContentHandlerEntity findContentHandlerByClassName( String value )
    {
        ContentHandlerEntity example = new ContentHandlerEntity();
        example.setClassName( value );
        return (ContentHandlerEntity) findFirstByExample( example );
    }

    public ContentTypeEntity findContentTypeByName( String value )
    {
        ContentTypeEntity example = new ContentTypeEntity();
        example.setName( value );
        return (ContentTypeEntity) findFirstByExample( example );
    }

    public UnitEntity findUnitByName( String value )
    {
        UnitEntity example = new UnitEntity();
        example.setName( value );
        return (UnitEntity) findFirstByExample( example );
    }

    public CategoryEntity findCategoryByKey( CategoryKey key )
    {
        List<CategoryEntity> list =
            typecastList( CategoryEntity.class, hibernateTemplate.find( "from CategoryEntity where key = ?", key ) );
        if ( list.isEmpty() )
        {
            return null;
        }
        return list.get( 0 );
    }

    public CategoryEntity findCategoryByName( String value )
    {
        CategoryEntity example = new CategoryEntity();
        example.setName( value );
        return (CategoryEntity) findFirstByExample( example );
    }

    public ContentEntity findFirstContentByCategory( CategoryEntity category )
    {
        ContentEntity example = new ContentEntity();
        example.setCategory( category );
        return (ContentEntity) findFirstByExample( example );
    }

    public int countAllContent()
    {
        List<ContentEntity> list = typecastList( ContentEntity.class, hibernateTemplate.find( "from ContentEntity" ) );
        return list.size();
    }

    public List<ContentEntity> findAllContent()
    {
        return typecastList( ContentEntity.class, hibernateTemplate.find( "from ContentEntity" ) );
    }

    public ContentEntity findContentByKey( ContentKey contentKey )
    {
        List<ContentEntity> list =
            typecastList( ContentEntity.class, hibernateTemplate.find( "from ContentEntity where key = ?", contentKey ) );
        if ( list.isEmpty() )
        {
            return null;
        }
        return list.get( 0 );
    }

    public List<ContentVersionEntity> findContentVersionsByContent( ContentKey key )
    {
        return typecastList( ContentVersionEntity.class,
                             hibernateTemplate.find( "from ContentVersionEntity where content.key = ? order by key ", key ) );
    }

    public int countContentVersionsByTitle( String title )
    {
        ContentVersionEntity example = new ContentVersionEntity();
        example.setTitle( title );
        List<ContentVersionEntity> list = typecastList( ContentVersionEntity.class, findByExample( example ) );
        return list.size();
    }

    public List<ContentVersionEntity> findContentVersionsByTitle( String title )
    {
        ContentVersionEntity example = new ContentVersionEntity();
        example.setTitle( title );
        return typecastList( ContentVersionEntity.class, findByExample( example ) );
    }

    public ContentVersionEntity findFirstContentVersionByTitle( String title )
    {
        ContentVersionEntity example = new ContentVersionEntity();
        example.setTitle( title );
        return (ContentVersionEntity) findFirstByExample( example );
    }

    public ContentVersionEntity findContentVersionByTitle( int index, String title )
    {
        ContentVersionEntity example = new ContentVersionEntity();
        example.setTitle( title );
        List<ContentVersionEntity> list = typecastList( ContentVersionEntity.class, findByExample( example ) );
        return list.get( index );
    }

    public ContentVersionEntity findContentVersionByKey( ContentVersionKey key )
    {
        List<ContentVersionEntity> list =
            typecastList( ContentVersionEntity.class, hibernateTemplate.find( "from ContentVersionEntity where key = ?", key ) );
        if ( list.isEmpty() )
        {
            return null;
        }
        return list.get( 0 );
    }

    public ContentVersionEntity findContentVersionByContent( int index, ContentKey key )
    {
        List<ContentVersionEntity> list = typecastList( ContentVersionEntity.class, hibernateTemplate.find(
            "from ContentVersionEntity where content.key = ? order by key ", key ) );
        if ( list.isEmpty() )
        {
            return null;
        }
        return list.get( index );
    }

    public int countContentVersionsByContent( ContentKey key )
    {
        List<ContentVersionEntity> list =
            typecastList( ContentVersionEntity.class, hibernateTemplate.find( "from ContentVersionEntity where content.key = ?", key ) );
        return list.size();
    }

    public List<RelatedContentEntity> findRelatedContentsByContentVersionKey( ContentVersionKey versionKey )
    {
        return typecastList( RelatedContentEntity.class,
                             hibernateTemplate.find( "from RelatedContentEntity where key.parentContentVersionKey = ?", versionKey ) );
    }

    public MenuItemEntity findMenuItemByName( String name, int order )
    {
        MenuItemEntity example = new MenuItemEntity();
        example.setName( name );
        example.setOrder( order );
        return (MenuItemEntity) findFirstByExample( example );
    }

    public SiteEntity findSiteByName( String value )
    {
        SiteEntity example = new SiteEntity();
        example.setName( value );
        return (SiteEntity) findFirstByExample( example );
    }

    public void save( Object... objects )
    {
        for ( Object obj : objects )
        {
            hibernateTemplate.save( obj );
        }

        flushAndClearHibernateSesssion();
    }

    public void saveOrUpdate( Object... objects )
    {
        for ( Object obj : objects )
        {
            hibernateTemplate.saveOrUpdate( obj );
        }

        flushAndClearHibernateSesssion();
    }

    public void flushAndClearHibernateSesssion()
    {
        hibernateTemplate.flush();
        hibernateTemplate.clear();
    }

    private List findByExample( Object example )
    {
        return hibernateTemplate.findByExample( example );
    }

    private Object findFirstByExample( Object example )
    {
        List list = hibernateTemplate.findByExample( example );
        if ( list.isEmpty() )
        {
            return null;
        }

        return list.get( 0 );
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> typecastList( Class<T> clazz, Object list )
    {
        return (List<T>) list;
    }
}