package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;

final class GlobalConfigImpl
    implements GlobalConfig
{
    private File homeDir;
    private File configDir;
    private Map<String, String> map;

    public File getHomeDir()
    {
        return this.homeDir;
    }

    public void setHomeDir(final File homeDir)
    {
        this.homeDir = homeDir;
    }

    public File getConfigDir()
    {
        return this.configDir;
    }

    public void setConfigDir(final File configDir)
    {
        this.configDir = configDir;
    }

    public Map<String, String> getMap()
    {
        return this.map;
    }

    public void setMap(final Map<String, String> map)
    {
        this.map = map;
    }
}
