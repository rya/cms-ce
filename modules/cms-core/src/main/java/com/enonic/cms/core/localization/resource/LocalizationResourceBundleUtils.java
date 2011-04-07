/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization.resource;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.enonic.esl.util.RegexpUtil;

import com.enonic.cms.domain.localization.LocaleParsingException;

/**
 * Created by rmy - Date: Apr 24, 2009
 */
public class LocalizationResourceBundleUtils
{
    public static final String RESOURCE_FILE_POSTFIX = "properties";

    // Pattern to parse locale string on format Languagcode[-country][anything]

    private static final String LOCALE_PATTERN = "^(\\w{2})(_(\\w{2}))?(_(\\w{2}))?$";

    public static Locale parseLocaleString( String localeAsString )
    {
        localeAsString = localeAsString.replace( '-', '_' );

        Matcher matcher = RegexpUtil.match( localeAsString, LOCALE_PATTERN, Pattern.CASE_INSENSITIVE );

        String language = "";
        String country = "";
        String variant = "";

        if ( matcher.matches() )
        {
            language = getLanguageFromMatcher( matcher );
            country = getCountryFromMatcher( matcher );
            variant = getVariantFromMatcher( matcher );
        }
        else
        {
            throw new LocaleParsingException( "Could not parse locale string: " + localeAsString + " to valid locale" );
        }

        return new Locale( language, country == null ? "" : country, variant == null ? "" : variant );
    }

    public static String createLocaleString( Locale locale )
    {
        String localeString = locale.getLanguage();

        return StringUtils.isNotEmpty( locale.getCountry() ) ? localeString + "-" + locale.getCountry() : localeString;
    }

    private static String getLanguageFromMatcher( Matcher matcher )
    {
        return matcher.group( 1 );
    }

    private static String getCountryFromMatcher( Matcher matcher )
    {
        return matcher.group( 3 );
    }

    private static String getVariantFromMatcher( Matcher matcher )
    {
        return matcher.group( 5 );
    }

}
