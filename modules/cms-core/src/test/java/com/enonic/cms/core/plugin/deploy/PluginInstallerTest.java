package com.enonic.cms.core.plugin.deploy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;

import java.io.File;

public class PluginInstallerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private BundleContext bundleContext;
    private PackageAdmin packageAdmin;
    private PluginInstaller pluginInstaller;
    private File pluginFile;
    private Bundle bundle;

    @Before
    public void setUp()
        throws Exception
    {
        this.bundleContext = Mockito.mock(BundleContext.class);
        this.packageAdmin = Mockito.mock(PackageAdmin.class);
        this.pluginInstaller = new PluginInstaller(this.bundleContext, this.packageAdmin);

        this.pluginFile = this.folder.newFile("plugin.jar");

        this.bundle = Mockito.mock(Bundle.class);
        Mockito.when(this.bundle.getSymbolicName()).thenReturn("some.plugin");
        Mockito.when(this.bundle.getLocation()).thenReturn(this.pluginFile.toURI().toURL().toExternalForm());
    }

    @Test
    public void testInstall()
        throws Exception
    {
        Mockito.when(this.bundleContext.getBundles()).thenReturn(new Bundle[0]);
        Mockito.when(this.bundleContext.installBundle(this.pluginFile.toURI().toURL().toExternalForm()))
                .thenReturn(this.bundle);

        this.pluginInstaller.onFileCreate(this.pluginFile);

        Mockito.verify(this.bundle, Mockito.times(1)).start(0);
        Mockito.verify(this.packageAdmin, Mockito.times(1)).refreshPackages(null);
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        Mockito.when(this.bundleContext.getBundles()).thenReturn(new Bundle[] { this.bundle });

        this.pluginInstaller.onFileChange(this.pluginFile);

        Mockito.verify(this.bundle, Mockito.times(1)).update();
        Mockito.verify(this.bundle, Mockito.times(1)).start(0);
        Mockito.verify(this.packageAdmin, Mockito.times(1)).refreshPackages(null);
    }

    @Test
    public void testUninstall()
        throws Exception
    {
        Mockito.when(this.bundleContext.getBundles()).thenReturn(new Bundle[] { this.bundle });

        this.pluginInstaller.onFileDelete(this.pluginFile);

        Mockito.verify(this.bundle, Mockito.times(1)).uninstall();
        Mockito.verify(this.packageAdmin, Mockito.times(1)).refreshPackages(null);
    }
}
