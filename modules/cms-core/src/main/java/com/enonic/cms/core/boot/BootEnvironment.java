package com.enonic.cms.core.boot;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.util.Properties;

final class BootEnvironment
{
    public static void start(final ConfigurableEnvironment env)
    {
        final MutablePropertySources sources = env.getPropertySources();
        
        final File homeDir = new HomeResolver2(env).resolve();
        sources.addFirst(new HomePropertySource(homeDir));

        final ConfigLoader loader = new ConfigLoader(env, homeDir);
        final Properties configProperties = loader.load();
        sources.addLast(new PropertiesPropertySource("config", configProperties));
    }
}
