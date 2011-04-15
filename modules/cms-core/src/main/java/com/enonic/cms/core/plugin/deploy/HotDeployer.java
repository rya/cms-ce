package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import java.util.Timer;

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

    private Timer timer;

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
        final FileScanner scanner = new FileScanner( deployDir, new JarFileFilter() );
        final PackageAdmin packageAdmin = OsgiHelper.requireService( context, PackageAdmin.class );
        final BundleInstaller installer = new BundleInstaller( context, packageAdmin, scanner );

        LOG.info( "Hot deploying plugins from [{0}]. Scanning every [{1}] ms.", this.deployDir.getAbsolutePath(), this.scanPeriod );

        this.timer = new Timer();
        this.timer.schedule( new HotDeployerTask( installer ), 0, this.scanPeriod );
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.timer.cancel();
    }
}
