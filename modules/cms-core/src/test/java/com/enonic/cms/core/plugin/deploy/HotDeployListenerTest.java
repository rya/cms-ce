package com.enonic.cms.core.plugin.deploy;

import com.enonic.cms.core.plugin.installer.BundleInstaller;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class HotDeployListenerTest
{
    private BundleInstaller installer;
    private HotDeployListener listener;
    private File pluginFile;
    private String pluginLocation;

    @Before
    public void setUp()
        throws Exception
    {
        this.installer = Mockito.mock(BundleInstaller.class);
        this.listener = new HotDeployListener(this.installer);
        this.pluginFile = new File("plugin.jar");
        this.pluginLocation = this.pluginFile.toURI().toURL().toExternalForm();
    }

    @Test
    public void testCreate()
    {
        this.listener.onFileCreate(this.pluginFile);
        Mockito.verify(this.installer, Mockito.times(1)).install(this.pluginLocation);
    }

    @Test
    public void testChange()
    {
        this.listener.onFileChange(this.pluginFile);
        Mockito.verify(this.installer, Mockito.times(1)).install(this.pluginLocation);
    }

    @Test
    public void testDelete()
    {
        this.listener.onFileDelete(this.pluginFile);
        Mockito.verify(this.installer, Mockito.times(1)).uninstall(this.pluginLocation);
    }
}
