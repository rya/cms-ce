package com.enonic.cms.core.plugin.context;

import org.osgi.framework.BundleContext;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class PluginContextManager
    extends OsgiContributor
{
    public PluginContextManager()
    {
        super(1);
    }
    
    public void start( final BundleContext context )
        throws Exception
    {
        final PluginContextFactory factory = new PluginContextFactory();
        context.registerService( PluginContext.class.getName(), factory, null );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
