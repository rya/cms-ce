/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

/**
 * Aug 6, 2010
 */
public class SiteBasePathAndSitePathToStringBuilder
{
    private String encoding = "UTF-8";

    private boolean urlEncodePath = true;

    private boolean htmlEscapeParameterAmps = false;

    private boolean includeFragment = true;

    private boolean includeParamsInPath = true;

    public String toString( SiteBasePathAndSitePath siteBasePathAndSitePath )
    {
        StringBuffer s = new StringBuffer();

        SiteBasePath siteBasePath = siteBasePathAndSitePath.getSiteBasePath();
        Path siteBasePathAsPath = siteBasePath.getAsPath();
        SitePath sitePath = siteBasePathAndSitePath.getSitePath();
        PathAndParams pathAndParams = sitePath.getPathAndParams();
        Path siteLocalPath = pathAndParams.getPath();

        if ( siteBasePathAsPath.endsWithSlash() && siteLocalPath.startsWithSlash() )
        {
            siteBasePathAsPath = siteBasePathAsPath.removeTrailingSlash();
        }

        s.append( siteBasePathAsPath.toString() );

        PathAndParamsToStringBuilder pathAndParamsToStringBuilder = new PathAndParamsToStringBuilder();
        pathAndParamsToStringBuilder.setEncoding( encoding );
        pathAndParamsToStringBuilder.setUrlEncodePath( urlEncodePath );
        pathAndParamsToStringBuilder.setHtmlEscapeParameterAmps( htmlEscapeParameterAmps );
        pathAndParamsToStringBuilder.setIncludeFragment( includeFragment );
        pathAndParamsToStringBuilder.setIncludeParamsInPath( includeParamsInPath );

        String siteLocalPathAsString = pathAndParamsToStringBuilder.toString( pathAndParams );

        s.append( siteLocalPathAsString );

        return s.toString();
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public void setUrlEncodePath( boolean urlEncodePath )
    {
        this.urlEncodePath = urlEncodePath;
    }

    public void setHtmlEscapeParameterAmps( boolean htmlEscapeParameterAmps )
    {
        this.htmlEscapeParameterAmps = htmlEscapeParameterAmps;
    }

    public void setIncludeFragment( boolean includeFragment )
    {
        this.includeFragment = includeFragment;
    }

    public void setIncludeParamsInPath( boolean includeParamsInPath )
    {
        this.includeParamsInPath = includeParamsInPath;
    }
}
