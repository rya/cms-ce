package com.enonic.cms.core.config;

import com.enonic.cms.core.home.HomeDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.*;
import java.util.Properties;

final class GlobalConfigLoader
{
    private final static Logger LOG = LoggerFactory.getLogger(GlobalConfigLoader.class);

    private final HomeDir homeDir;

    private Properties systemProperties;

    private ResourceLoader loader;

    public GlobalConfigLoader(final HomeDir homeDir)
    {
        this.homeDir = homeDir;
        setSystemProperties( System.getProperties() );
        setClassLoader( getClass().getClassLoader() );
    }

    public void setSystemProperties( final Properties props )
    {
        this.systemProperties = props;
    }

    public void setClassLoader( final ClassLoader loader )
    {
        this.loader = new DefaultResourceLoader(loader);
    }

    public Properties loadProperties()
    {
        final Properties props = new Properties();
        props.putAll( this.systemProperties );
        props.putAll( load("classpath:com/enonic/cms/core/config/default.properties") );
        props.putAll( load(new File(this.homeDir.getFile(), "config/cms.properties")) );
        props.putAll( this.homeDir.getMap() );
        return interpolate(props);
    }

    private Properties load( final Resource location )
    {
        try {
            return PropertiesLoaderUtils.loadProperties(location);
        } catch (final FileNotFoundException e) {
            LOG.warn( "Could not find ", location.getDescription() );
        } catch (final Exception e) {
            LOG.warn( "Could not load ", location.getDescription(), e );
        }

        return new Properties();
    }

    private Properties load( final String location )
    {
        return load(this.loader.getResource(location));
    }

    private Properties load( final File location )
    {
        return load(new FileSystemResource(location));
    }

    private static Properties interpolate(final Properties props)
    {
        final PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
        final Properties target = new Properties();

        for (final Object key : props.keySet()) {
            target.put(key, helper.replacePlaceholders((String)props.get(key), props));
        }

        return target;
    }
}
