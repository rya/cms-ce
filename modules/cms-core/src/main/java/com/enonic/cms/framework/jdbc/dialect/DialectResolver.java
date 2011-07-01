/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class resolves the dialect by name or metadata.
 */
public final class DialectResolver
{
    private final static Logger LOG = LoggerFactory.getLogger( DialectResolver.class );

     /**
     * List of dialects.
      */
    private final static Dialect[] DIALECTS;

    static {
        DIALECTS = new Dialect[] {
            new PostgreSqlDialect(),
            new MySqlDialect(),
            new OracleDialect(),
            new Db2Dialect(),
            new SqlServerDialect(),
            new H2Dialect()
        };
    }

    private final static int POSTGRESQL_INDEX = 0;
    
    private String dialectName;

    /**
     * Original datasource
     */
    private DataSource originalDataSource;

    /**
     * Resolved dialect.
     */
    private Dialect resolvedDialect;


    /**
     * Resolve dialect by name.
     * @param name dialect name
     * @return Dialect
     * @throws IllegalArgumentException exception
     */
    public static Dialect resolveByName( String name )
        throws IllegalArgumentException
    {
        for ( Dialect dialect : DIALECTS )
        {
            if ( name.equalsIgnoreCase( dialect.getName() ) )
            {
                return dialect;
            }
        }

        throw new IllegalArgumentException( "Dialect with name [" + name + "] not found" );
    }

    /**
     * Resolve dialect by database product.
     * @param conn Connection
     * @return Dialect
     * @throws IllegalArgumentException exception
     */
    public static Dialect resolveByVendor( Connection conn )
        throws IllegalArgumentException, SQLException
    {
        return resolveByVendor( conn.getMetaData().getDatabaseProductName() );
    }

    /**
     * Resolve dialect by database product.
     * @param vendorId database vendor id
     * @return Dialect
     * @throws IllegalArgumentException exception
     */
    public static Dialect resolveByVendor( String vendorId )
        throws IllegalArgumentException
    {
        for ( Dialect dialect : DIALECTS )
        {
            if ( dialect.matchesVendorId( vendorId ) )
            {
                return dialect;
            }
        }

        throw new IllegalArgumentException( "No dialects to match vendor [" + vendorId + "]" );
    }

    /**
     * Return all dialects.
     * @return Dialect array
     */
    public static Dialect[] getDialects()
    {
        return DIALECTS;
    }
    
    public void setDialectName( String dialectName )
     {
        if ( dialectName == null )
        {
            this.dialectName = null;
        }
        else if ( "auto".equals( dialectName ) )
        {
            this.dialectName = null;
        }
        else if ( dialectName.startsWith( "postgres" ) )
        {
            this.dialectName = DIALECTS[POSTGRESQL_INDEX].getName();
        }
        else
        {
            this.dialectName = dialectName;
        }
    }    


    public static Dialect resolveVendorName( DataSource dataSource )
    {
        Connection conn = null;

        try
        {
            conn = dataSource.getConnection();

            return resolveByVendor( conn );
        }
        catch ( Exception e ) // do not EAT exceptions !
        {
            LOG.error( "database resolving failed !", e );

            throw new RuntimeException( e );
        }
        finally
        {
            safeCloseConnection( conn );
        }
    }

    private static void safeCloseConnection( Connection conn )
    {
        if ( conn != null )
        {
            try
            {
                conn.close();
            }
            catch ( Exception e )
            {
                LOG.error( "closing connection to database failed !", e );
            }
        }
    }

    public Dialect resolveDialect()
    {
        if ( resolvedDialect == null )
        {
            if ( this.dialectName == null )
            {
                resolvedDialect = resolveVendorName( this.originalDataSource );
            }
            else
            {
                resolvedDialect = resolveByName( this.dialectName );
            }
        }

        return resolvedDialect;
    }

    public DataSource getOriginalDataSource()
    {
        return originalDataSource;
    }

    public void setOriginalDataSource( DataSource originalDataSource )
    {
        this.originalDataSource = originalDataSource;
    }
}
