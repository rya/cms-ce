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
    private ConfigProperties props;
    private GlobalConfigImpl config;

    @Before
    public void setUp()
    {
        final ConversionService converter = new DefaultConversionService();
        this.props = new ConfigProperties(converter);
        this.config = new GlobalConfigImpl(this.props);
    }
    
    @Test
    public void testMap()
    {
        this.props.put("cms.some.key", "value");

        final Map<String, String> result = this.config.getMap();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value", result.get("cms.some.key"));
    }

    @Test
    public void testProperties()
    {
        this.props.put("cms.some.key", "value");

        final Properties result = this.config.getProperties();
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

    @Test
    public void testConfigDir()
    {
        this.props.put("cms.home", "/path/to/home");
        final File configDir = this.config.getConfigDir();
        assertNotNull(configDir);
        assertEquals("/path/to/home/config", configDir.toString());
    }

    @Test
    public void testCountriesFile()
    {
        this.props.put("cms.home", "/path/to/home");
        final File countriesFile = this.config.getCountriesFile();
        assertNotNull(countriesFile);
        assertEquals("/path/to/home/config/countries.xml", countriesFile.toString());
    }

    @Test
    public void testJdbcDialect()
    {
        this.props.put("cms.jdbc.dialect", "auto");
        final String jdbcDialect = this.config.getJdbcDialect();
        assertNotNull(jdbcDialect);
        assertEquals("auto", jdbcDialect);
    }
}
