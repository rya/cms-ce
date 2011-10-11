package com.enonic.cms.core.config;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.framework.util.PropertiesUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class ConfigLoader
{
    private final static LogFacade LOG = LogFacade.get(ConfigLoader.class);

    private final static String CMS_PROPERTIES = "config/cms.properties";
    private final static String DEFAULT_PROPERTIES = "com/enonic/vertical/default.properties";

    private final File homeDir;
    private ClassLoader classLoader;
    private Properties systemProperties;

    public ConfigLoader(final File homeDir)
    {
        this.homeDir = homeDir;
        setClassLoader(getClass().getClassLoader());
        setSystemProperties(System.getProperties());
    }

    public void setClassLoader(final ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public void setSystemProperties( final Properties props )
    {
        this.systemProperties = props;
    }

    public Properties load()
    {
        final Properties props = new Properties();
        props.putAll( this.systemProperties );
        props.putAll( loadDefaultProperties() );
        props.putAll( loadCmsProperties() );
        props.putAll( getHomeDirProperties() );

        return PropertiesUtil.interpolate(props);
    }

    private Properties getHomeDirProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "cms.home", this.homeDir.getAbsolutePath() );
        props.setProperty( "cms.home.uri", this.homeDir.toURI().toString() );
        return props;
    }

    private Properties loadDefaultProperties()
    {
        final InputStream in = this.classLoader.getResourceAsStream( DEFAULT_PROPERTIES );
        if ( in == null )
        {
            throw new IllegalArgumentException( "Could not find default.properties [" +
                    DEFAULT_PROPERTIES + "] in classpath" );
        }

        try
        {
            return loadFromStream( in );
        }
        catch ( final Exception e )
        {
            throw new IllegalArgumentException( "Could not load default.properties [" +
                    DEFAULT_PROPERTIES + "] from classpath", e );
        }
    }

    private Properties loadCmsProperties()
    {
        final File file = new File(this.homeDir, CMS_PROPERTIES);
        if (!file.exists() || file.isDirectory()) {
            LOG.warning("Could not find cms.properties from [{0}]", file.getAbsolutePath());
            return new Properties();
        }

        try {
            return loadFromStream(new FileInputStream(file));
        } catch (final Exception e) {
            LOG.error(e, "Failed to load cms.properties from [{0}]", file.getAbsolutePath());
        }

        return new Properties();
    }

    private Properties loadFromStream( final InputStream in )
        throws IOException
    {
        final Properties props = new Properties();
        props.load( in );
        in.close();
        return props;
    }
}
