package com.enonic.cms.core.jdbc;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class JdbcDynaResult
    implements Iterator<JdbcDynaRow>, Iterable<JdbcDynaRow>
{
    private final SqlRowSet rowSet;
    private final String[] columnNames;
    private boolean eof = false;
    private boolean current = false;

    public JdbcDynaResult(final SqlRowSet rowSet)
    {
        this.rowSet = rowSet;

        final SqlRowSetMetaData meta = this.rowSet.getMetaData();
        this.columnNames = new String[meta.getColumnCount()];

        for (int i = 0; i < this.columnNames.length; i++) {
            this.columnNames[i] = meta.getColumnName(i + 1).toLowerCase();
        }
    }

    public Iterator<JdbcDynaRow> iterator()
    {
        return this;
    }

    public boolean hasNext()
    {
        advance();
        return !this.eof;
    }

    public JdbcDynaRow next()
    {
        advance();
        if (this.eof) {
            throw new NoSuchElementException();
        }

        this.current = false;
        return createRow();
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public Set<String> getColumnNames()
    {
        return ImmutableSet.copyOf(this.columnNames);
    }

    private void advance()
    {
        if (!this.current && !this.eof) {
            if (this.rowSet.next()) {
                this.current = true;
                this.eof = false;
            } else {
                this.current = false;
                this.eof = true;
            }
        }
    }

    private JdbcDynaRow createRow()
    {
        final Map<String, Object> data = Maps.newHashMap();
        for (int i = 0; i < this.columnNames.length; i++) {
            data.put(this.columnNames[i].toLowerCase(), this.rowSet.getObject(i + 1));
        }

        return new JdbcDynaRow(data);
    }
}
