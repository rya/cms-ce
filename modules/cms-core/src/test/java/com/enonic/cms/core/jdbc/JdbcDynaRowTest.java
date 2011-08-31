package com.enonic.cms.core.jdbc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class JdbcDynaRowTest
{
    @Test
    public void testStringDefault()
    {
        final JdbcDynaRow row = createRow("c1", null, "c2", "string");

        String val = row.getString("c1", "default");
        assertEquals("default", val);

        val = row.getString("c2", "default");
        assertEquals("string", val);
    }

    @Test
    public void testStringToString()
    {
        final JdbcDynaRow row = createRow("c1", null, "c2", "string");

        String val = row.getString("c1");
        assertNull(val);

        val = row.getString("c2");
        assertEquals("string", val);
    }

    @Test
    public void testBytesToString()
    {
        final JdbcDynaRow row = createRow("c1", new byte[3]);

        String val = row.getString("c1");
        assertEquals("[3 bytes]", val);
    }

    @Test
    public void testDateToString()
    {
        final Date now = new Date();
        final String nowString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(now);

        final JdbcDynaRow row = createRow("c1", now);

        String val = row.getString("c1");
        assertEquals(nowString, val);
    }

    @Test
    public void testObjectToString()
    {
        final JdbcDynaRow row = createRow("c1", 33);

        String val = row.getString("c1");
        assertEquals("33", val);
    }

    @Test
    public void testIntegerDefault()
    {
        final JdbcDynaRow row = createRow("c1", null, "c2", 111);

        Integer val = row.getInteger("c1", 333);
        assertEquals(new Integer(333), val);

        val = row.getInteger("c2", 333);
        assertEquals(new Integer(111), val);
    }

    @Test
    public void testIntegerToInteger()
    {
        final JdbcDynaRow row = createRow("c1", 33);

        Integer val = row.getInteger("c1");
        assertEquals(new Integer(33), val);
    }

    @Test
    public void testNumberToInteger()
    {
        final JdbcDynaRow row = createRow("c1", 33f);

        Integer val = row.getInteger("c1");
        assertEquals(new Integer(33), val);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalInteger()
    {
        final JdbcDynaRow row = createRow("c1", "abc");
        row.getInteger("c1");
    }

    @Test
    public void testLongDefault()
    {
        final JdbcDynaRow row = createRow("c1", null, "c2", 111l);

        Long val = row.getLong("c1", 333l);
        assertEquals(new Long(333), val);

        val = row.getLong("c2", 333l);
        assertEquals(new Long(111), val);
    }

    @Test
    public void testLongToLong()
    {
        final JdbcDynaRow row = createRow("c1", 33l);

        Long val = row.getLong("c1");
        assertEquals(new Long(33), val);
    }

    @Test
    public void testNumberToLong()
    {
        final JdbcDynaRow row = createRow("c1", 33f);

        Long val = row.getLong("c1");
        assertEquals(new Long(33), val);
    }

    @Test
    public void testDateToLong()
    {
        final Date now = new Date();
        final JdbcDynaRow row = createRow("c1", now);

        Long val = row.getLong("c1");
        assertEquals(new Long(now.getTime()), val);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalLong()
    {
        final JdbcDynaRow row = createRow("c1", "abc");
        row.getLong("c1");
    }

    @Test
    public void testBooleanDefault()
    {
        final JdbcDynaRow row = createRow("c1", null, "c2", false);

        Boolean val = row.getBoolean("c1", true);
        assertEquals(true, val);

        val = row.getBoolean("c2", true);
        assertEquals(false, val);
    }

    @Test
    public void testBooleanToBoolean()
    {
        final JdbcDynaRow row = createRow("c1", true);

        Boolean val = row.getBoolean("c1");
        assertEquals(true, val);
    }

    @Test
    public void testNumberToBoolean()
    {
        final JdbcDynaRow row = createRow("c1", 1);

        Boolean val = row.getBoolean("c1");
        assertEquals(true, val);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalBoolean()
    {
        final JdbcDynaRow row = createRow("c1", "abc");
        row.getBoolean("c1");
    }

    @Test
    public void testDateDefault()
    {
        final Date date1 = new Date(0);
        final Date date2 = new Date();
        
        final JdbcDynaRow row = createRow("c1", null, "c2", date1);

        Date val = row.getDate("c1", date2);
        assertEquals(date2, val);

        val = row.getDate("c2", date2);
        assertEquals(date1, val);
    }

    @Test
    public void testDateToDate()
    {
        final Date date = new Date();
        final JdbcDynaRow row = createRow("c1", date);

        Date val = row.getDate("c1");
        assertEquals(date, val);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalDate()
    {
        final JdbcDynaRow row = createRow("c1", "abc");
        row.getDate("c1");
    }

    @Test
    public void testBytesDefault()
    {
        final JdbcDynaRow row = createRow("c1", null, "c2", new byte[3]);

        byte[] val = row.getBytes("c1", new byte[1]);
        assertNotNull(val);
        assertEquals(1, val.length);

        val = row.getBytes("c2", new byte[1]);
        assertNotNull(val);
        assertEquals(3, val.length);
    }

    @Test
    public void testBytesToBytes()
    {
        final JdbcDynaRow row = createRow("c1", new byte[100]);

        byte[] val = row.getBytes("c1");
        assertNotNull(val);
        assertEquals(100, val.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalBytes()
    {
        final JdbcDynaRow row = createRow("c1", "abc");
        row.getBytes("c1");
    }

    @Test
    public void testObjectDefault()
    {
        final Object o1 = new Object();
        final Object o2 = new Object();
        
        final JdbcDynaRow row = createRow("c1", null, "c2", o1);

        Object val = row.getObject("c1", o2);
        assertSame(o2, val);

        val = row.getObject("c2", o2);
        assertSame(o1, val);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testColumnNotFound()
    {
        final JdbcDynaRow row = createRow("c1", "abc");
        row.getBytes("c2");
    }

    private JdbcDynaRow createRow(final Object... params)
    {
        return new JdbcDynaRow(createMap(params));
    }

    private Map<String, Object> createMap(final Object... params)
    {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < params.length; i += 2) {
            map.put(params[i].toString(), params[i + 1]);
        }

        return map;
    }
}
