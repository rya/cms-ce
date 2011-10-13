package com.enonic.cms.core.boot;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.util.Properties;

public class BootEnvironmentTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testConfigureSetHome()
    {
        final File homeDir = this.folder.newFolder("cms-home");

        final StandardEnvironment env = new StandardEnvironment();
        BootEnvironment.configure(env, homeDir);

        final PropertySource source = env.getPropertySources().iterator().next();
        assertNotNull(source);
        assertTrue(source instanceof HomePropertySource);
    }

    @Test
    public void testConfigureDetectHome()
    {
        final Properties props = new Properties();
        props.setProperty("cms.home", this.folder.newFolder("cms-home").getAbsolutePath());

        final StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(new PropertiesPropertySource("mock", props));

        BootEnvironment.configure(env);

        final PropertySource source = env.getPropertySources().iterator().next();
        assertNotNull(source);
        assertTrue(source instanceof HomePropertySource);
    }
}
