/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

import com.enonic.cms.framework.jdbc.ConnectionDecorator;

/**
 * This class implements the data source helper.
 */
public final class DecoratedDataSource
    extends AbstractDataSource
{

    /**
     * Data source.
     */
    private final DataSource dataSource;

    /**
     * Decorator manager.
     */
    private final ConnectionDecorator decorator;

    /**
     * Construc the data source.
     */
    public DecoratedDataSource( DataSource dataSource, ConnectionDecorator decorator )
    {
        this.dataSource = dataSource;
        this.decorator = decorator;
    }

    public DataSource getWrappedDataSource()
    {
        return this.dataSource;
    }

    /**
     * Return a connection from data source.
     */
    public Connection getConnection()
        throws SQLException
    {
        return this.decorator.decorate( this.dataSource.getConnection() );
    }

    /**
     * Return a connection from data source.
     */
    public Connection getConnection( String user, String password )
        throws SQLException
    {
        throw new SQLException( "Not implemented" );
    }
}
