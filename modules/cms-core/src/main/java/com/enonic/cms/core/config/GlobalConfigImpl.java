package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

final class GlobalConfigImpl
    implements GlobalConfig
{
    private final ConfigProperties props;

    public GlobalConfigImpl(final ConfigProperties props)
    {
        this.props = props;
    }

    public File getHomeDir()
    {
        return this.props.getValue("cms.home", File.class);
    }

    public File getConfigDir()
    {
        return new File(getHomeDir(), "config");
    }

    public String getJdbcDialect()
    {
        return this.props.getValue("cms.jdbc.dialect", String.class);
    }

    public File getPluginConfigDir()
    {
        return this.props.getValue("cms.plugin.configDir", File.class);
    }

    public File getPluginDeployDir()
    {
        return this.props.getValue("cms.plugin.deployDir", File.class);
    }

    public long getPluginScanPeriod()
    {
        return this.props.getValue("cms.plugin.scanPeriod", Long.class);
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
        return this.props.getValue("cms.jdbc.logging", Boolean.class);
    }

    public boolean getJdbcConnectionTrace()
    {
        return this.props.getValue("cms.jdbc.connectionTrace", Boolean.class);
    }

    public int getTxDefaultTimeout()
    {
        return this.props.getValue("cms.tx.defaultTimeout", Integer.class);
    }

    public File getBlobStoreDir()
    {
        return this.props.getValue("cms.blobstore.dir", File.class);
    }

    public int getAutoLoginTimeout()
    {
        return this.props.getValue("com.enonic.vertical.presentation.autologinTimeout", Integer.class);
    }

    public String getAdminDefaultLanguage()
    {
        return this.props.getValue("cms.admin.defaultLanguage", String.class);
    }

    public String getMainSmtpHost()
    {
        return this.props.getValue("cms.mail.smtpHost", String.class);
    }

    public String getAdminEmail()
    {
        return this.props.getValue("cms.admin.email", String.class);
    }

    public String getAdminPassword()
    {
        return this.props.getValue("cms.admin.password", String.class);
    }

    public boolean getLivePortalTraceEnabled()
    {
        return this.props.getValue("cms.livePortalTrace.enabled", Boolean.class);
    }

    public int getLivePortalTraceLongestSize()
    {
        return this.props.getValue("cms.livePortalTrace.longest.size", Integer.class);
    }

    public int getLivePortalTraceHistorySize()
    {
        return this.props.getValue("cms.livePortalTrace.history.size", Integer.class);
    }

    public Map<String, String> getMap()
    {
        return this.props.getMap();
    }

    public Properties getProperties()
    {
        return this.props.getProperties();
    }
}
