package com.enonic.cms.itest.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

import com.enonic.cms.core.config.GlobalConfig;

public class MockGlobalConfig
    implements GlobalConfig
{
    private final File homeDir = Files.createTempDir();

    public File getHomeDir()
    {
        return this.homeDir;
    }

    public File getConfigDir()
    {
        return new File( getHomeDir(), "config" );
    }

    public String getJdbcDialect()
    {
        return "auto";
    }

    public File getPluginConfigDir()
    {
        return new File( getHomeDir(), "plugins" );
    }

    public File getPluginDeployDir()
    {
        return new File( getHomeDir(), "plugins" );
    }

    public long getPluginScanPeriod()
    {
        return 0;
    }

    public File getCountriesFile()
    {
        return new File( getHomeDir(), "config/countries.xml" );
    }

    public File getVirtualHostConfigFile()
    {
        return new File( getHomeDir(), "config/vhost.properties" );
    }

    public boolean getJdbcLogging()
    {
        return false;
    }

    public boolean getJdbcConnectionTrace()
    {
        return false;
    }

    public int getTxDefaultTimeout()
    {
        return 1000;
    }

    public File getBlobStoreDir()
    {
        return new File( getHomeDir(), "data/blobs" );
    }

    public int getAutoLoginTimeout()
    {
        return -1;
    }

    public String getAdminDefaultLanguage()
    {
        return "en";
    }

    public String getMainSmtpHost()
    {
        return "localhost";
    }

    public String getAdminEmail()
    {
        return "dummy@enonic.com";
    }

    public String getAdminPassword()
    {
        return "password";
    }

    public boolean getLivePortalTraceEnabled()
    {
        return false;
    }

    public int getLivePortalTraceLongestSize()
    {
        return 100;
    }

    public int getLivePortalTraceHistorySize()
    {
        return 100;
    }

    public Map<String, String> getMap()
    {
        return Maps.newHashMap();
    }

    public Properties getProperties()
    {
        return new Properties();
    }
}
