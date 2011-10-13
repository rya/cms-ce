package com.enonic.cms.core.plugin.logger;

import com.enonic.cms.core.plugin.container.OsgiContributor;
import org.osgi.framework.BundleContext;
import org.springframework.stereotype.Component;

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
