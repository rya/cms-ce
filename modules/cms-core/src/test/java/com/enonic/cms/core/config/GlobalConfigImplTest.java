package com.enonic.cms.core.config;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.Map;
import java.util.Properties;
import static org.junit.Assert.*;

public class GlobalConfigImplTest
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

        assertEquals(1, config.getXslMaxRecursionDepth());

        final Map<String, String> map = config.getMap();
        assertNotNull(map);
        assertEquals(2, map.size());
    }

    private GlobalConfigImpl build()
    {
        GenericConversionService converterService = new GenericConversionService();
        converterService.addConverter(new String2FileConverter());
        final Map<String, String> props = Maps.newHashMap();
        props.put("cms.home", "/some/folder");
        props.put("cms.xsl.maxRecursionDepth", "1");

        return new GlobalConfigImpl(converterService, props);
    }
}
