package com.enonic.cms.core.plugin.installer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleInstallerImplTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private BundleContext bundleContext;
    private BundleInstallerImpl installer;
    private String pluginLocation;
    private Bundle bundle;

    @Before
    public void setUp()
        throws Exception
    {
        this.bundleContext = Mockito.mock(BundleContext.class);
        this.installer = new BundleInstallerImpl(this.bundleContext);

        this.pluginLocation = this.folder.newFile("plugin.jar").toURI().toURL().toExternalForm();

        this.bundle = Mockito.mock(Bundle.class);
        Mockito.when(this.bundle.getSymbolicName()).thenReturn("some.plugin");
        Mockito.when(this.bundle.getLocation()).thenReturn("plugin:" + this.pluginLocation);
    }

    @Test
    public void testInstall()
        throws Exception
    {
        Mockito.when(this.bundleContext.getBundles()).thenReturn(new Bundle[0]);
        Mockito.when(this.bundleContext.installBundle("plugin:" + this.pluginLocation))
                .thenReturn(this.bundle);

        this.installer.install(this.pluginLocation);

        Mockito.verify(this.bundle, Mockito.times(1)).start(0);
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        Mockito.when(this.bundleContext.getBundles()).thenReturn(new Bundle[] { this.bundle });

        this.installer.install(this.pluginLocation);

        Mockito.verify(this.bundle, Mockito.times(1)).update();
        Mockito.verify(this.bundle, Mockito.times(1)).start(0);
    }

    @Test
    public void testUninstall()
        throws Exception
    {
        Mockito.when(this.bundleContext.getBundles()).thenReturn(new Bundle[] { this.bundle });

        this.installer.uninstall(this.pluginLocation);

        Mockito.verify(this.bundle, Mockito.times(1)).uninstall();
    }
}
