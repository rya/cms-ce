/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import com.enonic.cms.domain.PathAndParams;
import com.enonic.cms.domain.SitePath;

public class ResourceKeyResolverForSiteLocalResources
{
    private String pathToPublicHome;

    public ResourceKeyResolverForSiteLocalResources( String pathToPublicHome )
    {
        this.pathToPublicHome = pathToPublicHome;

        if ( this.pathToPublicHome != null )
        {
            validatePathToPublicHome( this.pathToPublicHome );
        }
    }

    private void validatePathToPublicHome( String value )
    {
        if ( !value.startsWith( "/" ) )
        {
            throw new IllegalArgumentException( "pathToPublicHome must start with /" );
        }
    }

    public ResourceKey resolveResourceKey( SitePath sitePath )
    {
        PathAndParams localPathAndParams = new PathAndParams( sitePath.getLocalPath(), sitePath.getRequestParameters() );
        return resolveResourceKeyFromPath( localPathAndParams.getPath().toString() );
    }

    private ResourceKey resolveResourceKeyFromPath( String localPath )
    {
        if ( localPath.contains( "/~/" ) )
        {
            String resolvedPathToHome = pathToPublicHome + "/";
            localPath = localPath.substring( localPath.indexOf( "/~/" ) );
            localPath = localPath.replace( "/~/", resolvedPathToHome );
        }
        else if ( localPath.startsWith( "/" ) )
        {
            localPath = localPath.substring( "/".length() );
        }

        return new ResourceKey( localPath );
    }
}
