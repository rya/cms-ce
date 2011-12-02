package com.enonic.cms.core.security;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class UserParserTest
{

    private SecurityService securityService;

    private UserStoreService userStoreService;

    private UserDao userDao;

    private UserStoreDao userStoreDao;

    @Before
    public void before()
    {
        securityService = Mockito.mock( SecurityService.class );
        userStoreService = Mockito.mock( UserStoreService.class );
        userDao = Mockito.mock( UserDao.class );
        userStoreDao = Mockito.mock( UserStoreDao.class );
    }

    @Test
    public void parseUser_returns_logged_in_user_when_given_string_is_null()
    {
        // setup
        Mockito.when( securityService.getLoggedInPortalUserAsEntity() ).thenReturn(
            createUser( "ABC", "myuser", createUserStore( 1, "myUserStore" ) ) );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        UserEntity actualUser = parser.parseUser( null );

        // verify
        assertEquals( "myuser", actualUser.getName() );
    }

    @Test
    public void parseUser_returns_admin_user_when_given_string_is_admin()
    {
        // setup
        UserEntity admin = createUser( "666", "admin", null );
        admin.setType( UserType.ADMINISTRATOR );
        Mockito.when( userDao.findBuiltInGlobalByName( "admin" ) ).thenReturn( admin );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        UserEntity actualUser = parser.parseUser( "admin" );

        // verify
        assertEquals( "admin", actualUser.getName() );
    }

    @Test
    public void parseUser_returns_anonymous_user_when_given_string_is_anonymous()
    {
        // setup
        UserEntity admin = createUser( "333", "anonymous", null );
        admin.setType( UserType.ANONYMOUS );
        Mockito.when( userDao.findBuiltInGlobalByName( "anonymous" ) ).thenReturn( admin );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        UserEntity actualUser = parser.parseUser( "anonymous" );

        // verify
        assertEquals( "anonymous", actualUser.getName() );
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_non_existing_built_in_user()
    {
        // setup
        Mockito.when( userDao.findBuiltInGlobalByName( "nonexisting" ) ).thenReturn( null );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        try
        {
            parser.parseUser( "nonexisting" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void parseUser_returns_user_when_given_string_is_matching_user_by_key_prefixed_by_hash()
    {
        // setup
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn(
            createUser( "ABC", "myuser", createUserStore( 1, "myUserStore" ) ) );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        UserEntity actualUser = parser.parseUser( "#ABC" );

        // verify
        assertEquals( "myuser", actualUser.getName() );
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_matching_user_by_key_but_user_is_deleted()
    {
        // setup
        UserEntity user = createUser( "ABC", "myuser", createUserStore( 1, "myUserStore" ) );
        user.setDeleted( true );
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( user );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        try
        {
            parser.parseUser( "#ABC" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_not_matching_a_user_by_key()
    {
        // setup
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( null );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        try
        {
            parser.parseUser( "#ABC" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void parseUser_returns_user_when_given_string_is_matching_user_by_qualifiedName()
    {
        // setup
        UserStoreEntity myUserStore = createUserStore( 1, "myUserStore" );
        UserEntity user = createUser( "ABC", "myuser", myUserStore );
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( user );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( myUserStore );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.<QualifiedUsername>any() ) ).thenReturn( user );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        UserEntity actualUser = parser.parseUser( "myUserStore:myuser" );

        // verify
        assertEquals( "myuser", actualUser.getName() );
    }

    @Test
    public void parseUser_returns_user_when_given_string_is_matching_user_by_qualifiedName_that_does_not_exists_in_db_but_remote()
    {
        // setup
        UserStoreEntity myUserStore = createUserStore( 1, "myUserStore" );
        UserEntity user = createUser( "ABC", "myuser", myUserStore );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.<QualifiedUsername>any() ) ).thenReturn( null );
        Mockito.when( userStoreService.synchronizeUser( new UserStoreKey( 1 ), "myuser" ) ).thenReturn( user.getKey() );
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( user );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( myUserStore );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        UserEntity actualUser = parser.parseUser( "myUserStore:myuser" );

        // verify
        assertEquals( "myuser", actualUser.getName() );
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_matching_user_by_qualifiedName_that_does_not_exists_in_db_and_userStore_is_local()
    {
        // setup
        UserStoreEntity localUserStore = createUserStore( 1, "localUserStore" );
        localUserStore.setConnectorName( null );
        UserEntity user = createUser( "ABC", "myuser", localUserStore );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.<QualifiedUsername>any() ) ).thenReturn( null );
        Mockito.when( userStoreService.synchronizeUser( new UserStoreKey( 1 ), "myuser" ) ).thenReturn( user.getKey() );
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( user );
        Mockito.when( userStoreDao.findByName( "localUserStore" ) ).thenReturn( localUserStore );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        try
        {
            parser.parseUser( "localUserStore:myuser" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_matching_user_by_qualifiedName_and_synchronization_is_off_that_does_not_exists_in_db_but_remote()
    {
        // setup
        UserStoreEntity localUserStore = createUserStore( 1, "localUserStore" );
        UserEntity user = createUser( "ABC", "myuser", localUserStore );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.<QualifiedUsername>any() ) ).thenReturn( null );
        Mockito.when( userStoreService.synchronizeUser( new UserStoreKey( 1 ), "myuser" ) ).thenReturn( user.getKey() );
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( user );
        Mockito.when( userStoreDao.findByName( "localUserStore" ) ).thenReturn( localUserStore );

        // exercise
        UserParser parser =
            new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) ).synchronizeUser( false );
        try
        {
            parser.parseUser( "localUserStore:myuser" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_matching_user_by_qualifiedName_that_does_not_exists_in_db__and_neither_remote()
    {
        // setup
        UserStoreEntity myUserStore = createUserStore( 1, "myUserStore" );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.<QualifiedUsername>any() ) ).thenReturn( null );
        Mockito.when( userStoreService.synchronizeUser( new UserStoreKey( 1 ), "myuser" ) ).thenReturn( null );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( myUserStore );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        try
        {
            parser.parseUser( "myUserStore:myuser" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    @Test
    public void parseUser_throws_UserNotFoundException_when_given_string_is_matching_user_by_qualifiedName_but_user_is_deleted()
    {
        // setup
        UserStoreEntity myUserStore = createUserStore( 1, "myUserStore" );
        UserEntity user = createUser( "ABC", "myuser", myUserStore );
        user.setDeleted( true );
        Mockito.when( userDao.findByKey( new UserKey( "ABC" ) ) ).thenReturn( user );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( myUserStore );
        Mockito.when( userDao.findByQualifiedUsername( Mockito.<QualifiedUsername>any() ) ).thenReturn( user );

        // exercise
        UserParser parser = new UserParser( securityService, userStoreService, userDao, new UserStoreParser( userStoreDao ) );
        try
        {
            parser.parseUser( "myUserStore:myuser" );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof UserNotFoundException );
        }
    }

    private UserStoreEntity createUserStore( int key, String name )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( key ) );
        userStore.setName( name );
        userStore.setDeleted( false );
        userStore.setConnectorName( "myConnector" );
        return userStore;
    }

    private UserEntity createUser( String key, String name, UserStoreEntity userStore )
    {
        UserEntity user = new UserEntity();
        user.setKey( new UserKey( key ) );
        user.setName( name );
        user.setType( UserType.NORMAL );
        user.setDeleted( false );
        user.setUserStore( userStore );
        return user;
    }
}
