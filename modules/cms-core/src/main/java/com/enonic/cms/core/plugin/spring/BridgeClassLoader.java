package com.enonic.cms.core.plugin.spring;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

final class BridgeClassLoader
    extends ClassLoader
{
    private final Bundle bundle;

    public BridgeClassLoader( final ClassLoader parent, final Bundle bundle )
    {
        super( parent );
        this.bundle = bundle;
    }

    @Override
    protected Class<?> findClass( final String name )
        throws ClassNotFoundException
    {
        return this.bundle.loadClass( name );
    }

    @Override
    protected URL findResource( final String name )
    {
        return this.bundle.getResource( name );
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Enumeration<URL> findResources( final String name )
        throws IOException
    {
        return (Enumeration<URL>) this.bundle.getResources( name );
    }

    @Override
    public URL getResource( String name )
    {
        final URL url = findResource( name );
        if ( url != null )
        {
            return url;
        }

        return super.getResource( name );
    }

    @Override
    protected Class<?> loadClass( final String name, final boolean resolve )
        throws ClassNotFoundException
    {
        try
        {
            final Class<?> clz = findClass( name );

            if ( resolve )
            {
                resolveClass( clz );
            }

            return clz;
        }
        catch ( final ClassNotFoundException e )
        {
            return super.loadClass( name, resolve );
        }
    }
}
