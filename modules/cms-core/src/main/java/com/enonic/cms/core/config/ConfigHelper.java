package com.enonic.cms.core.config;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ConfigHelper
{
    private final static String CMS_PREFIX = "cms.";

    // Should be renamed to cms.*
    private final static String COM_ENONIC_PREFIX = "com.enonic.";
    
    public static Map<String, String> toMap(final ConfigurableEnvironment env)
    {
        final Set<String> unfilteredKeys = findKeys(env.getPropertySources());
        final Set<String> filteredKeys = filterKeys(unfilteredKeys);

        final Map<String, String> map = Maps.newHashMap();
        for (final String key : filteredKeys) {
            map.put(key, env.getProperty(key));
        }

        return map;
    }

    private static Set<String> filterKeys(final Set<String> set)
    {
        return Sets.filter(set, new Predicate<String>() {
            public boolean apply(final String input)
            {
                return input.startsWith(CMS_PREFIX) || input.startsWith(COM_ENONIC_PREFIX);
            }
        });
    }

    private static Set<String> findKeys(final MutablePropertySources sources)
    {
        final HashSet<String> keys = Sets.newHashSet();
        for (final PropertySource source : sources) {
            if (source instanceof EnumerablePropertySource) {
                final String[] names = ((EnumerablePropertySource)source).getPropertyNames();
                keys.addAll(Arrays.asList(names));
            }
        }

        return keys;
    }
}
