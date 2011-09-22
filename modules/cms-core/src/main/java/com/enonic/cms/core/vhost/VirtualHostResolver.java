/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class implements the virtual host resolver. This should be an interface if
 * multiple implementations will be added.
 */
public final class VirtualHostResolver
        implements InitializingBean
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostResolver.class );

    /**
     * List of virtual hosts.
     */
    private ArrayList<VirtualHost> virtualHosts = new ArrayList<VirtualHost>();

    /**
     * Configuration.
     */
    private Properties configuration;

    /**
     * Initializes the resolver.
     */
    public void afterPropertiesSet()
    {
        this.virtualHosts = new ArrayList<VirtualHost>();
        configureVirtualHosts();
        Collections.sort( this.virtualHosts );
    }

    /**
     * Configure property.
     */
    private void configureVirtualHosts()
    {
        for ( Object key : this.configuration.keySet() )
        {
            String pattern = key.toString();
            String targetPath = this.configuration.getProperty( pattern );
            addVirtualHost( pattern, targetPath );
        }
    }

    /**
     * Set the configuration.
     */
    public void setConfiguration( Properties configuration )
    {
        this.configuration = configuration;
    }

    /**
     * Resolve the virtual host. Returns null if no virtual host is found.
     */
    public VirtualHost resolve( HttpServletRequest req )
    {
        for ( VirtualHost virtualHost : this.virtualHosts )
        {
            if ( virtualHost.matches( req ) )
            {
                return virtualHost;
            }
        }

        return null;
    }

    /**
     * Add the virtual host.
     */
    public void addVirtualHost( String pattern, String targetPath )
    {
        pattern = pattern.trim();
        if ( !pattern.equals( "" ) )
        {
            try
            {
                this.virtualHosts.add( new VirtualHost( pattern, targetPath.trim() ) );
            }
            catch ( InvalidVirtualHostPatternException e )
            {
                LOG.warn( e.getMessage() );
            }
        }
    }
}
