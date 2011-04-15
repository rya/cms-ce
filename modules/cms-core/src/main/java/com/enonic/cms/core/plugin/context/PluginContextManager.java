package com.enonic.cms.core.plugin.context;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class PluginContextManager
    implements BundleActivator
{
    public void start( final BundleContext context )
        throws Exception
    {
        new PluginContextFactory().register( context );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
