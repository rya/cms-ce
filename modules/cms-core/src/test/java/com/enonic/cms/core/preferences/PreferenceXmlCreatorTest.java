/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.io.IOException;

import com.enonic.cms.core.preferences.PreferenceEntity;
import com.enonic.cms.core.preferences.PreferenceKey;
import com.enonic.cms.core.preferences.PreferenceXmlCreator;
import org.jdom.JDOMException;
import org.junit.Test;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.domain.AbstractXmlCreatorTest;

public class PreferenceXmlCreatorTest
    extends AbstractXmlCreatorTest
{

    @Test
    public void testPreferenceXmlCreator()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/domain/preference/PreferenceXmlCreatorTest-result.xml" );

        PreferenceEntity pref = new PreferenceEntity();
        PreferenceKey key = new PreferenceKey( "user:ABC1234.GLOBAL.testBase" );
        pref.setKey( key );
        pref.setValue( "testValue" );

        XMLDocument xmlDoc = PreferenceXmlCreator.createPreferencesDocument(pref);
        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }
}