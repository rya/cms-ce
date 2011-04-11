/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.vhost.VirtualHostHelper;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.PathAndParams;
import com.enonic.cms.domain.PathAndParamsToStringBuilder;
import com.enonic.cms.domain.SiteBasePath;
import com.enonic.cms.domain.SiteBasePathAndSitePath;
import com.enonic.cms.domain.SiteBasePathAndSitePathToStringBuilder;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;

public class SiteURLResolver
{
    public static final String DEFAULT_SITEPATH_PREFIX = "/site";

    private String sitePathPrefix = DEFAULT_SITEPATH_PREFIX;

    private SitePropertiesService sitePropertiesService;

    /**
     * If not null the value overrides setting in site properties.
     */
    private Boolean overridingSitePropertyCreateUrlAsPath;

    /**
     * Whether to encode & as &amp; or not.
     */
    private boolean htmlEscapeParameterAmps = false;


    public String createFullPathForRedirect( HttpServletRequest request, SiteKey siteKey, String path )
    {
        if ( siteIsCreatingRelativeUrlsFromRoot( siteKey ) )
        {
            String pathFromRoot;

            //if path is local or missing vhost - correct path
            if ( VirtualHostHelper.hasBasePath( request ) && !path.startsWith( VirtualHostHelper.getBasePath( request ) ) )
            {
                if ( path.startsWith( "/" ) )
                {
                    pathFromRoot = VirtualHostHelper.getBasePath( request ) + path;
                }
                else
                {
                    pathFromRoot = VirtualHostHelper.getBasePath( request ) + "/" + path;
                }
            }
            else
            {
                pathFromRoot = path;
            }
            return doCreateFullPathForRedirectFromRootPath( siteKey, pathFromRoot );
        }
        else
        {
            String fullPath = doCreateFullPathForRedirectFromLocalPath( request, siteKey, path );
            return fullPath;
        }
    }

    public String createPathWithinContextPath( HttpServletRequest request, SitePath sitePath, boolean externalPath )
    {
        String localPathPrefix = resolveLocalSitePathPrefix( request, sitePath.getSiteKey(), externalPath );
        return buildPath( localPathPrefix, sitePath );
    }

    public String createUrl( HttpServletRequest request, SitePath sitePath, boolean includeParamsInPath )
    {

        String basePathOverride = (String) request.getAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME );

        return doCreateUrl( request, sitePath, includeParamsInPath, basePathOverride );
    }

    public String createUrlWithBasePathOverride( HttpServletRequest request, SitePath sitePath, boolean includeParamsInPath,
                                                 String basePathOverride )
    {
        return doCreateUrl( request, sitePath, includeParamsInPath, basePathOverride );
    }

    private String doCreateUrl( HttpServletRequest request, SitePath sitePath, boolean includeParamsInPath, String basePathOverride )
    {
        boolean createPathOnly;
        if ( overridingSitePropertyCreateUrlAsPath != null )
        {
            createPathOnly = overridingSitePropertyCreateUrlAsPath;
        }
        else
        {
            //check property
            createPathOnly =
                sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, sitePath.getSiteKey() );
        }

        SiteBasePath siteBasePath = SiteBasePathResolver.resolveSiteBasePath( request, sitePath.getSiteKey() );
        SiteBasePathAndSitePath siteBasePathAndSitePath = new SiteBasePathAndSitePath( siteBasePath, sitePath );

        String url;

        if ( basePathOverride != null )
        {
            url = basePathOverride;
            String siteLocalUrl = sitePathAndSitePathToString( sitePath.getSiteKey(), sitePath.getPathAndParams(), includeParamsInPath );
            if ( siteLocalUrl.startsWith( "/" ) && url.endsWith( "/" ) )
            {
                // preventing double slashes (example: //news/politics)
                siteLocalUrl = siteLocalUrl.substring( 1 );
            }

            url += siteLocalUrl;
        }
        else if ( createPathOnly )
        {
            url = siteBasePathAndSitePathToString( siteBasePathAndSitePath, includeParamsInPath );
        }
        else
        {
            url = createAbsoluteUrl( request, siteBasePathAndSitePath, includeParamsInPath );
        }

        return url;
    }


    public String createAbsoluteUrl( HttpServletRequest request, SiteBasePathAndSitePath siteBasePathAndSitePath,
                                     boolean includeParamsInPath )
    {
        String pathFromRoot = siteBasePathAndSitePathToString( siteBasePathAndSitePath, includeParamsInPath );
        return createAbsoluteUrl( request, pathFromRoot );
    }

    private String createAbsoluteUrl( HttpServletRequest request, String pathFromRoot )
    {
        URL url;
        try
        {
            int port = request.getServerPort();
            final int defaultPort = 80;
            if ( port != defaultPort )
            {
                url = new URL( request.getScheme(), request.getServerName(), request.getServerPort(), pathFromRoot );
            }
            else
            {
                url = new URL( request.getScheme(), request.getServerName(), pathFromRoot );
            }
        }
        catch ( MalformedURLException e )
        {
            throw new RuntimeException( "Failed to create absolute url to path: " + pathFromRoot, e );
        }

        return url.toString();
    }

    private String siteBasePathAndSitePathToString( SiteBasePathAndSitePath siteBasePathAndSitePath, boolean includeParamsInPath )
    {
        SiteKey siteKey = siteBasePathAndSitePath.getSitePath().getSiteKey();

        SiteBasePathAndSitePathToStringBuilder siteBasePathAndSitePathToStringBuilder = new SiteBasePathAndSitePathToStringBuilder();
        siteBasePathAndSitePathToStringBuilder.setEncoding( getUrlCharacterEncodingForSite( siteKey ) );
        siteBasePathAndSitePathToStringBuilder.setHtmlEscapeParameterAmps( htmlEscapeParameterAmps );
        siteBasePathAndSitePathToStringBuilder.setIncludeFragment( true );
        siteBasePathAndSitePathToStringBuilder.setIncludeParamsInPath( includeParamsInPath );
        siteBasePathAndSitePathToStringBuilder.setUrlEncodePath( true );

        return siteBasePathAndSitePathToStringBuilder.toString( siteBasePathAndSitePath );
    }

    private String sitePathAndSitePathToString( SiteKey siteKey, PathAndParams siteLocalPathAndParams, boolean includeParamsInPath )
    {
        PathAndParamsToStringBuilder pathAndParamsToStringBuilder = new PathAndParamsToStringBuilder();
        pathAndParamsToStringBuilder.setEncoding( getUrlCharacterEncodingForSite( siteKey ) );
        pathAndParamsToStringBuilder.setHtmlEscapeParameterAmps( htmlEscapeParameterAmps );
        pathAndParamsToStringBuilder.setIncludeFragment( true );
        pathAndParamsToStringBuilder.setIncludeParamsInPath( includeParamsInPath );
        pathAndParamsToStringBuilder.setUrlEncodePath( true );

        return pathAndParamsToStringBuilder.toString( siteLocalPathAndParams );
    }

    /**
     * This method must behave different under redirect and forward in cases where rewrite is active.
     */
    private String resolveLocalSitePathPrefix( HttpServletRequest request, SiteKey siteKey, boolean externalPath )
    {

        if ( externalPath && VirtualHostHelper.hasBasePath( request ) )
        {
            return VirtualHostHelper.getBasePath( request );
        }
        else
        {
            final int capacity = 50;
            StringBuffer s = new StringBuffer( capacity );
            s.append( sitePathPrefix );
            s.append( "/" ).append( siteKey );
            return s.toString();
        }
    }

    /**
     * Buids up a path from given localPathPrefix and localPath in sitePath. Ensures that the localPath gets url
     * encoded.
     */
    private String buildPath( String localPathPrefix, SitePath sitePath )
    {
        final int capacity = 50;
        StringBuffer pathString = new StringBuffer( capacity );
        pathString.append( localPathPrefix );
        Path localPath = sitePath.getLocalPath();
        String localPathEncoded = localPath.getAsUrlEncoded( true, getUrlCharacterEncodingForSite( sitePath.getSiteKey() ) );
        pathString.append( localPathEncoded );
        return pathString.toString();
    }

    private String doCreateFullPathForRedirectFromRootPath( SiteKey siteKey, String pathFromRoot )
    {
        StringBuffer s = new StringBuffer();

        if ( !pathFromRoot.startsWith( "/" ) && !pathFromRoot.equals( "" ) )
        {
            pathFromRoot = "/" + pathFromRoot;
        }

        s.append( encodePath( pathFromRoot, siteKey ) );
        return s.toString();
    }

    /**
     * Builds up a path consisting of contextPath and sitePath from given params. Ensures that the localPath gets url
     * encoded.
     */
    private String doCreateFullPathForRedirectFromLocalPath( HttpServletRequest request, SiteKey siteKey, String localPath )
    {
        StringBuffer s = new StringBuffer();
        s.append( request.getContextPath() );
        s.append( resolveLocalSitePathPrefix( request, siteKey, true ) );

        if ( !localPath.startsWith( "/" ) && !localPath.equals( "" ) )
        {
            localPath = "/" + localPath;
        }

        s.append( encodePath( localPath, siteKey ) );
        return s.toString();
    }

    private String encodePath( String path, SiteKey siteKey )
    {
        String encoding = getUrlCharacterEncodingForSite( siteKey );
        return UrlPathEncoder.encodeUrlPath( path, encoding );
    }

    private String getUrlCharacterEncodingForSite( SiteKey siteKey )
    {
        return sitePropertiesService.getProperty( SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, siteKey );
    }

    private boolean siteIsCreatingRelativeUrlsFromRoot( SiteKey siteKey )
    {
        return sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.CREATE_URL_AS_PATH_PROPERTY, siteKey );
    }


    public void setOverridingSitePropertyCreateUrlAsPath( final Boolean value )
    {
        if ( value != null )
        {
            this.overridingSitePropertyCreateUrlAsPath = value;
        }
    }

    public void setHtmlEscapeParameterAmps( boolean htmlEscapeParameterAmps )
    {
        this.htmlEscapeParameterAmps = htmlEscapeParameterAmps;
    }

    public void setSitePathPrefix( String value )
    {
        this.sitePathPrefix = value;
    }

    public void setSitePropertiesService( SitePropertiesService value )
    {
        this.sitePropertiesService = value;
    }
}
