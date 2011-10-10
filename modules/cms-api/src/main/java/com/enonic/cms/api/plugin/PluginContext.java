package com.enonic.cms.api.plugin;

import com.enonic.cms.api.plugin.ext.Extension;

public interface PluginContext
{
    public String getId();

    public String getName();

    public String getVersion();

    public PluginConfig getConfig();

    public <T> T getService(Class<T> type)
        throws PluginException;

    public void register(Extension extension);

    // public List<Extension> getExtensions();
}
