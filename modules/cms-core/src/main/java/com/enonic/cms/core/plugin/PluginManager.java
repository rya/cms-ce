package com.enonic.cms.core.plugin;

import java.util.List;

import com.enonic.cms.api.plugin.ext.Extension;

// TODO: Should extend ExtensionManager
public interface PluginManager
{
    public List<Plugin> getPlugins();

    public List<Extension> getExtensions();

    public void updatePlugin(long key);
}
