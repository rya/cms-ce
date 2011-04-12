/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;


public abstract class AbstractAccessResolver<TEntity, TAccessType>
{
    private final GroupDao groupDao;

    private GroupEntity anonymousGroup;

    private GroupEntity enterpriseAdminsGroup;

    private final Map<UserStoreKey, GroupEntity> authenticatedUsersGroupByUserStoreKey = new HashMap<UserStoreKey, GroupEntity>();

    protected AbstractAccessResolver( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    protected GroupEntity getAnonymousGroup()
    {
        if ( anonymousGroup == null )
        {
            anonymousGroup = groupDao.findBuiltInAnonymous();
        }
        return anonymousGroup;
    }

    protected GroupEntity getEnterpriseAdminsGroup()
    {
        if ( enterpriseAdminsGroup == null )
        {
            enterpriseAdminsGroup = groupDao.findBuiltInEnterpriseAdministrator();
        }
        return enterpriseAdminsGroup;
    }

    protected GroupEntity getAuthenticatedUsersGroup( UserStoreEntity userstore )
    {
        GroupEntity authenticatedUsersGroup = authenticatedUsersGroupByUserStoreKey.get( userstore.getKey() );
        if ( authenticatedUsersGroup == null )
        {
            authenticatedUsersGroup = groupDao.findBuiltInAuthenticatedUsers( userstore.getKey() );
            authenticatedUsersGroupByUserStoreKey.put( userstore.getKey(), authenticatedUsersGroup );
        }
        return authenticatedUsersGroup;
    }

    protected boolean doHasAccess( final UserEntity user, final TEntity entity, final TAccessType accessType )
    {
        if ( user == null )
        {
            throw new IllegalArgumentException( "Given user cannot be null" );
        }

        if ( hasAccess( entity, getAnonymousGroup(), accessType, false ) )
        {
            return true;
        }

        // if user is anonymous, user does not have any rights since we checked that above
        if ( user.isAnonymous() )
        {
            return false;
        }

        // deep check if user have access
        if ( user.getUserGroup() != null )
        {
            if ( hasAccess( entity, user.getUserGroup(), accessType, true ) )
            {
                return true;
            }
        }

        // check "authenticated users" group
        if ( user.getUserStore() != null )
        {
            final GroupEntity authenticatedUsersGroup = getAuthenticatedUsersGroup( user.getUserStore() );
            // NB! All users are always implicit member of authenticated users
            if ( hasAccess( entity, authenticatedUsersGroup, accessType, true ) )
            {
                return true;
            }
        }

        // check "enterprise admins" group if user is member of that
        if ( user.isMemberOf( getEnterpriseAdminsGroup(), true ) )
        {
            return true;
        }

        if ( user.isRoot() )
        {
            return true;
        }

        return false;
    }


    protected abstract boolean hasAccess( TEntity entity, GroupEntity group, TAccessType accessType, boolean checkMemberships );


}
