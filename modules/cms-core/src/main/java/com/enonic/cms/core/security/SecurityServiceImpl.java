/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.QualifiedGroupname;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.GroupQuery;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import com.enonic.cms.core.security.userstore.UserStoreService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreNotFoundException;

public class SecurityServiceImpl
    implements SecurityService
{

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private VerticalProperties verticalProperties;

    private void initializeSecurityHolder()
    {
        if ( SecurityHolder.getAnonUser() == null )
        {
            SecurityHolder.setAnonUser( userDao.findBuiltInAnonymousUser().getKey() );
        }
    }

    /**
     * @inheritDoc
     */
    public UserKey getAnonymousUserKey()
    {
        return userDao.findBuiltInAnonymousUser().getKey();
    }

    public UserEntity getAnonymousUser()
    {
        return userDao.findBuiltInAnonymousUser();
    }

    /**
     * @inheritDoc
     */
    public GroupKey getEnterpriseAdministratorGroup()
    {
        return groupDao.findBuiltInEnterpriseAdministrator().getGroupKey();
    }

    public GroupEntity getAuthenticatedUsersGroup( UserStoreEntity userStore )
    {

        return groupDao.findSingleByGroupTypeAndUserStore( GroupType.AUTHENTICATED_USERS, userStore.getKey() );
    }

    public UserEntity getUser( UserKey userKey )
    {
        final UserEntity user = userDao.findByKey( userKey );
        if ( user != null && user.isDeleted() )
        {
            return null;
        }
        return user;
    }

    public UserEntity getUser( QualifiedUsername qname )
    {
        final UserEntity user = userDao.findByQualifiedUsername( qname );
        if ( user != null && user.isDeleted() )
        {
            return null;
        }
        return user;
    }

    /**
     * @inheritDoc
     */
    public UserEntity getUserFromDefaultUserStore( String username )
    {
        UserStoreEntity defaultUserStore = userStoreService.getDefaultUserStore();

        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setUserStoreKey( defaultUserStore.getKey() );
        userSpecification.setName( username );
        userSpecification.setDeletedStateNotDeleted();
        return userDao.findSingleBySpecification( userSpecification );
    }


    private User doGetOldUserObject( UserKey userKey )
    {
        return userStoreService.getUserByKey( userKey );
    }


    public UserEntity getUser( User oldUserObject )
    {
        return userDao.findByKey( oldUserObject.getKey() );
    }

    /**
     * @inheritDoc
     */
    public List<UserEntity> getUsers( UserStoreKey userStoreKey, Integer index, Integer count, boolean includeDeleted )
    {

        UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        if ( userStore == null )
        {
            throw new UserStoreNotFoundException( userStoreKey );
        }

        return userDao.findByUserStoreKey( userStore.getKey(), index, count, includeDeleted );
    }

    /**
     * @inheritDoc
     */
    public GroupEntity getGroup( QualifiedGroupname qname )
    {

        if ( qname.isGlobal() )
        {
            return groupDao.findGlobalGroupByName( qname.getGroupname(), false );
        }
        else
        {
            return groupDao.findSingleUndeletedByUserStoreKeyAndGroupname( qname.getUserStoreKey(), qname.getGroupname() );
        }
    }

    /**
     * @inheritDoc
     */
    public GroupEntity getGroup( GroupKey key )
    {
        return groupDao.findByKey( key );
    }

    public List<GroupEntity> getGroups( GroupQuery spec )
    {
        spec.validate();
        return groupDao.findByQuery( spec );
    }

    public User getLoggedInPortalUser()
    {
        return userStoreService.getUserByKey( doGetUserKeyForLoggedInPortalUser() );
    }

    public UserEntity getLoggedInPortalUserAsEntity()
    {
        return userDao.findByKey( doGetUserKeyForLoggedInPortalUser() );
    }

    public User getOldUserObject()
    {
        return doGetOldUserObject( doGetUserKeyForLoggedInPortalUser() );
    }

    public String getUserName()
    {
        return getLoggedInPortalUser().getName();
    }

    public UserEntity getRunAsUser()
    {
        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setKey( doGetUserKeyForPortalExecutor() );
        userSpecification.setDeletedStateNotDeleted();

        return userDao.findSingleBySpecification( userSpecification );
    }

    public User getRunAsOldUser()
    {
        return doGetOldUserObject( doGetUserKeyForPortalExecutor() );
    }

    public void loginPortalUser( final QualifiedUsername qualifiedUsername, final String password )
    {
        doLoginPortalUser( qualifiedUsername, password, true );
    }

    public void loginClientApiUser( final QualifiedUsername qualifiedUsername, final String password )
    {
        doLoginPortalUser( qualifiedUsername, password, true );
    }

    public void loginDavUser( final QualifiedUsername qualifiedUsername, final String password )
    {
        doLoginPortalUser( qualifiedUsername, password, true );
    }

    private void doLoginPortalUser( final QualifiedUsername qualifiedUsername, final String password, final boolean verifyPassword )
    {
        final String uid = qualifiedUsername.getUsername();

        if ( UserEntity.isBuiltInUser( uid ) )
        {
            UserKey userKey = authenticateBuiltInUser( uid, password, verifyPassword );
            SecurityHolder.setUser( userKey );
        }
        else
        {

            UserStoreEntity userStore;
            if ( qualifiedUsername.hasUserStoreSet() )
            {
                userStore = doResolveUserStore( qualifiedUsername );
            }
            else
            {
                userStore = doGetDefaultUserStore();
            }

            if ( userStore == null )
            {
                throw new InvalidCredentialsException( qualifiedUsername );
            }

            if ( verifyPassword )
            {
                userStoreService.authenticateUser( userStore.getKey(), uid, password );
            }

            userStoreService.synchronizeUser( userStore.getKey(), uid );

            UserSpecification userSpec = new UserSpecification();
            userSpec.setDeletedStateNotDeleted();
            userSpec.setUserStoreKey( userStore.getKey() );
            userSpec.setName( uid );
            UserEntity user = userDao.findSingleBySpecification( userSpec );
            SecurityHolder.setUser( user.getKey() );
        }
    }

    public UserEntity impersonate( QualifiedUsername qualifiedUsername )
    {
        UserStoreEntity userStoreEntity = doResolveUserStore( qualifiedUsername );
        if ( userStoreEntity == null )
        {
            throw new IllegalArgumentException( "Userstore does not exist: " + qualifiedUsername );
        }
        UserStoreKey userStoreKey = userStoreEntity.getKey();
        return doImpersonate( qualifiedUsername.getUsername(), userStoreKey );
    }

    private UserEntity doImpersonate( String uid, UserStoreKey userStoreKey )
    {
        User current = getLoggedInPortalUser();
        if ( !current.isEnterpriseAdmin() )
        {
            throw new IllegalArgumentException( "Impersonate not allowed" );
        }

        UserSpecification userSpec = new UserSpecification();
        userSpec.setUserStoreKey( userStoreKey );
        userSpec.setName( uid );
        userSpec.setDeletedStateNotDeleted();
        UserEntity user = userDao.findSingleBySpecification( userSpec );

        if ( user == null )
        {
            throw new IllegalStateException( "User to impersonate not found." );
        }
        else
        {
            SecurityHolder.setRunAsUser( user.getKey() );
        }
        return user;
    }

    public void logoutPortalUser()
    {
        doLogoutPortalUser( true );
    }

    public void logoutClientApiUser( boolean invalidateSession )
    {
        doLogoutPortalUser( invalidateSession );
    }

    public void changePassword( final QualifiedUsername qualifiedUsername, final String newPassword )
    {
        final String uid = qualifiedUsername.getUsername();
        final UserEntity user = userDao.findByQualifiedUsername( qualifiedUsername );
        if ( user == null )
        {
            throw new IllegalArgumentException( "Could not find user: " + qualifiedUsername );
        }
        final UserStoreKey userStoreKey = user.getUserStore() == null ? null : user.getUserStore().getKey();
        userStoreService.changePassword( userStoreKey, uid, newPassword );
    }

    private void doLogoutPortalUser( boolean invalidateSession )
    {
        SecurityHolder.setUser( null );
        SecurityHolder.setRunAsUser( null );
        SecurityHolder.setSubject( null );

        // Only invalidate session if logged out of both "portal" and "admin". Check admin user!
        if ( SecurityHolderAdmin.getUser() == null )
        {
            if ( invalidateSession )
            {
                invalidateSession();
            }
        }
    }

    private void invalidateSession()
    {
        HttpServletRequest request = ServletRequestAccessor.getRequest();
        HttpSession session = request.getSession( false );
        if ( null != session )
        {
            session.invalidate();
        }
    }

    private UserStoreEntity doGetDefaultUserStore()
    {

        UserStoreEntity defaultUserStore = userStoreDao.findDefaultUserStore();
        if ( defaultUserStore == null )
        {
            throw new IllegalStateException( "Expected default user store to be set" );
        }
        return defaultUserStore;
    }

    private UserStoreEntity doResolveUserStore( final QualifiedUsername qualifiedUsername )
    {

        UserStoreKey userStoreKey = qualifiedUsername.getUserStoreKey();
        if ( userStoreKey != null )
        {
            /* Key passed in as a part of the qualifiedUsername. Use it. */
            return userStoreDao.findByKey( userStoreKey );
        }

        if ( qualifiedUsername.hasUserStoreNameSet() )
        {
            /* Key not passed in as a part of the qualifiedUsername. But name is. Trying to find the key from name. */
            return userStoreDao.findByName( qualifiedUsername.getUserStoreName() );
        }

        throw new IllegalArgumentException( "Given qualified username has no user store set" );
    }

    private UserKey authenticateBuiltInUser( final String uid, final String password, final boolean verifyPassword )
    {
        final UserEntity user = userDao.findBuiltInGlobalByName( uid );

        if ( user == null )
        {
            throw new IllegalArgumentException( "Could not retrieve built-in user: " + uid );
        }

        if ( user.isRoot() )
        {
            if ( !verifyPassword || verticalProperties.getAdminPassword().equals( password ) )
            {
                return user.getKey();
            }
            throw new InvalidCredentialsException( uid );
        }

        if ( user.isAnonymous() )
        {
            return user.getKey();
        }

        throw new IllegalArgumentException( "Cannot authenticate built in user: " + uid );
    }

    private UserKey doGetUserKeyForLoggedInPortalUser()
    {
        initializeSecurityHolder();
        return SecurityHolder.getUser();
    }

    private UserKey doGetUserKeyForPortalExecutor()
    {
        initializeSecurityHolder();
        return SecurityHolder.getRunAsUser();
    }
}
