package com.enonic.cms.core.plugin.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.enonic.cms.core.plugin.*;
import com.enonic.cms.core.plugin.container.OsgiContributor;
import com.enonic.cms.core.plugin.util.OsgiHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("pluginManager")
public final class PluginManagerImpl
    extends OsgiContributor implements PluginManager
{
    private final ExtensionHolder holder;

    private ExtensionTracker tracker;

    private BundleContext context;

    public PluginManagerImpl()
    {
        super(1);
        this.holder = new ExtensionHolder();
    }
    
    public void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        this.tracker = new ExtensionTracker( this.context, this.holder );
        this.tracker.open();
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.tracker.close();
    }

    public List<PluginHandle> getPlugins()
    {
        if ( this.context == null )
        {
            return Collections.emptyList();
        }

        final ArrayList<PluginHandle> list = Lists.newArrayList();
        for ( final Bundle bundle : getBundles() )
        {
            list.add( new PluginHandleImpl( bundle, this.holder ) );
        }

        return list;
    }

    private List<Bundle> getBundles()
    {
        final ArrayList<Bundle> list = Lists.newArrayList();
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if (!OsgiHelper.isFrameworkBundle(bundle)) {
                list.add(bundle);
            }
        }

        return list;
    }

    public PluginHandle findPluginByKey(final long key)
    {
        for (final PluginHandle plugin : getPlugins()) {
            if (plugin.getKey() == key) {
                return plugin;
            }
        }

        return null;
    }

    @Autowired(required = false)
    public void setListeners(final List<ExtensionListener> list)
    {
        this.holder.setListeners(list);
    }

    public ExtensionSet getExtensions()
    {
        return new ExtensionSetImpl(this.holder.getAll());
    }
}
