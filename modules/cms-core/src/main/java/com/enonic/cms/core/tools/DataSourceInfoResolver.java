/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class makes a diagnostics for a connection.
 */
@Component
public final class DataSourceInfoResolver
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( DataSourceInfoResolver.class );

    /**
     * Set the data source.
     */
    private DataSource dataSource;

    /**
     * Properties.
     */
    private Properties info;

    /**
     * Set the data source.
     */
    @Autowired
    public void setDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    /**
     * Return diagnostic information.
     */
    public Properties getInfo()
    {
        return getInfo( false );
    }

    /**
     * Return diagnostic information.
     */
    public Properties getInfo( boolean force )
    {
        if ( force || ( this.info == null ) )
        {
            this.info = new Properties();
            collectInfo();
        }

        return info;
    }

    /**
     * Create info.
     */
    public void collectInfo()
    {
        try
        {
            collectInfo( this.dataSource );
        }
        catch ( Throwable e )
        {
            LOG.error( "Failed to collect datasource info", e );
        }
    }

    /**
     * Create info.
     */
    public void collectInfo( DataSource dataSource )
            throws Exception
    {
        Connection conn = null;

        try
        {
            conn = dataSource.getConnection();
            collectInfo( conn );
        }
        finally
        {
            if ( conn != null )
            {
                conn.close();
            }
        }
    }

    /**
     * Create info.
     */
    public void collectInfo( Connection conn )
            throws Exception
    {
        collectInfo( conn.getMetaData() );
        this.info.setProperty( "transactionIsolation", getTransactionIsolationName( conn.getTransactionIsolation() ) );
    }

    /**
     * Create info.
     */
    public void collectInfo( DatabaseMetaData md )
            throws Exception
    {

        this.info.setProperty( "driverName", md.getDriverName() );
        this.info.setProperty( "driverVersion", md.getDriverVersion() );
        this.info.setProperty( "databaseProductName", md.getDatabaseProductName() );
        this.info.setProperty( "databaseProductVersion", md.getDatabaseProductVersion() );
        this.info.setProperty( "JDBCMajorVersion", String.valueOf( md.getJDBCMajorVersion() ) );
        this.info.setProperty( "JDBCMinorVersion", String.valueOf( md.getJDBCMinorVersion() ) );
        this.info.setProperty( "url", String.valueOf( md.getURL() ) );

    }

    /**
     * Return transaction isolation name.
     */
    private String getTransactionIsolationName( int value )
    {
        switch ( value )
        {
            case Connection.TRANSACTION_READ_COMMITTED:
                return "TRANSACTION_READ_COMMITTED";
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                return "TRANSACTION_READ_UNCOMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "TRANSACTION_REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "TRANSACTION_SERIALIZABLE";
        }

        return "TRANSACTION_NONE";
    }
}
