package com.enonic.cms.core.plugin.spring;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.plugin.PluginEnvironment;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class BeansProcessorTest
{
    private PluginContext context;
    private ConfigurableListableBeanFactory factory;
    
    @Before
    public void setUp()
    {
        this.context = Mockito.mock(PluginContext.class);
        this.factory = new DefaultListableBeanFactory();
    }

    private void processBeanFactory()
    {
        final BeansProcessor processor = new BeansProcessor(this.context);
        processor.postProcessBeanFactory(this.factory);
    }

    @Test
    public void testPluginContext()
    {
        processBeanFactory();
        checkBean("plugin.context", PluginContext.class, this.context);
    }

    @Test
    public void testPluginConfig()
    {
        final PluginConfig config = Mockito.mock(PluginConfig.class);
        Mockito.when(this.context.getConfig()).thenReturn(config);

        processBeanFactory();

        checkBean("plugin.config", PluginConfig.class, config);
    }

    @Test
    public void testClientService()
    {
        checkService("client", Client.class);
    }

    @Test
    public void testPluginEnvironmentService()
    {
        checkService("pluginEnvironment", PluginEnvironment.class);
    }

    private void checkService(final String name, final Class<?> type)
    {
        final Object service = Mockito.mock(type);
        Mockito.when(this.context.getService(type)).thenReturn(service);

        processBeanFactory();

        checkBean("plugin.service." + name, type, service);
    }

    private void checkBean(final String name, final Class<?> type, final Object value)
    {
        final Object bean1 = this.factory.getBean(name);
        assertNotNull(bean1);
        assertSame(value, bean1);

        final Object bean2 = this.factory.getBean(type);
        assertNotNull(bean2);
        assertSame(value, bean2);
    }
}
