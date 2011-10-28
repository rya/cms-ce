/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.AbstractDataSource;

/**
 * This class implements the data source helper.
 */
public final class DecoratedDataSource
    extends AbstractDataSource
{
    private final DataSource dataSource;

    private final ConnectionDecorator decorator;

    public DecoratedDataSource( final DataSource dataSource, final ConnectionDecorator decorator )
    {
        this.dataSource = dataSource;
        this.decorator = decorator;
    }

    private DataSource getInnerDataSource()
    {
        if (this.dataSource instanceof DecoratedDataSource) {
            return ((DecoratedDataSource)this.dataSource).dataSource;
        } else {
            return this.dataSource;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> type)
        throws SQLException
    {
        if (type.equals(DataSource.class)) {
            return (T)getInnerDataSource();
        }

        return super.unwrap(type);
    }

    public Connection getConnection()
        throws SQLException
    {
        return this.decorator.decorate( this.dataSource.getConnection() );
    }

    public Connection getConnection( String user, String password )
        throws SQLException
    {
        return this.decorator.decorate( this.dataSource.getConnection( user, password ) );
    }
}
