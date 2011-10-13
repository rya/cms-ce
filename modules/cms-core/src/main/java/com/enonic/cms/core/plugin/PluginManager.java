package com.enonic.cms.core.plugin;

import java.util.List;

public interface PluginManager
{
    public List<PluginHandle> getPlugins();

    public ExtensionSet getExtensions();

    public PluginHandle findPluginByKey(long key);
}
