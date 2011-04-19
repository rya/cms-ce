package com.enonic.cms.core.plugin.util;

import java.io.File;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.felix.utils.manifest.Clause;
import org.apache.felix.utils.manifest.Parser;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.Sets;

import com.enonic.cms.api.plugin.PluginException;

public final class OsgiHelper
{
    public static boolean isFrameworkBundle( final Bundle bundle )
    {
        return bundle.getBundleId() == 0;
    }

    public static boolean isFragment( final Bundle bundle )
    {
        return bundle.getHeaders().get( Constants.FRAGMENT_HOST ) != null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T optionalService( final BundleContext context, final Class<T> type )
    {
        final ServiceReference ref = context.getServiceReference( type.getName() );
        if ( ref == null )
        {
            return null;
        }

        return (T) context.getService( ref );
    }

    public static <T> T requireService( final BundleContext context, final Class<T> type )
    {
        final T service = optionalService( context, type );
        if ( service != null )
        {
            return service;
        }

        throw new PluginException( "Failed to find service of type [{0}]", type.getName() );
    }

    public static String getBundleName( final Bundle bundle )
    {
        final String name = (String) bundle.getHeaders().get( Constants.BUNDLE_NAME );
        return name != null ? name : bundle.getSymbolicName();
    }

    public static boolean isOsgiBundle( final File file )
    {
        final JarFile jar = openJarFile( file );
        if ( jar == null )
        {
            return false;
        }

        try
        {
            return isOsgiBundle( jar );
        }
        finally
        {
            closeJarFile( jar );
        }
    }

    private static boolean isOsgiBundle( final JarFile file )
    {
        try
        {
            final Manifest mf = file.getManifest();
            final String name = mf.getMainAttributes().getValue( Constants.BUNDLE_SYMBOLICNAME );
            return name != null;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    private static JarFile openJarFile( final File file )
    {
        try
        {
            return new JarFile( file );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private static void closeJarFile( final JarFile file )
    {
        try
        {
            file.close();
        }
        catch ( Exception e )
        {
            // Do nothing
        }
    }

    public static Set<String> getExportPackages( final Bundle bundle )
    {
        return getPackages( bundle, Constants.EXPORT_PACKAGE );
    }

    public static Set<String> getImportPackages( final Bundle bundle )
    {
        return getPackages( bundle, Constants.IMPORT_PACKAGE );
    }

    private static Set<String> getPackages( final Bundle bundle, final String header )
    {
        final Set<String> set = Sets.newTreeSet();
        for ( final Clause c : parseHeader( bundle, header ) )
        {
            final String pck = c.getName();
            final String version = c.getAttribute( "version" );

            if ( version == null )
            {
                set.add( pck );
            }
            else
            {
                set.add( pck + " (" + version + ")" );
            }
        }

        return set;
    }

    private static Clause[] parseHeader( final Bundle bundle, final String header )
    {
        final String value = (String) bundle.getHeaders().get( header );
        if ( value == null )
        {
            return new Clause[0];
        }

        return Parser.parseHeader( value );
    }
}
