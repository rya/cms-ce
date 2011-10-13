package com.enonic.cms.core.boot;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import java.io.File;

public final class BootEnvironment
{
    public static void configure(final ConfigurableEnvironment env)
    {
        final File homeDir = new HomeResolver(env).resolve();
        configure(env, homeDir);
    }

    public static void configure(final ConfigurableEnvironment env, final File homeDir)
    {
        final MutablePropertySources sources = env.getPropertySources();
        sources.addFirst(new HomePropertySource(homeDir));
    }
}
