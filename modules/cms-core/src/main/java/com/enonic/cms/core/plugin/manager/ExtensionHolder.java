package com.enonic.cms.core.plugin.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ExtensionListener;

final class ExtensionHolder
{
    private final Map<ServiceReference, Extension> map;
    private final List<ExtensionListener> listeners;

    public ExtensionHolder()
    {
        this.map = Maps.newHashMap();
        this.listeners = Lists.newArrayList();
    }

    public void setListeners(final List<ExtensionListener> list)
    {
        this.listeners.clear();
        this.listeners.addAll(list);
    }
    
    public List<Extension> getAll()
    {
        return ImmutableList.copyOf(this.map.values());
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

    public void add(final ServiceReference ref, final Extension ext)
    {
        this.map.put(ref, ext);

        for (final ExtensionListener listener : this.listeners) {
            listener.extensionAdded(ext);
        }
    }

    public void remove(final ServiceReference ref)
    {
        final Extension ext = this.map.remove(ref);
        if (ext == null) {
            return;
        }

        for (final ExtensionListener listener : this.listeners) {
            listener.extensionRemoved(ext);
        }
    }
}
