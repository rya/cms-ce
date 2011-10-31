package com.enonic.cms.core.plugin.deploy;

import com.enonic.cms.core.plugin.installer.BundleInstaller;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DeployActivatorTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStartup()
        throws Exception
    {
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        final BundleInstaller installer = Mockito.mock(BundleInstaller.class);

        final BundleContext context = Mockito.mock(BundleContext.class);
        Mockito.when(context.getServiceReference(BundleInstaller.class.getName())).thenReturn(ref);
        Mockito.when(context.getService(ref)).thenReturn(installer);

        final DeployActivator activator = new DeployActivator();
        activator.setScanPeriod(100L);
        activator.setDeployDir(this.folder.newFolder("plugins"));

        activator.start(context);
        activator.stop(context);
    }
}
