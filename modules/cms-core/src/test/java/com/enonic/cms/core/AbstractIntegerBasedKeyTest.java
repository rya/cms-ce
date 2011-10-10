/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.junit.Test;

import static org.junit.Assert.*;


public class AbstractIntegerBasedKeyTest
{
    @Test
    public void testInstatiateNormal()
    {
        MyKey key = new MyKey( "1" );
        assertEquals( 1, key.intValue() );
        assertEquals( "1", key.toString() );
    }

    @Test(expected = InvalidKeyException.class)
    public void testInstatiateWithNullString()
    {
        MyKey key = new MyKey( (String) null );
        assertEquals( 1, key.intValue() );
    }

    @Test(expected = InvalidKeyException.class)
    public void testInstatiateWithNullInteger()
    {
        MyKey key = new MyKey( (Integer) null );
        assertEquals( 1, key.intValue() );
    }

    public class MyKey
        extends AbstractIntegerBasedKey
    {
        public MyKey( String value )
        {
            init( value );
        }

        public MyKey( Integer value )
        {
            init( value );
        }
    }
}
