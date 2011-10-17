package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public interface GlobalConfig
{
    public File getHomeDir();

    public File getConfigDir();

    public String getJdbcDialect();

    public File getPluginConfigDir();

    public File getPluginDeployDir();

    public long getPluginScanPeriod();

    public File getCountriesFile();

    public File getVirtualHostConfigFile();

    public boolean getJdbcLogging();

    public boolean getJdbcConnectionTrace();

    public int getTxDefaultTimeout();

    public Map<String, String> toMap();

    public Properties toProperties();
}
