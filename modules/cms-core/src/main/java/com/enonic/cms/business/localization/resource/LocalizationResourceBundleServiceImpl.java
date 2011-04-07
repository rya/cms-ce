/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.localization.resource;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.cache.CacheFacade;

import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.store.resource.FileResourceEvent;
import com.enonic.cms.store.resource.FileResourceListener;

import com.enonic.cms.domain.localization.LocalizationResourceBundle;
import com.enonic.cms.domain.localization.LocalizationResourceException;
import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;

/**
 * Created by rmy - Date: Apr 22, 2009
 */
public class LocalizationResourceBundleServiceImpl
    implements LocalizationResourceBundleService, FileResourceListener
{
    private ResourceService resourceService;

    private CacheFacade propertiesCache;

    private static final String LOCALIZATION_CACHE_GROUP = "localeproperties";

    public LocalizationResourceBundle getResourceBundle( SiteEntity site, Locale locale )
    {
        ResourceKey defaultLocalizationResourceKey = site.getDefaultLocalizationResource();

        if ( defaultLocalizationResourceKey == null )
        {
            return null;
        }

        return createResourceBundle( locale, defaultLocalizationResourceKey );
    }

    private Properties loadBundle( ResourceKey defaultLocalizationResourceKey, String bundleExtension )
    {

        String defaultLocalizationResourceName = defaultLocalizationResourceKey.toString();
        int pos = defaultLocalizationResourceName.lastIndexOf( '.' );

        String bundleResourceKey = defaultLocalizationResourceName;

        if ( pos > 0 )
        {
            bundleResourceKey = defaultLocalizationResourceName.substring( 0, pos );
        }

        bundleResourceKey = bundleResourceKey + bundleExtension + ".properties";

        return getOrCreateProperties( new ResourceKey( bundleResourceKey ) );
    }

    private Properties getOrCreateProperties( ResourceKey resourceKey )
    {

        Properties properties = getFromCache( resourceKey );

        if ( properties == null )
        {
            properties = loadPropertiesFromFile( resourceKey );
        }

        return properties;
    }

    private synchronized Properties loadPropertiesFromFile( ResourceKey resourceKey )
    {
        Properties properties = getFromCache( resourceKey );

        if ( properties != null )
        {
            return properties;
        }

        properties = new Properties();

        ResourceFile resourceFile = resourceService.getResourceFile( resourceKey );

        if ( resourceFile != null )
        {
            try
            {
                properties.load( resourceFile.getDataAsInputStream() );

            }
            catch ( IOException e )
            {
                throw new LocalizationResourceException( "Not able to load resourcefile: " + resourceFile.getName() + ". Reason: " + e );
            }
        }

        putInCache( resourceKey, properties );

        return properties;
    }

    private void putInCache( ResourceKey resourceKey, Properties properties )
    {
        propertiesCache.put( LOCALIZATION_CACHE_GROUP, resourceKey.toString(), properties );

    }

    private Properties getFromCache( ResourceKey resourceKey )
    {
        return (Properties) propertiesCache.get( LOCALIZATION_CACHE_GROUP, resourceKey.toString() );
    }

    private LocalizationResourceBundle createResourceBundle( Locale locale, ResourceKey defaultLocalizationResourceKey )
    {
        Properties props = new Properties();

        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        props.putAll( loadBundle( defaultLocalizationResourceKey, "" ) );

        if ( StringUtils.isNotEmpty( lang ) )
        {
            lang = lang.toLowerCase();
            props.putAll( loadBundle( defaultLocalizationResourceKey, "_" + lang ) );
        }

        if ( StringUtils.isNotEmpty( country ) )
        {
            country = country.toLowerCase();
            props.putAll( loadBundle( defaultLocalizationResourceKey, "_" + lang + "_" + country ) );
        }

        if ( StringUtils.isNotEmpty( variant ) )
        {
            variant = variant.toLowerCase();
            props.putAll( loadBundle( defaultLocalizationResourceKey, "_" + lang + "_" + country + "_" + variant ) );
        }

        return new LocalizationResourceBundle( props );
    }


    public void setPropertiesCache( CacheFacade propertiesCache )
    {
        this.propertiesCache = propertiesCache;
    }

    @Autowired
    public void setLocalizationResourceFileService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public void resourceChanged( FileResourceEvent event )
    {
        if ( event.getName().getName().endsWith( ".properties" ) )
        {
            this.propertiesCache.removeAll();
        }
    }
}
