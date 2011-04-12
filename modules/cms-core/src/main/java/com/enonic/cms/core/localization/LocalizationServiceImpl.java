/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.localization.resource.LocalizationResourceBundleService;

import com.enonic.cms.core.structure.SiteEntity;

/**
 * Created by rmy - Date: Apr 22, 2009
 */
public class LocalizationServiceImpl
    implements LocalizationService
{

    private LocalizationResourceBundleService localizationResourceBundleService;

    public static final String NO_TRANSLATION_FOUND_VALUE = "NOT TRANSLATED";


    public String getLocalizedPhrase( SiteEntity site, String phrase, Locale locale )
    {
        return getLocalizedPhrase( site, phrase, null, locale );
    }

    public String getLocalizedPhrase( SiteEntity site, String phrase, Object[] arguments, Locale locale )
    {
        if ( noLocalizationResourceDefinedForSite( site ) )
        {
            return createNotTranslated( phrase );
        }

        if ( locale == null )
        {
            return createNotTranslated( phrase );
        }

        LocalizationResourceBundle localizationResourceBundle = getResourceBundleForLocale( site, locale );

        if ( localizationResourceBundle == null )
        {
            return createNotTranslated( phrase );
        }

        String localizedPhrase = getLocalizedPhrase( phrase, arguments, localizationResourceBundle );

        return StringUtils.isNotEmpty( localizedPhrase ) ? localizedPhrase : createNotTranslated( phrase );
    }

    private boolean noLocalizationResourceDefinedForSite( SiteEntity site )
    {
        return site.getDefaultLocalizationResource() == null;
    }

    private String createNotTranslated( String phrase )
    {
        return NO_TRANSLATION_FOUND_VALUE + ": " + phrase;
    }

    private LocalizationResourceBundle getResourceBundleForLocale( SiteEntity site, Locale locale )
    {
        return localizationResourceBundleService.getResourceBundle( site, locale );
    }

    private String getLocalizedPhrase( String phrase, Object[] arguments, LocalizationResourceBundle resourceBundle )
    {
        if ( arguments == null )
        {
            return resourceBundle.getLocalizedPhrase( phrase );
        }

        return resourceBundle.getLocalizedPhrase( phrase, arguments );
    }

    @Autowired
    public void setLocalizationResourceBundleService( LocalizationResourceBundleService localizationResourceBundleService )
    {
        this.localizationResourceBundleService = localizationResourceBundleService;
    }
}

