/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;

public interface UserDao
    extends EntityDao<UserEntity>
{
    List<UserEntity> findAll( boolean deleted );

    List<UserEntity> findBySpecification( final UserSpecification spec );

    UserEntity findSingleBySpecification( final UserSpecification spec );

    UserEntity findBuiltInAnonymousUser();

    UserEntity findBuiltInEnterpriseAdminUser();

    UserEntity findByKey( String key );

    UserEntity findByKey( UserKey key );

    UserEntity findByQualifiedUsername( final QualifiedUsername qualifiedUsername );

    UserEntity findBuiltInGlobalByName( final String uid );

    UserEntity findByUserStoreKeyAndUsername( final UserStoreKey userStoreKey, final String uid );

    List<UserEntity> findByUserStoreKey( UserStoreKey userStoreKey, Integer index, Integer count, boolean includeDeleted );

    List<UserEntity> findByQuery( UserStoreKey userStoreKey, String queryStr, String orderBy, boolean orderAscending );

    EntityPageList<UserEntity> findAll( int index, int count );
}
