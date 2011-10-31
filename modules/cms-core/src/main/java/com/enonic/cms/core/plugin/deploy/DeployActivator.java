package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import com.enonic.cms.core.plugin.installer.BundleInstaller;
import com.enonic.cms.core.plugin.util.OsgiHelper;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class DeployActivator
    extends OsgiContributor
{
    private final HotDeployTask deployer;

    public DeployActivator()
    {
        super(10);
        this.deployer = new HotDeployTask();
    }

    @Value("#{config.pluginDeployDir}")
    public void setDeployDir( final File deployDir )
    {
        this.deployer.setDeployDir(deployDir);
    }

    @Value("#{config.pluginScanPeriod}")
    public void setScanPeriod( final long scanPeriod )
    {
        this.deployer.setScanPeriod(scanPeriod);
    }

    public void start( final BundleContext context )
        throws Exception
    {
        final BundleInstaller installer = OsgiHelper.requireService(context, BundleInstaller.class);
        this.deployer.setInstaller(installer);
        this.deployer.start();
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.deployer.stop();
    }
}
