package com.enonic.cms.core.plugin.host;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.plugin.PluginEnvironment;
import com.google.common.collect.Lists;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import java.util.List;

public class HostServicesTest
{
    @Test
    public void testLocalClient()
    {
        final LocalClient service = Mockito.mock(LocalClient.class);

        final HostServices services = new HostServices();
        services.register(service);

        final List<HostService> list = Lists.newArrayList(services);
        assertEquals(1, list.size());

        final BundleContext context = Mockito.mock(BundleContext.class);
        list.get(0).register(context);

        Mockito.verify(context, Mockito.times(1)).registerService(
                new String[] { LocalClient.class.getName(), Client.class.getName() },
                service, null);
    }

    @Test
    public void testPluginEnvironment()
    {
        final PluginEnvironment service = Mockito.mock(PluginEnvironment.class);

        final HostServices services = new HostServices();
        services.register(service);

        final List<HostService> list = Lists.newArrayList(services);
        assertEquals(1, list.size());

        final BundleContext context = Mockito.mock(BundleContext.class);
        list.get(0).register(context);

        Mockito.verify(context, Mockito.times(1)).registerService(
                new String[] { PluginEnvironment.class.getName() },
                service, null);
    }
}
