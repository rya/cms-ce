package com.enonic.cms.core.plugin.deploy;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import java.io.File;

final class HotDeployerListener
    extends FileAlterationListenerAdaptor
{
    private final PluginDeployer deployer;

    public HotDeployerListener(final PluginDeployer deployer)
    {
        this.deployer = deployer;
    }

    @Override
    public void onFileCreate(final File file)
    {
        install(file);
    }

    @Override
    public void onFileChange(final File file)
    {
        update(file);
    }

    @Override
    public void onFileDelete(final File file)
    {
        uninstall(file);
    }

    private void install(final File file)
    {
        this.deployer.install(file);
    }

    private void uninstall(final File file)
    {
        this.deployer.uninstall(file);
    }

    private void update(final File file)
    {
        uninstall(file);
        install(file);
    }
}
