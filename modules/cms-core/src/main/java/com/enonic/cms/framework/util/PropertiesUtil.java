/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;

/**
 * This class implements properties utility methods.
 */
public final class PropertiesUtil
{
    /**
     * Return a subset of properties.
     */
    public static Properties getSubSet( final Properties props, final String base )
    {
        Properties sub = new Properties();
        for ( Map.Entry entry : props.entrySet() )
        {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if ( key.startsWith( base ) )
            {
                sub.setProperty( key.substring( base.length() ), value );
            }
        }

        return sub;
    }

    /**
     * Interpolate properties names like ${..}.
     */
    public static Properties interpolate( final Properties props )
    {
        Properties target = new Properties();

        Properties source = new Properties();
        source.putAll( System.getProperties() );
        source.putAll( props );

        StrLookup lookup = StrLookup.mapLookup( source );
        StrSubstitutor substitutor = new StrSubstitutor( lookup );

        for ( Object key : props.keySet() )
        {
            String value = props.getProperty( (String) key );

            try
            {
                value = substitutor.replace( value );
            }
            catch ( IllegalStateException e )
            {
                // Do nothing
            }

            target.put( key, value );
        }

        return target;
    }
}
