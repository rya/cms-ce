package com.enonic.cms.server.service.tools;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.format.DateTimeFormatterBuilder;

import com.enonic.cms.core.plugin.Plugin;
import com.enonic.cms.core.plugin.util.OsgiHelper;

public final class PluginWrapper
{
    private final Plugin plugin;

    public PluginWrapper( final Plugin plugin )
    {
        this.plugin = plugin;
    }

    public long getKey()
    {
        return this.plugin.getKey();
    }

    public String getId()
    {
        return this.plugin.getId();
    }

    public String getName()
    {
        return this.plugin.getName();
    }

    public String getVersion()
    {
        return this.plugin.getVersion();
    }

    public String getTimestamp()
    {
        return new DateTimeFormatterBuilder().appendYear( 4, 4 ).appendLiteral( '-' ).appendMonthOfYear( 2 ).appendLiteral(
            '-' ).appendDayOfMonth( 2 ).appendLiteral( ' ' ).appendHourOfDay( 2 ).appendLiteral( ':' ).appendMinuteOfHour(
            2 ).appendLiteral( ':' ).appendSecondOfMinute( 2 ).toFormatter().print( this.plugin.getTimestamp() );
    }

    public boolean isActive()
    {
        return this.plugin.isActive();
    }

    public boolean isFramework()
    {
        return this.plugin.isFramework();
    }

    public Collection<String> getExportedPackages()
    {
        return OsgiHelper.getExportPackages( this.plugin.getBundle() );
    }

    public Collection<String> getImportedPackages()
    {
        return OsgiHelper.getImportPackages( this.plugin.getBundle() );
    }

    public static Collection<PluginWrapper> toWrapperList( final Collection<? extends Plugin> list )
    {
        final ArrayList<PluginWrapper> target = new ArrayList<PluginWrapper>();
        for ( final Plugin item : list )
        {
            target.add( new PluginWrapper( item ) );
        }

        return target;
    }
}
