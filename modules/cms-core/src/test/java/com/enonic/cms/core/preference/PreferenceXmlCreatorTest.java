/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preference;

import java.io.IOException;

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

        String expectedXml = getXml( "/com/enonic/cms/core/preference/PreferenceXmlCreatorTest-result.xml" );

        PreferenceEntity pref = new PreferenceEntity();
        PreferenceKey key = new PreferenceKey( "user:ABC1234.GLOBAL.testBase" );
        pref.setKey( key );
        pref.setValue( "testValue" );

        XMLDocument xmlDoc = PreferenceXmlCreator.createPreferencesDocument( pref );
        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }
}