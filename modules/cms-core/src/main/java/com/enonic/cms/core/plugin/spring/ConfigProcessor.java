package com.enonic.cms.core.plugin.spring;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.enonic.cms.api.plugin.PluginContext;

final class ConfigProcessor
    extends PropertyPlaceholderConfigurer
{
    private final Properties props;

    public ConfigProcessor( final PluginContext context )
    {
        this.props = new Properties();
        this.props.setProperty( "plugin.id", context.getId() );
        this.props.setProperty( "plugin.name", context.getName() );
        this.props.setProperty( "plugin.version", context.getVersion() );

        for ( final Map.Entry<String, String> entry : context.getConfig().entrySet() )
        {
            this.props.setProperty( "plugin.config." + entry.getKey(), entry.getValue() );
        }

        setIgnoreUnresolvablePlaceholders( true );
        setProperties( props );
    }

    public Properties getProperties()
    {
        return this.props;
    }
}
