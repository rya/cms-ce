package com.enonic.cms.core.plugin.context;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginException;
import com.enonic.cms.api.plugin.ext.Extension;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import java.util.Hashtable;

public class PluginContextImplTest
{
    private BundleContext bundleContext;
    private PluginContextImpl pluginContext;

    @Before
    public void setUp()
    {
        final Hashtable<String, String> headers = new Hashtable<String, String>();
        headers.put("Bundle-Name", "Some Name");

        this.bundleContext = Mockito.mock(BundleContext.class);

        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getSymbolicName()).thenReturn("some.id");
        Mockito.when(bundle.getHeaders()).thenReturn(headers);
        Mockito.when(bundle.getVersion()).thenReturn(new Version("1.1.1"));
        Mockito.when(bundle.getBundleContext()).thenReturn(this.bundleContext);

        this.pluginContext = new PluginContextImpl(bundle);
    }

    @Test
    public void testMetaData()
    {
        assertEquals("some.id", this.pluginContext.getId());
        assertEquals("Some Name", this.pluginContext.getName());
        assertEquals("1.1.1", this.pluginContext.getVersion());
    }

    @Test
    public void testRegister()
    {
        final Extension ext = new Extension() { };
        this.pluginContext.register(ext);

        Mockito.verify(this.bundleContext).registerService(Extension.class.getName(), ext, null);
    }

    @Test
    public void testGetService()
    {
        final Client service = Mockito.mock(Client.class);

        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        Mockito.when(this.bundleContext.getServiceReference(Client.class.getName())).thenReturn(ref);
        Mockito.when(this.bundleContext.getService(ref)).thenReturn(service);

        final Client returnedService = this.pluginContext.getService(Client.class);
        assertNotNull(returnedService);
        assertSame(service, returnedService);
    }

    @Test(expected = PluginException.class)
    public void testGetServiceNotFound()
    {
        this.pluginContext.getService(Client.class);
    }

    @Test
    public void testGetConfig()
    {
        final PluginConfig config = Mockito.mock(PluginConfig.class);

        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        Mockito.when(this.bundleContext.getServiceReference(PluginConfig.class.getName())).thenReturn(ref);
        Mockito.when(this.bundleContext.getService(ref)).thenReturn(config);

        final PluginConfig returnedConfig = this.pluginContext.getConfig();
        assertNotNull(returnedConfig);
        assertSame(config, returnedConfig);

        final PluginConfig cachedConfig = this.pluginContext.getConfig();
        assertNotNull(cachedConfig);
        assertSame(config, cachedConfig);

        Mockito.verify(this.bundleContext, Mockito.times(1)).getService(ref);
    }
}
