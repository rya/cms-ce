/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.configuration;

import java.util.Properties;

import com.enonic.cms.domain.SiteKey;

public class Site
    implements SiteMBean
{
    private SiteKey siteKey;

    private Properties properties;

    private String siteUrl;

    private boolean pageCacheEnabled;


    public Site( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public int getSiteKey()
    {
        return siteKey.toInt();
    }

    public Properties getSiteProperties()
    {
        return properties;
    }

    public String getSiteUrl()
    {
        return siteUrl;
    }

    public boolean getPageCacheEnabled()
    {
        return pageCacheEnabled;
    }

    protected void setSiteProperties( Properties properties )
    {
        this.properties = properties;
    }

    protected void setSiteUrl( String siteUrl )
    {
        this.siteUrl = siteUrl;
    }

    protected void setPageCacheEnabled( boolean pageCacheEnabled )
    {
        this.pageCacheEnabled = pageCacheEnabled;
    }
}
