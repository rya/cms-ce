package com.enonic.cms.core.boot;

import com.enonic.cms.api.util.LogFacade;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import static org.springframework.util.PropertyPlaceholderHelper.*;
import java.io.File;
import java.util.Properties;

final class ConfigLoader
{
    private final static LogFacade LOG = LogFacade.get(ConfigLoader.class);
    
    private final File homeDir;
    private final Environment env;
    private ResourceLoader resourceLoader;
    
    public ConfigLoader(final Environment env, final File homeDir)
    {
        this.env = env;
        this.homeDir = homeDir;
        setResourceLoader(new DefaultResourceLoader(getClass().getClassLoader()));
    }

    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public Properties load()
    {
        final Properties props = new Properties();
        props.putAll( loadDefaultProperties() );
        props.putAll( loadCmsProperties() );
        return interpolate(props);
    }

    private Properties interpolate(final Properties props)
    {
        final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}", ":", true);
        final PlaceholderResolver resolver = new PlaceholderResolver() {
            public String resolvePlaceholder(final String name)
            {
                final String value = props.getProperty(name);
                if (value != null) {
                    return value;
                }

                return env.getProperty(name);
            }
        };

        final Properties target = new Properties();
        for (final Object key : props.keySet()) {
            final String name = key.toString();
            final String value = props.getProperty(name);
            target.setProperty(name, helper.replacePlaceholders(value, resolver));
        }

        return target;
    }

    private Properties load(final String location, final boolean required)
    {
        return load(this.resourceLoader.getResource(location), required);
    }

    private Properties load( final Resource location, final boolean required )
    {
        if (!location.exists()) {
            if (required) {
                throw new IllegalArgumentException("Could not find required configuration " +
                        location.getDescription());
            } else {
                LOG.warning( "Could not find configuration {0}", location.getDescription() );
                return new Properties();
            }
        }
        
        try {
            return PropertiesLoaderUtils.loadProperties(location);
        } catch (final Exception e) {
            LOG.warning( e, "Could not load configuration {0}", location.getDescription() );
        }

        return new Properties();
    }
    
    private Properties loadDefaultProperties()
    {
        // final String basePath = getClass().getPackage().getName().replace('.', '/');
        final String basePath = "com/enonic/vertical";
        return load("classpath:" + basePath + "/default.properties", true);
    }

    private Properties loadCmsProperties()
    {
        return load(new File(this.homeDir, "config/cms.properties").getAbsolutePath(), false);
    }
}
