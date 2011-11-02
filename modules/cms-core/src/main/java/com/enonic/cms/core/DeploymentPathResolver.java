/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.vhost.VirtualHostHelper;

public class DeploymentPathResolver
{
    public static String getAdminDeploymentPath( HttpServletRequest request )
    {
        if ( VirtualHostHelper.hasBasePath( request ) )
        {
            return VirtualHostHelper.getBasePath( request );
        }

        String contextPath = request.getContextPath();
        if ( contextPath != null && contextPath.length() > 0 )
        {
            return contextPath + "/admin";
        }

        return "/admin";
    }

    public static String getSiteDeploymentPath( HttpServletRequest request )
    {
        if ( VirtualHostHelper.hasBasePath( request ) )
        {
            return VirtualHostHelper.getBasePath( request );
        }

        String sitePathPrefix = "/site";
        SiteKeyResolver siteKeyResolver = new SiteKeyResolver();
        String siteDeploymentPath = sitePathPrefix + "/" + siteKeyResolver.resolveSiteKey( request, sitePathPrefix );

        String contextPath = request.getContextPath();
        if ( contextPath != null && contextPath.length() > 0 )
        {
            siteDeploymentPath = contextPath + siteDeploymentPath;
            return siteDeploymentPath;
        }

        return siteDeploymentPath;
    }
}
