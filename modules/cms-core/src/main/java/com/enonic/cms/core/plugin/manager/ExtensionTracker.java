package com.enonic.cms.core.plugin.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ExtensionListener;
import com.enonic.cms.core.plugin.ExtensionManagerAccessor;

final class ExtensionTracker
    extends ServiceTracker
    implements ExtensionHolder
{
    private final Map<ServiceReference, Extension> map;

    public ExtensionTracker( final BundleContext context )
    {
        super( context, Extension.class.getName(), null );
        this.map = Maps.newHashMap();
    }

    @Override
    public Object addingService( final ServiceReference ref )
    {
        final Object ext = super.addingService( ref );
        if ( ext instanceof Extension )
        {
            this.map.put( ref, (Extension) ext );
            for ( ExtensionListener listener : ExtensionManagerAccessor.getExtensionManager().getListeners().values() )
            {
                listener.extensionAdded( (Extension) ext );
            }
        }

        return ext;
    }

    @Override
    public void removedService( final ServiceReference ref, final Object service )
    {
        Extension ext = this.map.get( ref );
        for ( ExtensionListener listener : ExtensionManagerAccessor.getExtensionManager().getListeners().values() )
        {
            listener.extensionRemoved( ext );
        }
        this.map.remove( ref );
        super.removedService( ref, service );
    }

    public List<Extension> getAll()
    {
        return ImmutableList.copyOf( this.map.values() );
    }

    public List<Extension> getAllForBundle( final Bundle bundle )
    {
        final Map<ServiceReference, Extension> cloned = Maps.newHashMap( this.map );

        final ArrayList<Extension> list = Lists.newArrayList();
        for ( final Map.Entry<ServiceReference, Extension> entry : cloned.entrySet() )
        {
            if ( entry.getKey().getBundle().getBundleId() == bundle.getBundleId() )
            {
                list.add( entry.getValue() );
            }
        }

        return list;
    }
}
