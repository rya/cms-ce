/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.domain.EntityPageList;

public interface UserStoreDao
    extends EntityDao<UserStoreEntity>
{
    UserStoreEntity findByKey( UserStoreKey key );

    UserStoreEntity findByName( String name );

    UserStoreEntity findDefaultUserStore();

    List<UserStoreEntity> findAll();

    EntityPageList<UserStoreEntity> findAll( int index, int count );
}

