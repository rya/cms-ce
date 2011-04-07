/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.home.HomeService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.structure.SiteProperties;

public class SitePropertiesServiceImpl
    implements SitePropertiesService, InitializingBean
{
    private Properties defaultProperties;

    private Map<SiteKey, Properties> sitePropertiesMap = new ConcurrentHashMap<SiteKey, Properties>();

    private HomeService homeService;

    private ResourceLoader resourceLoader = new FileSystemResourceLoader();

    public void afterPropertiesSet()
        throws Exception
    {
        Resource resource = resourceLoader.getResource( "classpath:com/enonic/cms/business/render/site-default.properties" );
        try
        {
            defaultProperties = new Properties();
            defaultProperties.load( resource.getInputStream() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load site-default.properties", e );
        }
    }

    public void setHomeService( HomeService value )
    {
        this.homeService = value;
    }

    public SiteProperties getSiteProperties( SiteKey siteKey )
    {
        return new SiteProperties( doGetSiteProperties( siteKey ) );
    }

    private Properties doGetSiteProperties( SiteKey siteKey )
    {

        Properties props = sitePropertiesMap.get( siteKey );
        if ( props == null )
        {
            props = loadSiteProperties( siteKey );
            sitePropertiesMap.put( siteKey, props );
        }

        return props;
    }

    public String getProperty( String key, SiteKey siteKey )
    {
        Properties props = doGetSiteProperties( siteKey );
        if ( props == null )
        {
            throw new IllegalArgumentException( "No properties for site " + siteKey );
        }

        return StringUtils.trimToNull( props.getProperty( key ) );
    }

    public Integer getPropertyAsInteger( String key, SiteKey siteKey )
    {
        String svalue = getProperty( key, siteKey );

        if ( svalue != null && !StringUtils.isNumeric( svalue ) )
        {
            throw new NumberFormatException( "Invalid value of property " + key + " = " + svalue + " in site-" + siteKey + ".properties" );
        }

        return svalue == null ? null : new Integer( svalue );
    }

    public Boolean getPropertyAsBoolean( String key, SiteKey siteKey )
    {
        String svalue = getProperty( key, siteKey );

        return svalue == null ? Boolean.FALSE : Boolean.valueOf( svalue );
    }

    private Properties loadSiteProperties( SiteKey siteKey )
    {
        Properties siteProperties = new Properties( defaultProperties );
        siteProperties.setProperty( "sitekey", String.valueOf( siteKey ) );

        String relativePathToCmsHome = "/config/site-" + siteKey + ".properties";
        try
        {
            String resourcePath = homeService.getHomeDir().getURL() + relativePathToCmsHome;
            Resource resource = resourceLoader.getResource( resourcePath );
            boolean useCustomProperties = resource.exists();
            if ( useCustomProperties )
            {
                InputStream stream = resource.getInputStream();
                siteProperties.load( stream );
                siteProperties.setProperty( "customSiteProperties", "true" );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load site properties file: " + relativePathToCmsHome, e );
        }

        siteProperties.setProperty( SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING,
                                    VerticalProperties.getVerticalProperties().getUrlCharacterEncoding() );
        sitePropertiesMap.put( siteKey, siteProperties );

        return siteProperties;
    }

    public void setResourceLoader( ResourceLoader resourceLoader )
    {
        this.resourceLoader = resourceLoader;
    }
}
