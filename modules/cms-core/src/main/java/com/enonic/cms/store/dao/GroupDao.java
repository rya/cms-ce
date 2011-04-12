/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreKey;


public interface GroupDao
    extends EntityDao<GroupEntity>
{
    GroupEntity find( String groupKey );

    GroupEntity findByKey( GroupKey groupKey );

    Collection<GroupEntity> findAll( boolean includeDeleted );

    List<GroupEntity> findBySpecification( GroupSpecification specification );

    GroupEntity findBuiltInEnterpriseAdministrator();

    GroupEntity findBuiltInAdministrator();

    GroupEntity findBuiltInDeveloper();

    GroupEntity findBuiltInExpertContributor();

    GroupEntity findBuiltInContributor();

    GroupEntity findBuiltInAnonymous();

    GroupEntity findBuiltInUserStoreAdministrator( UserStoreKey userStoreKey );

    GroupEntity findBuiltInAuthenticatedUsers( UserStoreKey userStoreKey );

    GroupEntity findSingleBySpecification( GroupSpecification specification );

    Collection<GroupEntity> findByUserstore( UserStoreKey userStoreKey, boolean includeDeleted );

    List<GroupEntity> findByUserStoreKeyAndGroupname( UserStoreKey userStoreKey, String groupName, boolean includeDeleted );

    GroupEntity findSingleUndeletedByUserStoreKeyAndGroupname( UserStoreKey userStoreKey, String groupName );

    GroupEntity findSingleByGroupType( GroupType groupType );

    GroupEntity findSingleByGroupTypeAndUserStore( GroupType groupType, UserStoreKey userStoreKey );

    GroupEntity findGlobalGroupByName( String name, boolean includeDeleted );

    List<GroupEntity> findByQuery( GroupQuery spec );

    GroupEntity findByUserStoreKeyAndSyncValue( UserStoreKey userStoreKey, String syncValue, boolean includeDeleted );

    EntityPageList<GroupEntity> findAll( int index, int count );
}
