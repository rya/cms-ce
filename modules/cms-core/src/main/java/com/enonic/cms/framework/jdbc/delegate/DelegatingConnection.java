/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.delegate;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * This class implements a delegating connection. All connections that will delegate to a source connection will typically extend this
 * class.
 */
public abstract class DelegatingConnection
    implements Connection
{
    protected final Connection conn;

    public DelegatingConnection( Connection conn )
    {
        this.conn = conn;
    }

    public int getHoldability()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getTransactionIsolation()
        throws SQLException
    {
        return this.conn.getTransactionIsolation();
    }

    public void clearWarnings()
        throws SQLException
    {
        this.conn.clearWarnings();
    }

    public void close()
        throws SQLException
    {
        this.conn.close();
    }

    public void commit()
        throws SQLException
    {
        this.conn.commit();
    }

    public void rollback()
        throws SQLException
    {
        this.conn.rollback();
    }

    public boolean getAutoCommit()
        throws SQLException
    {
        return this.conn.getAutoCommit();
    }

    public boolean isClosed()
        throws SQLException
    {
        return this.conn.isClosed();
    }

    public boolean isReadOnly()
        throws SQLException
    {
        return this.conn.isReadOnly();
    }

    public void setHoldability( int holdability )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTransactionIsolation( int level )
        throws SQLException
    {
        this.conn.setTransactionIsolation( level );
    }

    public void setAutoCommit( boolean autoCommit )
        throws SQLException
    {
        this.conn.setAutoCommit( autoCommit );
    }

    public void setReadOnly( boolean readOnly )
        throws SQLException
    {
        this.conn.setReadOnly( readOnly );
    }

    public String getCatalog()
        throws SQLException
    {
        return this.conn.getCatalog();
    }

    public void setCatalog( String catalog )
        throws SQLException
    {
        this.conn.setCatalog( catalog );
    }

    public DatabaseMetaData getMetaData()
        throws SQLException
    {
        return this.conn.getMetaData();
    }

    public SQLWarning getWarnings()
        throws SQLException
    {
        return this.conn.getWarnings();
    }

    public Savepoint setSavepoint()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void releaseSavepoint( Savepoint savepoint )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void rollback( Savepoint savepoint )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Statement createStatement()
        throws SQLException
    {
        return createWrappedStatement( this.conn.createStatement() );
    }

    public Statement createStatement( int resultSetType, int resultSetConcurrency )
        throws SQLException
    {
        return createWrappedStatement( this.conn.createStatement( resultSetType, resultSetConcurrency ) );
    }

    public Statement createStatement( int resultSetType, int resultSetConcurrency, int resultSetHoldability )
        throws SQLException
    {
        return createWrappedStatement( this.conn.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability ) );
    }

    public Map<String, Class<?>> getTypeMap()
        throws SQLException
    {
        return this.conn.getTypeMap();
    }

    public void setTypeMap( Map<String, Class<?>> map )
        throws SQLException
    {
        this.conn.setTypeMap( map );
    }

    public String nativeSQL( String sql )
        throws SQLException
    {
        return this.conn.nativeSQL( sql );
    }

    public CallableStatement prepareCall( String sql )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement( String sql )
        throws SQLException
    {
        return createWrappedPreparedStatement( this.conn.prepareStatement( sql ), sql );
    }

    public PreparedStatement prepareStatement( String sql, int autoGeneratedKeys )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement( String sql, int columnIndexes[] )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Savepoint setSavepoint( String name )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public PreparedStatement prepareStatement( String sql, String columnNames[] )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    protected abstract Statement createWrappedStatement( Statement stmt );

    protected abstract PreparedStatement createWrappedPreparedStatement( PreparedStatement stmt, String sql );

    public Clob createClob()
        throws SQLException
    {
        return this.conn.createClob();
    }

    public Blob createBlob()
        throws SQLException
    {
        return this.conn.createBlob();
    }

    public NClob createNClob()
        throws SQLException
    {
        return this.conn.createNClob();
    }

    public SQLXML createSQLXML()
        throws SQLException
    {
        return this.conn.createSQLXML();
    }

    public boolean isValid( int timeout )
        throws SQLException
    {
        return this.conn.isValid( timeout );
    }

    public void setClientInfo( String name, String value )
        throws SQLClientInfoException
    {
        this.conn.setClientInfo( name, value );
    }

    public void setClientInfo( Properties properties )
        throws SQLClientInfoException
    {
        this.conn.setClientInfo( properties );
    }

    public String getClientInfo( String name )
        throws SQLException
    {
        return this.conn.getClientInfo( name );
    }

    public Properties getClientInfo()
        throws SQLException
    {
        return this.conn.getClientInfo();
    }

    public Array createArrayOf( String typeName, Object[] elements )
        throws SQLException
    {
        return this.conn.createArrayOf( typeName, elements );
    }

    public Struct createStruct( String typeName, Object[] attributes )
        throws SQLException
    {
        return this.conn.createStruct( typeName, attributes );
    }

    public <T> T unwrap( Class<T> iface )
        throws SQLException
    {
        return this.conn.unwrap( iface );
    }

    public boolean isWrapperFor( Class<?> iface )
        throws SQLException
    {
        return this.conn.isWrapperFor( iface );
    }

    public int hashCode()
    {
        return this.conn.hashCode();
    }

    public String toString()
    {
        return this.conn.toString();
    }
}
