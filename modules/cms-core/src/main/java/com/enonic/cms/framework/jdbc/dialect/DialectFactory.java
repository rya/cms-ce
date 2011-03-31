/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * This class implements the dialect factory.
 */
public final class DialectFactory
        implements FactoryBean
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( DialectFactory.class );

    /**
     * Data source.
     */
    private DataSource dataSource;

    /**
     * Dialect name.
     */
    private String dialectName;

    /**
     * Resolved dialect.
     */
    private Dialect resolvedDialect;

    /**
     * Resolve dialect.
     */
    private Dialect resolveDialect()
            throws Exception
    {
        if ( this.dialectName != null )
        {
            return DialectResolver.getInstance().resolveByName( this.dialectName );
        }
        else
        {
            Connection conn = this.dataSource.getConnection();

            try
            {
                return DialectResolver.getInstance().resolveByVendor( conn );
            }
            finally
            {
                conn.close();
            }
        }
    }

    /**
     * Return the dialect.
     */
    public synchronized Object getObject()
            throws Exception
    {
        if ( this.resolvedDialect == null )
        {
            this.resolvedDialect = resolveDialect();
            LOG.info( "Using [" + this.resolvedDialect.getName() + "] dialect" );
        }

        return this.resolvedDialect;
    }

    /**
     * Return the object type.
     */
    public Class getObjectType()
    {
        return Dialect.class;
    }

    /**
     * Return true if singleton.
     */
    public boolean isSingleton()
    {
        return true;
    }

    /**
     * Set the dialect name.
     */
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
            this.dialectName = "postgresql";
        }
        else
        {
            this.dialectName = dialectName;
        }
    }

    /**
     * Set the datasource.
     */
    public void setDataSource( DataSource dataSource )
    {
        this.dataSource = dataSource;
    }
}
