package com.enonic.cms.core.plugin.deploy;

import java.io.File;

public interface PluginDeployer
{
    public void install(File file);

    public void uninstall(File file);
}
