package com.enonic.cms.core.plugin.deploy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

public class HotDeployer2Test
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testStartup()
        throws Exception
    {
        final HotDeployer2 hotDeployer = new HotDeployer2();
        hotDeployer.setInterval(100L);
        hotDeployer.setDirectory(this.folder.getRoot());
        hotDeployer.setDeployer(Mockito.mock(PluginDeployer.class));

        hotDeployer.start();
        hotDeployer.stop();
    }
}
