/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

/**
 * Jul 21, 2009
 */
public class MemberOfResolver
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;


    public boolean hasEnterpriseAdminPowers( final UserKey userKey )
    {
        return hasEnterpriseAdminPowers( userDao.findByKey( userKey ) );
    }

    public boolean hasEnterpriseAdminPowers( final UserEntity user )
    {
        if ( user.isRoot() )
        {
            return true;
        }

        return isMemberOfEnterpriseAdminGroup( user );
    }

    public boolean hasUserStoreAdministratorPowers( final UserKey userKey, final UserStoreKey userStoreKey )
    {
        return hasUserStoreAdministratorPowers( userDao.findByKey( userKey ), userStoreKey );
    }

    public boolean hasUserStoreAdministratorPowers( final UserEntity user, final UserStoreKey userStoreKey )
    {
        if ( hasEnterpriseAdminPowers( user ) )
        {
            return true;
        }

        final GroupEntity userStoreAdministratorGroup = groupDao.findBuiltInUserStoreAdministrator( userStoreKey );
        return isUserMemberOfGroup( user, userStoreAdministratorGroup );
    }

    public boolean hasAdministratorPowers( UserKey userKey )
    {
        return hasAdministratorPowers( userDao.findByKey( userKey ) );
    }

    public boolean hasAdministratorPowers( UserEntity user )
    {
        if ( hasEnterpriseAdminPowers( user ) )
        {
            return true;
        }

        return isMemberOfAdministratorsGroup( user );
    }

    public boolean hasDeveloperPowers( UserKey userKey )
    {
        return hasDeveloperPowers( userDao.findByKey( userKey ) );
    }

    public boolean hasDeveloperPowers( UserEntity user )
    {
        if ( hasAdministratorPowers( user ) )
        {
            return true;
        }

        return isMemberOfDevelopersGroup( user );
    }

    public boolean hasExpertContributorPowers( UserEntity user )
    {
        if ( hasDeveloperPowers( user ) )
        {
            return true;
        }

        return isMemberOfExpertContributorGroup( user );
    }

    public boolean hasExpertContributorPowers( UserKey userKey )
    {
        return hasExpertContributorPowers( userDao.findByKey( userKey ) );
    }

    public boolean hasContributorPowers( UserEntity user )
    {
        if ( hasExpertContributorPowers( user ) )
        {
            return true;
        }

        return isMemberOfContributorGroup( user );
    }

    public boolean hasContributorPowers( UserKey userKey )
    {
        return hasContributorPowers( userDao.findByKey( userKey ) );
    }

    public boolean isMemberOfEnterpriseAdminGroup( final UserEntity user )
    {
        GroupEntity group = groupDao.findBuiltInEnterpriseAdministrator();
        return isUserMemberOfGroup( user, group );
    }

    public boolean isMemberOfAdministratorsGroup( final UserEntity user )
    {
        GroupEntity group = groupDao.findBuiltInAdministrator();
        return isUserMemberOfGroup( user, group );
    }

    public boolean isMemberOfDevelopersGroup( final UserEntity user )
    {
        GroupEntity group = groupDao.findBuiltInDeveloper();
        return isUserMemberOfGroup( user, group );
    }

    public boolean isMemberOfContributorGroup( final UserEntity user )
    {
        GroupEntity group = groupDao.findBuiltInContributor();
        return isUserMemberOfGroup( user, group );
    }

    public boolean isMemberOfExpertContributorGroup( final UserEntity user )
    {
        GroupEntity group = groupDao.findBuiltInExpertContributor();
        return isUserMemberOfGroup( user, group );
    }

    public boolean isUserMemberOfGroup( final UserEntity user, final GroupEntity group )
    {
        Assert.notNull( user, "user cannot be null" );
        Assert.notNull( group, "group cannot be null" );

        // Check if Anonymous group is member of the group
        GroupEntity anonymousGroup = groupDao.findBuiltInAnonymous();
        if ( anonymousGroup.isMemberOf( group, true ) )
        {
            return true;
        }

        // Check if Authenticated user is member of the group
        UserStoreKey userStoreKey = user.getUserStoreKey();
        if ( userStoreKey != null && !user.isAnonymous() )
        {
            GroupEntity authenticatedGroup = groupDao.findBuiltInAuthenticatedUsers( userStoreKey );
            if ( authenticatedGroup.isMemberOf( group, true ) )
            {
                return true;
            }
        }

        return user.isMemberOf( group, true );
    }
}
