/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.springframework.beans.factory.InitializingBean;

import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.jdbc.ConnectionDecorator;
import com.enonic.cms.framework.jdbc.DialectConnectionDecorator;
import com.enonic.cms.framework.jdbc.LoggingConnectionDecorator;
import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.store.hibernate.cache.invalidation.CacheInvalidator;
import com.enonic.cms.store.hibernate.cache.invalidation.InvalidatorConnectionDecorator;

/**
 * This class implements the decorator manager.
 */
public final class DecoratorManager
    implements ConnectionDecorator, InitializingBean
{
    /**
     * Session factory.
     */
    private SessionFactory sessionFactory;

    /**
     * Cache manager.
     */
    private CacheManager cacheManager;

    /**
     * Dialect connection decorator.
     */
    private DialectConnectionDecorator dialectDecorator;

    /**
     * Invalidator connection decorator.
     */
    private InvalidatorConnectionDecorator invalidatorDecorator;

    /**
     * Logging decorator.
     */
    private LoggingConnectionDecorator loggingDecorator;

    /**
     * Resolved dialect.
     */
    private Dialect dialect;

    /**
     * Logging on/off.
     */
    private boolean logging;

    /**
     * Set true if logging.
     */
    public void setLogging( boolean logging )
    {
        this.logging = logging;
    }

    /**
     * Set the hibernate session factory.
     */
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Set the cache manager.
     */
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    /**
     * Decorate the connection.
     */
    public Connection decorate( Connection conn )
        throws SQLException
    {
        if ( this.logging )
        {
            conn = this.loggingDecorator.decorate( conn );
        }

        conn = this.dialectDecorator.decorate( conn );
        conn = this.invalidatorDecorator.decorate( conn );

        return conn;
    }

    /**
     * Set the dialect.
     */
    public void setDialect( Dialect dialect )
    {
        this.dialect = dialect;
    }

    /**
     * Configure the data source.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        SessionFactoryImplementor impl = (SessionFactoryImplementor) this.sessionFactory;
        Configuration config = HibernateConfigurator.getInstance().getHibernateConfiguration();
        CacheInvalidator cacheInvalidator = new CacheInvalidator( config, impl, this.cacheManager );
        this.invalidatorDecorator = new InvalidatorConnectionDecorator( cacheInvalidator );
        this.loggingDecorator = new LoggingConnectionDecorator();
        this.dialectDecorator = new DialectConnectionDecorator( this.dialect );
    }
}
