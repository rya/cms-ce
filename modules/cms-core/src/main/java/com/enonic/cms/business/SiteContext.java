/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import org.springframework.core.style.ToStringCreator;

import com.enonic.cms.portal.cache.PageCacheService;

import com.enonic.cms.domain.SiteKey;

public class SiteContext
{

    private SiteKey siteKey;

    private boolean accessLoggingEnabled;

    private boolean authenticationLoggingEnabled;

    private PageCacheService pageCacheService;

    public SiteContext( SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public PageCacheService getPageCacheService()
    {
        return pageCacheService;
    }

    public void setPageAndObjectCacheService( PageCacheService value )
    {
        this.pageCacheService = value;
    }

    /**
     * NOTE! This property may be called "menuKey" in the old parts of the presentation layer.
     *
     * @return siteKey
     */
    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void setAccessLoggingEnabled( boolean value )
    {
        this.accessLoggingEnabled = value;
    }

    public boolean isAccessLoggingEnabled()
    {
        return accessLoggingEnabled;
    }

    public boolean isAuthenticationLoggingEnabled()
    {
        return authenticationLoggingEnabled;
    }

    public void setAuthenticationLoggingEnabled( boolean value )
    {
        this.authenticationLoggingEnabled = value;
    }

    public String toString()
    {
        ToStringCreator tsc = new ToStringCreator( this );
        tsc.append( "siteKey", getSiteKey() );
        tsc.append( "readLogEnabled", isAccessLoggingEnabled() );
        return tsc.toString();
    }
}
