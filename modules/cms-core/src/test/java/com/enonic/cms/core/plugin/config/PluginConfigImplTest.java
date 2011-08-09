package com.enonic.cms.core.plugin.config;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PluginConfigImplTest
{
    @Test
    public void testSize()
    {
        final PluginConfigImpl config1 = create();
        assertEquals(0, config1.size());

        final PluginConfigImpl config2 = create("key", "value");
        assertEquals(1, config2.size());
    }

    @Test
    public void testIsEmpty()
    {
        final PluginConfigImpl config1 = create();
        assertTrue(config1.isEmpty());

        final PluginConfigImpl config2 = create("key", "value");
        assertFalse(config2.isEmpty());
    }

    @Test
    public void testContainsKey()
    {
        final PluginConfigImpl config = create("key", "value");
        assertFalse(config.containsKey("key2"));
        assertTrue(config.containsKey("key"));
    }

    @Test
    public void testContainsValue()
    {
        final PluginConfigImpl config = create("key", "value");
        assertFalse(config.containsValue("value2"));
        assertTrue(config.containsValue("value"));
    }

    @Test
    public void testGet()
    {
        final PluginConfigImpl config = create("key", "value");
        assertNull(config.get("key2"));
        assertEquals("value", config.get("key"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPut()
    {
        final PluginConfigImpl config = create();
        config.put("key", "value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutAll()
    {
        final PluginConfigImpl config = create();
        config.putAll(createMap("key", "value"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove()
    {
        final PluginConfigImpl config = create("key", "value");
        config.remove("key");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClear()
    {
        final PluginConfigImpl config = create("key", "value");
        config.clear();
    }

    @Test
    public void testKeySet()
    {
        final PluginConfigImpl config = create("key", "value");
        final Set<String> set = config.keySet();

        assertEquals(1, set.size());
        assertTrue(set.contains("key"));
    }

    @Test
    public void testValues()
    {
        final PluginConfigImpl config = create("key", "value");
        final Collection<String> set = config.values();

        assertEquals(1, set.size());
        assertTrue(set.contains("value"));
    }

    @Test
    public void testEntrySet()
    {
        final PluginConfigImpl config = create("key", "value");
        final Set<Map.Entry<String, String>> set = config.entrySet();

        assertEquals(1, set.size());
        assertNotNull(set.iterator().next());
    }

    @Test
    public void testGetString()
    {
        final PluginConfigImpl config = create("key", "value");
        assertNull(config.getString("key2"));
        assertEquals("value2", config.getString("key2", "value2"));
        assertEquals("value", config.getString("key"));
        assertEquals("value", config.getString("key", "value2"));
    }

    @Test
    public void testGetBoolean()
    {
        final PluginConfigImpl config = create("key1", "illegal", "key2", "true");
        assertNull(config.getBoolean("key3"));
        assertTrue(config.getBoolean("key3", true));
        assertFalse(config.getBoolean("key1"));
        assertFalse(config.getBoolean("key1", true));
        assertTrue(config.getBoolean("key2"));
        assertTrue(config.getBoolean("key2", false));
    }

    @Test
    public void testGetInteger()
    {
        final PluginConfigImpl config = create("key1", "illegal", "key2", "32");
        assertNull(config.getInteger("key3"));
        assertEquals(new Integer(11), config.getInteger("key3", 11));
        assertNull(config.getInteger("key1"));
        assertEquals(new Integer(11), config.getInteger("key1", 11));
        assertEquals(new Integer(32), config.getInteger("key2"));
        assertEquals(new Integer(32), config.getInteger("key2", 11));
    }

    @Test
    public void testGetLong()
    {
        final PluginConfigImpl config = create("key1", "illegal", "key2", "32");
        assertNull(config.getLong("key3"));
        assertEquals(new Long(11), config.getLong("key3", 11L));
        assertNull(config.getLong("key1"));
        assertEquals(new Long(11), config.getLong("key1", 11L));
        assertEquals(new Long(32), config.getLong("key2"));
        assertEquals(new Long(32), config.getLong("key2", 11L));
    }

    @Test
    public void testGetFloat()
    {
        final PluginConfigImpl config = create("key1", "illegal", "key2", "32");
        assertNull(config.getFloat("key3"));
        assertEquals(new Float(11), config.getFloat("key3", 11f));
        assertNull(config.getFloat("key1"));
        assertEquals(new Float(11), config.getFloat("key1", 11f));
        assertEquals(new Float(32), config.getFloat("key2"));
        assertEquals(new Float(32), config.getFloat("key2", 11f));
    }

    @Test
    public void testGetDouble()
    {
        final PluginConfigImpl config = create("key1", "illegal", "key2", "32");
        assertNull(config.getDouble("key3"));
        assertEquals(new Double(11), config.getDouble("key3", 11d));
        assertNull(config.getDouble("key1"));
        assertEquals(new Double(11), config.getDouble("key1", 11d));
        assertEquals(new Double(32), config.getDouble("key2"));
        assertEquals(new Double(32), config.getDouble("key2", 11d));
    }

    private PluginConfigImpl create(final String... values)
    {
        return new PluginConfigImpl(createMap(values));
    }

    private Map<String, String> createMap(final String... values)
    {
        final HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }

        return map;
    }
}
