package com.enonic.cms.core.plugin.deploy;

import java.util.TimerTask;

final class HotDeployerTask
    extends TimerTask
{
    private final BundleInstaller installer;

    public HotDeployerTask( final BundleInstaller installer )
    {
        this.installer = installer;
    }

    @Override
    public void run()
    {
        this.installer.process();
    }
}
