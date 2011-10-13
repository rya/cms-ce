package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import com.enonic.cms.core.plugin.container.OsgiContributor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.framework.BundleContext;
import com.enonic.cms.api.util.LogFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class HotDeployer
    extends OsgiContributor
{
    private final static LogFacade LOG = LogFacade.get( HotDeployer.class );

    private File deployDir;

    private long scanPeriod;

    private FileAlterationMonitor monitor;

    public HotDeployer()
    {
        super(10);
    }

    @Value("#{config.pluginDeployDir}")
    public void setDeployDir( final File deployDir )
    {
        this.deployDir = deployDir;
    }

    @Value("#{config.pluginScanPeriod}")
    public void setScanPeriod( final long scanPeriod )
    {
        this.scanPeriod = scanPeriod;
    }

    public void start( final BundleContext context )
        throws Exception
    {
        final JarFileFilter filter = new JarFileFilter();
        final PluginInstaller installer = new PluginInstaller( context );

        final FileAlterationObserver observer = new FileAlterationObserver(this.deployDir, filter);
        observer.addListener(installer);
        observer.checkAndNotify();

        this.monitor = new FileAlterationMonitor(this.scanPeriod, observer);
        this.monitor.start();

        LOG.info("Hot deploying plugins from [{0}]. Scanning every [{1}] ms.", this.deployDir.getAbsolutePath(), this.scanPeriod);

    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.monitor.stop();
    }
}
