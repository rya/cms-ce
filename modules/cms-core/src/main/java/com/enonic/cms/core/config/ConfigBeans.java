package com.enonic.cms.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Properties;

@Configuration
public class ConfigBeans
{
    @Bean
    public GlobalConfig config(final ConfigurableEnvironment env)
    {
        return new GlobalConfigImpl(env);
    }

    @Bean
    public Properties loadedVerticalProperties(final GlobalConfig config)
    {
        return config.toProperties();
    }
}
