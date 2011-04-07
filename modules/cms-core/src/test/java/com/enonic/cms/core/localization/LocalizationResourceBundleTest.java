/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Properties;

import org.junit.Test;

import com.enonic.cms.business.SpecialCharacterTestStrings;

import com.enonic.cms.domain.localization.LocalizationResourceBundle;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Apr 24, 2009
 */
public class LocalizationResourceBundleTest
{

    @Test
    public void testNorwegianCharacters()
    {
        LocalizationResourceBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();
        assertEquals( SpecialCharacterTestStrings.NORWEGIAN, resourceBundle.getLocalizedPhrase( "norsketegn" ) );
    }

    @Test
    public void testResourceOrdering()
    {
        LocalizationResourceBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        assertEquals( resourceBundle.getLocalizedPhrase( "only_in_en-us" ), "en-us" );
        assertEquals( resourceBundle.getLocalizedPhrase( "in_all" ), "en-us" );
        assertEquals( resourceBundle.getLocalizedPhrase( "no_and_default" ), "no" );
        assertEquals( resourceBundle.getLocalizedPhrase( "only_in_default" ), "default" );
    }

    @Test
    public void testNonExistingKey()
    {
        LocalizationResourceBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        assertNull( resourceBundle.getLocalizedPhrase( "in_all_not" ) );
        assertNotNull( resourceBundle.getLocalizedPhrase( "in_all" ) );
        assertNull( resourceBundle.getLocalizedPhrase( "only_in_en" ) );
        assertNotNull( resourceBundle.getLocalizedPhrase( "only_in_en-us" ) );
    }

    @Test
    public void testEmptyResourceBundle()
    {
        LocalizationResourceBundle resourceBundle = new LocalizationResourceBundle( new Properties() );
        assertNull( resourceBundle.getLocalizedPhrase( "in_all" ) );
    }

    @Test
    public void testParameterizedPhrase()
    {
        LocalizationResourceBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        Object[] testArgs = {"torsk", 8};

        String resolvedPhrase = resourceBundle.getLocalizedPhrase( "fiskmessage", testArgs );

        assertEquals( "det ble fisket 8 fisk av type torsk med musse p\u00e5 stampen", resolvedPhrase );
    }

    @Test
    public void testMissingParametersPhrase()
    {
        LocalizationResourceBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        Object[] testArgs = {"torsk"};

        String resolvedPhrase = resourceBundle.getLocalizedPhrase( "fiskmessage", testArgs );

        assertEquals( "det ble fisket {1} fisk av type torsk med musse p\u00e5 stampen", resolvedPhrase );
    }

    @Test
    public void testNullParametersPhrase()
    {
        LocalizationResourceBundle resourceBundle = LocalizationTestUtils.create_US_NO_DEFAULT_resourceBundle();

        String resolvedPhrase = resourceBundle.getLocalizedPhrase( "fiskmessage", null );

        assertEquals( "det ble fisket {1} fisk av type {0} med musse p\u00e5 stampen", resolvedPhrase );
    }

}
