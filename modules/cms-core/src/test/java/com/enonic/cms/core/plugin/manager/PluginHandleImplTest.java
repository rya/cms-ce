package com.enonic.cms.core.plugin.manager;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.core.plugin.ExtensionSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.*;
import java.util.Hashtable;
import static org.junit.Assert.*;

public class PluginHandleImplTest
{
    private PluginHandleImpl handle;
    private Bundle bundle;
    private BundleContext context;

    @Before
    public void setUp()
    {
        this.context = Mockito.mock(BundleContext.class);

        this.bundle = Mockito.mock(Bundle.class);
        Mockito.when(this.bundle.getSymbolicName()).thenReturn("some.plugin");
        Mockito.when(this.bundle.getBundleContext()).thenReturn(this.context);

        this.handle = new PluginHandleImpl(this.bundle, new ExtensionHolder());
    }

    @Test
    public void testGetInfo()
    {
        Mockito.when(this.bundle.getBundleId()).thenReturn(1L);
        Mockito.when(this.bundle.getSymbolicName()).thenReturn("plugin.id");
        Mockito.when(this.bundle.getVersion()).thenReturn(new Version("1.1.1"));

        final Hashtable<String, String> headers = new Hashtable<String, String>();
        headers.put("Bundle-Name", "Plugin Name");
        Mockito.when(this.bundle.getHeaders()).thenReturn(headers);

        assertEquals(1, this.handle.getKey());
        assertEquals("plugin.id", this.handle.getId());
        assertEquals("Plugin Name", this.handle.getName());
        assertEquals("1.1.1", this.handle.getVersion());
    }

    @Test
    public void testGetTimestamp()
    {
        final long tm = System.currentTimeMillis();
        Mockito.when(this.bundle.getLastModified()).thenReturn(tm);

        assertEquals(tm, this.handle.getTimestamp().toDate().getTime());
    }

    @Test
    public void testIsActive()
    {
        assertFalse(this.handle.isActive());

        Mockito.when(this.bundle.getState()).thenReturn(Bundle.ACTIVE);
        assertTrue(this.handle.isActive());
    }

    @Test
    public void testGetConfig()
    {
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        final PluginConfig config = Mockito.mock(PluginConfig.class);

        Mockito.when(this.context.getServiceReference(PluginConfig.class.getName())).thenReturn(ref);
        Mockito.when(this.context.getService(ref)).thenReturn(config);

        final PluginConfig result = this.handle.getConfig();
        assertSame(config, result);
    }

    @Test
    public void testGetExtensions()
    {
        final ExtensionSet result = this.handle.getExtensions();
        assertNotNull(result);
    }

    @Test
    public void testUpdate()
        throws Exception
    {
        this.handle.update();
        Mockito.verify(this.bundle, Mockito.times(1)).update();

        Mockito.doThrow(new BundleException("dummy")).when(this.bundle).update();
        this.handle.update();
    }
}
