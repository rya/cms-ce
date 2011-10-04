package com.enonic.cms.core.plugin.util;

import com.enonic.cms.api.plugin.PluginException;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

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
    public void testIsFragment()
    {
        final Hashtable<String, Object> headers = new Hashtable<String, Object>();

        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getHeaders()).thenReturn(headers);

        assertFalse(OsgiHelper.isFragment(bundle));

        headers.put("Fragment-Host", "com.enonic.some.bundle");
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

    @Test
    public void testGetExportPackages()
    {
        final Hashtable<String, Object> headers = new Hashtable<String, Object>();

        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getHeaders()).thenReturn(headers);

        final Set<String> set1 = OsgiHelper.getExportPackages(bundle);
        assertNotNull(set1);
        assertEquals(0, set1.size());

        headers.put("Export-Package", "com.company;version=1.0,org.organization");

        final Set<String> set2 = OsgiHelper.getExportPackages(bundle);
        assertNotNull(set2);
        assertEquals(2, set2.size());
        assertTrue(set2.contains("com.company (1.0)"));
        assertTrue(set2.contains("org.organization"));
    }

    @Test
    public void testGetImportPackages()
    {
        final Hashtable<String, Object> headers = new Hashtable<String, Object>();

        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getHeaders()).thenReturn(headers);

        final Set<String> set1 = OsgiHelper.getImportPackages(bundle);
        assertNotNull(set1);
        assertEquals(0, set1.size());

        headers.put("Import-Package", "com.company;version=1.0,org.organization");

        final Set<String> set2 = OsgiHelper.getImportPackages(bundle);
        assertNotNull(set2);
        assertEquals(2, set2.size());
        assertTrue(set2.contains("com.company (1.0)"));
        assertTrue(set2.contains("org.organization"));
    }

    @Test
    public void testIsOsgiBundle()
        throws Exception
    {
        assertFalse(OsgiHelper.isOsgiBundle(createTextFile("not-a-jar-file")));

        final Manifest mf = new Manifest();
        mf.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        assertFalse(OsgiHelper.isOsgiBundle(createJarFile("jar-file-not-bundle", mf)));

        mf.getMainAttributes().putValue("Bundle-SymbolicName", "com.company.plugin");
        assertTrue(OsgiHelper.isOsgiBundle(createJarFile("jar-file-bundle", mf)));
    }

    private File createJarFile(final String name, final Manifest mf)
        throws Exception
    {
        final File file = File.createTempFile(name, "jar");
        file.deleteOnExit();

        final JarOutputStream out = new JarOutputStream(new FileOutputStream(file), mf);
        out.flush();
        out.close();

        return file;
    }

    private File createTextFile(final String name)
        throws Exception
    {
        return File.createTempFile(name, "txt");
    }
}
