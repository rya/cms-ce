/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import javax.sql.DataSource;

import com.enonic.cms.framework.jdbc.DecoratedDataSource;
import com.enonic.cms.framework.jdbc.DriverFixConnectionDecorator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

public final class DataSourceFactory
    implements FactoryBean<DataSource>, InitializingBean
{
    private DataSource dataSource;

    private String jndiName;

    public void setJndiName( String jndiName )
    {
        this.jndiName = jndiName;
    }

    public void afterPropertiesSet()
    {
        final JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        final DataSource original = lookup.getDataSource(this.jndiName);

        // Create decorated datasource to cope with driver defects
        this.dataSource = new DecoratedDataSource( original, new DriverFixConnectionDecorator() );
    }

    public DataSource getObject()
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
