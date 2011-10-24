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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingPreparedStatement;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;

/**
 * This class implements the logging connection decorator.
 */
public final class LoggingConnectionDecorator
    implements ConnectionDecorator
{
    private static final Logger LOG = LoggerFactory.getLogger( LoggingConnectionDecorator.class.getName() );

    public Connection decorate( final Connection connection )
        throws SQLException
    {
        return new ConnectionImpl( connection );
    }

    private void logSql( final String sql )
        throws SQLException
    {
        LOG.info( "JdbcSql: {}", sql );
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
            logSql( sql );
            return super.prepareStatement( sql );
        }
    }

    private final class StatementImpl
        extends DelegatingStatement
    {
        public StatementImpl( Statement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            logSql( sql );
            return super.executeUpdate( sql );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            logSql( sql );
            return super.execute( sql );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            logSql( sql );
            return super.executeQuery( sql );
        }
    }

    private final class PreparedStatementImpl
        extends DelegatingPreparedStatement
    {
        public PreparedStatementImpl( PreparedStatement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        public int executeUpdate( String sql )
            throws SQLException
        {
            logSql(sql );
            return super.executeUpdate( sql );
        }

        public boolean execute( String sql )
            throws SQLException
        {
            logSql( sql );
            return super.execute( sql );
        }

        public ResultSet executeQuery( String sql )
            throws SQLException
        {
            logSql( sql );
            return super.executeQuery( sql );
        }
    }
}
