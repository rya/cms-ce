package com.enonic.cms.store.support.decorators;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * generated using IntelliJ IDEA's Delegate Methods... ( Alt+Ins )
 */
public class DecoratedDataSource
        implements DataSource
{
    private DataSource dataSource;

    public DecoratedDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    public Connection getConnection()
            throws SQLException
    {
        return dataSource.getConnection();
    }

    public Connection getConnection( String username, String password )
            throws SQLException
    {
        return dataSource.getConnection( username, password );
    }

    public PrintWriter getLogWriter()
            throws SQLException
    {
        return dataSource.getLogWriter();
    }

    public void setLogWriter( PrintWriter out )
            throws SQLException
    {
        dataSource.setLogWriter( out );
    }

    public void setLoginTimeout( int seconds )
            throws SQLException
    {
        dataSource.setLoginTimeout( seconds );
    }

    public int getLoginTimeout()
            throws SQLException
    {
        return dataSource.getLoginTimeout();
    }

    public <T> T unwrap( Class<T> iface )
            throws SQLException
    {
        return dataSource.unwrap( iface );
    }

    public boolean isWrapperFor( Class<?> iface )
            throws SQLException
    {
        return dataSource.isWrapperFor( iface );
    }

}
