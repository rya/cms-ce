/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingPreparedStatement;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;

/**
 * This class implements the logging connection decorator.
 */
public final class LoggingConnectionDecorator
    implements ConnectionDecorator
{
    /**
     * Decorate the connection.
     */
    public Connection decorate( final Connection connection )
        throws SQLException
    {
        return new ConnectionImpl( connection );
    }

    /**
     * Log the sql.
     */
    private void logSql( final Connection conn, final String sql )
        throws SQLException
    {

        StringBuffer message = new StringBuffer( sql.length() + 50 );
        message.append( "JdbcSql: " );
        message.append( sql );
        System.out.println( message.toString() );
    }

    /**
     * Dialect connection.
     */
    private final class ConnectionImpl
        extends DelegatingConnection
    {
        /**
         * Construct the connection.
         */
        public ConnectionImpl( Connection conn )
        {
            super( conn );
        }

        /**
         * Create prepared statement.
         */
        protected Statement createWrappedStatement( Statement stmt )
        {
            return new StatementImpl( stmt, this );
        }

        /**
         * Create prepared statement.
         */
        protected PreparedStatement createWrappedPreparedStatement( PreparedStatement stmt, String sql )
        {
            return new PreparedStatementImpl( stmt, this );
        }

        public PreparedStatement prepareStatement( String sql )
            throws SQLException
        {
            logSql( this, sql );
            return super.prepareStatement( sql );
        }
    }

    /**
     * Dialect statement.
     */
    private final class StatementImpl
        extends DelegatingStatement
    {
        /**
         * Construct the statement.
         */
        public StatementImpl( Statement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            logSql( getConnection(), sql );
            return super.executeUpdate( sql );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            logSql( getConnection(), sql );
            return super.execute( sql );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            logSql( getConnection(), sql );
            return super.executeQuery( sql );
        }
    }

    /**
     * Dialect prepared statement.
     */
    private final class PreparedStatementImpl
        extends DelegatingPreparedStatement
    {
        /**
         * Construct the statement.
         */
        public PreparedStatementImpl( PreparedStatement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            logSql( getConnection(), sql );
            return super.executeUpdate( sql );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            logSql( getConnection(), sql );
            return super.execute( sql );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            logSql( getConnection(), sql );
            return super.executeQuery( sql );
        }
    }
}
