package com.enonic.cms.core.boot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;
    private ConfigLoader configLoader;
    private ResourceLoader resourceLoader;
    private Resource prop1Resource;
    private Resource prop2Resource;
    private Environment env;

    @Before
    public void setUp()
    {
        this.resourceLoader = Mockito.mock(ResourceLoader.class);
        this.env = Mockito.mock(Environment.class);
        Mockito.when(this.env.getProperty("e1")).thenReturn("v1xx");

        this.homeDir = this.folder.newFolder("cms-home");
        this.configLoader = new ConfigLoader(this.env, this.homeDir);
        this.configLoader.setResourceLoader(this.resourceLoader);

        final Resource noResource = newResource("unknown", null);
        this.prop1Resource = newResource("prop1", "k1=v1\nk2=v2\nk8=${k1}\nk6=${e1}");
        this.prop2Resource = newResource("prop2", "k3=v3\nk2=v4\nk9=${k8}x\nk7=${k10}");

        Mockito.when(this.resourceLoader.getResource(Mockito.anyString())).thenReturn(noResource);
    }

    private Resource newResource(final String name, final String text)
    {
        if (text != null) {
            return new ByteArrayResource(text.getBytes(), name);
        } else {
            return new AbstractResource() {
                public String getDescription()
                {
                    return name;
                }

                public InputStream getInputStream()
                    throws IOException
                {
                    throw new IOException();
                }
            };
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoDefaultProperties()
        throws Exception
    {
        this.configLoader.load();
    }

    @Test
    public void testDefaultProperties()
        throws Exception
    {
        Mockito.when(this.resourceLoader.getResource(
                "classpath:com/enonic/cms/core/boot/default.properties")).thenReturn(this.prop1Resource);

        final Properties props = this.configLoader.load();
        assertNotNull(props);
        assertEquals(4, props.size());
        assertEquals("v1", props.getProperty("k1"));
        assertEquals("v2", props.getProperty("k2"));
        assertEquals("v1", props.getProperty("k8"));
        assertEquals("v1xx", props.getProperty("k6"));
    }

    @Test
    public void testCmsProperties()
        throws Exception
    {
        Mockito.when(this.resourceLoader.getResource(
                "classpath:com/enonic/cms/core/boot/default.properties")).thenReturn(this.prop1Resource);
        Mockito.when(this.resourceLoader.getResource(
                this.homeDir.getAbsolutePath() + "/config/cms.properties")).thenReturn(this.prop2Resource);

        final Properties props = this.configLoader.load();
        assertNotNull(props);
        assertEquals(7, props.size());
        assertEquals("v1", props.getProperty("k1"));
        assertEquals("v4", props.getProperty("k2"));
        assertEquals("v3", props.getProperty("k3"));
        assertEquals("v1", props.getProperty("k8"));
        assertEquals("v1xx", props.getProperty("k6"));
        assertEquals("v1x", props.getProperty("k9"));
        assertEquals("${k10}", props.getProperty("k7"));
    }
}
