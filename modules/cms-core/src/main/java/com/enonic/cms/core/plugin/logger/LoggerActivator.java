package com.enonic.cms.core.plugin.logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class LoggerActivator
    implements BundleActivator
{
    public void start( final BundleContext context )
        throws Exception
    {
        context.addBundleListener( new BundleEventLogger() );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        // Do nothing
    }
}
