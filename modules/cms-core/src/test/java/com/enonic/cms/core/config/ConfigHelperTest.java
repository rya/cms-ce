package com.enonic.cms.core.config;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Map;
import java.util.Properties;

public class ConfigHelperTest
{
    @Test
    public void testToMap()
    {
        final Properties props = new Properties();
        props.put("some.property", "value");
        props.put("cms.key1", "value1");
        props.put("cms.key2", "value2");

        final StandardEnvironment env = new StandardEnvironment();
        final MutablePropertySources sources = env.getPropertySources();
        sources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME);
        sources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME);
        sources.addLast(new PropertiesPropertySource("config", props));

        final Map<String, String> result = ConfigHelper.toMap(env);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("cms.key1"));
        assertEquals("value2", result.get("cms.key2"));
    }
}
