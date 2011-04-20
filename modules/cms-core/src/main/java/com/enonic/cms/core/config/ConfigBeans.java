package com.enonic.cms.core.config;

import com.enonic.cms.core.home.HomeDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

@Configuration
public class ConfigBeans
{
    @Autowired
    private HomeDir homeDir;

    @Bean(name = "config")
    public GlobalConfig config()
    {
        final GlobalConfigLoader loader = new GlobalConfigLoader(this.homeDir);
        final Properties properties = loader.loadProperties();

        final GlobalConfigBuilder builder = new GlobalConfigBuilder(properties);
        return builder.build();
    }
}
