package com.enonic.cms.core.plugin.manager;

import java.util.List;

import org.joda.time.DateTime;
import org.osgi.framework.Bundle;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.Plugin;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class PluginImpl
    implements Plugin
{
    private final static LogFacade LOG = LogFacade.get( PluginImpl.class );

    private final Bundle bundle;

    private final ExtensionHolder holder;

    public PluginImpl( final Bundle bundle, final ExtensionHolder holder )
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

    public List<Extension> getExtensions()
    {
        return this.holder.getAllForBundle( this.bundle );
    }

    public DateTime getTimestamp()
    {
        return new DateTime( this.bundle.getLastModified() );
    }

    public void update()
    {
        if ( isFramework() )
        {
            return;
        }

        try
        {
            this.bundle.update();
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error updating bundle [{0}]", this.bundle.getSymbolicName() );
        }
    }

    public boolean isFramework()
    {
        return OsgiHelper.isFrameworkBundle( this.bundle );
    }
}
