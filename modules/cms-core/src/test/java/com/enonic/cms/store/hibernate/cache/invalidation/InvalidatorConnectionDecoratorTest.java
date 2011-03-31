/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.framework.jdbc.AbstractConnectionDecoratorTest;
import com.enonic.cms.framework.jdbc.ConnectionDecorator;

/**
 * Jan 19, 2010
 */
public class InvalidatorConnectionDecoratorTest
    extends AbstractConnectionDecoratorTest
{
    private ConnectionDecorator connectionDecorator;

    @Before
    public void before()
        throws SQLException
    {
        CacheInvalidator cacheInvalidator = Mockito.mock( CacheInvalidator.class );
        connectionDecorator = new InvalidatorConnectionDecorator( cacheInvalidator );
        setupRealConnectionStatementAndResultSet();
    }

    @Test
    public void one_level_of_decorating()
        throws SQLException
    {
        testDecoratingLevel( connectionDecorator, 1 );
    }

    @Test
    public void two_levels_of_decorating()
        throws SQLException
    {
        testDecoratingLevel( connectionDecorator, 2 );
    }

    @Test
    public void three_levels_of_decorating()
        throws SQLException
    {
        testDecoratingLevel( connectionDecorator, 3 );
    }
}
