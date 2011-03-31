/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.store.support.ConnectionFactory;

public final class SqlOperationHelper
{
    private final static String MODEL_VERSION_TABLE = "tModelVersion";

    private final static String SELECT_MODEL_VERSION_SQL = "SELECT mve_lversion FROM tModelVersion WHERE mve_skey = 'model'";

    private final static String UPDATE_MODEL_VERSION_SQL = "UPDATE tModelVersion SET mve_lversion = ? WHERE mve_skey = 'model'";

    private final static String SELECT_KEY_SQL = "SELECT key_lLastKey FROM tKey WHERE lower(key_sTableName) = ?";

    private final static String UPDATE_KEY_SQL = "UPDATE tKey SET key_lLastKey = key_lLastKey + 1 WHERE lower(key_sTableName) = ?";

    private final Dialect dialect;

    private final ConnectionFactory connectionFactory;

    private final TransactionTemplate transactionTemplate;

    public SqlOperationHelper( Dialect dialect, ConnectionFactory connectionFactory, TransactionTemplate transactionTemplate )
    {
        this.dialect = dialect;
        this.connectionFactory = connectionFactory;
        this.transactionTemplate = transactionTemplate;
    }

    public Dialect getDialect()
    {
        return this.dialect;
    }

    public DataSource getUndecoratedDataSource()
        throws SQLException
    {
        return this.connectionFactory.getAsDataSource( false );
    }

    public Connection getConnection()
        throws SQLException
    {
        return this.connectionFactory.getConnection( true );
    }

    public void close( Connection conn )
    {
        this.connectionFactory.releaseConnection( conn );
    }

    public int generateNextKey( String tableName )
        throws Exception
    {
        Connection conn = this.connectionFactory.getUnmanagedConnection( false );

        try
        {
            return generateNextKey( conn, tableName );
        }
        finally
        {
            conn.close();
        }
    }

    private int generateNextKey( Connection conn, String tableName )
        throws Exception
    {
        incrementKeyValue( conn, tableName );
        return selectKeyValue( conn, tableName );
    }

    private void incrementKeyValue( Connection conn, String tableName )
        throws Exception
    {
        PreparedStatement stmt = conn.prepareStatement( UPDATE_KEY_SQL );

        try
        {
            stmt.setString( 1, tableName.toLowerCase() );
            stmt.executeUpdate();
        }
        finally
        {
            close( stmt );
        }
    }

    private int selectKeyValue( Connection conn, String tableName )
        throws Exception
    {
        PreparedStatement stmt = conn.prepareStatement( SELECT_KEY_SQL );
        ResultSet result;

        try
        {
            stmt.setString( 1, tableName.toLowerCase() );
            result = stmt.executeQuery();
            if ( result.next() )
            {
                return result.getInt( 1 );
            }
            else
            {
                throw new SQLException( "Failed to generate key value for table: " + tableName );
            }
        }
        finally
        {
            close( stmt );
        }
    }

    public int getModelNumber()
        throws Exception
    {
        Connection conn = getConnection();

        try
        {
            return getModelNumber( conn );
        }
        finally
        {
            close( conn );
        }
    }

    private int getModelNumber( Connection conn )
        throws Exception
    {
        if ( hasTable( conn, MODEL_VERSION_TABLE ) )
        {
            return selectModelNumber( conn );
        }
        else
        {
            return -1;
        }
    }

    public void updateModelNumber( int number )
        throws Exception
    {
        Connection conn = getConnection();

        try
        {
            updateModelNumber( conn, number );
        }
        finally
        {
            close( conn );
        }
    }

    private int selectModelNumber( Connection conn )
        throws Exception
    {
        Statement stmt = conn.createStatement();
        ResultSet result = null;

        try
        {
            result = stmt.executeQuery( SELECT_MODEL_VERSION_SQL );
            if ( result.next() )
            {
                return result.getInt( 1 );
            }
            else
            {
                return 0;
            }
        }
        finally
        {
            close( result );
            close( stmt );
        }
    }

    private void updateModelNumber( Connection conn, int number )
        throws Exception
    {
        PreparedStatement stmt = conn.prepareStatement( UPDATE_MODEL_VERSION_SQL );

        try
        {
            stmt.setInt( 1, number );
            stmt.executeUpdate();
        }
        finally
        {
            close( stmt );
        }
    }

    private boolean hasTable( Connection conn, String tableName )
    {
        Statement stmt = null;

        try
        {
            stmt = conn.createStatement();
            close( stmt.executeQuery( "SELECT * FROM " + tableName ) );
            return true;
        }
        catch ( SQLException e )
        {
            try
            {
                conn.rollback();
            }
            catch ( Exception e2 )
            {
                // Do nothing
            }

            return false;
        }
        finally
        {
            close( stmt );
        }
    }

    public void close( Statement stmt )
    {
        JdbcUtils.closeStatement( stmt );
    }

    public void close( ResultSet result )
    {
        JdbcUtils.closeResultSet( result );
    }

    public Object execute( TransactionCallback callback )
    {
        return this.transactionTemplate.execute( callback );
    }

    public int getTransactionTimeout()
    {
        return transactionTemplate.getTimeout();
    }

    public void setTransactionTimeout( final int timeout )
    {
        transactionTemplate.setTimeout( timeout );
    }
}
