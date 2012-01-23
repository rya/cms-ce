package com.enonic.cms.core.plugin.context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class PluginConfigHelperTest
{
    private File propFile;

    @Before
    public void setUp()
        throws Exception
    {
        final Properties props = new Properties();
        props.put("key1", "value1");
        props.put("key2", "value2");

        this.propFile = File.createTempFile("sample", "properties");
        this.propFile.deleteOnExit();
        props.store(new FileWriter(this.propFile), "sample properties");
   }

    @After
    public void tearDown()
    {
        this.propFile.delete();
    }

    @Test
    public void testInterpolate()
    {
        final BundleContext context = Mockito.mock(BundleContext.class);
        Mockito.when(context.getProperty("external1")).thenReturn("x1");
        Mockito.when(context.getProperty("external2")).thenReturn("x2");

        final HashMap<String, String> source = new HashMap<String, String>();
        source.put("key1", "value1");
        source.put("key2", "value2 ${key1}");
        source.put("key3", "${external1}");
        source.put("key4", "${key1} ${external2}");
        source.put("key5", "${illegal}");
        source.put("key6", "${illegal");

        final Map<String, String> target = PluginConfigHelper.interpolate(context, source);
        assertNotNull(target);
        assertEquals(6, target.size());
        assertEquals("value1", target.get("key1"));
        assertEquals("value2 value1", target.get("key2"));
        assertEquals("x1", target.get("key3"));
        assertEquals("value1 x2", target.get("key4"));
        assertEquals("${illegal}", target.get("key5"));
        assertEquals("${illegal", target.get("key6"));
    }

    @Test
    public void testLoadFromBundle()
        throws Exception
    {
        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getEntry("META-INF/cms/default.properties")).thenReturn(this.propFile.toURI().toURL());

        final Map<String, String> map = PluginConfigHelper.loadDefaultProperties(bundle);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    public void testLoadFromBundleNotFound()
        throws Exception
    {
        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getEntry("META-INF/cms/default.properties")).thenReturn(null);

        final Map<String, String> map = PluginConfigHelper.loadDefaultProperties(bundle);
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testLoadFromFile()
    {
        final Map<String, String> map = PluginConfigHelper.loadProperties(this.propFile);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    public void testLoadFromFileNotFound()
    {
        final Map<String, String> map = PluginConfigHelper.loadProperties(new File(UUID.randomUUID().toString()));
        assertNotNull(map);
        assertEquals(0, map.size());
    }
}
