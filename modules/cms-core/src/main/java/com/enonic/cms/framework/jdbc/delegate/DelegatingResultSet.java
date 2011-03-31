/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.delegate;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * This class implements the delegating resultset.
 */
public class DelegatingResultSet
    extends DelegatingBase
    implements ResultSet
{
    /**
     * Real result set.
     */
    private final ResultSet result;

    /**
     * Statement.
     */
    private final Statement stmt;

    /**
     * Construct the resultset.
     */
    public DelegatingResultSet( ResultSet result, Statement stmt )
    {
        this.stmt = stmt;
        this.result = result;
    }

    /**
     * Return the delegate.
     */
    public Object getDelegate()
    {
        return this.result;
    }

    public int getConcurrency()
        throws SQLException
    {
        return result.getConcurrency();
    }

    public int getFetchDirection()
        throws SQLException
    {
        return result.getFetchDirection();
    }

    public int getFetchSize()
        throws SQLException
    {
        return result.getFetchSize();
    }

    public int getRow()
        throws SQLException
    {
        return result.getRow();
    }

    public int getType()
        throws SQLException
    {
        return result.getType();
    }

    public void afterLast()
        throws SQLException
    {
        result.afterLast();
    }

    public void beforeFirst()
        throws SQLException
    {
        result.beforeFirst();
    }

    public void cancelRowUpdates()
        throws SQLException
    {
        result.cancelRowUpdates();
    }

    public void clearWarnings()
        throws SQLException
    {
        result.clearWarnings();
    }

    public void close()
        throws SQLException
    {
        result.close();
    }

    public void deleteRow()
        throws SQLException
    {
        result.deleteRow();
    }

    public void insertRow()
        throws SQLException
    {
        result.insertRow();
    }

    public void moveToCurrentRow()
        throws SQLException
    {
        result.moveToCurrentRow();
    }

    public void moveToInsertRow()
        throws SQLException
    {
        result.moveToInsertRow();
    }

    public void refreshRow()
        throws SQLException
    {
        result.refreshRow();
    }

    public void updateRow()
        throws SQLException
    {
        result.updateRow();
    }

    public boolean first()
        throws SQLException
    {
        return result.first();
    }

    public boolean isAfterLast()
        throws SQLException
    {
        return result.isAfterLast();
    }

    public boolean isBeforeFirst()
        throws SQLException
    {
        return result.isBeforeFirst();
    }

    public boolean isFirst()
        throws SQLException
    {
        return result.isFirst();
    }

    public boolean isLast()
        throws SQLException
    {
        return result.isLast();
    }

    public boolean last()
        throws SQLException
    {
        return result.last();
    }

    public boolean next()
        throws SQLException
    {
        return result.next();
    }

    public boolean previous()
        throws SQLException
    {
        return result.previous();
    }

    public boolean rowDeleted()
        throws SQLException
    {
        return result.rowDeleted();
    }

    public boolean rowInserted()
        throws SQLException
    {
        return result.rowInserted();
    }

    public boolean rowUpdated()
        throws SQLException
    {
        return result.rowUpdated();
    }

    public boolean wasNull()
        throws SQLException
    {
        return result.wasNull();
    }

    public byte getByte( int columnIndex )
        throws SQLException
    {
        return result.getByte( columnIndex );
    }

    public double getDouble( int columnIndex )
        throws SQLException
    {
        return result.getDouble( columnIndex );
    }

    public float getFloat( int columnIndex )
        throws SQLException
    {
        return result.getFloat( columnIndex );
    }

    public int getInt( int columnIndex )
        throws SQLException
    {
        return result.getInt( columnIndex );
    }

    public long getLong( int columnIndex )
        throws SQLException
    {
        return result.getLong( columnIndex );
    }

    public short getShort( int columnIndex )
        throws SQLException
    {
        return result.getShort( columnIndex );
    }

    public void setFetchDirection( int direction )
        throws SQLException
    {
        result.setFetchDirection( direction );
    }

    public void setFetchSize( int rows )
        throws SQLException
    {
        result.setFetchSize( rows );
    }

    public void updateNull( int columnIndex )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean absolute( int row )
        throws SQLException
    {
        return result.absolute( row );
    }

    public boolean getBoolean( int columnIndex )
        throws SQLException
    {
        return result.getBoolean( columnIndex );
    }

    public boolean relative( int rows )
        throws SQLException
    {
        return result.relative( rows );
    }

    public byte[] getBytes( int columnIndex )
        throws SQLException
    {
        return result.getBytes( columnIndex );
    }

    public void updateByte( int columnIndex, byte x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateDouble( int columnIndex, double x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateFloat( int columnIndex, float x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateInt( int columnIndex, int x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateLong( int columnIndex, long x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateShort( int columnIndex, short x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean( int columnIndex, boolean x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBytes( int columnIndex, byte[] x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream( int columnIndex )
        throws SQLException
    {
        return result.getAsciiStream( columnIndex );
    }

    public InputStream getBinaryStream( int columnIndex )
        throws SQLException
    {
        return result.getBinaryStream( columnIndex );
    }

    public InputStream getUnicodeStream( int columnIndex )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream( int columnIndex, InputStream x, int length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream( int columnIndex, InputStream x, int length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream( int columnIndex )
        throws SQLException
    {
        return result.getCharacterStream( columnIndex );
    }

    public void updateCharacterStream( int columnIndex, Reader x, int length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject( int columnIndex )
        throws SQLException
    {
        return result.getObject( columnIndex );
    }

    public void updateObject( int columnIndex, Object x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject( int columnIndex, Object x, int scale )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getCursorName()
        throws SQLException
    {
        return result.getCursorName();
    }

    public String getString( int columnIndex )
        throws SQLException
    {
        return result.getString( columnIndex );
    }

    public void updateString( int columnIndex, String x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public byte getByte( String columnName )
        throws SQLException
    {
        return result.getByte( columnName );
    }

    public double getDouble( String columnName )
        throws SQLException
    {
        return result.getDouble( columnName );
    }

    public float getFloat( String columnName )
        throws SQLException
    {
        return result.getFloat( columnName );
    }

    public int findColumn( String columnName )
        throws SQLException
    {
        return result.findColumn( columnName );
    }

    public int getInt( String columnName )
        throws SQLException
    {
        return result.getInt( columnName );
    }

    public long getLong( String columnName )
        throws SQLException
    {
        return result.getLong( columnName );
    }

    public short getShort( String columnName )
        throws SQLException
    {
        return result.getShort( columnName );
    }

    public void updateNull( String columnName )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean( String columnName )
        throws SQLException
    {
        return result.getBoolean( columnName );
    }

    public byte[] getBytes( String columnName )
        throws SQLException
    {
        return result.getBytes( columnName );
    }

    public void updateByte( String columnName, byte x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateDouble( String columnName, double x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateFloat( String columnName, float x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateInt( String columnName, int x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateLong( String columnName, long x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateShort( String columnName, short x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBoolean( String columnName, boolean x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBytes( String columnName, byte[] x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal( int columnIndex )
        throws SQLException
    {
        return result.getBigDecimal( columnIndex );
    }

    public BigDecimal getBigDecimal( int columnIndex, int scale )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal( int columnIndex, BigDecimal x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL( int columnIndex )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Array getArray( int i )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateArray( int columnIndex, Array x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob( int i )
        throws SQLException
    {
        return result.getBlob( i );
    }

    public void updateBlob( int columnIndex, Blob x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob getClob( int i )
        throws SQLException
    {
        return result.getClob( i );
    }

    public void updateClob( int columnIndex, Clob x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate( int columnIndex )
        throws SQLException
    {
        return result.getDate( columnIndex );
    }

    public void updateDate( int columnIndex, Date x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef( int i )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRef( int columnIndex, Ref x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSetMetaData getMetaData()
        throws SQLException
    {
        return result.getMetaData();
    }

    public SQLWarning getWarnings()
        throws SQLException
    {
        return result.getWarnings();
    }

    public Statement getStatement()
        throws SQLException
    {
        return this.stmt;
    }

    public Time getTime( int columnIndex )
        throws SQLException
    {
        return result.getTime( columnIndex );
    }

    public void updateTime( int columnIndex, Time x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp( int columnIndex )
        throws SQLException
    {
        return result.getTimestamp( columnIndex );
    }

    public void updateTimestamp( int columnIndex, Timestamp x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getAsciiStream( String columnName )
        throws SQLException
    {
        return result.getAsciiStream( columnName );
    }

    public InputStream getBinaryStream( String columnName )
        throws SQLException
    {
        return result.getBinaryStream( columnName );
    }

    public InputStream getUnicodeStream( String columnName )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateAsciiStream( String columnName, InputStream x, int length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBinaryStream( String columnName, InputStream x, int length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream( String columnName )
        throws SQLException
    {
        return result.getCharacterStream( columnName );
    }

    public void updateCharacterStream( String columnName, Reader reader, int length )
        throws SQLException
    {
        result.updateCharacterStream( columnName, reader, length );
    }

    public Object getObject( String columnName )
        throws SQLException
    {
        return result.getObject( columnName );
    }

    public void updateObject( String columnName, Object x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateObject( String columnName, Object x, int scale )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject( int i, Map map )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public String getString( String columnName )
        throws SQLException
    {
        return result.getString( columnName );
    }

    public void updateString( String columnName, String x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public BigDecimal getBigDecimal( String columnName )
        throws SQLException
    {
        return result.getBigDecimal( columnName );
    }

    public BigDecimal getBigDecimal( String columnName, int scale )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateBigDecimal( String columnName, BigDecimal x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public URL getURL( String columnName )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Array getArray( String colName )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateArray( String columnName, Array x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Blob getBlob( String colName )
        throws SQLException
    {
        return result.getBlob( colName );
    }

    public void updateBlob( String columnName, Blob x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Clob getClob( String colName )
        throws SQLException
    {
        return result.getClob( colName );
    }

    public void updateClob( String columnName, Clob x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate( String columnName )
        throws SQLException
    {
        return result.getDate( columnName );
    }

    public void updateDate( String columnName, Date x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate( int columnIndex, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Ref getRef( String colName )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void updateRef( String columnName, Ref x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime( String columnName )
        throws SQLException
    {
        return result.getTime( columnName );
    }

    public void updateTime( String columnName, Time x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime( int columnIndex, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp( String columnName )
        throws SQLException
    {
        return result.getTimestamp( columnName );
    }

    public void updateTimestamp( String columnName, Timestamp x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp( int columnIndex, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Object getObject( String colName, Map map )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Date getDate( String columnName, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Time getTime( String columnName, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Timestamp getTimestamp( String columnName, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public <T> T unwrap( Class<T> iface )
        throws SQLException
    {
        return this.result.unwrap( iface );
    }

    public boolean isWrapperFor( Class<?> iface )
        throws SQLException
    {
        return this.result.isWrapperFor( iface );
    }

    public RowId getRowId( int columnIndex )
        throws SQLException
    {
        return this.result.getRowId( columnIndex );
    }

    public RowId getRowId( String columnLabel )
        throws SQLException
    {
        return this.result.getRowId( columnLabel );
    }

    public void updateRowId( int columnIndex, RowId x )
        throws SQLException
    {
        this.result.updateRowId( columnIndex, x );
    }

    public void updateRowId( String columnLabel, RowId x )
        throws SQLException
    {
        this.result.updateRowId( columnLabel, x );
    }

    public int getHoldability()
        throws SQLException
    {
        return this.result.getHoldability();
    }

    public boolean isClosed()
        throws SQLException
    {
        return this.result.isClosed();
    }

    public void updateNString( int columnIndex, String nString )
        throws SQLException
    {
        this.result.updateNString( columnIndex, nString );
    }

    public void updateNString( String columnLabel, String nString )
        throws SQLException
    {
        this.result.updateNString( columnLabel, nString );
    }

    public void updateNClob( int columnIndex, NClob nClob )
        throws SQLException
    {
        this.result.updateNClob( columnIndex, nClob );
    }

    public void updateNClob( String columnLabel, NClob nClob )
        throws SQLException
    {
        this.result.updateNClob( columnLabel, nClob );
    }

    public NClob getNClob( int columnIndex )
        throws SQLException
    {
        return this.result.getNClob( columnIndex );
    }

    public NClob getNClob( String columnLabel )
        throws SQLException
    {
        return this.result.getNClob( columnLabel );
    }

    public SQLXML getSQLXML( int columnIndex )
        throws SQLException
    {
        return this.result.getSQLXML( columnIndex );
    }

    public SQLXML getSQLXML( String columnLabel )
        throws SQLException
    {
        return this.result.getSQLXML( columnLabel );
    }

    public void updateSQLXML( int columnIndex, SQLXML xmlObject )
        throws SQLException
    {
        this.result.updateSQLXML( columnIndex, xmlObject );
    }

    public void updateSQLXML( String columnLabel, SQLXML xmlObject )
        throws SQLException
    {
        this.result.updateSQLXML( columnLabel, xmlObject );
    }

    public String getNString( int columnIndex )
        throws SQLException
    {
        return this.result.getNString( columnIndex );
    }

    public String getNString( String columnLabel )
        throws SQLException
    {
        return this.result.getNString( columnLabel );
    }

    public Reader getNCharacterStream( int columnIndex )
        throws SQLException
    {
        return this.result.getNCharacterStream( columnIndex );
    }

    public Reader getNCharacterStream( String columnLabel )
        throws SQLException
    {
        return this.result.getNCharacterStream( columnLabel );
    }

    public void updateNCharacterStream( int columnIndex, Reader x, long length )
        throws SQLException
    {
        this.result.updateNCharacterStream( columnIndex, x, length );
    }

    public void updateNCharacterStream( String columnLabel, Reader reader, long length )
        throws SQLException
    {
        this.result.updateNCharacterStream( columnLabel, reader, length );
    }

    public void updateAsciiStream( int columnIndex, InputStream x, long length )
        throws SQLException
    {
        this.result.updateAsciiStream( columnIndex, x, length );
    }

    public void updateBinaryStream( int columnIndex, InputStream x, long length )
        throws SQLException
    {
        this.result.updateBinaryStream( columnIndex, x, length );
    }

    public void updateCharacterStream( int columnIndex, Reader x, long length )
        throws SQLException
    {
        this.result.updateCharacterStream( columnIndex, x, length );
    }

    public void updateAsciiStream( String columnLabel, InputStream x, long length )
        throws SQLException
    {
        this.result.updateAsciiStream( columnLabel, x, length );
    }

    public void updateBinaryStream( String columnLabel, InputStream x, long length )
        throws SQLException
    {
        this.result.updateBinaryStream( columnLabel, x, length );
    }

    public void updateCharacterStream( String columnLabel, Reader reader, long length )
        throws SQLException
    {
        this.result.updateCharacterStream( columnLabel, reader, length );
    }

    public void updateBlob( int columnIndex, InputStream inputStream, long length )
        throws SQLException
    {
        this.result.updateBlob( columnIndex, inputStream, length );
    }

    public void updateBlob( String columnLabel, InputStream inputStream, long length )
        throws SQLException
    {
        this.result.updateBlob( columnLabel, inputStream, length );
    }

    public void updateClob( int columnIndex, Reader reader, long length )
        throws SQLException
    {
        this.result.updateClob( columnIndex, reader, length );
    }

    public void updateClob( String columnLabel, Reader reader, long length )
        throws SQLException
    {
        this.result.updateClob( columnLabel, reader, length );
    }

    public void updateNClob( int columnIndex, Reader reader, long length )
        throws SQLException
    {
        this.result.updateNClob( columnIndex, reader, length );
    }

    public void updateNClob( String columnLabel, Reader reader, long length )
        throws SQLException
    {
        this.result.updateNClob( columnLabel, reader, length );
    }

    public void updateNCharacterStream( int columnIndex, Reader x )
        throws SQLException
    {
        this.result.updateNCharacterStream( columnIndex, x );
    }

    public void updateNCharacterStream( String columnLabel, Reader reader )
        throws SQLException
    {
        this.result.updateNCharacterStream( columnLabel, reader );
    }

    public void updateAsciiStream( int columnIndex, InputStream x )
        throws SQLException
    {
        this.result.updateAsciiStream( columnIndex, x );
    }

    public void updateBinaryStream( int columnIndex, InputStream x )
        throws SQLException
    {
        this.result.updateBinaryStream( columnIndex, x );
    }

    public void updateCharacterStream( int columnIndex, Reader x )
        throws SQLException
    {
        this.result.updateCharacterStream( columnIndex, x );
    }

    public void updateAsciiStream( String columnLabel, InputStream x )
        throws SQLException
    {
        this.result.updateAsciiStream( columnLabel, x );
    }

    public void updateBinaryStream( String columnLabel, InputStream x )
        throws SQLException
    {
        this.result.updateBinaryStream( columnLabel, x );
    }

    public void updateCharacterStream( String columnLabel, Reader reader )
        throws SQLException
    {
        this.result.updateCharacterStream( columnLabel, reader );
    }

    public void updateBlob( int columnIndex, InputStream inputStream )
        throws SQLException
    {
        this.result.updateBlob( columnIndex, inputStream );
    }

    public void updateBlob( String columnLabel, InputStream inputStream )
        throws SQLException
    {
        this.result.updateBlob( columnLabel, inputStream );
    }

    public void updateClob( int columnIndex, Reader reader )
        throws SQLException
    {
        this.result.updateClob( columnIndex, reader );
    }

    public void updateClob( String columnLabel, Reader reader )
        throws SQLException
    {
        this.result.updateClob( columnLabel, reader );
    }

    public void updateNClob( int columnIndex, Reader reader )
        throws SQLException
    {
        this.result.updateNClob( columnIndex, reader );
    }

    public void updateNClob( String columnLabel, Reader reader )
        throws SQLException
    {
        this.result.updateNClob( columnLabel, reader );
    }
}
