package com.enonic.cms.core.config;

import com.enonic.cms.core.boot.BootEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import java.io.File;

@Configuration
public class ConfigBeans
{
    @Bean
    public GlobalConfig config(final ConfigurableEnvironment env)
    {
        final File homeDir = BootEnvironment.getHomeDir(env);
        final ConfigProperties props = new ConfigProperties(env.getConversionService());
        props.putAll(new ConfigLoader(homeDir, env).load());
        return new GlobalConfigImpl(props);
    }
}
