package com.enonic.cms.core.plugin.spring;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class SpringActivator
    implements BundleActivator
{
    private SpringExtender tracker;

    public void start( final BundleContext context )
        throws Exception
    {
        this.tracker = new SpringExtender( context );
        this.tracker.open();
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.tracker.close();
    }
}
