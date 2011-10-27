package com.enonic.cms.core.plugin.manager;

import org.joda.time.DateTime;
import org.osgi.framework.Bundle;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class PluginHandleImpl
    implements PluginHandle
{
    private final static LogFacade LOG = LogFacade.get(PluginHandleImpl.class);

    private final Bundle bundle;
    private final ExtensionHolder holder;

    public PluginHandleImpl(final Bundle bundle, final ExtensionHolder holder)
    {
        this.bundle = bundle;
        this.holder = holder;
    }

    public long getKey()
    {
        return this.bundle.getBundleId();
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

    public boolean isActive()
    {
        return this.bundle.getState() == Bundle.ACTIVE;
    }

    public DateTime getTimestamp()
    {
        return new DateTime( this.bundle.getLastModified() );
    }

    public PluginConfig getConfig()
    {
        return OsgiHelper.requireService(this.bundle.getBundleContext(), PluginConfig.class);
    }

    public void update()
    {
        try {
            this.bundle.update();
        } catch (final Exception e) {
            LOG.warningCause("Exception when updating plugin [{0}]", e, this.bundle.getSymbolicName());
        }
    }

    public ExtensionSet getExtensions()
    {
        return new ExtensionSetImpl(this.holder.getAllForBundle(this.bundle));
    }
}
