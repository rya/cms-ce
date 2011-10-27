package com.enonic.cms.core.plugin.logger;

import org.osgi.framework.BundleContext;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class LoggerActivator
    extends OsgiContributor
{
    public LoggerActivator()
    {
        super(0);
    }
    
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
