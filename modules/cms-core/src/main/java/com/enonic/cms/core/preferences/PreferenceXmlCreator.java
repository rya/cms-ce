/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.Collection;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;

public class PreferenceXmlCreator
{

    public static XMLDocument createPreferencesDocument( PreferenceEntity preference )
    {

        XMLBuilder builder = new XMLBuilder();
        builder.startElement( "preferences" );
        doCreatePreferenceElement( builder, preference );
        builder.setAttribute( "count", "1" );
        builder.endElement();

        return builder.getDocument();
    }

    public static XMLDocument createPreferencesDocument( Collection<PreferenceEntity> preferences )
    {

        XMLBuilder builder = new XMLBuilder();
        builder.startElement( "preferences" );
        for ( PreferenceEntity preference : preferences )
        {
            doCreatePreferenceElement( builder, preference );
        }
        builder.setAttribute( "count", Integer.valueOf( preferences.size() ) );
        builder.endElement();
        return builder.getDocument();
    }

    public static XMLDocument createEmptyPreferencesDocument( String message )
    {
        XMLBuilder builder = new XMLBuilder();
        builder.startElement( "preferences" );
        builder.setAttribute( "message", message != null ? message : "" );
        builder.endElement();
        return builder.getDocument();
    }

    private static void doCreatePreferenceElement( XMLBuilder builder, PreferenceEntity preference )
    {

        builder.startElement( "preference" );
        PreferenceKey preferenceKey = preference.getKey();
        builder.setAttribute( "scope", preferenceKey.getScopeType().getName() );
        builder.setAttribute( "key", preferenceKey.getKeyExcludingTypePart() );
        builder.setAttribute( "basekey", preferenceKey.getBaseKey() );
        builder.addContent( preference.getValue() );
        builder.endElement();
    }
}