package com.enonic.cms.core.plugin.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.plugin.PluginEnvironment;

final class BeansProcessor
    implements BeanFactoryPostProcessor
{
    private final PluginContext context;

    public BeansProcessor( final PluginContext context )
    {
        this.context = context;
    }

    public void postProcessBeanFactory( final ConfigurableListableBeanFactory factory )
        throws BeansException
    {
        factory.registerSingleton( "plugin.context", this.context );
        factory.registerSingleton( "plugin.config", this.context.getConfig() );
        factory.registerSingleton( "plugin.service.client", this.context.getService( Client.class ) );
        factory.registerSingleton( "plugin.service.pluginEnvironment", this.context.getService( PluginEnvironment.class ) );
    }
}
