/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.enonic.cms.framework.jdbc.ConnectionDecorator;
import com.enonic.cms.framework.jdbc.delegate.DelegatingConnection;

/**
 * This class implements the connection factory.
 */
public final class ConnectionFactory
{
    /**
     * Data source.
     */
    private DataSource dataSource;

    /**
     * Decorator manager.
     */
    private ConnectionDecorator decorator;

    /**
     * Set the datasource.
     */
    public void setDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    /**
     * Set the dialect manager.
     */
    public void setDecorator( ConnectionDecorator decorator )
    {
        this.decorator = decorator;
    }

    /**
     * Return a managed connection.
     */
    public Connection getConnection( boolean decorated )
        throws SQLException
    {
        Connection conn = DataSourceUtils.getConnection( this.dataSource );
        return decorated ? this.decorator.decorate( conn ) : conn;
    }

    /**
     * Release connection.
     */
    public void releaseConnection( Connection conn )
    {
        if ( conn != null )
        {
            if ( conn instanceof DelegatingConnection )
            {
                conn = (Connection) ( (DelegatingConnection) conn ).getInnerDelegate();
            }

            DataSourceUtils.releaseConnection( conn, this.dataSource );
        }
    }

    /**
     * Return unmanaged connection.
     */
    public Connection getUnmanagedConnection( boolean decorated )
        throws SQLException
    {
        Connection conn = this.dataSource.getConnection();
        return decorated ? this.decorator.decorate( conn ) : conn;
    }

    /**
     * Return the factory as a data source.
     */
    public DataSource getAsDataSource( boolean decorated )
    {
        return decorated ? new DecoratedDataSource( this.dataSource, this.decorator ) : this.dataSource;
    }
}
