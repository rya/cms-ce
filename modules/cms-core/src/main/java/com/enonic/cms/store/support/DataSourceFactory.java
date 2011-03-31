/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public final class DataSourceFactory
    implements FactoryBean, InitializingBean
{
    private boolean traceEnabled;

    private DataSource dataSource;

    private DataSource originalDataSource;

    public void setTraceEnabled( boolean traceEnabled )
    {
        this.traceEnabled = traceEnabled;
    }

    public void setOriginalDataSource( DataSource value )
    {
        this.originalDataSource = value;
    }

    public void afterPropertiesSet()
    {
        if ( !this.traceEnabled )
        {
            this.dataSource = this.originalDataSource;
        }
        else
        {
            this.dataSource = new TraceableDataSourceImpl( this.originalDataSource );
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
}
