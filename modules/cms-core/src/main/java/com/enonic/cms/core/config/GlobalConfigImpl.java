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

    public File getConfigDir()
    {
        return new File(getHomeDir(), "config)");
    }

    public String getJdbcDialect()
    {
        return getValue("cms.jdbc.dialect", String.class);
    }

    public File getPluginConfigDir()
    {
        return getValue("cms.plugin.configDir", File.class);
    }

    public File getPluginDeployDir()
    {
        return getValue("cms.plugin.deployDir", File.class);
    }

    public long getPluginScanPeriod()
    {
        return getValue("cms.plugin.scanPeriod", Long.class);
    }

    public File getCountriesFile()
    {
        return new File(getConfigDir(), "countries.xml");
    }

    public File getVirtualHostConfigFile()
    {
        return new File(getConfigDir(), "vhost.properties");
    }

    public boolean getJdbcLogging()
    {
        return getValue("cms.jdbc.logging", Boolean.class);
    }

    public boolean getJdbcConnectionTrace()
    {
        return getValue("cms.jdbc.connectionTrace", Boolean.class);
    }

    public int getTxDefaultTimeout()
    {
        return getValue("cms.tx.defaultTimeout", Integer.class);
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
