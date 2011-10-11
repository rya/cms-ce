package com.enonic.cms.core.config;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public interface GlobalConfig
{
    public File getHomeDir();

    public Map<String, String> toMap();

    public Properties toProperties();
}
