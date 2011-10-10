package com.enonic.cms.core.plugin.spring;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

public class BundleClassLoaderTest
{
    private Bundle bundle;
    private BundleClassLoader loader;

    @Before
    public void setUp()
    {
        this.bundle = Mockito.mock(Bundle.class);
        Mockito.when(this.bundle.getSymbolicName()).thenReturn("some.id");
        Mockito.when(this.bundle.getVersion()).thenReturn(new Version("1.1.1"));

        this.loader = new BundleClassLoader(this.bundle);
    }

    @Test
    public void testLoadClass()
        throws Exception
    {
        Mockito.when(this.bundle.loadClass("ClassInBundle")).thenReturn(String.class);
        final Class clz = this.loader.loadClass("ClassInBundle");
        assertNotNull(clz);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadClassNotFound()
        throws Exception
    {
        Mockito.when(this.bundle.loadClass("ClassInBundle")).thenThrow(new ClassNotFoundException());
        this.loader.loadClass("ClassInBundle");
    }

    @Test
    public void testGetResource()
        throws Exception
    {
        final URL url = new URL("file://some/path");
        Mockito.when(this.bundle.getResource("/to/some/resource")).thenReturn(url);

        final URL result = this.loader.getResource("/to/some/resource");
        assertNotNull(result);
        assertEquals(url, result);
    }

    @Test
    public void testGetResources()
        throws Exception
    {
        final URL url = new URL("file://some/path");
        final Enumeration<URL> list = Collections.enumeration(Arrays.asList(url));

        Mockito.when(this.bundle.getResources("/a/package")).thenReturn(list);

        final Enumeration<URL> result = this.loader.getResources("/a/package");
        assertNotNull(result);
        assertTrue(result.hasMoreElements());
        assertEquals(url, result.nextElement());
        assertFalse(result.hasMoreElements());
    }

    @Test
    public void testEquals()
    {
        final ClassLoader other1 = Mockito.mock(ClassLoader.class);
        final ClassLoader other2 = new BundleClassLoader(Mockito.mock(Bundle.class));

        assertFalse(this.loader.equals(null));
        assertFalse(this.loader.equals(other1));
        assertFalse(this.loader.equals(other2));

        assertTrue(this.loader.equals(this.loader));
        assertTrue(this.loader.equals(new BundleClassLoader(this.bundle)));
    }

    @Test
    public void testToString()
    {
        assertEquals("BundleClassLoader[some.id:1.1.1]", this.loader.toString());
    }

    @Test
    public void testHashCode()
    {
        final int hashCode = this.loader.hashCode();
        assertEquals(this.bundle.hashCode(), hashCode);
    }
}
