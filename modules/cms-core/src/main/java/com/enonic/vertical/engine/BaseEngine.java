/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.support.JdbcUtils;

import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.store.support.ConnectionFactory;

public abstract class BaseEngine
{

    private ConnectionFactory connectionFactory;

    protected DatasourceExecutorFactory datasourceExecutorFactory;

    protected DataSourceService dataSourceService;

    private final static ThreadLocal<Connection> SHARED_CONNECTION = new ThreadLocal<Connection>();

    public void setDataSourceService( DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }

    public void setConnectionFactory( ConnectionFactory connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }

    public void setDatasourceExecutorFactory( DatasourceExecutorFactory value )
    {
        this.datasourceExecutorFactory = value;
    }

    /**
     * Tries to close database connection.
     */
    public final void close( Connection connection )
    {
        if ( SHARED_CONNECTION.get() != connection )
        {
            this.connectionFactory.releaseConnection( connection );
        }
    }

    /**
     * Tries to close a database result set.
     */
    public final void close( ResultSet resultSet )
    {
        JdbcUtils.closeResultSet( resultSet );
    }

    /**
     * Tries to close a database statement.
     */
    public final void close( Statement stmt )
    {
        JdbcUtils.closeStatement( stmt );
    }

    /**
     * Returns a connection.
     */
    public final Connection getConnection()
        throws SQLException
    {
        Connection conn = SHARED_CONNECTION.get();
        if ( conn == null )
        {
            conn = this.connectionFactory.getConnection( true );
        }

        return conn;
    }

}
