package com.enonic.cms.core.plugin.context;

import java.io.File;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class ContextActivator
    extends OsgiContributor
{
    private final static LogFacade LOG = LogFacade.get( ContextActivator.class );

    private File configDir;

    public ContextActivator()
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
        final PluginConfigFactory configFactory = new PluginConfigFactory(this.configDir);
        context.registerService(PluginConfig.class.getName(), configFactory, null);

        final PluginContextFactory contextFactory = new PluginContextFactory();
        context.registerService( PluginContext.class.getName(), contextFactory, null );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
