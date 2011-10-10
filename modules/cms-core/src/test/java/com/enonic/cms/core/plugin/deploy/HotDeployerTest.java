package com.enonic.cms.core.plugin.deploy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class HotDeployerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStartup()
        throws Exception
    {
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        final PackageAdmin packageAdmin = Mockito.mock(PackageAdmin.class);

        final BundleContext context = Mockito.mock(BundleContext.class);
        Mockito.when(context.getServiceReference(PackageAdmin.class.getName())).thenReturn(ref);
        Mockito.when(context.getService(ref)).thenReturn(packageAdmin);

        final HotDeployer hotDeployer = new HotDeployer();
        hotDeployer.setScanPeriod(100L);
        hotDeployer.setDeployDir(this.folder.newFolder("plugins"));

        hotDeployer.start(context);
        hotDeployer.stop(context);
    }
}
