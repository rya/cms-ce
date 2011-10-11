package com.enonic.cms.core.config;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigBeansTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;
    private ConfigBeans beans;

    @Before
    public void setUp()
    {
        this.homeDir = this.folder.newFolder("cms-home");

        final Environment env = Mockito.mock(Environment.class);
        Mockito.when(env.getRequiredProperty("cms.home", File.class)).thenReturn(this.homeDir);

        this.beans = new ConfigBeans();
        this.beans.setEnvironment(env);
    }

    @Test
    public void testConfig()
    {
        final GlobalConfig config = this.beans.config();
        assertNotNull(config);
        assertEquals(this.homeDir, config.getHomeDir());
    }

    @Test
    public void testConfigProperties()
    {
        final Properties props = this.beans.configProperties();
        assertNotNull(props);
        assertEquals(this.homeDir.getAbsolutePath(), props.getProperty("cms.home"));
    }
}
