package com.enonic.cms.core.plugin.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import com.enonic.cms.api.plugin.PluginException;

public final class OsgiHelper
{
    public static boolean isFrameworkBundle( final Bundle bundle )
    {
        return bundle.getBundleId() == 0;
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
}
