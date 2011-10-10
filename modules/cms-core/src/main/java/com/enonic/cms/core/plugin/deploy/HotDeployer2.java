package com.enonic.cms.core.plugin.deploy;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import java.io.File;

public final class HotDeployer2
{
    private long interval;
    private File directory;
    private FileAlterationMonitor monitor;
    private PluginDeployer deployer;

    public void setDirectory(final File directory)
    {
        this.directory = directory;
    }

    public void setInterval(final long interval)
    {
        this.interval = interval;
    }

    public void setDeployer(final PluginDeployer deployer)
    {
        this.deployer = deployer;
    }

    public void start()
        throws Exception
    {
        final JarFileFilter filter = new JarFileFilter();
        final FileAlterationObserver observer = new FileAlterationObserver(this.directory, filter);
        observer.addListener(new HotDeployerListener(this.deployer));

        this.monitor = new FileAlterationMonitor(this.interval, observer);
        this.monitor.start();
    }

    public void stop()
        throws Exception
    {
        try {
            this.monitor.stop();
        } finally {
            this.monitor = null;
        }
    }
}
