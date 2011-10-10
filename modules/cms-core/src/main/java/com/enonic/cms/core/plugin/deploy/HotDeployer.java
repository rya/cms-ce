package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.util.OsgiHelper;

public final class HotDeployer
    implements BundleActivator
{
    private final static LogFacade LOG = LogFacade.get( HotDeployer.class );

    private File deployDir;

    private long scanPeriod;

    private FileAlterationMonitor monitor;

    public void setDeployDir( final File deployDir )
    {
        this.deployDir = deployDir;
    }

    public void setScanPeriod( final long scanPeriod )
    {
        this.scanPeriod = scanPeriod;
    }

    public void start( final BundleContext context )
        throws Exception
    {
        final JarFileFilter filter = new JarFileFilter();
        final PackageAdmin packageAdmin = OsgiHelper.requireService( context, PackageAdmin.class );
        final PluginInstaller installer = new PluginInstaller( context, packageAdmin );

        final FileAlterationObserver observer = new FileAlterationObserver(this.deployDir, filter);
        observer.addListener(installer);

        this.monitor = new FileAlterationMonitor(this.scanPeriod, observer);
        this.monitor.start();

        LOG.info( "Hot deploying plugins from [{0}]. Scanning every [{1}] ms.", this.deployDir.getAbsolutePath(), this.scanPeriod );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.monitor.stop();
    }
}
