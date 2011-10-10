package com.enonic.cms.core.plugin.config;

import com.enonic.cms.api.plugin.PluginConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

public class PluginConfigFactoryTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File defaultFile;
    private PluginConfigFactory factory;

    @Before
    public void setUp()
        throws Exception
    {
        final Properties bundleProps = new Properties();
        bundleProps.put("key1", "value1");
        bundleProps.put("key2", "value2 ${default1}");
        bundleProps.put("key3", "${external1}");

        final File propFile = this.folder.newFile("my.sample.plugin.properties");
        bundleProps.store(new FileWriter(propFile), "bundle properties");

        final Properties defaultProps = new Properties();
        defaultProps.put("default1", "default-value1");
        defaultProps.put("default2", "default-value2");

        this.defaultFile = this.folder.newFile("default.properties");
        defaultProps.store(new FileWriter(this.defaultFile), "default properties");

        this.factory = new PluginConfigFactory(this.folder.getRoot());
    }

    @Test
    public void testGetService()
        throws Exception
    {
        final BundleContext context = Mockito.mock(BundleContext.class);
        Mockito.when(context.getProperty("external1")).thenReturn("external-value1");

        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getSymbolicName()).thenReturn("my.sample.plugin");
        Mockito.when(bundle.getBundleContext()).thenReturn(context);
        Mockito.when(bundle.getEntry("META-INF/cms/default.properties")).thenReturn(this.defaultFile.toURI().toURL());

        final Object service = this.factory.getService(bundle, null);

        assertNotNull(service);
        assertTrue(service instanceof PluginConfig);

        final PluginConfig config = (PluginConfig)service;
        assertEquals(5, config.size());
        assertEquals("value1", config.getString("key1"));
        assertEquals("value2 default-value1", config.getString("key2"));
        assertEquals("external-value1", config.getString("key3"));
        assertEquals("default-value1", config.getString("default1"));
        assertEquals("default-value2", config.getString("default2"));
    }

    @Test
    public void testUnGetService()
    {
        this.factory.ungetService(null, null, null);
    }
}
