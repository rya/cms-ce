package com.enonic.cms.core.plugin.host;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public final class HostActivator
    implements BundleActivator
{
    private final HostServices services;

    public HostActivator()
    {
        this.services = new HostServices();
    }

    public void start( final BundleContext context )
        throws Exception
    {
        this.services.register( context );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
    }

    public void addService( final Object service )
    {
        this.services.add( service );
    }
}