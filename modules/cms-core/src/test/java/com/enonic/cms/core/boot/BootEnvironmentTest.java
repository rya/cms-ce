package com.enonic.cms.core.boot;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.Properties;

public class BootEnvironmentTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testConfig()
    {
        final Properties props = new Properties();
        props.setProperty("cms.home", this.folder.newFolder("cms-home").getAbsolutePath());

        final StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(new PropertiesPropertySource("mock", props));

        BootEnvironment.config(env);

        final PropertySource source = env.getPropertySources().iterator().next();
        assertNotNull(source);
        assertTrue(source instanceof HomePropertySource);
    }
}
