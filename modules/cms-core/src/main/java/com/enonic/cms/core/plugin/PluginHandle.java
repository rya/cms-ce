package com.enonic.cms.core.plugin;

import com.enonic.cms.api.plugin.PluginConfig;
import org.joda.time.DateTime;

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
