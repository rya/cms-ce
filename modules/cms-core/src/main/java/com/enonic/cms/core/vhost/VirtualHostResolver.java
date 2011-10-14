/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

/**
 * This class implements the virtual host resolver. This should be an interface if
 * multiple implementations will be added.
 */
@Component
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

    private File configFile;

    /**
     * Initializes the resolver.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        this.virtualHosts = new ArrayList<VirtualHost>();
        this.configuration = new Properties();

        if ((this.configFile != null) && this.configFile.exists()) {
            this.configuration = PropertiesLoaderUtils.loadProperties(new FileSystemResource(this.configFile));
        }

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

    @Value("#{config.virtualHostConfigFile}")
    public void setConfigFile(final File file)
    {
        this.configFile = file;
    }
}
