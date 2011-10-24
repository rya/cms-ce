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
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * This class implements the delegating statement.
 */
public class DelegatingPreparedStatement
    extends DelegatingStatement
    implements PreparedStatement
{
    protected final PreparedStatement stmt;

    public DelegatingPreparedStatement( PreparedStatement stmt, Connection conn )
    {
        super( stmt, conn );
        this.stmt = stmt;
    }

    public int executeUpdate()
        throws SQLException
    {
        return this.stmt.executeUpdate();
    }

    public void addBatch()
        throws SQLException
    {
        this.stmt.addBatch();
    }

    public void clearParameters()
        throws SQLException
    {
        this.stmt.clearParameters();
    }

    public boolean execute()
        throws SQLException
    {
        return this.stmt.execute();
    }

    public void setByte( int parameterIndex, byte x )
        throws SQLException
    {
        this.stmt.setByte( parameterIndex, x );
    }

    public void setDouble( int parameterIndex, double x )
        throws SQLException
    {
        this.stmt.setDouble( parameterIndex, x );
    }

    public void setFloat( int parameterIndex, float x )
        throws SQLException
    {
        this.stmt.setFloat( parameterIndex, x );
    }

    public void setInt( int parameterIndex, int x )
        throws SQLException
    {
        this.stmt.setInt( parameterIndex, x );
    }

    public void setNull( int parameterIndex, int sqlType )
        throws SQLException
    {
        this.stmt.setNull( parameterIndex, sqlType );
    }

    public void setLong( int parameterIndex, long x )
        throws SQLException
    {
        this.stmt.setLong( parameterIndex, x );
    }

    public void setShort( int parameterIndex, short x )
        throws SQLException
    {
        this.stmt.setShort( parameterIndex, x );
    }

    public void setBoolean( int parameterIndex, boolean x )
        throws SQLException
    {
        this.stmt.setBoolean( parameterIndex, x );
    }

    public void setBytes( int parameterIndex, byte x[] )
        throws SQLException
    {
        this.stmt.setBytes( parameterIndex, x );
    }

    public void setAsciiStream( int parameterIndex, InputStream x, int length )
        throws SQLException
    {
        this.stmt.setAsciiStream( parameterIndex, x, length );
    }

    public void setBinaryStream( int parameterIndex, InputStream x, int length )
        throws SQLException
    {
        this.stmt.setBinaryStream( parameterIndex, x, length );
    }

    public void setUnicodeStream( int parameterIndex, InputStream x, int length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream( int parameterIndex, Reader reader, int length )
        throws SQLException
    {
        this.stmt.setCharacterStream( parameterIndex, reader, length );
    }

    public void setObject( int parameterIndex, Object x )
        throws SQLException
    {
        this.stmt.setObject( parameterIndex, x );
    }

    public void setObject( int parameterIndex, Object x, int targetSqlType )
        throws SQLException
    {
        this.stmt.setObject( parameterIndex, x, targetSqlType );
    }

    public void setObject( int parameterIndex, Object x, int targetSqlType, int scale )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNull( int paramIndex, int sqlType, String typeName )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setString( int parameterIndex, String x )
        throws SQLException
    {
        this.stmt.setString( parameterIndex, x );
    }

    public void setBigDecimal( int parameterIndex, BigDecimal x )
        throws SQLException
    {
        this.stmt.setBigDecimal( parameterIndex, x );
    }

    public void setURL( int parameterIndex, URL x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setArray( int i, Array x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob( int i, Blob x )
        throws SQLException
    {
        this.stmt.setBlob( i, x );
    }

    public void setClob( int i, Clob x )
        throws SQLException
    {
        this.stmt.setClob( i, x );
    }

    public void setDate( int parameterIndex, Date x )
        throws SQLException
    {
        this.stmt.setDate( parameterIndex, x );
    }

    public ParameterMetaData getParameterMetaData()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setRef( int i, Ref x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet executeQuery()
        throws SQLException
    {
        return executeQuery( true );
    }

    public ResultSetMetaData getMetaData()
        throws SQLException
    {
        return this.stmt.getMetaData();
    }

    public void setTime( int parameterIndex, Time x )
        throws SQLException
    {
        this.stmt.setTime( parameterIndex, x );
    }

    public void setTimestamp( int parameterIndex, Timestamp x )
        throws SQLException
    {
        this.stmt.setTimestamp( parameterIndex, x );
    }

    public void setDate( int parameterIndex, Date x, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTime( int parameterIndex, Time x, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setTimestamp( int parameterIndex, Timestamp x, Calendar cal )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Executes the query and wraps the result if asked to do so.
     *
     * @param wrapped If <code>true</code>, the result will be wrapped.
     * @return An original or wrapped result set with the result of the execution.
     * @throws SQLException If the sql fails for any reason.
     */
    private ResultSet executeQuery( boolean wrapped )
        throws SQLException
    {
        ResultSet rs;
        if ( this.stmt instanceof DelegatingPreparedStatement )
        {
            rs = ( (DelegatingPreparedStatement) this.stmt ).executeQuery( wrapped );
        }
        else
        {
            rs = this.stmt.executeQuery();
        }

        if ( ( rs != null ) && wrapped )
        {
            return createWrappedResultSet( rs );
        }
        else
        {
            return rs;
        }
    }

    public void setRowId( int parameterIndex, RowId x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNString( int parameterIndex, String value )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNCharacterStream( int parameterIndex, Reader value, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob( int parameterIndex, NClob value )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob( int parameterIndex, Reader reader, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob( int parameterIndex, InputStream inputStream, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob( int parameterIndex, Reader reader, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setSQLXML( int parameterIndex, SQLXML xmlObject )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream( int parameterIndex, InputStream x, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream( int parameterIndex, InputStream x, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream( int parameterIndex, Reader reader, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setAsciiStream( int parameterIndex, InputStream x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBinaryStream( int parameterIndex, InputStream x )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setCharacterStream( int parameterIndex, Reader reader )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNCharacterStream( int parameterIndex, Reader value )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setClob( int parameterIndex, Reader reader )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setBlob( int parameterIndex, InputStream inputStream )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void setNClob( int parameterIndex, Reader reader )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
