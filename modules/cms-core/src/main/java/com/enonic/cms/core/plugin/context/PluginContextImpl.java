package com.enonic.cms.core.plugin.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class PluginContextImpl
    implements PluginContext
{
    private final Bundle bundle;

    private final BundleContext context;

    private PluginConfig config;

    public PluginContextImpl( final Bundle bundle )
    {
        this.bundle = bundle;
        this.context = this.bundle.getBundleContext();
    }

    public String getId()
    {
        return this.bundle.getSymbolicName();
    }

    public String getName()
    {
        return OsgiHelper.getBundleName( this.bundle );
    }

    public String getVersion()
    {
        return this.bundle.getVersion().toString();
    }

    public synchronized PluginConfig getConfig()
    {
        if ( this.config == null )
        {
            this.config = OsgiHelper.requireService( this.context, PluginConfig.class );
        }

        return this.config;
    }

    public <T> T getService( final Class<T> type )
    {
        return OsgiHelper.requireService( this.context, type );
    }

    public void register( final Extension ext )
    {
        this.context.registerService( Extension.class.getName(), ext, null );
    }
}
