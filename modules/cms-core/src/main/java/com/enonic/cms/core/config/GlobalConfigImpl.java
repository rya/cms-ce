package com.enonic.cms.core.config;

import com.enonic.cms.api.util.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.springframework.core.convert.ConversionService;

import java.io.File;
import java.util.Map;

final class GlobalConfigImpl
    implements GlobalConfig
{
    private final Map<String, String> map;
    private final ConversionService converter;

    public GlobalConfigImpl(final ConversionService converter, final Map<String, String> map)
    {
        this.converter = converter;
        this.map = map;
    }
    
    public File getHomeDir()
    {
        return get("cms.home", File.class);
    }

    public File getConfigDir()
    {
        return new File(getHomeDir(), "config");
    }

    public int getXslMaxRecursionDepth()
    {
        return get("cms.xsl.maxRecursionDepth", Integer.class);
    }

    public Map<String, String> getMap()
    {
        return ImmutableMap.copyOf(this.map);
    }

    private <T> T get(final String key, final Class<T> type)
    {
        final String value = this.map.get(key);
        Preconditions.checkNotNull(value, "Property [" + key + "] is not defined");
        return this.converter.convert(value, type);
    }
}
