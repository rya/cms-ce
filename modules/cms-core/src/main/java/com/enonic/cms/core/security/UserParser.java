/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserNotFoundException;
import com.enonic.cms.domain.security.user.UserSpecification;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

public class UserParser
{

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserStoreParser userStoreParser;

    @Autowired
    private UserDao userDao;

    public UserEntity parseUser( String userString )
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
        else
        {
            user = parseUserByKey( new UserKey( userString.substring( 1 ) ) );
        }

        return user;
    }

    private UserEntity parseUserByLoggedInPresentationUser()
    {
        User loggedInUser;
        try
        {
            loggedInUser = securityService.getLoggedInPortalUser();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to get logged in user", e );
        }

        final UserKey userKey = loggedInUser.getKey();
        final UserEntity user = securityService.getUser( userKey );
        if ( user == null )
        {
            throw new UserNotFoundException( userKey );
        }
        return user;
    }

    private UserEntity parseUserByQualifiedUsername( QualifiedUsername qualifiedUsername )
    {
        UserEntity user = securityService.getUser( qualifiedUsername );

        if ( user == null )
        {
            // User not in db, try trigging a synchronize against user storeage...
            UserStoreKey userStoreKey = qualifiedUsername.getUserStoreKey();
            String uid = qualifiedUsername.getUsername();
            UserKey userKey = userStoreService.synchronizeUser( userStoreKey, uid );

            if ( userKey == null )
            {
                throw new UserNotFoundException( qualifiedUsername );
            }

            user = securityService.getUser( userKey );
        }
        else
        {
            UserSpecification userSpec = new UserSpecification();
            userSpec.setKey( user.getKey() );
            // Do a synchronize to check that user also resides in userstore
            userStoreService.synchronizeUser( userSpec );
            user = securityService.getUser( user.getKey() );
        }

        if ( user == null )
        {
            throw new UserNotFoundException( qualifiedUsername );
        }

        return user;
    }


    private UserEntity parseUserByKey( UserKey userKey )
    {
        UserSpecification userSpecification = new UserSpecification();
        userSpecification.setKey( userKey );
        userSpecification.setDeletedState( UserSpecification.DeletedState.ANY );
        UserEntity user = userDao.findSingleBySpecification( userSpecification );

        if ( user == null )
        {
            throw new UserNotFoundException( userKey );
        }

        // Do a synchronize, in case user is deleted remotely
        if ( !( user.isRoot() || user.isAnonymous() ) )
        {
            userStoreService.synchronizeUser( userSpecification );

            if ( securityService.getUser( userKey ) == null )
            {
                throw new UserNotFoundException( userKey );
            }
        }
        return user;
    }


    private QualifiedUsername parseQualifiedUsername( String string )
    {

        if ( string == null )
        {
            return null;
        }

        final int colonIndex = string.indexOf( ":" );
        String userStore = string.substring( 0, colonIndex );
        String username = string.substring( colonIndex + 1 );

        UserStoreEntity userStoreEntity = userStoreParser.parseUserStore( userStore );
        return new QualifiedUsername( userStoreEntity.getKey(), username );
    }
}
