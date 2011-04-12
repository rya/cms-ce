/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class UserStoreEntityDao
    extends AbstractBaseEntityDao<UserStoreEntity>
    implements UserStoreDao
{

    public UserStoreEntity findByKey( UserStoreKey key )
    {
        return get( UserStoreEntity.class, key );
    }

    public UserStoreEntity findByName( String name )
    {
        name = name.replace( '%', ' ' ); // Usikker p� hva dette er til, sjekk om dette kan fjernes
        name = name.toLowerCase();
        return findSingleByNamedQuery( UserStoreEntity.class, "UserStoreEntity.findByName", "name", name );
    }

    public UserStoreEntity findDefaultUserStore()
    {
        return findSingleByNamedQuery( UserStoreEntity.class, "UserStoreEntity.findDefaultUserStore" );
    }

    public List<UserStoreEntity> findAll()
    {
        return findByNamedQuery( UserStoreEntity.class, "UserStoreEntity.findAll" );
    }

    public EntityPageList<UserStoreEntity> findAll( int index, int count )
    {
        return findPageList( UserStoreEntity.class, "x.deleted = 0", index, count );
    }
}