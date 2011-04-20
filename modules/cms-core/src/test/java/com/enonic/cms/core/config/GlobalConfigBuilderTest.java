package com.enonic.cms.core.config;

import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Map;
import java.util.Properties;
import static org.junit.Assert.*;

public class GlobalConfigBuilderTest
{
    @Test
    public void testBuild()
    {
        final GlobalConfig config = build();
        assertNotNull(config);

        assertNotNull(config.getHomeDir());
        assertEquals("/some/folder", config.getHomeDir().toString());
        
        assertNotNull(config.getConfigDir());
        assertEquals("/some/folder/config", config.getConfigDir().toString());

        final Map<String, String> map = config.getMap();
        assertNotNull(map);
        assertEquals(1, map.size());
    }

    private GlobalConfig build()
    {
        final Properties props = new Properties();
        props.setProperty("cms.home", "/some/folder");

        final ConversionService converter = new DefaultConversionService();
        return new GlobalConfigBuilder(props, converter).build();
    }
}
