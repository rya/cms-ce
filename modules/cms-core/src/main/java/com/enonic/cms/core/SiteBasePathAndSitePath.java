/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Aug 6, 2010
 */
public class SiteBasePathAndSitePath
{
    private SiteBasePath siteBasePath;

    private SitePath sitePath;

    public SiteBasePathAndSitePath( SiteBasePath siteBasePath, SitePath sitePath )
    {
        this.siteBasePath = siteBasePath;
        this.sitePath = sitePath;
    }

    public SiteBasePath getSiteBasePath()
    {
        return siteBasePath;
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public String toString()
    {
        ToStringBuilder s = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE );
        s.append( "siteBasePath", siteBasePath );
        s.append( "sitePath", sitePath );
        return s.toString();
    }
}
