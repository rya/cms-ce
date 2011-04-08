/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Preconditions;

import com.enonic.cms.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.AdminSiteDebugBasePath;
import com.enonic.cms.domain.AdminSitePreviewBasePath;
import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.PortalSiteBasePath;
import com.enonic.cms.domain.SiteBasePath;
import com.enonic.cms.domain.SiteKey;

/**
 * Sep 3, 2010
 */
public class SiteBasePathResolver
{
    public static SiteBasePath resolveSiteBasePath( HttpServletRequest httpRequest, SiteKey sitekey )
    {
        Preconditions.checkNotNull( httpRequest );
        Preconditions.checkNotNull( sitekey );

        SiteBasePath siteBasePath;

        if ( isInDebugMode() )
        {
            Path adminPath = new Path( DeploymentPathResolver.getAdminDeploymentPath( httpRequest ) );
            siteBasePath = new AdminSiteDebugBasePath( adminPath, sitekey );
        }
        else if ( isInPreviewMode( httpRequest ) )
        {
            Path adminPath = new Path( DeploymentPathResolver.getAdminDeploymentPath( httpRequest ) );
            siteBasePath = new AdminSitePreviewBasePath( adminPath, sitekey );
        }
        else
        {
            Path sitePrefixPath = new Path( DeploymentPathResolver.getSiteDeploymentPath( httpRequest ) );
            siteBasePath = new PortalSiteBasePath( sitePrefixPath, sitekey );
        }

        return siteBasePath;
    }

    private static boolean isInDebugMode()
    {
        return RenderTrace.isTraceOn();
    }

    private static boolean isInPreviewMode( HttpServletRequest httpRequest )
    {
        String previewEnabled = (String) httpRequest.getAttribute( Attribute.PREVIEW_ENABLED );
        return "true".equals( previewEnabled );
    }
}
