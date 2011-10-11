package com.enonic.cms.core.config;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import static org.junit.Assert.*;
import java.io.File;
import java.util.Map;
import java.util.Properties;

public class GlobalConfigImplTest
{
    private Properties props;
    private GlobalConfigImpl config;

    @Before
    public void setUp()
    {
        this.props = new Properties();
        final ConversionService converter = new DefaultConversionService();

        this.config = new GlobalConfigImpl(this.props, converter);
    }
    
    @Test
    public void testToMap()
    {
        this.props.put("cms.some.key", "value");

        final Map<String, String> result = this.config.toMap();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value", result.get("cms.some.key"));
    }

    @Test
    public void testToProperties()
    {
        this.props.put("cms.some.key", "value");

        final Properties result = this.config.toProperties();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value", result.get("cms.some.key"));
    }

    @Test
    public void testCmsHome()
    {
        this.props.put("cms.home", "/path/to/home");
        final File homeDir = this.config.getHomeDir();
        assertNotNull(homeDir);
        assertEquals("/path/to/home", homeDir.toString());
    }
}
