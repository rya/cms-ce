package com.enonic.cms.framework.jdbc;

import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;
import com.enonic.cms.framework.jdbc.delegate.DelegatingPreparedStatement;
import com.enonic.cms.framework.jdbc.delegate.DelegatingStatement;

import java.sql.*;

/**
 * This class fixes some annoying things in jdbc drivers. At this point, only setQueryTimeout is fixed.
 * Reason: PostgreSQL JDBC driver versions 8.3, 8.4, 9.0 do not implement <code>setQueryTimeout(int)</code> method.
 */
public final class DriverFixConnectionDecorator
    implements ConnectionDecorator
{
    public Connection decorate( final Connection connection )
        throws SQLException
    {
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
    }

    private final class StatementImpl
        extends DelegatingStatement
    {
        public StatementImpl( Statement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        @Override
        public void setQueryTimeout( int seconds )
            throws SQLException
        {
            doSetQueryTimeout( this.stmt, seconds );
        }
    }

    private final class PreparedStatementImpl
        extends DelegatingPreparedStatement
    {
        public PreparedStatementImpl( PreparedStatement stmt, Connection conn )
        {
            super( stmt, conn );
        }

        @Override
        public void setQueryTimeout( int seconds )
            throws SQLException
        {
            doSetQueryTimeout( this.stmt, seconds );
        }
    }

    private void doSetQueryTimeout( final Statement stmt, final int seconds )
        throws SQLException
    {
        try {
            stmt.setQueryTimeout( seconds );
        } catch ( final SQLException e ) {
            // Ignore
        }
    }
}
