/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.core.security.user.UserKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Feb 25, 2010
 * Time: 11:38:29 AM
 */
public class PreferenceUniqueMatchResolverTest
    extends TestCase
{

    PreferenceUniqueMatchResolver uniqueMatchResolver;

    UserKey userKey = new UserKey( "1" );

    @Before
    public void setUp()
    {
        uniqueMatchResolver = new PreferenceUniqueMatchResolver();
    }

    @Test
    public void testSinglePreference()
    {
        List<PreferenceEntity> preferences = new ArrayList<PreferenceEntity>();

        preferences.add( createPreference( PreferenceScopeType.WINDOW, "test" ) );
        preferences.add( createPreference( PreferenceScopeType.PORTLET, "test" ) );
        preferences.add( createPreference( PreferenceScopeType.SITE, "test" ) );

        for ( PreferenceEntity pref : preferences )
        {
            uniqueMatchResolver.addPreferenceKeyIfHigherPriority( pref.getKey() );
        }

        List<PreferenceKey> uniquePreferences = uniqueMatchResolver.preferenceKeys;

        assertEquals( "Should contain 1 preference only", 1, uniquePreferences.size() );
        assertEquals( uniquePreferences.get( 0 ).getScopeType(), PreferenceScopeType.WINDOW );
    }

    @Test
    public void testTwoPreferences()
    {
        List<PreferenceEntity> preferences = new ArrayList<PreferenceEntity>();

        preferences.add( createPreference( PreferenceScopeType.WINDOW, "test2" ) );
        preferences.add( createPreference( PreferenceScopeType.GLOBAL, "test2" ) );
        preferences.add( createPreference( PreferenceScopeType.PORTLET, "test" ) );
        preferences.add( createPreference( PreferenceScopeType.SITE, "test" ) );

        for ( PreferenceEntity pref : preferences )
        {
            uniqueMatchResolver.addPreferenceKeyIfHigherPriority( pref.getKey() );
        }

        List<PreferenceKey> uniquePreferences = uniqueMatchResolver.preferenceKeys;

        assertEquals( "Should contain 2 preferences", 2, uniquePreferences.size() );
        assertEquals( uniquePreferences.get( 0 ).getScopeType(), PreferenceScopeType.WINDOW );
        assertEquals( uniquePreferences.get( 1 ).getScopeType(), PreferenceScopeType.PORTLET );
    }


    @Test
    public void testSeveralPreferences()
    {
        List<PreferenceEntity> preferences = new ArrayList<PreferenceEntity>();

        preferences.add( createPreference( PreferenceScopeType.PORTLET, "test" ) );
        preferences.add( createPreference( PreferenceScopeType.SITE, "test" ) );

        preferences.add( createPreference( PreferenceScopeType.WINDOW, "test2" ) );
        preferences.add( createPreference( PreferenceScopeType.GLOBAL, "test2" ) );

        preferences.add( createPreference( PreferenceScopeType.SITE, "test3" ) );
        preferences.add( createPreference( PreferenceScopeType.GLOBAL, "test3" ) );

        preferences.add( createPreference( PreferenceScopeType.PAGE, "test4" ) );
        preferences.add( createPreference( PreferenceScopeType.SITE, "test4" ) );
        preferences.add( createPreference( PreferenceScopeType.GLOBAL, "test4" ) );

        preferences.add( createPreference( PreferenceScopeType.PORTLET, "test5" ) );
        preferences.add( createPreference( PreferenceScopeType.SITE, "test5" ) );

        preferences.add( createPreference( PreferenceScopeType.GLOBAL, "test6" ) );

        for ( PreferenceEntity pref : preferences )
        {
            uniqueMatchResolver.addPreferenceKeyIfHigherPriority( pref.getKey() );
        }

        List<PreferenceKey> uniquePreferences = uniqueMatchResolver.preferenceKeys;

        assertEquals( "Should contain 5 unique preferences", 6, uniquePreferences.size() );
        assertEquals( uniquePreferences.get( 0 ).getScopeType(), PreferenceScopeType.PORTLET );
        assertEquals( uniquePreferences.get( 1 ).getScopeType(), PreferenceScopeType.WINDOW );
        assertEquals( uniquePreferences.get( 2 ).getScopeType(), PreferenceScopeType.SITE );
        assertEquals( uniquePreferences.get( 3 ).getScopeType(), PreferenceScopeType.PAGE );
        assertEquals( uniquePreferences.get( 4 ).getScopeType(), PreferenceScopeType.PORTLET );
        assertEquals( uniquePreferences.get( 5 ).getScopeType(), PreferenceScopeType.GLOBAL );

    }


    private PreferenceEntity createPreference( PreferenceScopeType scopeType, String preferenceKeyString )
    {
        PreferenceScopeKey preferenceScopeKey = new PreferenceScopeKey( "1" );

        if ( scopeType.equals( PreferenceScopeType.GLOBAL ) )
        {
            preferenceScopeKey = null;
        }

        PreferenceScope preferenceScope = new PreferenceScope( scopeType, preferenceScopeKey );

        PreferenceKey preferenceKey = new PreferenceKey( userKey, preferenceScope, preferenceKeyString );

        PreferenceEntity preference = new PreferenceEntity();
        preference.setKey( preferenceKey );
        preference.setValue( "test" );

        return preference;
    }
}
