package com.enonic.cms.core.security;


import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class UserStoreParserTest
{
    @Test
    public void parseUserStore_returns_null_when_given_string_is_null()
    {
        // setup
        UserStoreDao userStoreDao = Mockito.mock( UserStoreDao.class );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( createUserStore( 1, "myUserStore" ) );
        Mockito.when( userStoreDao.findByKey( new UserStoreKey( 1 ) ) ).thenReturn( createUserStore( 1, "myUserStore" ) );

        // exercise
        UserStoreParser parser = new UserStoreParser( userStoreDao );
        UserStoreEntity actualUserStore = parser.parseUserStore( null );

        // verify
        assertEquals( null, actualUserStore );
    }

    @Test(expected = UserStoreNotFoundException.class)
    public void parseUserStore_throws_exception_when_given_string_is_not_matching_any_userstore_by_name()
    {
        // setup
        UserStoreDao userStoreDao = Mockito.mock( UserStoreDao.class );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( createUserStore( 1, "myUserStore" ) );
        Mockito.when( userStoreDao.findByKey( new UserStoreKey( 1 ) ) ).thenReturn( createUserStore( 1, "myUserStore" ) );

        // exercise
        UserStoreParser parser = new UserStoreParser( userStoreDao );
        UserStoreEntity actualUserStore = parser.parseUserStore( "nonExistingUserStore" );

        // verify
        assertEquals( null, actualUserStore );
    }

    @Test(expected = UserStoreNotFoundException.class)
    public void parseUserStore_throws_exception_when_given_string_is_not_matching_any_userstore_by_key()
    {
        // setup
        UserStoreDao userStoreDao = Mockito.mock( UserStoreDao.class );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( createUserStore( 1, "myUserStore" ) );
        Mockito.when( userStoreDao.findByKey( new UserStoreKey( 1 ) ) ).thenReturn( createUserStore( 1, "myUserStore" ) );

        // exercise
        UserStoreParser parser = new UserStoreParser( userStoreDao );
        UserStoreEntity actualUserStore = parser.parseUserStore( "#123" );

        // verify
        assertEquals( null, actualUserStore );
    }

    @Test
    public void parseUserStore_returns_userstore_when_given_string_is_a_matching_name()
    {
        // setup
        UserStoreDao userStoreDao = Mockito.mock( UserStoreDao.class );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( createUserStore( 1, "myUserStore" ) );
        Mockito.when( userStoreDao.findByKey( new UserStoreKey( 1 ) ) ).thenReturn( createUserStore( 1, "myUserStore" ) );

        // exercise
        UserStoreParser parser = new UserStoreParser( userStoreDao );
        UserStoreEntity actualUserStore = parser.parseUserStore( "myUserStore" );

        // verify
        assertEquals( "myUserStore", actualUserStore.getName() );
    }

    @Test
    public void parseUserStore_returns_userstore_when_given_string_is_a_number_matching_the_key()
    {
        // setup
        UserStoreDao userStoreDao = Mockito.mock( UserStoreDao.class );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( createUserStore( 1, "myUserStore" ) );
        Mockito.when( userStoreDao.findByKey( new UserStoreKey( 1 ) ) ).thenReturn( createUserStore( 1, "myUserStore" ) );

        // exercise
        UserStoreParser parser = new UserStoreParser( userStoreDao );
        UserStoreEntity actualUserStore = parser.parseUserStore( "1" );

        // verify
        assertEquals( "myUserStore", actualUserStore.getName() );
    }

    @Test
    public void parseUserStore_returns_userstore_when_given_string_is_a_number_prefixed_with_hash_matching_the_key()
    {
        // setup
        UserStoreDao userStoreDao = Mockito.mock( UserStoreDao.class );
        Mockito.when( userStoreDao.findByName( "myUserStore" ) ).thenReturn( createUserStore( 1, "myUserStore" ) );
        Mockito.when( userStoreDao.findByKey( new UserStoreKey( 1 ) ) ).thenReturn( createUserStore( 1, "myUserStore" ) );

        // exercise
        UserStoreParser parser = new UserStoreParser( userStoreDao );
        UserStoreEntity actualUserStore = parser.parseUserStore( "#1" );

        // verify
        assertEquals( "myUserStore", actualUserStore.getName() );
    }

    private UserStoreEntity createUserStore( int key, String name )
    {
        UserStoreEntity userStore = new UserStoreEntity();
        userStore.setKey( new UserStoreKey( key ) );
        userStore.setName( name );
        return userStore;
    }
}
