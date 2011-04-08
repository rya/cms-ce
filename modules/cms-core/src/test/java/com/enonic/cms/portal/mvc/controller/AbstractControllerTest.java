/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserType;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

public abstract class AbstractControllerTest
{

    private static int lastUsedId = -1;

    @Autowired
    protected HibernateTemplate hibernateTemplate;

    protected void initSystemData()
    {
        hibernateTemplate.save( createLanguage( "en" ) );
        hibernateTemplate.save( createUserStore( "testuserstore" ) );
        hibernateTemplate.save( createGroup( "ENTADM", GroupType.ENTERPRISE_ADMINS.getName(), GroupType.ENTERPRISE_ADMINS ) );
        hibernateTemplate.save( createGroupInUserstore( "AUTHUSERS", GroupType.AUTHENTICATED_USERS.getName(), GroupType.AUTHENTICATED_USERS,
                                                        "testuserstore" ) );
        hibernateTemplate.save( createUser( "ANON", "anonymous", "Anonmymous User", UserType.ANONYMOUS, "testuserstore" ) );
        hibernateTemplate.save( createUserGroup( "GROUPANON", "anonymous", GroupType.ANONYMOUS ) );
    }


    private LanguageEntity createLanguage( String code )
    {
        LanguageEntity language = new LanguageEntity();
        language.setKey( new LanguageKey( ++lastUsedId ) );
        language.setCode( code );
        language.setTimestamp( new Date() );
        return language;
    }

    private UserStoreEntity createUserStore( String name )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( ++lastUsedId ) );
        userStore.setName( name );
        return userStore;
    }

    private UserEntity createUser( String key, String uid, String fullName, UserType type, String userStoreName )
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( key ) );
        user.setName( uid );
        user.setDisplayName( fullName );
        user.setSyncValue( uid );
        user.setTimestamp( new DateTime() );
        user.setType( type );
        user.setDeleted( 0 );
        if ( userStoreName != null )
        {
            user.setUserStore( findUserStoreByName( userStoreName ) );
        }

        return user;
    }

    private GroupEntity createGroup( String key, String name, GroupType groupType )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( key );
        group.setName( name );
        group.setSyncValue( "sync_" + name );
        group.setDeleted( 0 );
        group.setType( groupType );
        return group;
    }

    private GroupEntity createGroupInUserstore( String key, String name, GroupType groupType, String userstoreName )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( key );
        group.setName( name );
        group.setSyncValue( "sync_" + name );
        group.setDeleted( 0 );
        group.setType( groupType );
        group.setUserStore( findUserStoreByName( userstoreName ) );
        return group;
    }

    private GroupEntity createUserGroup( String key, String username, GroupType groupType )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( key );
        group.setName( "userGroup_" + username );
        final UserEntity user = findUserByName( username );
        if ( user != null )
        {
            group.setUser( user );
        }
        group.setSyncValue( "userGroup_" + username );
        group.setDeleted( 0 );
        group.setType( groupType );
        return group;
    }

    private UserStoreEntity findUserStoreByName( String userStoreName )
    {
        UserStoreEntity example = new UserStoreEntity();
        example.setName( userStoreName );
        return (UserStoreEntity) findFirstByExample( example );
    }

    private UserEntity findUserByName( String value )
    {
        UserEntity example = new UserEntity();
        example.setName( value );
        return (UserEntity) findFirstByExample( example );
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

}
