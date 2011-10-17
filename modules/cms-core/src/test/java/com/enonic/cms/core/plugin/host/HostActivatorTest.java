package com.enonic.cms.core.plugin.host;

import com.enonic.cms.api.client.LocalClient;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import java.util.Dictionary;

public class HostActivatorTest
{
    @Test
    public void testStart()
        throws Exception
    {
        final LocalClient client = Mockito.mock(LocalClient.class);
        final BundleContext context = Mockito.mock(BundleContext.class);

        final HostActivator activator = new HostActivator();
        activator.setClient(client);

        activator.start(context);

        Mockito.verify(context, Mockito.times(2)).registerService(
                (String[])Mockito.anyObject(), Mockito.anyObject(), (Dictionary)Mockito.anyObject());

        activator.stop(context);
    }
}
