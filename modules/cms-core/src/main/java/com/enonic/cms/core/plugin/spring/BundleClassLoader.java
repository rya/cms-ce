package com.enonic.cms.core.plugin.spring;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import org.osgi.framework.Bundle;

final class BundleClassLoader
    extends ClassLoader
{
    private final Bundle bundle;

    public BundleClassLoader(final Bundle bundle)
    {
        this.bundle = bundle;
    }

    protected Class<?> findClass(final String name)
        throws ClassNotFoundException
    {
        return this.bundle.loadClass(name);
    }

    protected URL findResource(final String name)
    {
        return this.bundle.getResource(name);
    }

    @SuppressWarnings("unchecked")
    protected Enumeration<URL> findResources(final String name)
        throws IOException
    {
        return this.bundle.getResources(name);
    }

    public URL getResource(final String name)
    {
        return findResource(name);
    }

    public Class<?> loadClass(final String name)
        throws ClassNotFoundException
    {
        return findClass(name);
    }

    public boolean equals(final Object obj)
    {
        if (this == obj) {
            return true;
        }

        if (obj instanceof BundleClassLoader) {
            final BundleClassLoader cl = (BundleClassLoader)obj;
            return this.bundle.equals(cl.bundle);
        }

        return false;
    }

    public int hashCode()
    {
        return this.bundle.hashCode();
    }

    public String toString()
    {
        return "BundleClassLoader[" + this.bundle.getSymbolicName() +
                ":" + this.bundle.getVersion().toString() + "]";
    }
}
