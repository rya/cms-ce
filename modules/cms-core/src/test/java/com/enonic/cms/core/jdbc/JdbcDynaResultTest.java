package com.enonic.cms.core.jdbc;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import javax.sql.RowSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class JdbcDynaResultTest
{
    private SqlRowSet rowSet;

    @Before
    public void startUp()
    {
        final SqlRowSetMetaData metaData = Mockito.mock(SqlRowSetMetaData.class);
        Mockito.when(metaData.getColumnCount()).thenReturn(2);
        Mockito.when(metaData.getColumnName(1)).thenReturn("c1");
        Mockito.when(metaData.getColumnName(2)).thenReturn("c2");

        this.rowSet = Mockito.mock(SqlRowSet.class);
        Mockito.when(rowSet.getMetaData()).thenReturn(metaData);
        Mockito.when(rowSet.getObject(1)).thenReturn("v1");
        Mockito.when(rowSet.getObject(2)).thenReturn("v2");
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmpty()
    {
        final JdbcDynaResult result = new JdbcDynaResult(this.rowSet);
        assertFalse(result.hasNext());
        result.next();
    }

    @Test
    public void testIterate()
    {
        final JdbcDynaResult result = new JdbcDynaResult(this.rowSet);

        Mockito.when(this.rowSet.next()).thenReturn(true);
        assertTrue(result.hasNext());
        JdbcDynaRow row = result.next();
        assertNotNull(row);

        Mockito.when(this.rowSet.next()).thenReturn(false);
        assertFalse(result.hasNext());
    }

    @Test
    public void testGetIterator()
    {
        final JdbcDynaResult result = new JdbcDynaResult(this.rowSet);
        final Iterator it = result.iterator();

        assertNotNull(it);
        assertSame(result, it);
    }

    @Test
    public void testGetColumnNames()
    {
        final JdbcDynaResult result = new JdbcDynaResult(this.rowSet);
        final Set<String> set = result.getColumnNames();

        assertNotNull(set);
        assertEquals(2, set.size());
        assertTrue(set.contains("c1"));
        assertTrue(set.contains("c2"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove()
    {
        final JdbcDynaResult result = new JdbcDynaResult(this.rowSet);
        result.remove();
    }
}
