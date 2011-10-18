package com.enonic.cms.core.config;

import com.enonic.cms.core.boot.BootEnvironment;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import java.io.File;

public final class GlobalConfigFactory
    implements EnvironmentAware
{
    private ConfigurableEnvironment env;

    public void setEnvironment(final Environment env)
    {
        this.env = (ConfigurableEnvironment)env;
    }

    public GlobalConfig create()
    {
        final File homeDir = BootEnvironment.getHomeDir(this.env);
        final ConfigProperties props = new ConfigProperties(this.env.getConversionService());
        props.putAll(new ConfigLoader(homeDir, this.env).load());
        return new GlobalConfigImpl(props);
    }
}
