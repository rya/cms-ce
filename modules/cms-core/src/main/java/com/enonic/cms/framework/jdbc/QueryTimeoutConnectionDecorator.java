package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingPreparedStatement;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;

/**
 * This class implements the connection decorator.
 *
 * Ignores setQueryTimeout calls in Statement and PreparedStatement instances
 *
 * required for PostgreSQL databases to fix "not implemented" issue
 *
 */
public final class QueryTimeoutConnectionDecorator
    implements ConnectionDecorator
{
    private final static Logger LOG = LoggerFactory.getLogger( QueryTimeoutConnectionDecorator.class );

    /**
     * Decorate the connection.
     */
    public Connection decorate( final Connection connection )
        throws SQLException
    {
        return new ConnectionImpl( connection );
    }

    /**
     * Dialect connection.
     */
    private final class ConnectionImpl
        extends DelegatingConnection
    {
        /**
         * Construct the connection.
         * @param conn Connection
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
    }

    /**
     * Dialect statement.
     */
    private final class StatementImpl
        extends DelegatingStatement
    {
        /**
         * Construct the statement.
         * @param stmt Statement
         * @param conn Connection
         */
        public StatementImpl( Statement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        @Override
        public void setQueryTimeout( int seconds )
                throws SQLException
        {
            LOG.info( "ignoring setQueryTimeout ( {} ) call", seconds );
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
         * @param stmt PreparedStatement
         * @param conn Connection
         */
        public PreparedStatementImpl( PreparedStatement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        @Override
        public void setQueryTimeout( int seconds )
                throws SQLException
        {
            LOG.info( "ignoring setQueryTimeout ( {} ) call", seconds );
        }
    }
}
