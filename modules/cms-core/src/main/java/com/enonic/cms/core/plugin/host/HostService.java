package com.enonic.cms.core.plugin.host;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;

import com.google.common.collect.Sets;

final class HostService
{
    private final static String[] ALLOW_PREFIXES = {"com.enonic.cms.api."};

    private final String[] types;

    private final Object instance;

    public HostService( final Object instance )
    {
        this.instance = instance;
        this.types = findTypes( instance.getClass() );
    }

    public void register( final BundleContext context )
    {
        context.registerService( this.types, instance, null );
    }

    private static String[] findTypes( final Class<?> type )
    {
        final HashSet<String> set = Sets.newHashSet();
        collectTypes( set, type );
        return set.toArray( new String[set.size()] );
    }

    private static void collectTypes( final Set<String> types, final Class<?> type )
    {
        if ( type == null )
        {
            return;
        }

        final String typeName = type.getName();
        if ( allowType( typeName ) )
        {
            types.add( typeName );
        }

        collectTypes( types, type.getSuperclass() );

        for ( Class<?> iface : type.getInterfaces() )
        {
            collectTypes( types, iface );
        }
    }

    private static boolean allowType( final String type )
    {
        for ( final String prefix : ALLOW_PREFIXES )
        {
            if ( type.startsWith( prefix ) )
            {
                return true;
            }
        }

        return false;
    }
}
