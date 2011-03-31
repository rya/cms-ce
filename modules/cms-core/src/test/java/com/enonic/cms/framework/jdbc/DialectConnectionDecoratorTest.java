/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.jdbc.dialect.PostgreSqlDialect;

/**
 * Jan 19, 2010
 */
public class DialectConnectionDecoratorTest
    extends AbstractConnectionDecoratorTest
{
    private ConnectionDecorator connectionDecorator;

    @Before
    public void before()
        throws SQLException
    {
        connectionDecorator = new DialectConnectionDecorator( new PostgreSqlDialect() );
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
