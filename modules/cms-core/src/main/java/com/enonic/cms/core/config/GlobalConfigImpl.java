package com.enonic.cms.core.config;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.core.convert.ConversionService;
import java.io.File;
import java.util.*;

final class GlobalConfigImpl
    implements GlobalConfig
{
    private final Properties props;
    private final ConversionService converter;

    public GlobalConfigImpl(final Properties props, final ConversionService converter)
    {
        this.props = props;
        this.converter = converter;
    }

    public File getHomeDir()
    {
        return getValue("cms.home", File.class);
    }

    public String getJdbcDialect()
    {
        return getValue("cms.jdbc.dialect", String.class);
    }

    public Map<String, String> toMap()
    {
        return Maps.fromProperties(this.props);
    }

    private <T> T getValue(final String key, final Class<T> type)
    {
        final String value = this.props.getProperty(key);
        if (Strings.isNullOrEmpty(value)) {
            throw new IllegalArgumentException("No value for configuration property [" + key + "]");
        }

        return this.converter.convert(value, type);
    }

    public Properties toProperties()
    {
        final Properties target = new Properties();
        target.putAll(this.props);
        return target;
    }
}
