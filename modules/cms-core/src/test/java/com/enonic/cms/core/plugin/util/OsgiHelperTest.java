package com.enonic.cms.core.plugin.util;

import com.enonic.cms.api.plugin.PluginException;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import java.util.Hashtable;
import java.util.Set;
import static org.junit.Assert.*;

public class OsgiHelperTest
{
    @Test
    public void testIsFrameworkBundle()
    {
        final Bundle bundle = Mockito.mock(Bundle.class);

        Mockito.when(bundle.getBundleId()).thenReturn(1L);
        assertFalse(OsgiHelper.isFrameworkBundle(bundle));

        Mockito.when(bundle.getBundleId()).thenReturn(0L);
        assertTrue(OsgiHelper.isFrameworkBundle(bundle));
    }

    @Test
    public void testGetBundleName()
    {
        final Hashtable<String, Object> headers = new Hashtable<String, Object>();

        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getSymbolicName()).thenReturn("SymbolicName");
        Mockito.when(bundle.getHeaders()).thenReturn(headers);

        assertEquals("SymbolicName", OsgiHelper.getBundleName(bundle));

        headers.put("Bundle-Name", "BundleName");
        assertEquals("BundleName", OsgiHelper.getBundleName(bundle));
    }

    @Test
    public void testOptionalService()
    {
        final String service = "Service";
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        final BundleContext context = Mockito.mock(BundleContext.class);

        assertNull(OsgiHelper.optionalService(context, String.class));

        Mockito.when(context.getServiceReference("java.lang.String")).thenReturn(ref);
        Mockito.when(context.getService(ref)).thenReturn(service);

        assertSame(service, OsgiHelper.optionalService(context, String.class));
    }

    @Test
    public void testRequiredService()
    {
        final String service = "Service";
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        final BundleContext context = Mockito.mock(BundleContext.class);

        Mockito.when(context.getServiceReference("java.lang.String")).thenReturn(ref);
        Mockito.when(context.getService(ref)).thenReturn(service);

        assertSame(service, OsgiHelper.requireService(context, String.class));
    }

    @Test(expected = PluginException.class)
    public void testRequiredServiceNotFound()
    {
        final BundleContext context = Mockito.mock(BundleContext.class);
        OsgiHelper.requireService(context, String.class);
    }
}
