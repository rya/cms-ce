package com.enonic.cms.core.plugin.manager;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ExtensionListener;
import com.enonic.cms.core.plugin.Plugin;
import com.enonic.cms.core.plugin.PluginManager;

final class PluginManagerImpl
    implements PluginManager, BundleActivator
{
    private ExtensionTracker extensions;

    private List<ExtensionListener> listeners;

    private BundleContext context;

    public void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        this.extensions = new ExtensionTracker( this.context, this.listeners );
        this.extensions.open();
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.extensions.close();
    }

    public List<Plugin> getPlugins()
    {
        if ( this.context == null )
        {
            return ImmutableList.of();
        }

        final ArrayList<Plugin> list = Lists.newArrayList();
        for ( final Bundle bundle : this.context.getBundles() )
        {
            list.add( new PluginImpl( bundle, this.extensions ) );
        }

        return ImmutableList.copyOf( list );
    }

    public List<Extension> getExtensions()
    {
        if ( this.extensions != null )
        {
            return this.extensions.getAll();
        }

        return ImmutableList.of();
    }

    public void setListeners( List<ExtensionListener> listeners )
    {
        this.listeners = listeners;
    }

    public void updatePlugin(long key)
    {
        throw new UnsupportedOperationException();
    }
}
