package com.enonic.cms.core.plugin;

import org.joda.time.DateTime;

import com.enonic.cms.api.plugin.PluginConfig;

public interface PluginHandle
{
    public long getKey();

    public String getId();

    public String getName();

    public String getVersion();

    public boolean isActive();

    public DateTime getTimestamp();

    public ExtensionSet getExtensions();

    public PluginConfig getConfig();

    public void update();
}
