package com.enonic.cms.core.config;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigPropertiesTest
{
    private ConfigProperties props;

    @Before
    public void setUp()
    {
        final ConversionService converter = new DefaultConversionService();
        this.props = new ConfigProperties(converter);
    }

    @Test
    public void testGetMap()
    {
        this.props.put("cms.some.key", "value");

        final Map<String, String> result = this.props.getMap();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value", result.get("cms.some.key"));
    }

    @Test
    public void testGetProperties()
    {
        this.props.put("cms.some.key", "value");

        final Properties result = this.props.getProperties();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value", result.get("cms.some.key"));
    }

    @Test
    public void testGetValue()
    {
        this.props.put("cms.some.key", "11");

        final int result = this.props.getValue("cms.some.key", Integer.class);
        assertNotNull(result);
        assertEquals(11, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValueNotFound()
    {
        this.props.getValue("cms.some.key", Integer.class);
    }
}
