package com.enonic.cms.core.plugin.deploy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class HotDeployerListenerTest
{
    private PluginDeployer deployer;
    private HotDeployerListener listener;

    @Before
    public void setUp()
    {
        this.deployer = Mockito.mock(PluginDeployer.class);
        this.listener = new HotDeployerListener(this.deployer);
    }

    @Test
    public void testCreate()
    {
        final File file = new File("plugin.jar");

        this.listener.onFileCreate(file);
        Mockito.verify(this.deployer, Mockito.times(1)).install(file);
    }

    @Test
    public void testChange()
    {
        final File file = new File("plugin.jar");

        this.listener.onFileChange(file);
        Mockito.verify(this.deployer, Mockito.times(1)).uninstall(file);
        Mockito.verify(this.deployer, Mockito.times(1)).install(file);
    }

    @Test
    public void testDelete()
    {
        final File file = new File("plugin.jar");

        this.listener.onFileDelete(file);
        Mockito.verify(this.deployer, Mockito.times(1)).uninstall(file);
    }
}
