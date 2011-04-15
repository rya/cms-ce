package com.enonic.cms.core.plugin.container;

import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Iterables;

final class CompositeActivator
    implements BundleActivator
{
    public final List<BundleActivator> list;

    public CompositeActivator( final List<BundleActivator> list )
    {
        this.list = list;
    }

    public void start( final BundleContext context )
        throws Exception
    {
        for ( final BundleActivator activator : this.list )
        {
            activator.start( context );
        }
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        for ( final BundleActivator activator : Iterables.reverse( this.list ) )
        {
            activator.stop( context );
        }
    }
}
