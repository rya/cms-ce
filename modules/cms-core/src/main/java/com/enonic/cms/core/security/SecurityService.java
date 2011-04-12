/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import java.util.List;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.QualifiedGroupname;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.store.dao.GroupQuery;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public interface SecurityService
{

    UserKey getAnonymousUserKey();

    UserEntity getAnonymousUser();

    GroupKey getEnterpriseAdministratorGroup();

    GroupEntity getAuthenticatedUsersGroup( UserStoreEntity userStore );

    /**
     * Finds the specified user unless it is deleted.
     *
     * @param qname The fully qualified userName, which includes user store and uid.
     * @return Only non-deleted users.
     */
    UserEntity getUser( QualifiedUsername qname );

    /**
     * Finds the specified user unless it is deleted.
     *
     * @param username The id of the user in the given user store.
     * @return Only non-deleted users.
     */
    UserEntity getUserFromDefaultUserStore( String username );

    /**
     * Finds specified user unless it is deleted;
     *
     * @return Only non-deleted users.
     */
    UserEntity getUser( UserKey userKey );

    UserEntity getUser( User oldUserObject );

    List<UserEntity> getUsers( UserStoreKey userStoreKey, Integer index, Integer count, boolean includeDeleted );

    /**
     * Finds group specified by qname. Does not find deleted groups.
     *
     * @param qname The qualified group name.
     * @return The requested group.
     */
    GroupEntity getGroup( QualifiedGroupname qname );

    GroupEntity getGroup( GroupKey key );

    List<GroupEntity> getGroups( GroupQuery spec );

    void loginPortalUser( final QualifiedUsername qualifiedUsername, final String password );

    void loginClientApiUser( final QualifiedUsername qualifiedUsername, final String password );

    void loginDavUser( final QualifiedUsername qualifiedUsername, final String password );

    /**
     * Impersonate given user. This user then becomes then currently run-as user.
     *
     * @param qualifiedUsername The fully qualified userName, which includes user store and uid.
     */
    UserEntity impersonate( QualifiedUsername qualifiedUsername );

    @Transactional(propagation = Propagation.REQUIRED)
    User getLoggedInPortalUser();

    UserEntity getLoggedInPortalUserAsEntity();

    User getOldUserObject();

    /**
     * @return the user name of the current logged in user.
     */
    String getUserName();

    /**
     * @return The user you are currently running as. The run-as user will only be different from logged in user if you have impersonated
     *         some other user.
     */
    UserEntity getRunAsUser();

    User getRunAsOldUser();

    void logoutPortalUser();

    void logoutClientApiUser( boolean invalidateSession );

    void changePassword( final QualifiedUsername qualifiedUsername, final String newPassword );
}
