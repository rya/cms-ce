package com.enonic.cms.core.config;

import org.springframework.core.env.ConfigurableEnvironment;
import java.io.File;
import java.util.*;

final class GlobalConfigImpl
    implements GlobalConfig
{
    private final ConfigurableEnvironment env;

    public GlobalConfigImpl(final ConfigurableEnvironment env)
    {
        this.env = env;
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
        return ConfigHelper.toMap(this.env);
    }

    private <T> T getValue(final String key, final Class<T> type)
    {
        return this.env.getRequiredProperty(key, type);
    }

    public Properties toProperties()
    {
        final Properties props = new Properties();
        props.putAll(toMap());
        return props;
    }
}
