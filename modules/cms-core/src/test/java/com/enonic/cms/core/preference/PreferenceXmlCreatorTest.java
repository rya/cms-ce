/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preference;

import org.junit.Test;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.domain.AbstractXmlCreatorTest;

import java.util.Arrays;

public class PreferenceXmlCreatorTest
    extends AbstractXmlCreatorTest
{
    @Test
    public void testCreatePreferencesDocumentMultiple()
        throws Exception
    {
        final String expectedXml = getXml( "/com/enonic/cms/core/preference/MultiplePreferencesDocument-result.xml" );

        final PreferenceEntity pref1 = new PreferenceEntity();
        final PreferenceKey key1 = new PreferenceKey( "user:ABC1234.GLOBAL.key1" );
        pref1.setKey( key1 );
        pref1.setValue( "value1" );

        final PreferenceEntity pref2 = new PreferenceEntity();
        final PreferenceKey key2 = new PreferenceKey( "user:ABC1234.GLOBAL.key2" );
        pref2.setKey( key2 );
        pref2.setValue( "value2" );

        final XMLDocument xmlDoc = PreferenceXmlCreator.createPreferencesDocument( Arrays.asList(pref1, pref2) );
        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    @Test
    public void testCreatePreferencesDocumentEmpty()
        throws Exception
    {
        final String expectedXml = getXml( "/com/enonic/cms/core/preference/EmptyPreferencesDocument-result.xml" );

        final XMLDocument xmlDoc = PreferenceXmlCreator.createEmptyPreferencesDocument( "Some message" );
        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }
}
