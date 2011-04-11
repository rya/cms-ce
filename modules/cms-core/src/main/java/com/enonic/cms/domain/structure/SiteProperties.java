/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure;

import java.util.Properties;

import com.enonic.cms.core.SitePropertyNames;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 7, 2010
 * Time: 1:15:52 PM
 */
public class SiteProperties
{
    private Properties properties;

    public SiteProperties( Properties properties )
    {
        this.properties = properties;
    }

    public String getSiteURL()
    {
        return properties.getProperty( SitePropertyNames.SITE_URL );
    }

    public String getProperty( String propertyKey )
    {
        return properties.getProperty( propertyKey );
    }


    public Properties getProperties()
    {
        return properties;
    }
}
