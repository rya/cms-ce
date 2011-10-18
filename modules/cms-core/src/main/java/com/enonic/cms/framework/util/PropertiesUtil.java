/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.Map;
import java.util.Properties;

import com.google.common.base.Strings;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.core.env.Environment;
import org.springframework.util.PropertyPlaceholderHelper;
import static org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

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

    public static Properties interpolate( final Properties props, final Environment env )
    {
        final PlaceholderResolver resolver = new PlaceholderResolver() {
            public String resolvePlaceholder(final String key)
            {
                String value = props.getProperty(key);
                if (!Strings.isNullOrEmpty(value)) {
                    return value;
                }

                return env.getProperty(key);
            }
        };

        final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":", true);
        final Properties result = new Properties();
        
        for (final Object o : props.keySet()) {
            final String key = (String)o;
            final String value = props.getProperty(key);
            final String resolved = helper.replacePlaceholders(value, resolver);
            result.put(key, resolved);
        }

        return result;
    }
}
