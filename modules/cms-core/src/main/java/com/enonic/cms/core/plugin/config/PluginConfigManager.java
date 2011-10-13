package com.enonic.cms.core.plugin.config;

import java.io.File;
import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.core.plugin.container.OsgiContributor;
import org.osgi.framework.BundleContext;
import com.enonic.cms.api.util.LogFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class PluginConfigManager
    extends OsgiContributor
{
    private final static LogFacade LOG = LogFacade.get( PluginConfigManager.class );

    private File configDir;

    public PluginConfigManager()
    {
        super(1);
    }

    @Value("#{config.pluginConfigDir}")
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
