package com.enonic.cms.core.plugin.context;

import com.enonic.cms.api.plugin.PluginContext;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import java.util.Dictionary;

public class PluginContextManagerTest
{
    @Test
    public void testRegister()
        throws Exception
    {
        final BundleContext context = Mockito.mock(BundleContext.class);

        final PluginContextManager manager = new PluginContextManager();

        manager.start(context);
        Mockito.verify(context, Mockito.times(1)).registerService(
                Mockito.eq(PluginContext.class.getName()),
                Mockito.any(PluginContextFactory.class), Mockito.any(Dictionary.class));

        manager.stop(context);
    }
}
