/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.util.HashMap;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.jdbc.dialect.Db2Dialect;
import com.enonic.cms.framework.jdbc.dialect.Dialect;
import com.enonic.cms.framework.jdbc.dialect.H2Dialect;
import com.enonic.cms.framework.jdbc.dialect.MySqlDialect;
import com.enonic.cms.framework.jdbc.dialect.OracleDialect;
import com.enonic.cms.framework.jdbc.dialect.PostgreSqlDialect;
import com.enonic.cms.framework.jdbc.dialect.SqlServerDialect;

import com.enonic.cms.store.hibernate.cache.HibernateCacheBootstrap;

/**
 * This class implements the hibernate configurator.
 */
public final class HibernateConfigurator
    extends LocalSessionFactoryBean
{
    /**
     * Shared instance.
     */
    private static HibernateConfigurator INSTANCE;

    /**
     * Dialect.
     */
    private Dialect dialect;

    /**
     * Cache manager.
     */
    private CacheManager cacheManager;

    /**
     * Construc the configurator.
     */
    public HibernateConfigurator()
    {
        INSTANCE = this;
    }

    /**
     * Set the dialect name.
     */
    public void setDialect( Dialect dialect )
    {
        this.dialect = dialect;
    }

    /**
     * Set the cache manager.
     */
    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }
    
    public Configuration getHibernateConfiguration()
    {
        return getConfiguration();
    }

    /**
     * After set properties.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        HibernateCacheBootstrap cacheBootstrap = new HibernateCacheBootstrap();
        cacheBootstrap.setCacheName( "entity" );
        cacheBootstrap.setCacheManager( this.cacheManager );
        setHibernateDialect( getHibernateProperties() );

        final HashMap<String, Object> listenerMap = new HashMap<String, Object>();
        listenerMap.put( "post-delete", EntityChangeListenerHub.getInstance() );
        listenerMap.put( "post-insert", EntityChangeListenerHub.getInstance() );
        listenerMap.put( "post-update", EntityChangeListenerHub.getInstance() );
        setEventListeners( listenerMap );

        getHibernateProperties().setProperty(Environment.SHOW_SQL, "true");

        super.afterPropertiesSet();
    }

    /**
     * Set the hibernate dialect.
     */
    private void setHibernateDialect( Properties props )
    {
        String dialectClass = resolveDialectClass();
        if ( ( props != null ) && ( dialectClass != null ) )
        {
            props.setProperty( Environment.DIALECT, dialectClass );
        }
    }

    /**
     * Resolve the dialect class.
     */
    private String resolveDialectClass()
    {
        if ( this.dialect instanceof Db2Dialect )
        {
            return org.hibernate.dialect.DB2Dialect.class.getName();
        }
        else if ( this.dialect instanceof SqlServerDialect )
        {
            return org.hibernate.dialect.SQLServerDialect.class.getName();
        }
        else if ( this.dialect instanceof MySqlDialect )
        {
            return org.hibernate.dialect.MySQLDialect.class.getName();
        }
        else if ( this.dialect instanceof OracleDialect )
        {
            return org.hibernate.dialect.Oracle10gDialect.class.getName();
        }
        else if ( this.dialect instanceof PostgreSqlDialect )
        {
            return org.hibernate.dialect.PostgreSQLDialect.class.getName();
        }
        else if ( this.dialect instanceof H2Dialect )
        {
            return org.hibernate.dialect.H2Dialect.class.getName();
        }
        else
        {
            return null;
        }
    }

    /**
     * Return the instance.
     */
    public static HibernateConfigurator getInstance()
    {
        return INSTANCE;
    }
}
