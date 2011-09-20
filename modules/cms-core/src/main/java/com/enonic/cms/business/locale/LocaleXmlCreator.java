/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.locale;

import java.util.Locale;

import com.google.common.base.Preconditions;
import org.jdom.Document;
import org.jdom.Element;

public class LocaleXmlCreator
{

    public Document createLocalesDocument( final Locale[] locales )
    {
        Preconditions.checkNotNull(locales, "locales cannot be null" );

        Element localesEl = new Element( "locales" );
        for ( Locale locale : locales )
        {
            localesEl.addContent( doCreateLocaleElement( locale, null ) );
        }
        return new Document( localesEl );
    }

    public Document createLocalesDocument( final Locale locale )
    {
        Preconditions.checkNotNull(locale, "locale cannot be null" );

        Element localesEl = new Element( "locales" );
        localesEl.addContent( doCreateLocaleElement( locale, null ) );
        return new Document( localesEl );
    }

    public Document createLocaleDocument( final Locale locale, Locale inLocale )
    {
        Preconditions.checkNotNull(locale, "locale cannot be null" );

        Element localesEl = new Element( "locales" );
        localesEl.addContent( doCreateLocaleElement( locale, inLocale ) );
        return new Document( localesEl );
    }

    private Element doCreateLocaleElement( final Locale locale, final Locale inLocale )
    {
        Element localeEl = new Element( "locale" );
        localeEl.addContent( new Element( "name" ).setText( locale.toString() ) );
        localeEl.addContent( new Element( "country" ).setText( asEmptyIfNull( locale.getCountry() ) ) );
        localeEl.addContent( new Element( "display-country" ).setText( asEmptyIfNull( locale.getDisplayCountry() ) ) );
        localeEl.addContent( new Element( "display-language" ).setText( asEmptyIfNull( locale.getDisplayLanguage() ) ) );
        localeEl.addContent( new Element( "display-name" ).setText( asEmptyIfNull( locale.getDisplayName() ) ) );
        localeEl.addContent( new Element( "display-variant" ).setText( asEmptyIfNull( locale.getDisplayVariant() ) ) );
        localeEl.addContent( new Element( "iso3country" ).setText( asEmptyIfNull( locale.getISO3Country() ) ) );
        localeEl.addContent( new Element( "iso3language" ).setText( asEmptyIfNull( locale.getISO3Language() ) ) );
        localeEl.addContent( new Element( "language" ).setText( asEmptyIfNull( locale.getLanguage() ) ) );
        localeEl.addContent( new Element( "variant" ).setText( asEmptyIfNull( locale.getVariant() ) ) );

        if ( inLocale != null )
        {
            localeEl.addContent( new Element( "display-country-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayCountry( inLocale ) ) ) );
            localeEl.addContent( new Element( "display-language-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayLanguage( inLocale ) ) ) );
            localeEl.addContent( new Element( "display-name-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayName( inLocale ) ) ) );
            localeEl.addContent( new Element( "display-variant-in-locale" ).setAttribute( "language", inLocale.getLanguage() ).setText(
                asEmptyIfNull( locale.getDisplayVariant( inLocale ) ) ) );
        }
        return localeEl;
    }

    private String asEmptyIfNull( final String value )
    {
        return value == null ? "" : value;
    }

}
