/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.junit.Test;

import static org.junit.Assert.*;


public class QualifiedUsernameTest
{
    @Test
    public void testUsernameOnly()
    {
        QualifiedUsername qu1 = new QualifiedUsername( "testusername" );
        assertEquals( "testusername", qu1.toString() );
        assertEquals( "testusername", qu1.getUsername() );
        assertTrue( null == qu1.getUserStoreName() );
        assertTrue( null == qu1.getUserStoreKey() );
    }

    @Test
    public void testUsernameAndUserStoreName()
    {
        QualifiedUsername qu1 = new QualifiedUsername( "testuserstore", "testusername" );
        assertEquals( "testuserstore\\testusername", qu1.toString() );
        assertEquals( "testusername", qu1.getUsername() );
        assertEquals( "testuserstore", qu1.getUserStoreName() );
        assertTrue( null == qu1.getUserStoreKey() );
    }

    @Test
    public void testUsernameAndUserStoreKey()
    {
        UserStoreKey usk = new UserStoreKey( 123 );
        QualifiedUsername qu1 = new QualifiedUsername( usk, "testusername" );
        assertEquals( "#123\\testusername", qu1.toString() );
        assertEquals( "testusername", qu1.getUsername() );
        assertTrue( null == qu1.getUserStoreName() );
        assertEquals( usk.toString(), qu1.getUserStoreKey().toString() );
    }

    @Test
    public void testQualifiedUsername()
    {
        QualifiedUsername qu1 = QualifiedUsername.parse( "testuserstore:testusername" );
        assertEquals( "testuserstore\\testusername", qu1.toString() );
        assertEquals( "testusername", qu1.getUsername() );
        assertEquals( "testuserstore", qu1.getUserStoreName() );
        assertTrue( null == qu1.getUserStoreKey() );

        QualifiedUsername qu2 = QualifiedUsername.parse( "testuserstore\\testusername" );
        assertEquals( "testuserstore\\testusername", qu2.toString() );
        assertEquals( "testusername", qu2.getUsername() );
        assertEquals( "testuserstore", qu2.getUserStoreName() );
        assertTrue( null == qu2.getUserStoreKey() );

        QualifiedUsername qu3 = QualifiedUsername.parse( "#123:testusername" );
        assertEquals( "#123\\testusername", qu3.toString() );
        assertEquals( "testusername", qu3.getUsername() );
        assertTrue( null == qu3.getUserStoreName() );
        assertEquals( ( new UserStoreKey( 123 ) ).toString(), qu3.getUserStoreKey().toString() );

        QualifiedUsername qu4 = QualifiedUsername.parse( "#123\\testusername" );
        assertEquals( "#123\\testusername", qu4.toString() );
        assertEquals( "testusername", qu4.getUsername() );
        assertTrue( null == qu4.getUserStoreName() );
        assertEquals( ( new UserStoreKey( 123 ) ).toString(), qu4.getUserStoreKey().toString() );
    }
}
