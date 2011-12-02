/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;

public class UserParser
{
    private SecurityService securityService;

    private UserStoreService userStoreService;

    private UserDao userDao;

    private UserStoreParser userStoreParser;

    private boolean synchronizeUser = true;

    public UserParser( SecurityService securityService, UserStoreService userStoreService, UserDao userDao,
                       UserStoreParser userStoreParser )
    {
        this.securityService = securityService;
        this.userStoreService = userStoreService;
        this.userDao = userDao;
        this.userStoreParser = userStoreParser;
    }

    /**
     * Wether the user parser should synchronize the or not.
     *
     * @param synchronizeUser
     */
    public UserParser synchronizeUser( boolean synchronizeUser )
    {
        this.synchronizeUser = synchronizeUser;
        return this;
    }

    /**
     * @throws UserNotFoundException thrown when user is not found
     */
    public UserEntity parseUser( String userString )
        throws UserNotFoundException
    {
        UserEntity user;

        if ( userString == null )
        {
            user = parseUserByLoggedInPresentationUser();
        }
        else if ( userString.indexOf( ":" ) > 0 )
        {
            user = parseUserByQualifiedUsername( parseQualifiedUsername( userString ) );
        }
        else if ( userString.startsWith( "#" ) )
        {
            user = parseUserByKey( userString );
        }
        else
        {
            user = parseBuiltInUser( userString );
        }

        return user;
    }

    private UserEntity parseUserByLoggedInPresentationUser()
    {
        return securityService.getLoggedInPortalUserAsEntity();
    }

    private UserEntity parseUserByQualifiedUsername( final UserStoreAndQualifiedUsername userStoreAndQualifiedUsername )
    {
        final QualifiedUsername qualifiedUsername = userStoreAndQualifiedUsername.qualifiedUsername;
        UserEntity user = userDao.findByQualifiedUsername( qualifiedUsername );
        final String uid = qualifiedUsername.getUsername();

        if ( user == null )
        {
            // User not in db, try triggering a synchronize against user storage...
            UserKey userKey = synchronizeIfRemoteUserStore( userStoreAndQualifiedUsername.userStore, uid );

            if ( userKey == null )
            {
                throw new UserNotFoundException( qualifiedUsername );
            }

            user = userDao.findByKey( userKey );
        }
        else
        {
            // Do a synchronize to check that user also resides in userstore
            synchronizeIfRemoteUserStore( userStoreAndQualifiedUsername.userStore, uid );
            user = userDao.findByKey( user.getKey() );
        }

        if ( user == null || user.isDeleted() )
        {
            throw new UserNotFoundException( qualifiedUsername );
        }

        return user;
    }

    private UserEntity parseUserByKey( String userString )
    {
        UserKey userKey = new UserKey( userString.substring( 1 ) );
        UserEntity user = userDao.findByKey( userKey );
        if ( user == null )
        {
            throw new UserNotFoundException( userKey );
        }

        // Do a synchronize, in case user is deleted remotely
        if ( !user.isBuiltIn() )
        {
            synchronizeIfRemoteUserStore( user.getUserStore(), user.getName() );
            user = userDao.findByKey( userKey );
            if ( user.isDeleted() )
            {
                throw new UserNotFoundException( userKey );
            }
        }
        return user;
    }

    private UserEntity parseBuiltInUser( String userString )
    {
        UserEntity user;
        user = userDao.findBuiltInGlobalByName( userString );
        if ( user == null )
        {
            throw new UserNotFoundException( new QualifiedUsername( userString ) );
        }
        return user;
    }

    private UserKey synchronizeIfRemoteUserStore( UserStoreEntity userStore, String uid )
    {
        if ( synchronizeUser && userStore.isRemote() )
        {
            return userStoreService.synchronizeUser( userStore.getKey(), uid );
        }
        return null;
    }

    private UserStoreAndQualifiedUsername parseQualifiedUsername( String string )
    {
        final int colonIndex = string.indexOf( ":" );
        final String userStoreStr = string.substring( 0, colonIndex );
        final String username = string.substring( colonIndex + 1 );

        final UserStoreEntity userStore = userStoreParser.parseUserStore( userStoreStr );
        final UserStoreAndQualifiedUsername userStoreAndQualifiedUsername = new UserStoreAndQualifiedUsername();
        userStoreAndQualifiedUsername.userStore = userStore;
        userStoreAndQualifiedUsername.qualifiedUsername = new QualifiedUsername( userStore.getKey(), username );
        return userStoreAndQualifiedUsername;
    }

    private class UserStoreAndQualifiedUsername
    {
        private UserStoreEntity userStore;

        private QualifiedUsername qualifiedUsername;
    }
}
