/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.business.DeploymentPathResolver;

public class AdminHelper
{
    public static String getAdminPath( HttpServletRequest request, boolean relative )
    {
        StringBuffer path = new StringBuffer();
        if ( !relative )
        {
            path.append( request.getScheme() );
            path.append( "://" );
            path.append( request.getServerName() );
            if ( request.getServerPort() != 80 )
            {
                path.append( ":" );
                path.append( request.getServerPort() );
            }
        }
        path.append( DeploymentPathResolver.getAdminDeploymentPath( request ) );

        return path.toString();
    }
}
