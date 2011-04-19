package com.enonic.cms.core.plugin.host;

import java.util.List;

import org.osgi.framework.BundleContext;

import com.google.common.collect.Lists;

final class HostServices
{
    private final List<HostService> list;

    public HostServices()
    {
        this.list = Lists.newArrayList();
    }

    public void register( final BundleContext context )
    {
        for ( final HostService service : this.list )
        {
            service.register( context );
        }
    }

    public void add( final Object instance )
    {
        this.list.add( new HostService( instance ) );
    }
}
