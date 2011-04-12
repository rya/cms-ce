/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.enonic.cms.core.structure.SiteProperties;
import org.apache.commons.lang.StringUtils;

import com.enonic.cms.domain.SiteKey;

public class MockSitePropertiesService
    implements SitePropertiesService
{

    private Map<SiteKey, SiteProperties> sitePropertiesMapBySiteKey = new HashMap<SiteKey, SiteProperties>();

    public void setProperty( SiteKey siteKey, String key, String value )
    {
        Properties props = getSiteProperties( siteKey ).getProperties();
        props.setProperty( key, value );
    }

    public SiteProperties getSiteProperties( SiteKey siteKey )
    {
        SiteProperties props = (SiteProperties) sitePropertiesMapBySiteKey.get( siteKey );
        if ( props == null )
        {
            props = new SiteProperties( new Properties() );
            sitePropertiesMapBySiteKey.put( siteKey, props );
        }

        return props;
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

    public String getProperty( String key, SiteKey siteKey )
    {
        SiteProperties props = getSiteProperties( siteKey );
        if ( props == null )
        {
            throw new IllegalArgumentException( "No properties for site " + siteKey );
        }

        return StringUtils.trimToNull( props.getProperty( key ) );
    }
}
