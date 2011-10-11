package com.enonic.cms.core.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Properties;

@Configuration
public class ConfigBeans
    implements EnvironmentAware
{
    private Environment env;
    private ConversionService converter = new DefaultConversionService();

    @Bean
    public GlobalConfig config()
    {
        return new GlobalConfigImpl(configProperties(), this.converter);
    }

    @Bean(name = "loadedVerticalProperties")
    public Properties configProperties()
    {
        final File homeDir = this.env.getRequiredProperty("cms.home", File.class);
        return new ConfigLoader(homeDir).load();
    }

    public void setEnvironment(final Environment env)
    {
        this.env = env;
    }

    /*
    @Autowired
    public void setConverter(final ConversionService converter)
    {
        this.converter = converter;
    }*/
}
