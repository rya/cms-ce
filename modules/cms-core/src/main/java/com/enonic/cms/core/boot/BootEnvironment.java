package com.enonic.cms.core.boot;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import java.io.File;

final class BootEnvironment
{
    public static void config(final ConfigurableEnvironment env)
    {
        final MutablePropertySources sources = env.getPropertySources();
        final File homeDir = new HomeResolver(env).resolve();
        sources.addFirst(new HomePropertySource(homeDir));
    }
}
