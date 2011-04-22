package com.enonic.cms.core.config;

import com.enonic.cms.core.home.HomeDir;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import java.util.Map;
import java.util.Properties;

@Configuration
public class ConfigBeans
{
    @Autowired
    private HomeDir homeDir;

    @Autowired
    private ConversionService converter;

    @Bean(name = "config")
    public GlobalConfig config()
    {
        final GlobalConfigLoader loader = new GlobalConfigLoader(this.homeDir);
        final Properties properties = loader.loadProperties();
        final Map<String, String> map = Maps.fromProperties(properties);
        return new GlobalConfigImpl(this.converter, map);
    }
}
