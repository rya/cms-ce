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

    private DialectResolver dialectResolver;

    /**
     * Return the dialect.
     */
    public synchronized Object getObject()
            throws Exception
    {
        return dialectResolver.resolveDialect();
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

    public void setDialectResolver( DialectResolver dialectResolver )
    {
        this.dialectResolver = dialectResolver;
    }
}
