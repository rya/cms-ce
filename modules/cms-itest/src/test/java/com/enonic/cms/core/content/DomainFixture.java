/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.category.CategoryAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;

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

        deleteAllEntities( ContentAccessEntity.class );
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
        deleteAllEntities( UserStoreEntity.class );

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
        CategoryEntity example = new CategoryEntity();
        example.setKey( key );
        return (CategoryEntity) findFirstByExample( example );
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

    public ContentEntity findContentByKey( ContentKey contentKey )
    {
        ContentEntity example = new ContentEntity();
        example.setKey( contentKey );
        return (ContentEntity) findFirstByExample( example );
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


    public void save( Object... objects )
    {
        for ( Object obj : objects )
        {
            hibernateTemplate.save( obj );
        }

        flushAndClearHibernateSesssion();
    }

    public void flushAndClearHibernateSesssion()
    {
        hibernateTemplate.flush();
        hibernateTemplate.clear();
    }


}
