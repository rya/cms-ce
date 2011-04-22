package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;

public interface GlobalConfig
{
    public File getHomeDir();

    public File getConfigDir();

    public int getXslMaxRecursionDepth();

    public Map<String, String> getMap();
}
