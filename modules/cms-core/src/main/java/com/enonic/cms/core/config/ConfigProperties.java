package com.enonic.cms.core.config;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.core.convert.ConversionService;
import java.util.Map;
import java.util.Properties;

final class ConfigProperties
    extends Properties
{
    private final ConversionService converter;

    public ConfigProperties(final ConversionService converter)
    {
        this.converter = converter;
    }

    public Map<String, String> getMap()
    {
        return Maps.fromProperties(this);
    }

    public <T> T getValue(final String key, final Class<T> type)
    {
        final String value = getProperty(key);
        if (Strings.isNullOrEmpty(value)) {
            throw new IllegalArgumentException("No value for configuration property [" + key + "]");
        }

        return this.converter.convert(value, type);
    }

    public Properties getProperties()
    {
        final Properties target = new Properties();
        target.putAll(this);
        return target;
    }
}
