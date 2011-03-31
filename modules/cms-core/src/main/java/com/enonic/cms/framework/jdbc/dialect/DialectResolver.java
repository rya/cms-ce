/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class resolves the dialect by name or metadata.
 */
public final class DialectResolver
{
    /**
     * Instance of resolver.
     */
    private final static DialectResolver INSTANCE = new DialectResolver();

    /**
     * List of dialects.
     */
    private final ArrayList<Dialect> dialects;

    /**
     * Construct the resolver.
     */
    private DialectResolver()
    {
        this.dialects = new ArrayList<Dialect>();
        this.dialects.add( new PostgreSqlDialect() );
        this.dialects.add( new MySqlDialect() );
        this.dialects.add( new OracleDialect() );
        this.dialects.add( new Db2Dialect() );
        this.dialects.add( new SqlServerDialect() );
        this.dialects.add( new H2Dialect() );
    }

    /**
     * Resolve dialect by name.
     */
    public Dialect resolveByName( String name )
        throws IllegalArgumentException
    {
        for ( Dialect dialect : this.dialects )
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
     */
    public Dialect resolveByVendor( Connection conn )
        throws IllegalArgumentException, SQLException
    {
        return resolveByVendor( conn.getMetaData().getDatabaseProductName() );
    }

    /**
     * Resolve dialect by database product.
     */
    public Dialect resolveByVendor( String vendorId )
        throws IllegalArgumentException
    {
        for ( Dialect dialect : this.dialects )
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
     */
    public Dialect[] getDialects()
    {
        return this.dialects.toArray( new Dialect[this.dialects.size()] );
    }

    /**
     * Return the instance.
     */
    public static DialectResolver getInstance()
    {
        return INSTANCE;
    }
}
