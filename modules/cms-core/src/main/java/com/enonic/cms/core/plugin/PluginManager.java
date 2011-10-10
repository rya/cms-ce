package com.enonic.cms.core.plugin;

import java.util.List;

import com.enonic.cms.api.plugin.ext.Extension;

public interface PluginManager
{
    public List<Plugin> getPlugins();

    public List<Extension> getExtensions();

    public Plugin getPluginByKey(long key);

    public void updatePlugin(long key);
}
