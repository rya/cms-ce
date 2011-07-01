/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.enonic.cms.framework.jdbc.QueryTimeoutConnectionDecorator;
import com.enonic.cms.framework.jdbc.dialect.DialectResolver;
import com.enonic.cms.framework.jdbc.dialect.PostgreSqlDialect;

public final class DataSourceFactory
    implements FactoryBean, InitializingBean, BeanFactoryPostProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( DataSourceFactory.class );

    private boolean traceEnabled;

    private DataSource dataSource;

    private DialectResolver dialectResolver;

    public void setTraceEnabled( boolean traceEnabled )
    {
        this.traceEnabled = traceEnabled;
    }

    public void afterPropertiesSet()
    {
        if ( !this.traceEnabled )
        {
            this.dataSource = dialectResolver.getOriginalDataSource();
        }
        else
        {
            this.dataSource = new TraceableDataSourceImpl( dialectResolver.getOriginalDataSource() );
        }
    }

    public Object getObject()
    {
        return this.dataSource;
    }

    public Class getObjectType()
    {
        return DataSource.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    /**
     * <code>postProcessBeanFactory</code> sets transactions timeouts to default value when PostgreSQL database is used
     *
     * <p>also decorate datasource to return proxied Statement that will ignore setQueryTimeout calls
     *
     * <p>Reason: PostgreSQL JDBC driver versions 8.3, 8.4, 9.0 do not implement <code>setQueryTimeout(int)</code> method
     */
    @Override
    public void postProcessBeanFactory( ConfigurableListableBeanFactory beanFactory )
            throws BeansException
    {
        if ( dialectResolver.resolveDialect() instanceof PostgreSqlDialect ) {
            // set timeout value for unspecified timeout in @Transactional annotation to JDBC default value
            BeanDefinition transactionManager = beanFactory.getBeanDefinition( "transactionManager" );
            // remove defaultTimeout that is specified in dataSourceContext.xml .
            transactionManager.getPropertyValues().removePropertyValue( "defaultTimeout" );

            LOG.info( "decorating database connection for ignoring setQueryTimeout calls" );

            this.dataSource = new DecoratedDataSource( this.dataSource, new QueryTimeoutConnectionDecorator() );
        }
    }

    public void setDialectResolver( DialectResolver dialectResolver )
    {
        this.dialectResolver = dialectResolver;
    }
}
