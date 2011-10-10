package com.enonic.cms.core.plugin.config;

import com.enonic.cms.api.plugin.PluginConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import java.util.Dictionary;

public class PluginConfigManagerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testRegister()
        throws Exception
    {
        final BundleContext context = Mockito.mock(BundleContext.class);

        final PluginConfigManager manager = new PluginConfigManager();
        manager.setConfigDir(this.folder.newFolder("config"));

        manager.start(context);
        Mockito.verify(context, Mockito.times(1)).registerService(
                Mockito.eq(PluginConfig.class.getName()),
                Mockito.any(PluginConfigFactory.class), Mockito.any(Dictionary.class));

        manager.stop(context);
    }
}
