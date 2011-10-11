package com.enonic.cms.core.boot;

import com.enonic.cms.api.util.LogFacade;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import java.io.File;
import java.util.Properties;

final class ConfigLoader
{
    private final static LogFacade LOG = LogFacade.get(ConfigLoader.class);
    
    private final File homeDir;
    private ResourceLoader resourceLoader;
    
    public ConfigLoader(final File homeDir)
    {
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
        return props;
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
        final String basePath = getClass().getPackage().getName().replace('.', '/');
        return load("classpath:" + basePath + "/default.properties", true);
    }

    private Properties loadCmsProperties()
    {
        return load(new File(this.homeDir, "config/cms.properties").getAbsolutePath(), false);
    }
}
