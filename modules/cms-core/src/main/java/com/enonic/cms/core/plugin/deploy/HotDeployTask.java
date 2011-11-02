package com.enonic.cms.core.plugin.deploy;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.installer.BundleInstaller;

final class HotDeployTask
{
    private final static LogFacade LOG = LogFacade.get( HotDeployTask.class );

    private File deployDir;

    private long scanPeriod;

    private FileAlterationMonitor monitor;

    private HotDeployListener listener;

    public void setDeployDir( final File deployDir )
    {
        this.deployDir = deployDir;
    }

    public void setScanPeriod( final long scanPeriod )
    {
        this.scanPeriod = scanPeriod;
    }

    public void setInstaller( final BundleInstaller installer )
    {
        this.listener = new HotDeployListener(installer);
    }

    public void start()
        throws Exception
    {
        final JarFileFilter filter = new JarFileFilter();

        final FileAlterationObserver observer = new FileAlterationObserver(this.deployDir, filter);
        observer.addListener(this.listener);
        observer.checkAndNotify();

        this.monitor = new FileAlterationMonitor(this.scanPeriod, observer);
        this.monitor.start();

        LOG.info("Hot deploying plugins from [{0}]. Scanning every [{1}] ms.", this.deployDir.getAbsolutePath(), this.scanPeriod);
    }

    public void stop()
        throws Exception
    {
        this.monitor.stop();
    }
}
