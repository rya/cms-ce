/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.httpservices;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SitePath;

/**
 *
 */
public class UserServicesParameterResolver
{

    public static String resolveHandlerFromSitePath( SitePath userServicesSitePath )
    {
        Path localPath = userServicesSitePath.getLocalPath();

        if ( localPath.getPathElementsCount() < 2 )
        {
            return null;
        }

        return localPath.getPathElement( 1 );
    }


    public static String resolveOperationFromSitePath( SitePath userServicesSitePath )
    {
        Path localPath = userServicesSitePath.getLocalPath();

        if ( localPath.getPathElementsCount() < 3 )
        {
            return null;
        }

        return localPath.getPathElement( 2 );
    }
}
