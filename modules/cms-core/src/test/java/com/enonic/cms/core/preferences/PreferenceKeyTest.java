/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import org.junit.Test;

import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.core.security.user.UserKey;

import static org.junit.Assert.*;


public class PreferenceKeyTest
{

    @Test
    public void testToString()
    {

        assertEquals( "USER:ABC.GLOBAL.language",
                      new PreferenceKey( new UserKey( "ABC" ), PreferenceScopeType.GLOBAL, null, "language" ).getRawKey() );

        assertEquals( "USER:ABC.SITE:1.language",
                      new PreferenceKey( new UserKey( "ABC" ), PreferenceScopeType.SITE, new PreferenceScopeKey( "1" ),
                                         "language" ).toString() );
    }

    @Test
    public void testCreateFromString()
    {

        PreferenceKey key = new PreferenceKey( "USER:ABC.SITE:1.language" );

        assertEquals( "USER:ABC.SITE:1.language", key.getRawKey() );
        assertEquals( "USER:ABC.SITE:1.language", key.toString() );

        assertEquals( "USER", key.getType() );
        assertEquals( "ABC", key.getTypeKey() );

        assertEquals( PreferenceScopeType.SITE, key.getScopeType() );
        assertEquals( new PreferenceScopeKey( "1" ), key.getScopeKey() );

        assertEquals( "language", key.getBaseKey() );
    }

    @Test
    public void testRawKey()
    {

        assertEquals( "USER:ABC.GLOBAL.language",
                      new PreferenceKey( new UserKey( "ABC" ), PreferenceScopeType.GLOBAL, null, "language" ).getRawKey() );

        assertEquals( "USER:ABC.SITE:1.language",
                      new PreferenceKey( new UserKey( "ABC" ), PreferenceScopeType.SITE, new PreferenceScopeKey( "1" ),
                                         "language" ).getRawKey() );
    }


    @Test
    public void testPreferenceKeyToString()
    {

        assertEquals( "user:ABC123.global.language", new PreferenceKey( "user:ABC123.global.language" ).toString() );
        assertEquals( "user:ABC123.site:1.language", new PreferenceKey( "user:ABC123.site:1.language" ).toString() );
        assertEquals( "user:ABC123.page:101.language", new PreferenceKey( "user:ABC123.page:101.language" ).toString() );
        assertEquals( "user:ABC123.portlet:101:53.language", new PreferenceKey( "user:ABC123.portlet:101:53.language" ).toString() );

        assertEquals( "user:ABC123.global.language.dialect", new PreferenceKey( "user:ABC123.global.language.dialect" ).toString() );
        assertEquals( "user:ABC123.global.language:dialect", new PreferenceKey( "user:ABC123.global.language:dialect" ).toString() );
    }

    @Test
    public void testGetRawKey()
    {

        assertEquals( "user:ABC123.global.language", new PreferenceKey( "user:ABC123.global.language" ).getRawKey() );
    }

    @Test
    public void testGetKeyExcludingTypePart()
    {

        assertEquals( "site:0.language", new PreferenceKey( "user:ABC.site:0.language" ).getKeyExcludingTypePart() );
        assertEquals( "site:0.language:dialect", new PreferenceKey( "user:ABC.site:0.language:dialect" ).getKeyExcludingTypePart() );
    }

    @Test
    public void testGetType()
    {

        assertEquals( "USER", new PreferenceKey( "user:ABC.site:0.language" ).getType() );
        assertEquals( "USER", new PreferenceKey( "user:ABC.site:0.language:dialect" ).getType() );
    }

    @Test
    public void testGetTypeKey()
    {

        assertEquals( "1", new PreferenceKey( "user:1.site:0.language" ).getTypeKey() );
        assertEquals( "1", new PreferenceKey( "user:1.site:0.language:dialect" ).getTypeKey() );
    }

    @Test
    public void testGetScope()
    {

        assertEquals( new PreferenceScopeKey( "0" ), new PreferenceKey( "user:tlund123.site:0.language.2" ).getScopeKey() );

        assertEquals( PreferenceScopeType.GLOBAL, new PreferenceKey( "user:ABC.global.baseKey" ).getScopeType() );
        assertEquals( PreferenceScopeType.SITE, new PreferenceKey( "user:ABC.site:0.baseKey" ).getScopeType() );
        assertEquals( PreferenceScopeType.PAGE, new PreferenceKey( "user:ABC.page:0.baseKey" ).getScopeType() );
        assertEquals( PreferenceScopeType.PORTLET, new PreferenceKey( "user:ABC.portlet:0.baseKey" ).getScopeType() );
        assertEquals( PreferenceScopeType.PORTLET, new PreferenceKey( "user:ABC.portlet:0.baseKey:extra" ).getScopeType() );
    }

    @Test
    public void testGetScopeKey()
    {

        assertEquals( new PreferenceScopeKey( "123" ), new PreferenceKey( "user:ABC.site:123.language" ).getScopeKey() );
        assertEquals( new PreferenceScopeKey( "123" ), new PreferenceKey( "user:ABC.site:123.language:dialect" ).getScopeKey() );
    }

    @Test
    public void testWindowKey()
    {
        PreferenceKey preferenceKey = new PreferenceKey( ( "USER:3AC11D7A2123CC625E87B9ECB3D67B446AB42501.WINDOW:105:75.wintest" ) );

        assertNotNull( preferenceKey.getScopeKey() );
        assertEquals( new Integer( 105 ), preferenceKey.getScopeKey().getFirstKey() );
        assertEquals( new Integer( 75 ), preferenceKey.getScopeKey().getSecondKey() );

    }


    @Test
    public void testBaseKey()
    {

        assertEquals( "httpServer", new PreferenceKey( "user:ABC.site:0.httpServer" ).getBaseKey() );
        assertEquals( "language", new PreferenceKey( "user:ABC.site:0.language" ).getBaseKey() );
        assertEquals( "language.dialect", new PreferenceKey( "user:ABC.site:0.language.dialect" ).getBaseKey() );
        assertEquals( "language:dialect", new PreferenceKey( "user:ABC.site:0.language:dialect" ).getBaseKey() );
    }

    @Test(expected = InvalidKeyException.class)
    public void testIllegalPreferenceKey1()
    {

        new PreferenceKey( null );
    }

    @Test(expected = InvalidKeyException.class)
    public void testIllegalPreferenceKey2()
    {

        new PreferenceKey( "" );
    }

    @Test(expected = InvalidKeyException.class)
    public void testIllegalPreferenceKey3()
    {

        new PreferenceKey( "language" );
    }

    @Test(expected = InvalidKeyException.class)
    public void testIllegalPreferenceKey4()
    {

        new PreferenceKey( "site:4.language" );
    }

    @Test(expected = InvalidKeyException.class)
    public void testIllegalPreferenceKey5()
    {

        new PreferenceKey( "Test:007.site:4" );
    }
}
