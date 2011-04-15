package com.enonic.cms.core.plugin.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.util.LogFacade;

final class PluginConfigFactory
    implements ServiceFactory
{
    private final static LogFacade LOG = LogFacade.get( PluginConfigFactory.class );

    private final File configDir;

    public PluginConfigFactory( final File configDir )
    {
        this.configDir = configDir;
    }

    private PluginConfig loadConfig( final Bundle bundle )
    {
        final File file = getConfigFile( bundle );

        final Map<String, String> config = new HashMap<String, String>();
        config.putAll( PluginConfigHelper.loadDefaultProperties( bundle ) );
        config.putAll( PluginConfigHelper.loadProperties( file ) );

        if ( file.exists() )
        {
            LOG.info( "Loaded configuration for bundle [{0}] from [{1}]", bundle.getSymbolicName(), file.getAbsolutePath() );
        }

        return new PluginConfigImpl( PluginConfigHelper.interpolate( bundle.getBundleContext(), config ) );
    }

    private File getConfigFile( final Bundle bundle )
    {
        final String id = getConfigurationId( bundle );
        return new File( this.configDir, id + ".properties" );
    }

    private String getConfigurationId( final Bundle bundle )
    {
        return bundle.getSymbolicName();
    }

    public Object getService( final Bundle bundle, final ServiceRegistration reg )
    {
        return loadConfig( bundle );
    }

    public void ungetService( final Bundle bundle, final ServiceRegistration reg, final Object o )
    {
        // Do nothing
    }

    public void register( final BundleContext context )
    {
        context.registerService( PluginConfig.class.getName(), this, null );
    }
}
