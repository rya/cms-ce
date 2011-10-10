package com.enonic.cms.core.plugin.spring;

import com.enonic.cms.api.plugin.PluginConfig;
import com.enonic.cms.api.plugin.PluginContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConfigProcessorTest
{
    private PluginContext context;

    @Before
    public void setUp()
    {
        final Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("nested.key", "value3");

        final PluginConfig config = Mockito.mock(PluginConfig.class);
        Mockito.when(config.entrySet()).thenReturn(map.entrySet());

        this.context = Mockito.mock(PluginContext.class);
        Mockito.when(this.context.getConfig()).thenReturn(config);
        Mockito.when(this.context.getId()).thenReturn("my.id");
        Mockito.when(this.context.getName()).thenReturn("My Plugin");
        Mockito.when(this.context.getVersion()).thenReturn("1.1.1");
    }

    @Test
    public void testMetaData()
    {
        final Properties props = configure();
        assertEquals("my.id", props.getProperty("plugin.id"));
        assertEquals("My Plugin", props.getProperty("plugin.name"));
        assertEquals("1.1.1", props.getProperty("plugin.version"));
    }

    @Test
    public void testConfig()
    {
        final Properties props = configure();
        assertNull(props.getProperty("plugin.config.key3"));
        assertEquals("value1", props.getProperty("plugin.config.key1"));
        assertEquals("value2", props.getProperty("plugin.config.key2"));
        assertEquals("value3", props.getProperty("plugin.config.nested.key"));
    }

    private Properties configure()
    {
        return new ConfigProcessor(this.context).getProperties();
    }
}
