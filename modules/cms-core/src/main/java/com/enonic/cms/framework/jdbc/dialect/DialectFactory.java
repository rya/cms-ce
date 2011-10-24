/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.dialect;

import javax.sql.DataSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class implements the dialect factory.
 */
public final class DialectFactory
    implements FactoryBean<Dialect>, InitializingBean
{
    private final DialectResolver resolver;
    private Dialect dialect;

    public DialectFactory()
    {
        this.resolver = new DialectResolver();
    }

    public void setDialectName(final String dialectName)
    {
        this.resolver.setDialectName(dialectName);
    }

    public void setDataSource(final DataSource dataSource)
    {
        this.resolver.setDataSource(dataSource);
    }

    public void afterPropertiesSet()
    {
        this.dialect = this.resolver.resolveDialect();
    }

    public Dialect getObject()
    {
        return this.dialect;
    }

    public Class getObjectType()
    {
        return Dialect.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
