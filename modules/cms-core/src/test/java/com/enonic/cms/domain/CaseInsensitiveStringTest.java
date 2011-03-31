/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.HashMap;

import org.junit.Test;

import static junitx.framework.Assert.assertNotEquals;
import static org.junit.Assert.*;


public class CaseInsensitiveStringTest
    extends AbstractEqualsTest
{
    private CaseInsensitiveString lowerCase1 = new CaseInsensitiveString( "this is a test!" );

    private CaseInsensitiveString lowerCase2 = new CaseInsensitiveString( "where are you?" );

    private CaseInsensitiveString mixedCase1 = new CaseInsensitiveString( "This Is a Test!" );

    private CaseInsensitiveString mixedCase2 = new CaseInsensitiveString( "Where Are you?" );

    private CaseInsensitiveString upperCase1 = new CaseInsensitiveString( "THIS IS A TEST!" );

    private CaseInsensitiveString upperCase2 = new CaseInsensitiveString( "WHERE ARE YOU?" );

    public Object getObjectX()
    {
        return lowerCase1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{lowerCase2, mixedCase2, upperCase2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return mixedCase1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return upperCase1;
    }

//    @Before
//    public void setUp()
//    {
//        // Add your code here
//    }

    @Test
    public void testCompareTo()
    {
        assertEquals( "Strings different in case should still compare the same", 0, lowerCase1.compareTo( mixedCase1 ) );
        assertEquals( "Strings different in case should still compare the same", 0, lowerCase1.compareTo( upperCase1 ) );
        assertTrue( "A string starting with W is greater than one starting with T", lowerCase1.compareTo( lowerCase2 ) < 0 );
        assertTrue( "A string starting with W is greater than one starting with T", lowerCase1.compareTo( mixedCase2 ) < 0 );
        assertTrue( "A string starting with T is smaller than one starting with W", upperCase2.compareTo( lowerCase1 ) > 0 );
    }

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    @Test
    public void testHashCode()
    {
        assertEquals( "String of different case should still have same HashCode", lowerCase1.hashCode(), mixedCase1.hashCode() );
        assertEquals( "String of different case should still have same HashCode", lowerCase1.hashCode(), upperCase1.hashCode() );
        assertNotEquals( "Different strings of same case should not have same hashcode", lowerCase1.hashCode(), lowerCase2.hashCode() );
        assertNotEquals( "Different strings of same case should not have same hashcode", mixedCase1.hashCode(), mixedCase2.hashCode() );
        assertNotEquals( "Different strings of same case should not have same hashcode", upperCase1.hashCode(), upperCase2.hashCode() );
    }

    @Test
    public void testWorksAsMapKeys()
    {
        String object1 = "Object1";
        Integer object2 = 2;
        Double object3 = 3.14;
        HashMap<CaseInsensitiveString, Object> testMap = new HashMap<CaseInsensitiveString, Object>();

        testMap.put( lowerCase1, object1 );
        testMap.remove( mixedCase1 );
        assertEquals( "Map should not contain any elements now.", 0, testMap.size() );

        testMap.put( lowerCase1, object1 );
        Object o = testMap.put( mixedCase1, object2 );
        assertEquals( "The String should have been replaced", object1, o );
        assertEquals( "Map should only contain the one Integer object (test 1)", 1, testMap.size() );
        assertEquals( "Map should only contain the one Integer object (test 2)", object2, testMap.get( upperCase1 ) );

        assertFalse( "The map does not contain any keys of type 2", testMap.containsKey( upperCase2 ) );
        testMap.put( lowerCase2, object2 );
        assertTrue( "Now, the map contains keys of type 2", testMap.containsKey( upperCase2 ) );
        testMap.put( mixedCase2, object3 );
        assertEquals( "Key 1 did not get overwritten", object2, testMap.get( upperCase1 ) );
        assertEquals( "Key 2 did get overwritten", object3, testMap.get( upperCase2 ) );
    }

}
