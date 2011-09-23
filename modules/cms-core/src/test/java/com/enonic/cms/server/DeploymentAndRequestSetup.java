/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server;

import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.vhost.VirtualHost;
import com.enonic.cms.core.vhost.VirtualHostResolver;

import com.enonic.cms.core.vhost.VirtualHostHelper;

import com.enonic.cms.domain.Attribute;

/**
 * Aug 24, 2010
 */
public class DeploymentAndRequestSetup
{
    private VirtualHostResolver virtualHostResolver = new VirtualHostResolver();

    private String appDeploymentPath;

    private String scheme = "http";

    private String originalRequestedHost;

    private String originalRequestedURI;

    private int requestedSiteKey;

    private String requestedSiteLocalPath;

    private PortalRequestSetup portalRequestSetup;

    private AdminDebugRequestSetup adminDebugRequestSetup;

    private AdminPreviewRequestSetup adminPreviewRequestSetup;

    public DeploymentAndRequestSetup appDeployedAtRoot()
    {
        return appDeployedAt( "/" );
    }

    public DeploymentAndRequestSetup appDeployedAt( String path )
    {
        this.appDeploymentPath = path;
        return this;
    }

    public String getAppDeploymentPath()
    {
        return appDeploymentPath;
    }

    public String getOriginalRequestedHost()
    {
        return originalRequestedHost;
    }

    public String getOriginalRequestedURI()
    {
        return originalRequestedURI;
    }

    public int getRequestedSiteKey()
    {
        return requestedSiteKey;
    }

    public String getRequestedSiteLocalPath()
    {
        return requestedSiteLocalPath;
    }

    public DeploymentAndRequestSetup requestedScheme( String scheme )
    {
        this.scheme = scheme;
        return this;
    }

    public DeploymentAndRequestSetup addVirtualHost( String pattern, String targetPath )
    {
        if ( !isAppDeployedAtRoot() )
        {
            throw new IllegalArgumentException( "Virtual Host setup is only supported when deployed at root" );
        }
        virtualHostResolver.addVirtualHost( pattern, targetPath );
        return this;
    }

    public DeploymentAndRequestSetup originalRequest( String requestedHost, String requestedURI )
    {
        if ( !requestedURI.startsWith( "/" ) )
        {
            throw new IllegalArgumentException( "original requestURI must start with /" );
        }
        originalRequestedHost = requestedHost;
        originalRequestedURI = requestedURI;

        return this;
    }

    public DeploymentAndRequestSetup requestedSite( int siteKey, String siteLocalPath )
    {
        if ( siteLocalPath.startsWith( "/" ) )
        {
            throw new IllegalArgumentException( "siteLocalPath cannot start with /" );
        }
        this.requestedSiteKey = siteKey;
        this.requestedSiteLocalPath = siteLocalPath;
        return this;
    }

    public PortalRequestSetup requestedPortalAt()
    {
        this.portalRequestSetup = new PortalRequestSetup();
        return this.portalRequestSetup;
    }


    public AdminDebugRequestSetup requestedAdminDebugAt()
    {
        this.adminDebugRequestSetup = new AdminDebugRequestSetup();
        return this.adminDebugRequestSetup;
    }


    public AdminPreviewRequestSetup requestedAdminPreviewAt()
    {
        this.adminPreviewRequestSetup = new AdminPreviewRequestSetup();
        return this.adminPreviewRequestSetup;
    }

    public void setup( MockHttpServletRequest request )
    {
        request.setScheme( scheme );
        if ( !appDeploymentPath.equals( "/" ) )
        {
            request.setContextPath( appDeploymentPath );
        }
        request.setServerName( originalRequestedHost );
        request.setRequestURI( originalRequestedURI );

        setupVhost( request );

        if ( portalRequestSetup != null )
        {
            portalRequestSetup.setup( request );
        }
        else if ( adminDebugRequestSetup != null )
        {
            adminDebugRequestSetup.setup( request );
        }
        else if ( adminPreviewRequestSetup != null )
        {
            adminPreviewRequestSetup.setup( request );
        }
    }

    private void setupVhost( MockHttpServletRequest request )
    {

        VirtualHost virtualHost = virtualHostResolver.resolve( request );
        if ( virtualHost != null )
        {
            String fullSourcePath = virtualHost.getFullSourcePath( request );
            VirtualHostHelper.setBasePath( request, fullSourcePath );
        }
    }

    private boolean isAppDeployedAtRoot()
    {
        return appDeploymentPath == null || appDeploymentPath.equals( "" ) || appDeploymentPath.equals( "/" );
    }

    private String spacesToPlus( String s )
    {
        return s.replaceAll( " ", "+" );
    }

    public class PortalRequestSetup
    {
        private String siteBasePath;

        public PortalRequestSetup()
        {
        }

        public PortalRequestSetup siteSetupAtRoot()
        {
            this.siteBasePath = "";
            return this;
        }

        public PortalRequestSetup siteSetupAtPath( String path )
        {
            this.siteBasePath = path;
            return this;
        }

        public PortalRequestSetup siteSetupAtDefaultPath()
        {
            this.siteBasePath = "/site";
            return this;
        }

        public void setup( MockHttpServletRequest request )
        {
            request.setServletPath( "/site" );

            if ( isAppDeployedAtRoot() )
            {
                request.setRequestURI( "/site/" + getRequestedSiteKey() + "/" + spacesToPlus( getRequestedSiteLocalPath() ) );
            }
            else
            {
                request.setRequestURI(
                    appDeploymentPath + "/site/" + getRequestedSiteKey() + "/" + spacesToPlus( getRequestedSiteLocalPath() ) );
            }
        }

        public DeploymentAndRequestSetup back()
        {
            return DeploymentAndRequestSetup.this;
        }
    }

    public class AdminPreviewRequestSetup
    {
        private String adminPath;

        public AdminPreviewRequestSetup()
        {
            if ( !getOriginalRequestedURI().contains( "/preview/" ) )
            {
                throw new IllegalStateException( "Expected original requested URI to contain '/preview/'" );
            }
        }

        public AdminPreviewRequestSetup setupAtDefaultPath()
        {
            this.adminPath = "/admin";
            return this;
        }

        public void setup( MockHttpServletRequest request )
        {
            request.setServletPath( "/site" );
            request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );

            String basePath = "";
            if ( !isAppDeployedAtRoot() )
            {
                basePath = getAppDeploymentPath();
            }

            request.setRequestURI( basePath + "/site/" + getRequestedSiteKey() + "/" + spacesToPlus( getRequestedSiteLocalPath() ) );
        }

        public DeploymentAndRequestSetup back()
        {
            return DeploymentAndRequestSetup.this;
        }

        private boolean isSetupAtRoot()
        {
            return adminPath == null || adminPath.equals( "" ) || adminPath.equals( "/" );
        }
    }

    public class AdminDebugRequestSetup
    {
        private String adminPath;

        public AdminDebugRequestSetup()
        {

        }

        public AdminDebugRequestSetup setupAtRoot()
        {
            this.adminPath = "";
            return this;
        }

        public AdminDebugRequestSetup setupAtDefaultPath()
        {
            this.adminPath = "/admin";
            return this;
        }

        public void setup( MockHttpServletRequest request )
        {
            request.setServletPath( "/site" );
            String basePath = "";
            if ( !isAppDeployedAtRoot() )
            {
                basePath = getAppDeploymentPath();
            }

            request.setRequestURI( basePath + "/site/" + getRequestedSiteKey() + "/" + spacesToPlus( getRequestedSiteLocalPath() ) );
        }

        public DeploymentAndRequestSetup back()
        {
            return DeploymentAndRequestSetup.this;
        }

    }
}
