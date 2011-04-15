package com.enonic.cms.core.plugin.spring;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.enonic.cms.api.plugin.PluginContext;

final class ConfigProcessor
    extends PropertyPlaceholderConfigurer
{
    public ConfigProcessor( final PluginContext context )
    {
        final Properties props = new Properties();
        props.setProperty( "plugin.id", context.getId() );
        props.setProperty( "plugin.name", context.getName() );
        props.setProperty( "plugin.version", context.getVersion() );

        for ( final Map.Entry<String, String> entry : context.getConfig().entrySet() )
        {
            props.setProperty( "plugin.config." + entry.getKey(), entry.getValue() );
        }

        setIgnoreUnresolvablePlaceholders( true );
        setProperties( props );
    }
}
