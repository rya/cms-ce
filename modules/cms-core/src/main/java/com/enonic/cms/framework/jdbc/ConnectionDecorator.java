/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This interface defines a connection decorator.
 */
public interface ConnectionDecorator
{
    /**
     * Return the decorated connection.
     */
    public Connection decorate( Connection connection )
        throws SQLException;
}
