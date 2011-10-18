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

    public File getBlobStoreDir();

    public int getAutoLoginTimeout();

    public String getAdminDefaultLanguage();

    public String getMainSmtpHost();

    public String getAdminEmail();

    public String getAdminPassword();

    public boolean getLivePortalTraceEnabled();

    public int getLivePortalTraceLongestSize();

    public int getLivePortalTraceHistorySize();

    public Map<String, String> getMap();

    public Properties getProperties();
}
