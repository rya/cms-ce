/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.enonic.cms.framework.jdbc.ConnectionDecorator;
import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingPreparedStatement;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;

/**
 * This class implements a connection decorator based on the auto cache invalidator.
 */
public final class InvalidatorConnectionDecorator
    implements ConnectionDecorator
{
    /**
     * Cache invalidator.
     */
    private final CacheInvalidator invalidator;

    /**
     * Construct the decorator.
     */
    public InvalidatorConnectionDecorator( CacheInvalidator invalidator )
    {
        this.invalidator = invalidator;
    }

    /**
     * Decorate the connection.
     */
    public Connection decorate( Connection connection )
        throws SQLException
    {
        return new ConnectionImpl( connection, this.invalidator );
    }

    /**
     * Dialect connection.
     */
    private final class ConnectionImpl
        extends DelegatingConnection
    {
        /**
         * Invalidator.
         */
        private final CacheInvalidator invalidator;

        /**
         * Construct the connection.
         */
        public ConnectionImpl( Connection conn, CacheInvalidator invalidator )
        {
            super( conn );
            this.invalidator = invalidator;
        }

        /**
         * Create prepared statement.
         */
        protected Statement createWrappedStatement( Statement stmt )
        {
            return new StatementImpl( stmt, this, this.invalidator );
        }

        /**
         * Create prepared statement.
         */
        protected PreparedStatement createWrappedPreparedStatement( PreparedStatement stmt, String sql )
        {
            return new PreparedStatementImpl( sql, stmt, this, this.invalidator );
        }

        public PreparedStatement prepareStatement( String sql )
            throws SQLException
        {
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
         * Invalidator.
         */
        private final CacheInvalidator invalidator;

        /**
         * Construct the statement.
         */
        public StatementImpl( Statement stmt, Connection conn, CacheInvalidator invalidator )
        {
            super( stmt, conn );
            this.invalidator = invalidator;
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            this.invalidator.invalidateSql( sql );
            return super.executeUpdate( sql );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            this.invalidator.invalidateSql( sql );
            return super.execute( sql );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
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
         * Sql.
         */
        private final String sql;

        /**
         * Invalidator.
         */
        private final CacheInvalidator invalidator;

        /**
         * List of parameters.
         */
        private final ArrayList<Object> paramList;

        /**
         * Construct the statement.
         */
        public PreparedStatementImpl( String sql, PreparedStatement stmt, Connection conn, CacheInvalidator invalidator )
        {
            super( stmt, conn );
            this.sql = sql;
            this.invalidator = invalidator;
            this.paramList = new ArrayList<Object>();
        }

        public boolean execute()
            throws SQLException
        {
            this.invalidator.invalidateSql( this.sql, this.paramList );
            return super.execute();
        }

        public int executeUpdate()
            throws SQLException
        {
            this.invalidator.invalidateSql( this.sql, this.paramList );
            return super.executeUpdate();
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            this.invalidator.invalidateSql( sql );
            return super.executeUpdate( sql );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            this.invalidator.invalidateSql( sql );
            return super.execute( sql );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            return super.executeQuery( sql );
        }

        public void setObject( int parameterIndex, Object x )
            throws SQLException
        {
            super.setObject( parameterIndex, x );
            setParam( parameterIndex, x );
        }

        public void setObject( int parameterIndex, Object x, int targetSqlType )
            throws SQLException
        {
            super.setObject( parameterIndex, x, targetSqlType );
            setParam( parameterIndex, x );
        }

        public void setObject( int parameterIndex, Object x, int targetSqlType, int scale )
            throws SQLException
        {
            super.setObject( parameterIndex, x, targetSqlType, scale );
            setParam( parameterIndex, x );
        }

        public void setString( int parameterIndex, String x )
            throws SQLException
        {
            super.setString( parameterIndex, x );
            setParam( parameterIndex, x );
        }

        public void setInt( int parameterIndex, int x )
            throws SQLException
        {
            super.setInt( parameterIndex, x );
            setParam( parameterIndex, x );
        }

        private void setParam( int index, Object value )
        {
            while ( this.paramList.size() < index )
            {
                this.paramList.add( null );
            }

            this.paramList.set( index - 1, value );
        }
    }
}
