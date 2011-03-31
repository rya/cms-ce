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
    /**
     * Dialect to use.
     */
    private final Dialect dialect;

    /**
     * Construct the decorator.
     */
    public DialectConnectionDecorator( Dialect dialect )
    {
        this.dialect = dialect;
    }

    /**
     * Decorate the connection.
     */
    public Connection decorate( Connection connection )
        throws SQLException
    {
        this.dialect.initConnection( connection );
        return new ConnectionImpl( connection, this.dialect );
    }

    /**
     * Dialect connection.
     */
    private final class ConnectionImpl
        extends DelegatingConnection
    {
        /**
         * Dialect.
         */
        private final Dialect dialect;

        /**
         * Construct the connection.
         */
        public ConnectionImpl( Connection conn, Dialect dialect )
        {
            super( conn );
            this.dialect = dialect;
        }

        /**
         * Create prepared statement.
         */
        protected Statement createWrappedStatement( Statement stmt )
        {
            return new StatementImpl( stmt, this, this.dialect );
        }

        /**
         * Create prepared statement.
         */
        protected PreparedStatement createWrappedPreparedStatement( PreparedStatement stmt, String sql )
        {
            return new PreparedStatementImpl( stmt, this, this.dialect );
        }

        public PreparedStatement prepareStatement( String sql )
            throws SQLException
        {
            return super.prepareStatement( this.dialect.translateStatement( sql ) );
        }
    }

    /**
     * Dialect statement.
     */
    private final class StatementImpl
        extends DelegatingStatement
    {
        /**
         * Dialect.
         */
        private final Dialect dialect;

        /**
         * Construct the statement.
         */
        public StatementImpl( Statement stmt, Connection conn, Dialect dialect )
        {
            super( stmt, conn );
            this.dialect = dialect;
        }

        /**
         * Create wrapped result set.
         */
        protected ResultSet createWrappedResultSet( ResultSet rs )
        {
            return new ResultSetImpl( rs, this, this.dialect );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            return super.executeUpdate( this.dialect.translateStatement( sql ) );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            return super.execute( this.dialect.translateStatement( sql ) );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            return super.executeQuery( this.dialect.translateStatement( sql ) );
        }
    }

    /**
     * Dialect prepared statement.
     */
    private final class PreparedStatementImpl
        extends DelegatingPreparedStatement
    {
        /**
         * Dialect.
         */
        private final Dialect dialect;

        /**
         * Construct the statement.
         */
        public PreparedStatementImpl( PreparedStatement stmt, Connection conn, Dialect dialect )
        {
            super( stmt, conn );
            this.dialect = dialect;
        }

        /**
         * Return the real statement.
         */
        private PreparedStatement getRealStatement()
        {
            return (PreparedStatement) getDelegate();
        }

        /**
         * Create wrapped result set.
         */
        protected ResultSet createWrappedResultSet( ResultSet rs )
        {
            return new ResultSetImpl( rs, this, this.dialect );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            return super.executeUpdate( this.dialect.translateStatement( sql ) );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            return super.execute( this.dialect.translateStatement( sql ) );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            return super.executeQuery( this.dialect.translateStatement( sql ) );
        }

        public void setByte( int parameterIndex, byte x )
            throws SQLException
        {
            this.dialect.setByte( getRealStatement(), parameterIndex, x );
        }

        public void setDouble( int parameterIndex, double x )
            throws SQLException
        {
            this.dialect.setDouble( getRealStatement(), parameterIndex, x );
        }

        public void setFloat( int parameterIndex, float x )
            throws SQLException
        {
            this.dialect.setFloat( getRealStatement(), parameterIndex, x );
        }

        public void setInt( int parameterIndex, int x )
            throws SQLException
        {
            this.dialect.setInt( getRealStatement(), parameterIndex, x );
        }

        public void setNull( int parameterIndex, int sqlType )
            throws SQLException
        {
            this.dialect.setObject( getRealStatement(), parameterIndex, null, sqlType );
        }

        public void setLong( int parameterIndex, long x )
            throws SQLException
        {
            this.dialect.setLong( getRealStatement(), parameterIndex, x );
        }

        public void setShort( int parameterIndex, short x )
            throws SQLException
        {
            this.dialect.setShort( getRealStatement(), parameterIndex, x );
        }

        public void setBoolean( int parameterIndex, boolean x )
            throws SQLException
        {
            this.dialect.setBoolean( getRealStatement(), parameterIndex, x );
        }

        public void setBytes( int parameterIndex, byte x[] )
            throws SQLException
        {
            this.dialect.setBytes( getRealStatement(), parameterIndex, x );
        }

        public void setBinaryStream( int parameterIndex, InputStream x, int length )
            throws SQLException
        {
            this.dialect.setBinaryStream( getRealStatement(), parameterIndex, x, length );
        }

        public void setObject( int parameterIndex, Object x )
            throws SQLException
        {
            this.dialect.setObject( getRealStatement(), parameterIndex, x );
        }

        public void setObject( int parameterIndex, Object x, int targetSqlType )
            throws SQLException
        {
            this.dialect.setObject( getRealStatement(), parameterIndex, x, targetSqlType );
        }

        public void setString( int parameterIndex, String x )
            throws SQLException
        {
            this.dialect.setString( getRealStatement(), parameterIndex, x );
        }

        public void setBigDecimal( int parameterIndex, BigDecimal x )
            throws SQLException
        {
            this.dialect.setBigDecimal( getRealStatement(), parameterIndex, x );
        }

        public void setAsciiStream( int parameterIndex, InputStream x, int length )
            throws SQLException
        {
            this.dialect.setAsciiStream( getRealStatement(), parameterIndex, x, length );
        }

        public void setCharacterStream( int parameterIndex, Reader reader, int length )
            throws SQLException
        {
            this.dialect.setCharacterStream( getRealStatement(), parameterIndex, reader, length );
        }
    }

    /**
     * Dialect resultset.
     */
    private final class ResultSetImpl
        extends DelegatingResultSet
    {
        /**
         * Dialect.
         */
        private final Dialect dialect;

        /**
         * Construct the statement.
         */
        public ResultSetImpl( ResultSet rs, Statement stmt, Dialect dialect )
        {
            super( rs, stmt );
            this.dialect = dialect;
        }

        /**
         * Return the real result set.
         */
        private ResultSet getRealResultSet()
        {
            return (ResultSet) getDelegate();
        }

        public byte getByte( int columnIndex )
            throws SQLException
        {
            return this.dialect.getByte( getRealResultSet(), columnIndex );
        }

        public double getDouble( int columnIndex )
            throws SQLException
        {
            return this.dialect.getDouble( getRealResultSet(), columnIndex );
        }

        public float getFloat( int columnIndex )
            throws SQLException
        {
            return this.dialect.getFloat( getRealResultSet(), columnIndex );
        }

        public int getInt( int columnIndex )
            throws SQLException
        {
            return this.dialect.getInt( getRealResultSet(), columnIndex );
        }

        public long getLong( int columnIndex )
            throws SQLException
        {
            return this.dialect.getLong( getRealResultSet(), columnIndex );
        }

        public short getShort( int columnIndex )
            throws SQLException
        {
            return this.dialect.getShort( getRealResultSet(), columnIndex );
        }

        public boolean getBoolean( int columnIndex )
            throws SQLException
        {
            return this.dialect.getBoolean( getRealResultSet(), columnIndex );
        }

        public byte[] getBytes( int columnIndex )
            throws SQLException
        {
            return this.dialect.getBytes( getRealResultSet(), columnIndex );
        }

        public InputStream getAsciiStream( int columnIndex )
            throws SQLException
        {
            return this.dialect.getAsciiStream( getRealResultSet(), columnIndex );
        }

        public InputStream getBinaryStream( int columnIndex )
            throws SQLException
        {
            return this.dialect.getBinaryStream( getRealResultSet(), columnIndex );
        }

        public Reader getCharacterStream( int columnIndex )
            throws SQLException
        {
            return this.dialect.getCharacterStream( getRealResultSet(), columnIndex );
        }

        public Object getObject( int columnIndex )
            throws SQLException
        {
            return this.dialect.getObject( getRealResultSet(), columnIndex );
        }

        public String getString( int columnIndex )
            throws SQLException
        {
            return this.dialect.getString( getRealResultSet(), columnIndex );
        }

        public BigDecimal getBigDecimal( int columnIndex )
            throws SQLException
        {
            return this.dialect.getBigDecimal( getRealResultSet(), columnIndex );
        }

        public Blob getBlob( int columnIndex )
            throws SQLException
        {
            return this.dialect.getBlob( getRealResultSet(), columnIndex );
        }

        public Clob getClob( int columnIndex )
            throws SQLException
        {
            return this.dialect.getClob( getRealResultSet(), columnIndex );
        }

        public Date getDate( int columnIndex )
            throws SQLException
        {
            return this.dialect.getDate( getRealResultSet(), columnIndex );
        }

        public Time getTime( int columnIndex )
            throws SQLException
        {
            return this.dialect.getTime( getRealResultSet(), columnIndex );
        }

        public Timestamp getTimestamp( int columnIndex )
            throws SQLException
        {
            return this.dialect.getTimestamp( getRealResultSet(), columnIndex );
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
