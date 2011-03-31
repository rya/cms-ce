/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Aug 6, 2010
 */
public class PortalSiteBasePath
    implements SiteBasePath
{
    private SiteKey siteKey;

    private Path sitePrefixPath;

    private Path asPath;

    public PortalSiteBasePath( Path sitePrefixPath, SiteKey siteKey )
    {
        this.siteKey = siteKey;
        this.sitePrefixPath = sitePrefixPath;
        this.asPath = generatePath();
    }

    private Path generatePath()
    {
        Path path = new Path( "" );

        path = path.appendPath( sitePrefixPath );
        return path;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public Path getAsPath()
    {
        return asPath;
    }

    public String toString()
    {
        ToStringBuilder s = new ToStringBuilder( this, ToStringStyle.MULTI_LINE_STYLE );
        s.append( "sitePrefixPath", sitePrefixPath );
        s.append( "siteKey", siteKey.toString() );
        s.append( "asPath", asPath.toString() );
        return s.toString();
    }
}
