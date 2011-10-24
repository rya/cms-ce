/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingPreparedStatement;
import com.enonic.cms.framework.jdbc.delegate.DelegatingResultSet;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;
import com.enonic.cms.framework.jdbc.dialect.Dialect;

/**
 * This class implements the dialect connection decorator.
 */
public final class DialectConnectionDecorator
    implements ConnectionDecorator
{
    private final Dialect dialect;

    public DialectConnectionDecorator( final Dialect dialect )
    {
        this.dialect = dialect;
    }

    public Connection decorate( Connection connection )
        throws SQLException
    {
        this.dialect.initConnection( connection );
        return new ConnectionImpl( connection );
    }

    private final class ConnectionImpl
        extends DelegatingConnection
    {
        public ConnectionImpl( Connection conn )
        {
            super( conn );
        }

        protected Statement createWrappedStatement( Statement stmt )
        {
            return new StatementImpl( stmt, this );
        }

        protected PreparedStatement createWrappedPreparedStatement( PreparedStatement stmt, String sql )
        {
            return new PreparedStatementImpl( stmt, this );
        }

        public PreparedStatement prepareStatement( String sql )
            throws SQLException
        {
            return super.prepareStatement( dialect.translateStatement( sql ) );
        }
    }

    private final class StatementImpl
        extends DelegatingStatement
    {
        public StatementImpl( Statement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        protected ResultSet createWrappedResultSet( ResultSet rs )
        {
            return new ResultSetImpl( rs, this );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            return super.executeUpdate( dialect.translateStatement( sql ) );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            return super.execute( dialect.translateStatement( sql ) );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            return super.executeQuery( dialect.translateStatement( sql ) );
        }
    }

    private final class PreparedStatementImpl
        extends DelegatingPreparedStatement
    {
        public PreparedStatementImpl( PreparedStatement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        protected ResultSet createWrappedResultSet( ResultSet rs )
        {
            return new ResultSetImpl( rs, this );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            return super.executeUpdate( dialect.translateStatement( sql ) );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            return super.execute( dialect.translateStatement( sql ) );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            return super.executeQuery( dialect.translateStatement( sql ) );
        }

        public void setByte( int parameterIndex, byte x )
            throws SQLException
        {
            dialect.setByte( this.stmt, parameterIndex, x );
        }

        public void setDouble( int parameterIndex, double x )
            throws SQLException
        {
            dialect.setDouble( this.stmt, parameterIndex, x );
        }

        public void setFloat( int parameterIndex, float x )
            throws SQLException
        {
            dialect.setFloat( this.stmt, parameterIndex, x );
        }

        public void setInt( int parameterIndex, int x )
            throws SQLException
        {
            dialect.setInt( this.stmt, parameterIndex, x );
        }

        public void setNull( int parameterIndex, int sqlType )
            throws SQLException
        {
            dialect.setObject( this.stmt, parameterIndex, null, sqlType );
        }

        public void setLong( int parameterIndex, long x )
            throws SQLException
        {
            dialect.setLong( this.stmt, parameterIndex, x );
        }

        public void setShort( int parameterIndex, short x )
            throws SQLException
        {
            dialect.setShort( this.stmt, parameterIndex, x );
        }

        public void setBoolean( int parameterIndex, boolean x )
            throws SQLException
        {
            dialect.setBoolean( this.stmt, parameterIndex, x );
        }

        public void setBytes( int parameterIndex, byte x[] )
            throws SQLException
        {
            dialect.setBytes( this.stmt, parameterIndex, x );
        }

        public void setBinaryStream( int parameterIndex, InputStream x, int length )
            throws SQLException
        {
            dialect.setBinaryStream( this.stmt, parameterIndex, x, length );
        }

        public void setObject( int parameterIndex, Object x )
            throws SQLException
        {
            dialect.setObject( this.stmt, parameterIndex, x );
        }

        public void setObject( int parameterIndex, Object x, int targetSqlType )
            throws SQLException
        {
            dialect.setObject( this.stmt, parameterIndex, x, targetSqlType );
        }

        public void setString( int parameterIndex, String x )
            throws SQLException
        {
            dialect.setString( this.stmt, parameterIndex, x );
        }

        public void setBigDecimal( int parameterIndex, BigDecimal x )
            throws SQLException
        {
            dialect.setBigDecimal( this.stmt, parameterIndex, x );
        }

        public void setAsciiStream( int parameterIndex, InputStream x, int length )
            throws SQLException
        {
            dialect.setAsciiStream( this.stmt, parameterIndex, x, length );
        }

        public void setCharacterStream( int parameterIndex, Reader reader, int length )
            throws SQLException
        {
            dialect.setCharacterStream( this.stmt, parameterIndex, reader, length );
        }
    }

    private final class ResultSetImpl
        extends DelegatingResultSet
    {
        public ResultSetImpl( ResultSet rs, Statement stmt )
        {
            super( rs, stmt );
        }

        public byte getByte( int columnIndex )
            throws SQLException
        {
            return dialect.getByte( this.result, columnIndex );
        }

        public double getDouble( int columnIndex )
            throws SQLException
        {
            return dialect.getDouble( this.result, columnIndex );
        }

        public float getFloat( int columnIndex )
            throws SQLException
        {
            return dialect.getFloat( this.result, columnIndex );
        }

        public int getInt( int columnIndex )
            throws SQLException
        {
            return dialect.getInt( this.result, columnIndex );
        }

        public long getLong( int columnIndex )
            throws SQLException
        {
            return dialect.getLong( this.result, columnIndex );
        }

        public short getShort( int columnIndex )
            throws SQLException
        {
            return dialect.getShort( this.result, columnIndex );
        }

        public boolean getBoolean( int columnIndex )
            throws SQLException
        {
            return dialect.getBoolean( this.result, columnIndex );
        }

        public byte[] getBytes( int columnIndex )
            throws SQLException
        {
            return dialect.getBytes( this.result, columnIndex );
        }

        public InputStream getAsciiStream( int columnIndex )
            throws SQLException
        {
            return dialect.getAsciiStream( this.result, columnIndex );
        }

        public InputStream getBinaryStream( int columnIndex )
            throws SQLException
        {
            return dialect.getBinaryStream( this.result, columnIndex );
        }

        public Reader getCharacterStream( int columnIndex )
            throws SQLException
        {
            return dialect.getCharacterStream( this.result, columnIndex );
        }

        public Object getObject( int columnIndex )
            throws SQLException
        {
            return dialect.getObject( this.result, columnIndex );
        }

        public String getString( int columnIndex )
            throws SQLException
        {
            return dialect.getString( this.result, columnIndex );
        }

        public BigDecimal getBigDecimal( int columnIndex )
            throws SQLException
        {
            return dialect.getBigDecimal( this.result, columnIndex );
        }

        public Blob getBlob( int columnIndex )
            throws SQLException
        {
            return dialect.getBlob( this.result, columnIndex );
        }

        public Clob getClob( int columnIndex )
            throws SQLException
        {
            return dialect.getClob( this.result, columnIndex );
        }

        public Date getDate( int columnIndex )
            throws SQLException
        {
            return dialect.getDate( this.result, columnIndex );
        }

        public Time getTime( int columnIndex )
            throws SQLException
        {
            return dialect.getTime( this.result, columnIndex );
        }

        public Timestamp getTimestamp( int columnIndex )
            throws SQLException
        {
            return dialect.getTimestamp( this.result, columnIndex );
        }

        public byte getByte( String columnName )
            throws SQLException
        {
            return getByte( findColumn( columnName ) );
        }

        public double getDouble( String columnName )
            throws SQLException
        {
            return getDouble( findColumn( columnName ) );
        }

        public float getFloat( String columnName )
            throws SQLException
        {
            return getFloat( findColumn( columnName ) );
        }

        public int getInt( String columnName )
            throws SQLException
        {
            return getInt( findColumn( columnName ) );
        }

        public long getLong( String columnName )
            throws SQLException
        {
            return getLong( findColumn( columnName ) );
        }

        public short getShort( String columnName )
            throws SQLException
        {
            return getShort( findColumn( columnName ) );
        }

        public boolean getBoolean( String columnName )
            throws SQLException
        {
            return getBoolean( findColumn( columnName ) );
        }

        public byte[] getBytes( String columnName )
            throws SQLException
        {
            return getBytes( findColumn( columnName ) );
        }

        public InputStream getAsciiStream( String columnName )
            throws SQLException
        {
            return getAsciiStream( findColumn( columnName ) );
        }

        public InputStream getBinaryStream( String columnName )
            throws SQLException
        {
            return getBinaryStream( findColumn( columnName ) );
        }

        public Reader getCharacterStream( String columnName )
            throws SQLException
        {
            return getCharacterStream( findColumn( columnName ) );
        }

        public Object getObject( String columnName )
            throws SQLException
        {
            return getObject( findColumn( columnName ) );
        }

        public String getString( String columnName )
            throws SQLException
        {
            return getString( findColumn( columnName ) );
        }

        public BigDecimal getBigDecimal( String columnName )
            throws SQLException
        {
            return getBigDecimal( findColumn( columnName ) );
        }

        public Blob getBlob( String columnName )
            throws SQLException
        {
            return getBlob( findColumn( columnName ) );
        }

        public Clob getClob( String columnName )
            throws SQLException
        {
            return getClob( findColumn( columnName ) );
        }

        public Date getDate( String columnName )
            throws SQLException
        {
            return getDate( findColumn( columnName ) );
        }

        public Time getTime( String columnName )
            throws SQLException
        {
            return getTime( findColumn( columnName ) );
        }

        public Timestamp getTimestamp( String columnName )
            throws SQLException
        {
            return getTimestamp( findColumn( columnName ) );
        }
    }
}
