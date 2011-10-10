package com.enonic.cms.core.plugin.config;

import java.io.File;
import com.enonic.cms.api.plugin.PluginConfig;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.enonic.cms.api.util.LogFacade;

public final class PluginConfigManager
    implements BundleActivator
{
    private final static LogFacade LOG = LogFacade.get( PluginConfigManager.class );

    private File configDir;

    public void setConfigDir( final File configDir )
    {
        this.configDir = configDir;
    }

    public void start( final BundleContext context )
        throws Exception
    {
        LOG.info( "Plugin configuration is loaded from [{0}]", this.configDir.getAbsolutePath() );
        final PluginConfigFactory factory = new PluginConfigFactory(this.configDir);
        context.registerService(PluginConfig.class.getName(), factory, null);
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
