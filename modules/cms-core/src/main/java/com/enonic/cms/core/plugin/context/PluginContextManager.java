package com.enonic.cms.core.plugin.context;

import com.enonic.cms.api.plugin.PluginContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class PluginContextManager
    implements BundleActivator
{
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
