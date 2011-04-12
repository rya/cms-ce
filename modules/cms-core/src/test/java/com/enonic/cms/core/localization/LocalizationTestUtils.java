/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.io.IOException;
import java.util.Properties;

import com.enonic.cms.core.structure.SiteEntity;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.enonic.cms.core.resource.ResourceKey;

public class LocalizationTestUtils
{
    private static final String BASE_RESOURCE_CLASSPATH = "classpath:com/enonic/cms/core/localization/";

    private static final String[] RESOURCES = {"phrases", "phrases_en", "phrases_no", "phrases_en-us"};

    public static Properties create_Default_Properties()
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases.properties" );
    }

    public static Properties create_NO_Properties()
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_no.properties" );
    }

    public static Properties create_EN_Properties()
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_en.properties" );
    }

    public static Properties create_EN_US_Properties()
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_en-us.properties" );
    }


    public static Properties getPropertiesFromFile( String path )
    {
        Properties properties = new Properties();

        ResourceLoader resourceLoader = new FileSystemResourceLoader();
        Resource resource = resourceLoader.getResource( path );

        try
        {
            properties.load( resource.getInputStream() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return properties;
    }

    public static LocalizationResourceBundle create_US_NO_DEFAULT_resourceBundle()
    {
        Properties properties = new Properties();

        properties.putAll( create_Default_Properties() );
        properties.putAll( create_NO_Properties() );
        properties.putAll( create_EN_US_Properties() );

        LocalizationResourceBundle resourceBundle = new LocalizationResourceBundle( properties );

        return resourceBundle;
    }

    public static LocalizationResourceBundle create_NO_DEFAULT_resourceBundle()
    {
        Properties properties = new Properties();

        properties.putAll( create_Default_Properties() );
        properties.putAll( create_NO_Properties() );

        LocalizationResourceBundle resourceBundle = new LocalizationResourceBundle( properties );

        return resourceBundle;
    }

    public static SiteEntity createSite( String defaultLocalizationResource )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( 0 );
        site.setDefaultLocalizationResource( new ResourceKey( defaultLocalizationResource ) );

        return site;
    }

}